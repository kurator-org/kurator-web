package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import models.WorkflowRun;
import org.apache.commons.io.FileUtils;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import views.html.*;

/**
 * Created by lowery on 2/4/16.
 */
public class Workflow extends Controller {
    /**
     * Workflow page
     */
    @Security.Authenticated(Secured.class)
    public static Result helloworkflow() {
        return ok(
                helloworkflow.render()
        );
    }

    /**
     * Worms workflow page
     */
    @Security.Authenticated(Secured.class)
    public static Result wormsworkflow() {
        return ok(
                wormsworkflow.render()
        );
    }

    /**
     * Run workflow
     */
    @Security.Authenticated(Secured.class)
    public static Result runhello() {
        WorkflowRun run = new WorkflowRun();
        run.user = User.find.byId(Long.parseLong(session().get("uid")));
        run.startTime = new Date();
        run.save();

        Map<String, Object> settings = new HashMap<String, Object>();

        try {
            File file = File.createTempFile("result-", ".csv");
            settings.put("out", file.getAbsolutePath());

            String output = runYamlWorkflow("hello_file.yaml", settings);

            ObjectNode result = Json.newObject();

            result.put("output", output);
            result.put("filename", file.getName());

            run.endTime = new Date();
            run.outputText = output;
            run.resultFile = file.getAbsolutePath();
            run.update();

            return ok(result);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    private static String runYamlWorkflow(String yamlFile, Map<String, Object> settings) throws Exception {
        InputStream yamlStream = Play.application().classloader().getResourceAsStream(yamlFile);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(buffer);
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.err)
                    .run();

            return new String(buffer.toByteArray());
    }



    @Security.Authenticated(Secured.class)
    public static Result result(String fileName) {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        return ok(file);
    }

    @Security.Authenticated(Secured.class)
    public static Result upload() {
        try {
            File source = request().body().asRaw().asFile();
            File target = File.createTempFile("upload-", ".csv");

            FileUtils.copyFile(source, target);

            session("input", target.getAbsolutePath());

            return ok();
        } catch (IOException e) {
            return internalServerError(e.getMessage());
        }
    }

    /**
     * Run worms workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result runworms() {
        Map<String, Object> settings = new HashMap<String, Object>();

        try {
            File inFile = new File(session().get("input"));
            File outFile = File.createTempFile("worms_result-", ".csv");

            settings.put("in", inFile.getAbsolutePath());
            settings.put("out", outFile.getAbsolutePath());

            String output = runYamlWorkflow("hello_worms.yaml", settings);

            ObjectNode result = Json.newObject();

            result.put("output", output);
            result.put("filename", outFile.getName());

            return ok(result);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
