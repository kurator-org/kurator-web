package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.*;
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
    public static Result workflow() {
        return ok(
                workflow.render()
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
    public static Result run() {
        InputStream yamlStream = Play.application().classloader().getResourceAsStream("hello_file.yaml");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(buffer);
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            File file = File.createTempFile("result-", ".csv");

            Map<String, Object> settings = new HashMap<String,Object>();
            settings.put("out", file.getAbsolutePath());

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();

            ObjectNode result = Json.newObject();

            result.put("output", new String(buffer.toByteArray()));
            result.put("filename", file.getName());

            return ok(result);

        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result result(String fileName) {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        return ok(file);
    }

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
        InputStream yamlStream = Play.application().classloader().getResourceAsStream("hello_worms.yaml");

        try {
            File inFile = new File(session().get("input"));

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(buffer);
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            File outFile = File.createTempFile("worms_result-", ".csv");

            Map<String, Object> settings = new HashMap<String,Object>();
            settings.put("in", inFile.getAbsolutePath());
            settings.put("out", outFile.getAbsolutePath());

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();

            ObjectNode result = Json.newObject();

            result.put("output", new String(buffer.toByteArray()));
            result.put("filename", outFile.getName());

            return ok(result);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
