package util;

import config.Artifact;
import dao.WorkflowDao;
import models.db.user.User;
import models.db.workflow.*;
import models.json.ArtifactDef;
import models.json.WorkflowDefinition;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.data.WorkflowProduct;
import play.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by lowery on 5/10/16.
 */
public class AsyncWorkflowRunnable implements Runnable {
    private static final WorkflowDao workflowDao = new WorkflowDao();

    private WorkflowRunner runner;
    private WorkflowRun run;

    private String workflow;
    private WorkflowDefinition workflowDef;
    private Map<String, String> artifactDefs;
    private String yamlFile;

    private Date startTime;
    private Date endTime;

    private ByteArrayOutputStream errStream;
    private ByteArrayOutputStream outStream;

    private List<ResultFile> resultFiles = new ArrayList<>();
    private String dqReportFile;

    public synchronized void init(String name, Workflow workflow, WorkflowDefinition workflowDef, Map<String, String> artifactDefs, User user, WorkflowRunner runner, ByteArrayOutputStream errStream, ByteArrayOutputStream outStream) {
        this.errStream = errStream;
        this.outStream = outStream;
        this.runner = runner;

        this.workflow = workflow.getTitle();
        this.workflowDef = workflowDef;
        this.artifactDefs = artifactDefs;
        this.yamlFile = workflow.getYamlFile();
        this.startTime = new Date(); // Workflow run start time

        run = workflowDao.createWorkflowRun(name, workflow, user, startTime);
    }

    public long getRunId() {
        return run.getId();
    }

    public void run() {
        System.out.println("Runnable");
        System.out.println(runner.getWorkflowProducts().size());
        System.out.println("Got products");
        endTime = new Date(); // Workflow run end time

        try {
            // Process workflow artifacts
            for (WorkflowProduct product : runner.getWorkflowProducts()) {
                String fileName = String.valueOf(product.value);
                File file = new File(fileName);
                if (file.exists()) {
                    // if one of the products is a dq report save it for later
                    System.out.println(product.type);
                    if (product.type.equals("DQ_REPORT")) {
                        dqReportFile = file.getAbsolutePath();
                    }

                    // Include descriptive text
                    String description = artifactDefs.get(product.label);

                    // Create a result file from the workflow product and persist it to the db
                    ResultFile resultFile = workflowDao.createResultFile(product.label, description, String.valueOf(product.value));
                    resultFiles.add(resultFile);

                } else {
                    Logger.error("artifact specified does not exist: " + fileName);
                }
            }

            // TODO: yaml file should be workflow yaml and not web app version
            //File yamlFile = new File(yamlFile);

            String outputText = new String(outStream.toByteArray());
            String errorText = new String(errStream.toByteArray());

            // Create output error log file
            resultFiles.add(createTextFile("output", outputText));

            // Create readme file
            resultFiles.add(createReadmeFile(workflow, startTime, endTime));

            // Package result files in archive
            File archive = createArchive(resultFiles);
            System.out.println("created archive");

            // Persist the result to the db and update the workflow run
            WorkflowResult workflowResult = workflowDao.createWorkflowResult(resultFiles, archive.getAbsolutePath(),
                    dqReportFile, outputText, errorText);

            // Default status is success unless errors are present in the error log
            Status status = Status.SUCCESS;

            if (!errorText.isEmpty()) {
                status = Status.ERRORS;
                resultFiles.add(createTextFile("error", errorText));
            }

            workflowDao.updateRunResult(run, workflowResult, status, endTime);
        } catch (Exception e) {
            throw new RuntimeException("Failure during processing of workflow result", e);
        }
    }

    private ResultFile createTextFile(String prefix, String content) throws IOException {
        File file = File.createTempFile(prefix + '_', ".txt");

        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();

        return workflowDao.createResultFile(prefix, null, file.getAbsolutePath());
    }

    private ResultFile createReadmeFile(String workflow, Date startTime, Date endTime) throws IOException {
        File readmeFile = File.createTempFile("README_", ".txt");
        FileWriter readme = new FileWriter(readmeFile);

        readme.append("Workflow: " + workflow + "\n");
        readme.append("Start time: " + startTime + "\n");
        readme.append("End time: " + endTime + "\n");

        readme.close();

        return workflowDao.createResultFile("readme", null, readmeFile.getAbsolutePath());
    }

    private File createArchive(List<ResultFile> resultFiles) throws IOException {
        System.out.println("about to create archive");
            File archive = File.createTempFile("artifacts_", ".zip");
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));

            for (ResultFile resultFile : resultFiles) {
                File file = new File(resultFile.getFileName());
                writeFile(file, out);
            }

            out.close();
            return archive;
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
}
