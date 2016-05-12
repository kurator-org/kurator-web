package controllers;

import akka.actor.ActorRef;
import models.ResultFile;
import models.Workflow;
import models.WorkflowResult;
import models.WorkflowRun;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.actors.StringAppender;
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

            if (result.resultFiles.size() > 1) {
                result.archivePath = createArchive(result.resultFiles);
            }

            result.errorText = new String(errStream.toByteArray());
            result.outputText = new String(outStream.toByteArray());
            result.save();

            run.result = result;
            run.endTime = new Date();
            run.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ResultNotificationMailer mailer = new ResultNotificationMailer();
            mailer.sendNotification(run.user, run);
        } catch (Exception e) {
            // TODO: Handle exceptions related to sending the email
            e.printStackTrace();
        }
    }

    private String createArchive(List<ResultFile> resultFiles) {
        try {
            File archive = File.createTempFile("artifacts", ".zip");
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));

            for (ResultFile resultFile : resultFiles) {
                File file = new File(resultFile.fileName);
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

            out.close();

            System.out.println(archive.getAbsolutePath());
            return archive.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
