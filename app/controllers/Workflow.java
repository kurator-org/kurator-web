package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import models.UserUpload;
import models.WorkflowResult;
import models.WorkflowRun;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
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
     * Geo validator workflow page
     */
    @Security.Authenticated(Secured.class)
    public static Result geoworkflow() {
        return ok(
                geovalidatorworkflow.render()
        );
    }

    /**
     * Run workflow
     */
    @Security.Authenticated(Secured.class)
    public static Result runhello() {
            return ok(run("hello_file.yaml", "Hello File", null));
    }

    /**
     * Run worms workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result runworms() {
        try {
            return ok(run("hello_worms.yaml", "Hello Worms", getCurrentUpload()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Run geo validator workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result rungeo() {
        try {
            return ok(run("geo_validator.yaml", "Geo Validator", getCurrentUpload()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectNode run(String yamlFile, String workflowName, File inFile) {
        InputStream yamlStream = null;

        try {
            yamlStream = Play.application().classloader().getResourceAsStream(yamlFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not load workflow from yaml file.", e);
        }

        File outFile = null;

        try {
            outFile = File.createTempFile("result-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Could not create temporary output file.", e);
        }

        WorkflowRun run = new WorkflowRun();
        run.user = getCurrentUser();
        run.workflow = workflowName;
        run.startTime = new Date();
        run.save();

        WorkflowResult result = runYamlWorkflow(yamlStream, inFile, outFile);

        run.result = result;
        run.endTime = new Date();
        run.save();

        ObjectNode response = Json.newObject();

        response.put("output", result.outputText);
        response.put("filename", outFile.getName());
        response.put("runId", run.id);

        return response;
    }

    private static WorkflowResult runYamlWorkflow(InputStream yamlStream, File inFile, File outFile) {
        Map<String, Object> settings = new HashMap<String, Object>();
        settings.put("out", outFile.getAbsolutePath());

        if (inFile != null) {
            settings.put("in", inFile.getAbsolutePath());
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();

        WorkflowResult result = new WorkflowResult();

        try {
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            runner.apply(settings)
                    .outputStream(new PrintStream(outStream))
                    .errorStream(new PrintStream(errStream))
                    .run();

            result.errorText = new String(errStream.toByteArray());
            result.outputText = new String(outStream.toByteArray());
            result.resultFile = outFile.getAbsolutePath();
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            result.errorText = writer.toString();
        }

        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result result(long workflowRunId) {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            File file = new File(run.result.resultFile);
            if (file.exists()) {
                return ok(file);
            }
        }

        return notFound("No result found for workflow run with id " + workflowRunId);
    }

    @Security.Authenticated(Secured.class)
    public static Result error(long workflowRunId) {
        response().setHeader("Content-Disposition", "attachment; filename=error_log.txt");
        response().setContentType("text/plain");

        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            return ok(run.result.errorText);
        } else {
            return notFound("No error log found for workflow run with id " + workflowRunId);
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result output(long workflowRunId) {
        // TODO: add support for output log

        return null;
    }

    @Security.Authenticated(Secured.class)
    public static Result upload() {
        FileOutputStream out = null;

        // TODO: refactor upload to use multipart/form-data encoding for multiple files and to retain filename

        try {
            byte[] data = request().body().asRaw().asBytes();
            File target = File.createTempFile("upload-", ".csv");

            out = new FileOutputStream(target);
            out.write(data);

            UserUpload uploadFile = new UserUpload();
            uploadFile.absolutePath = target.getAbsolutePath();
            uploadFile.user = getCurrentUser();
            uploadFile.save();

            session("uploadFileId", Long.toString(uploadFile.id));
            return ok();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (out != null) out.close(); } catch (IOException e) { }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result file(long uploadFileId) {
        UserUpload uploadFile = UserUpload.find.byId(uploadFileId);
        if (uploadFile == null) {
            return notFound("No file found for id " + uploadFileId);
        }

        if (uploadFile.user.equals(getCurrentUser())) {
            File file = new File(uploadFile.absolutePath);

            if (!file.exists()) {
                throw new RuntimeException(new FileNotFoundException("Could not load input from file."));
            }

            return ok(file);
        } else {
            return unauthorized("The current user is not authorized to access this file!");
        }
    }

    private static User getCurrentUser() {
        String uid = session().get("uid");
        return User.find.byId(Long.parseLong(uid));
    }

    private static File getCurrentUpload() throws FileNotFoundException {
        String uploadFileId = session().get("uploadFileId");
        UserUpload uploadFile = UserUpload.find.byId(Long.parseLong(uploadFileId));
        File file = new File(uploadFile.absolutePath);

        if (!file.exists()) {
            throw new FileNotFoundException("Could not load input from file.");
        }

        return file;
    }
}
