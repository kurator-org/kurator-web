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

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

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

    public synchronized void error(String errorText, String outputText) {
        WorkflowResult result = new WorkflowResult();

        result.errorText = errorText;
        result.outputText = outputText;

        run.result = result;
        run.endTime = new Date();
        run.save();
    }
}
