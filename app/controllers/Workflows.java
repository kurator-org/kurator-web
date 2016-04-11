package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.FormDefinition;
import forms.input.*;
import models.UserUpload;
import models.Workflow;
import models.WorkflowResult;
import models.WorkflowRun;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import org.kurator.akka.data.DQReport.DQReport;
import org.kurator.akka.data.DQReport.Improvement;
import org.kurator.akka.data.DQReport.Measure;
import org.kurator.akka.data.DQReport.Validation;
import org.restflow.yaml.spring.YamlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import play.Play;
import play.api.data.validation.Valid;
import play.libs.Json;
import play.mvc.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import postproccess.PostProcessor;
import postproccess.ReportSummary;
import postproccess.ffdq.RecordRowPostProcessor;
import util.ResultNotificationMailer;
import views.html.*;

/**
 * Created by lowery on 2/4/16.
    /**
     */
    public class Workflows extends Controller {
        List<FormDefinition> workflows = new ArrayList<>();

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
            UserUpload userUpload = uploadFile(filePart);

            BasicField fileInputField = form.getField(filePart.getKey());
            fileInputField.setValue(userUpload);
        }

        Map<String, String[]> data = body.asFormUrlEncoded();
        for (String key : data.keySet()) {
            BasicField field = form.getField(key);
            field.setValue(data.get(key));
        }

        Map<String, Object> settings = new HashMap<>();
        for (BasicField field : form.fields) {
            settings.put(field.name, field.value());
        }

        Workflow workflow = Workflow.find.where().eq("name", form.name).findUnique();

        if (workflow == null) {
            workflow = new Workflow();
            workflow.name = form.name;
            workflow.title = form.title;
            workflow.outputFormat = form.outputFormat;

            workflow.save();
        }

        ObjectNode response = run(form.yamlFile, workflow, settings);

        return ok(
                response
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

    private static File getUploadFileById(Long uploadId) throws FileNotFoundException {
        UserUpload uploadFile = UserUpload.find.byId(uploadId);
        File file = new File(uploadFile.absolutePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Could not load input from file.");
        }

        return file;
    }

    private static ObjectNode run(String yamlFile, Workflow workflow, Map<String, Object> settings) {
        InputStream yamlStream = null;

        try {
            yamlStream = new FileInputStream(new File(yamlFile));
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
        run.workflow = workflow;
        run.startTime = new Date();
        run.save();

        runYamlWorkflow(yamlStream, outFile, settings, run);

        try {
            ResultNotificationMailer mailer = new ResultNotificationMailer();
            mailer.sendNotification(run.user, run);
        } catch (Exception e) {
            // TODO: Handle exceptions related to sending the email
            e.printStackTrace();
        }

        ObjectNode response = Json.newObject();

        response.put("filename", outFile.getName());
        response.put("runId", run.id);

        return response;
    }

    private static void runYamlWorkflow(InputStream yamlStream, File outFile, Map<String, Object> settings, WorkflowRun run) {
        settings.put("out", outFile.getAbsolutePath());

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();

        WorkflowResult result = new WorkflowResult();

        try {
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            runner.apply(settings)
                    .outputStream(new PrintStream(outStream))
                    .errorStream(new PrintStream(errStream))
                    .runAsync(new Runnable() {
                        @Override
                        public void run() {
                            result.errorText = new String(errStream.toByteArray());
                            result.outputText = new String(outStream.toByteArray());
                            result.resultFile = outFile.getAbsolutePath();

                            run.result = result;
                            run.endTime = new Date();
                            run.save();
                        }
                    });
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            result.errorText = writer.toString();
            result.outputText = new String(outStream.toByteArray());

            run.result = result;
            run.endTime = new Date();
            run.save();
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result result(long workflowRunId, String format) {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        try {
            if (run != null) {
                File file = new File(run.result.resultFile);
                FileInputStream in = new FileInputStream(file);

                if (run.workflow.outputFormat.equals("ffdq")) {
                    if (format == null) {
                        format = "xls";
                    }

                    if (format.equals("xls") || format.equals("json")) {
                        response().setHeader("Content-Disposition", "attachment; filename=result." + format);
                    } else {
                        throw new IllegalArgumentException("Unsupported file format: " + format);
                    }

                    PostProcessor postProcessor = new RecordRowPostProcessor();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    postProcessor.postprocess(in, format, out);

                    return ok(out.toByteArray());
                } else {
                    response().setHeader("Content-Disposition", "attachment; filename=" + run.result.getResultFileName());

                    if (file.exists()) {
                        return ok(file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return notFound("No result found for workflow run with id " + workflowRunId);
    }

    @Security.Authenticated(Secured.class)
    public static Result summary(long workflowRunId) {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        try {
            if (run != null) {
                File file = new File(run.result.resultFile);
                FileInputStream in = new FileInputStream(file);

                ReportSummary reportSummary = new ReportSummary(in);
                System.out.println("Compliant");
                for (String criterion : reportSummary.getCompliantCriterion()) {
                    System.out.println("    " + criterion + ": " + reportSummary.getCompliantCount(criterion));
                }

                System.out.println("\nNon Compliant");
                for (String criterion : reportSummary.getNonCompliantCriterion()) {
                    System.out.println("    " + criterion + ": " + reportSummary.getNonCompliantCount(criterion));
                }

                System.out.println("\nImprovements");
                for (String enhancement : reportSummary.getEnhancements()) {
                    System.out.println("    " + enhancement + ": " + reportSummary.getImprovementsCount(enhancement));
                }

                ArrayNode data = Json.newArray();

                int compliantCount = reportSummary.getCompliantCount("Coordinates must fall inside the country");
                ObjectNode compliant = Json.newObject();

                compliant.put("value", compliantCount);
                compliant.put("color", "#66cdaa");
                compliant.put("highlight", "#a3e1cc");
                compliant.put("label", "Compliant");

                data.add(compliant);

                int improvementsCount = reportSummary.getImprovementsCount(
                        "Recommendation to transform decimal latitude and or decimal longitude");
                ObjectNode improvements = Json.newObject();

                improvements.put("value", improvementsCount);
                improvements.put("color", "#6897bb");
                improvements.put("highlight", "#a4c0d6");
                improvements.put("label", "Improvements");

                data.add(improvements);


                int nonCompliantCount = reportSummary.getNonCompliantAfterImprovementsCount(
                        "Coordinates must fall inside the country",
                        "Recommendation to transform decimal latitude and or decimal longitude");
                ObjectNode nonCompliant = Json.newObject();

                nonCompliant.put("value", nonCompliantCount);
                nonCompliant.put("color", "#fa8072");
                nonCompliant.put("highlight", "#fcb2aa");
                nonCompliant.put("label", "Non Compliant");

                data.add(nonCompliant);
                String json = data.toString();
                return ok(summary.render(json));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return badRequest();
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

    public static UserUpload uploadFile(Http.MultipartFormData.FilePart filePart) {
        File src = filePart.getFile();
        File file = null;
        try {
            file = File.createTempFile(filePart.getFilename() + "-", ".csv");
            FileUtils.copyFile(src, file);
        } catch (IOException e) {
           e.printStackTrace();
        }

        UserUpload uploadFile = new UserUpload();
        uploadFile.absolutePath = file.getAbsolutePath();
        uploadFile.fileName = filePart.getFilename();
        uploadFile.user = Application.getCurrentUser();
        uploadFile.save();

        return uploadFile;
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

    public static List<FormDefinition> loadWorkflowFormDefinitions() {
        List<FormDefinition> formDefs = new ArrayList<>();

        URL path = Play.application().classloader().getResource("workflows");
        try {
            File dir = new File(path.toURI());

            File[] workflows = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".yaml");
                }
            });

            for (File file : workflows) {
                formDefs.add(loadFormDefinition(file.getAbsolutePath()));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return formDefs;
    }

    private static FormDefinition loadFormDefinition(String yamlFile) {
        try {
            GenericApplicationContext springContext = new GenericApplicationContext();
            YamlBeanDefinitionReader yamlBeanReader = new YamlBeanDefinitionReader(springContext);
            yamlBeanReader.loadBeanDefinitions(new FileInputStream(yamlFile), "-");
            springContext.refresh();

            FormDefinition formDefinition = springContext.getBean(FormDefinition.class);
            formDefinition.yamlFile = yamlFile;

            return formDefinition;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
