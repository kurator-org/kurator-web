package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.FormDefinition;
import forms.input.*;
import models.User;
import models.UserUpload;
import models.WorkflowResult;
import models.WorkflowRun;
import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.EmailException;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import scala.App;
import util.ResultNotificationMailer;
import util.YamlFormDefinitionParser;
import views.html.*;

/**
 * Created by lowery on 2/4/16.
    /**
     */
    public class Workflow extends Controller {
        List<FormDefinition> workflows = new ArrayList<>();

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
        List<UserUpload> uploads = UserUpload.findUploadsByUserId(Application.getCurrentUserId());

        return ok(
                wormsworkflow.render(uploads)
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result test(String name) {
        List<FormDefinition> formDefs = loadWorkflowFormDefinitions();
        FormDefinition form = null;

        for (FormDefinition formDef : formDefs) {
            if (formDef.name.equals(name)) {
                form = formDef;
            }
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();

        for (Http.MultipartFormData.FilePart filePart : body.getFiles()) {
            BasicField fileInputField = form.getField(filePart.getKey());
            fileInputField.setValue(filePart);
        }

        Map<String, String[]> data = body.asFormUrlEncoded();
        for (String key : data.keySet()) {
            BasicField field = form.getField(key);
            field.setValue(data.get(key));
        }

        Map<String, Object> settings = new HashMap<>();
        for (BasicField field : form.fields.values()) {
            settings.put(field.name, field.getValue());
        }

        return ok(
                run("workflows/geo_validator.yaml", "Geo Validator", settings)
        );
    }

    /**
     * Geo validator workflow page
     */
    @Security.Authenticated(Secured.class)
    public static Result geoworkflow() {
        List<UserUpload> uploads = UserUpload.findUploadsByUserId(Application.getCurrentUserId());

        return ok(
                geovalidatorworkflow.render(uploads)
        );
    }

    /**
     * Workflow page
     */
    @Security.Authenticated(Secured.class)
    public static Result workflow(String name) {

        List<FormDefinition> workflows = loadWorkflowFormDefinitions();
        for (FormDefinition form : workflows) {
            if (form.name.equals(name)) {
                return ok(
                        workflow.render(form)
                );
            }
        }

        return notFound("No workflow found for name " + name);
    }

    /**
     * Run workflow
     */
    @Security.Authenticated(Secured.class)
    public static Result runhello() {
            return ok(run("hello_file.yaml", "Hello File", new HashMap<>()));
    }

    /**
     * Run worms workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result runworms(Long uploadId) {
        System.out.println(uploadId);
        Map<String, Object> settings = new HashMap<>();

        try {
            File inFile = getUploadFileById(uploadId);
            settings.put("in", inFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }

        return ok(run("hello_worms.yaml", "Hello Worms", settings));
    }

    private static File getUploadFileById(Long uploadId) throws FileNotFoundException {
        UserUpload uploadFile = UserUpload.find.byId(uploadId);
        File file = new File(uploadFile.absolutePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Could not load input from file.");
        }

        return file;
    }

    /**
     * Run geo validator workflow.
     */
    @Security.Authenticated(Secured.class)
    public static Result rungeo(Long uploadId) {
        Map<String, Object> settings = new HashMap<>();

        try {
            File inFile = getUploadFileById(uploadId);
            settings.put("in", inFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }

        return ok(run("geo_validator.yaml", "Geo Validator", settings));
    }

    private static ObjectNode run(String yamlFile, String workflowName, Map<String, Object> settings) {
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
        run.user = Application.getCurrentUser();
        run.workflow = workflowName;
        run.startTime = new Date();
        run.save();

        WorkflowResult result = runYamlWorkflow(yamlStream, outFile, settings);

        run.result = result;
        run.endTime = new Date();
        run.save();

        try {
            ResultNotificationMailer mailer = new ResultNotificationMailer();
            mailer.sendNotification(run.user, run);
        } catch (Exception e) {
            // TODO: Handle exceptions related to sending the email
            e.printStackTrace();
        }

        ObjectNode response = Json.newObject();

        response.put("output", result.outputText);
        response.put("filename", outFile.getName());
        response.put("runId", run.id);

        return response;
    }

    private static WorkflowResult runYamlWorkflow(InputStream yamlStream, File outFile, Map<String, Object> settings) {
        settings.put("out", outFile.getAbsolutePath());

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
            return result;
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            result.errorText = writer.toString();
            result.outputText = new String(outStream.toByteArray());
            return result;
        }
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
        response().setHeader("Content-Disposition", "attachment; filename=output_log.txt");
        response().setContentType("text/plain");

        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            return ok(run.result.outputText);
        } else {
            return notFound("No output log found for workflow run with id " + workflowRunId);
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("input");

        File src = filePart.getFile();
        File file = null;
        try {
            file = File.createTempFile(filePart.getFilename() + "-", ".csv");
            FileUtils.copyFile(src, file);
        } catch (IOException e) {
            return internalServerError("Could not create upload file");
        }

        UserUpload uploadFile = new UserUpload();
        uploadFile.absolutePath = file.getAbsolutePath();
        uploadFile.fileName = filePart.getFilename();
        uploadFile.user = Application.getCurrentUser();
        uploadFile.save();

        session("uploadFileId", Long.toString(uploadFile.id));

        ObjectNode response = Json.newObject();
        response.put("uploadId", uploadFile.id);
        return ok(response);
    }

    @Security.Authenticated(Secured.class)
    public static Result file(long uploadFileId) {
        UserUpload uploadFile = UserUpload.find.byId(uploadFileId);
        if (uploadFile == null) {
            return notFound("No file found for id " + uploadFileId);
        }

        if (uploadFile.user.equals(Application.getCurrentUser())) {
            File file = new File(uploadFile.absolutePath);

            if (!file.exists()) {
                throw new RuntimeException(new FileNotFoundException("Could not load input from file."));
            }

            response().setHeader("Content-Disposition", "attachment; filename=" + uploadFile.fileName);

            return ok(file);
        } else {
            return unauthorized("The current user is not authorized to access this file!");
        }
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

    public static FormDefinition getWormsFormDefinition() {
        FormDefinition form = new FormDefinition();

        FileInput fileParam = new FileInput("in", "Upload file", false);

        form.addField(fileParam);

        return form;
    }

    public static FormDefinition getGeoValidatorFormDefinition() {
        FormDefinition form = new FormDefinition();

        FileInput fileParam = new FileInput("in", "Upload file", false);

        form.addField(fileParam);

        return form;
    }

    public static FormDefinition getTestFormDefinition() {
        FormDefinition form = new FormDefinition();

        FileInput fileParam = new FileInput("in", "Upload file", false);
        TextField textParam = new TextField("name", "Name", "", false);
        TextField textAreaParam = new TextField("description", "Description", "", true);

        List<Option> radioOptions = new ArrayList<>();
        Option defaultOption = new Option("csv", "csv", "CSV");
        radioOptions.add(defaultOption);
        radioOptions.add(new Option("json", "json", "JSON"));

        RadioGroup radioParam = new RadioGroup("type", "Type", radioOptions);
        radioParam.value = defaultOption;

        CheckBox checkBoxParam1 = new CheckBox("hasTaxonFields", "Taxon fields");
        CheckBox checkBoxParam2 = new CheckBox("hasGeoreferenceFields", "Georeference Fields");

        List<Option> selectOptions = new ArrayList<>();
        selectOptions.add(new Option("csvOutput", "csvOutput", "CSV"));
        selectOptions.add(new Option("jsonOutput", "jsonOutput", "JSON"));

        SelectField selectParam = new SelectField("output", "Output", selectOptions, false);

        form.addField(fileParam);
        form.addField(textParam);
        form.addField(textAreaParam);
        form.addField(radioParam);
        form.addField(checkBoxParam1);
        form.addField(checkBoxParam2);
        form.addField(selectParam);

        return form;
    }

    public static List<FormDefinition> loadWorkflowFormDefinitions() {
        List<FormDefinition> formDefs = new ArrayList<>();
        YamlFormDefinitionParser parser = new YamlFormDefinitionParser();

        URL path = Play.application().classloader().getResource("workflows");
        try {
            File dir = new File(path.toURI());

            File[] workflows = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".yaml");
                }
            });

            for (File file : workflows) {
                InputStream yamlStream = new FileInputStream(file);
                FormDefinition formDef = parser.parse(yamlStream);

                formDef.name = file.getName().split("\\.")[0];
                formDefs.add(formDef);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return formDefs;
    }
}
