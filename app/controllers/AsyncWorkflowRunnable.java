package controllers;

import akka.actor.ActorRef;
import org.apache.commons.mail.EmailException;

import models.ResultFile;
import models.Workflow;
import models.WorkflowResult;
import models.WorkflowRun;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.actors.StringAppender;
import org.kurator.akka.actors.StringFileWriter;
import org.kurator.akka.data.WorkflowProduct;
import util.ResultNotificationMailer;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by lowery on 5/10/16.
 */
public class AsyncWorkflowRunnable implements Runnable {
    private WorkflowRunner runner;
    private WorkflowRun run;
    private ByteArrayOutputStream errStream;
    private ByteArrayOutputStream outStream;

    public synchronized void init(Workflow workflow, WorkflowRunner runner, ByteArrayOutputStream errStream, ByteArrayOutputStream outStream) {
        this.errStream = errStream;
        this.outStream = outStream;
        this.runner = runner;

        run = new WorkflowRun();
        run.user = Application.getCurrentUser();
        run.workflow = workflow;
        run.startTime = new Date();
        run.save();
    }

    public WorkflowRun getWorkflowRun() {
        return run;
    }

    @Override
    public void run() {
        WorkflowResult result = new WorkflowResult();

        try {
            for (WorkflowProduct product : runner.getWorkflowProducts()) {
                System.out.println(product);
                ResultFile file = new ResultFile();
                file.fileName = String.valueOf(product.value);
                file.label = product.label;

                result.resultFiles.add(file);
            }

            File archive = File.createTempFile("artifacts_", ".zip");

            result.archivePath = archive.getAbsolutePath();

            result.errorText = new String(errStream.toByteArray());
            result.outputText = new String(outStream.toByteArray());
            result.save();

            run.result = result;
            run.endTime = new Date();
            run.save();

            createArchive(archive, run);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ResultNotificationMailer mailer = new ResultNotificationMailer();
            mailer.sendNotification(run.user, run);
        
        } catch (EmailException e) { 
            // TODO: Handle exceptions related to sending the email
            System.out.println("Error sending notification email message: " + e.getMessage());
        }
    }

    private void createArchive(File archive, WorkflowRun run) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
            List<ResultFile> resultFiles = run.result.resultFiles;

            for (ResultFile resultFile : resultFiles) {
                File file = new File(resultFile.fileName);
                writeFile(file, out);
            }

            File readmeFile = File.createTempFile("README_", ".txt");
            File outputFile = File.createTempFile("output_log_", ".txt");
            File errorFile = File.createTempFile("error_log_", ".txt");

            File yamlFile = new File(run.workflow.yamlFile);

            FileWriter readme = new FileWriter(readmeFile);

            readme.append("Workflow: " + run.workflow.title + "\n");
            readme.append("Start time: " + run.startTime + "\n");
            readme.append("End time: " + run.endTime + "\n");

            readme.close();

            writeFile(readmeFile, out);

            FileWriter output = new FileWriter(outputFile);
            FileWriter error = new FileWriter(errorFile);

            output.write(run.result.outputText);
            error.write(run.result.errorText);

            output.close();
            error.close();

            writeFile(outputFile, out);
            writeFile(errorFile, out);
            writeFile(yamlFile, out);

            out.close();

            System.out.println(archive.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        WorkflowResult result = new WorkflowResult();

        result.errorText = errorText;
        result.outputText = outputText;

        run.result = result;
        run.endTime = new Date();
        run.save();
    }
}
