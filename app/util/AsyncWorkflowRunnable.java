package util;

import controllers.Application;
import dao.UserDao;
import dao.WorkflowDao;
import models.db.user.User;
import models.db.workflow.*;

import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.data.WorkflowProduct;
import play.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static play.mvc.Controller.request;

/**
 * Created by lowery on 5/10/16.
 */
public class AsyncWorkflowRunnable implements Runnable {
    private static final WorkflowDao workflowDao = new WorkflowDao();
    private static final UserDao userDao = new UserDao();

    private WorkflowRunner runner;
    private WorkflowRun run;

    private String workflow;

    private Date startTime;
    private Date endTime;

    private ByteArrayOutputStream errStream;
    private ByteArrayOutputStream outStream;

    public synchronized void init(Workflow workflow, WorkflowRunner runner, ByteArrayOutputStream errStream, ByteArrayOutputStream outStream) {
        this.errStream = errStream;
        this.outStream = outStream;
        this.runner = runner;

        this.workflow = workflow.getTitle();
        this.startTime = new Date(); // Workflow run start time

        User user = userDao.findByUsername(request().username());
        run = workflowDao.createWorkflowRun(workflow, user, startTime);
    }

    public WorkflowRun getWorkflowRun() {
        return run;
    }

    public void run() {
        endTime = new Date(); // Workflow run end time

        List<ResultFile> resultFiles = new ArrayList<>();
        String dqReport = null;

        try {

            // Process workflow artifacts
            for (WorkflowProduct product : runner.getWorkflowProducts()) {
                String fileName = String.valueOf(product.value);
                File file = new File(fileName);

                if (file.exists()) {
                    if (product.type.equals("DQ_REPORT")) {
                        dqReport = String.valueOf(product.value);
                    }

                    ResultFile resultFile = workflowDao.createResultFile(product.label, String.valueOf(product.value));
                    resultFiles.add(resultFile);
                } else {
                    Logger.error("artifact specified does not exist: " + fileName);
                }
            }

            // TODO: yaml file should be workflow yaml and not web app version
            //File yamlFile = new File(run.workflow.yamlFile);

            String outputText = new String(outStream.toByteArray());
            String errorText = new String(errStream.toByteArray());

            // Create output and error log files
            ResultFile outputFile = createTextFile("output", outputText);
            resultFiles.add(outputFile);
            ResultFile errorFile = createTextFile("error", errorText);
            resultFiles.add(errorFile);

            // Create readme file
            ResultFile readmeFile = createReadmeFile(workflow, startTime, endTime);
            resultFiles.add(readmeFile);

            // Create archive
            File archive = createArchive(resultFiles);

            WorkflowResult workflowResult = workflowDao.createWorkflowResult(resultFiles, archive.getAbsolutePath(),
                    dqReport, outputText, errorText);

            workflowDao.updateRunResult(run, workflowResult, Status.SUCCESS, endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        try {

            ResultNotificationMailer mailer = new ResultNotificationMailer();
            mailer.sendNotification(run.user, run);
        
        } catch (EmailException e) { 
            // TODO: Handle exceptions related to sending the email
            System.out.println("Error sending notification email message: " + e.getMessage());
        }         */
    }

    private ResultFile createTextFile(String prefix, String content) throws IOException {
        // TODO: replace all temp files with files in a designated workspace
        File file = File.createTempFile(prefix + '_', ".txt");

        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        return workflowDao.createResultFile(prefix, file.getAbsolutePath());
    }

    private File createArchive(List<ResultFile> resultFiles) throws IOException {
            // TODO: replace all temp files with files in a designated workspace
            File archive = File.createTempFile("artifacts_", ".zip");
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));

            for (ResultFile resultFile : resultFiles) {
                File file = new File(resultFile.getFileName());
                writeFile(file, out);
            }

            out.close();
            return archive;
    }

    private ResultFile createReadmeFile(String workflow, Date startTime, Date endTime) throws IOException {
        // TODO: replace all temp files with files in a designated workspace
        File readmeFile = File.createTempFile("README_", ".txt");
        FileWriter readme = new FileWriter(readmeFile);

        readme.append("Workflow: " + workflow + "\n");
        readme.append("Start time: " + startTime + "\n");
        readme.append("End time: " + endTime + "\n");

        readme.close();

        return workflowDao.createResultFile("readme", readmeFile.getAbsolutePath());
    }

    private void writeFile(File file, ZipOutputStream out) throws IOException {
        FileInputStream in = new FileInputStream(file);

        out.putNextEntry(new ZipEntry(file.getName()));

        byte[] b = new byte[1024];
        int count;

        while ((count = in.read(b)) > 0) {
            System.out.println();
            out.write(b, 0, count);
        }

        in.close();
    }

    public synchronized void error(String errorText, String outputText) {
        WorkflowResult workflowResult = workflowDao.createWorkflowResult(Collections.emptyList(), null,
                null, outputText, errorText);

        workflowDao.updateRunResult(run, workflowResult, Status.ERROR, new Date());
    }
}
