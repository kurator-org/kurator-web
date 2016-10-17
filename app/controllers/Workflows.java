package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import config.ConfigManager;
import config.ParameterConfig;
import forms.FormDefinition;
import forms.input.*;
import models.*;
import org.apache.commons.io.FileUtils;
import org.kurator.akka.WorkflowConfig;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import org.restflow.yaml.spring.YamlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import play.api.mvc.MultipartFormData;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import util.AsyncWorkflowRunnable;

import util.ClasspathStreamHandler;
import util.ConfigurableStreamHandlerFactory;
import views.html.*;
import views.html.admin.deploy;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This controller deals with actions related to running a workflow, uploading files, checking the status of a run, and
 * obtaining artifacts produced by the run.
 *
 */
@Singleton
public class Workflows extends Controller {
    private static final String WORKFLOWS_PATH = "workflows";

    static {
        try {
            URL.setURLStreamHandlerFactory(new ConfigurableStreamHandlerFactory("classpath",
                    new ClasspathStreamHandler()));
        } catch (Error e) {
            // if the stream handler was already set ignore the "factory already defined" error
        }
    }

    /**
     * Start the workflow run asynchronously.
     *
     * @param name The name of the workflow
     * @return json response containing id
     */
    @Security.Authenticated(Secured.class)
    public Result runWorkflow(String name) {
        FormDefinition form = formDefinitionForWorkflow(name);

        // Process file upload first if present in form data
        Http.MultipartFormData body = request().body().asMultipartFormData();

        for (Object obj : body.getFiles()) {
            Http.MultipartFormData.FilePart filePart = (Http.MultipartFormData.FilePart) obj;
            UserUpload userUpload = uploadFile(filePart);

            BasicField fileInputField = form.getField(filePart.getKey());
            fileInputField.setValue(userUpload);
        }

        //  Set the form definition field values from the request data
        Map<String, String[]> data = body.asFormUrlEncoded();
        for (String key : data.keySet()) {
            BasicField field = form.getField(key);
            field.setValue(data.get(key));
        }

        // Transfer form field data to workflow settings map
        Map<String, Object> settings = new HashMap<>();

        for (BasicField field : form.fields) {
            settings.put(field.name, field.value());
        }

            settings.putAll(settingsFromConfig( form));

        // Update the workflow model object and persist to the db
        Workflow workflow = Workflow.find.where().eq("name", form.name).findUnique();

        if (workflow == null) {
            workflow = new Workflow();
        }

        workflow.name = form.name;
        workflow.title = form.title;
        workflow.yamlFile = form.yamlFile;

        workflow.save();

        // Run the workflow
        ObjectNode response = runYamlWorkflow(form.yamlFile, workflow, settings);

        return redirect(
                routes.Application.index()
        );
    }

    /**
     * Helper method for running yaml workflows using an instance of WorkflowRunner.
     *
     * @param yamlFile The workflow yaml file
     * @param workflow Workflow definition object
     * @param settings A map of the settings provided as input to the runner
     * @return json containing the id of this run
     */
    private static ObjectNode runYamlWorkflow(String yamlFile, Workflow workflow, Map<String, Object> settings) {
        InputStream yamlStream = null;
        try {
            yamlStream = loadYamlStream(yamlFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not load workflow from yaml file.", e);
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();

        // This instance of runnable will be executed at the end of a workflow run
        AsyncWorkflowRunnable runnable = new AsyncWorkflowRunnable();

        try {

            // Get jython home and path variables from application.conf and set them in workflow runner global config
            String jythonPath = ConfigFactory.defaultApplication().getString("jython.path");
            String jythonHome = ConfigFactory.defaultApplication().getString("jython.home");

            Map<String, Object> config = new HashMap<String, Object>();
            config.put("jython_home", jythonHome);
            config.put("jython_path", jythonPath);

            // Initialize and run the yaml workflow
            WorkflowRunner runner = new YamlStreamWorkflowRunner()
                    .yamlStream(yamlStream).configure(config);

            runnable.init(workflow, runner, errStream, outStream);

            runner.apply(settings)
                    .outputStream(new PrintStream(outStream))
                    .errorStream(new PrintStream(errStream))
                    .runAsync(runnable);
        } catch (Exception e) {
            e.printStackTrace();

            // Log exceptions as part of the workflow error log
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));

            String errorText = writer.toString();
            String outputText = new String(outStream.toByteArray());

            runnable.error(errorText, outputText);
        }

        // The response json contains the workflow run id for later reference
        ObjectNode response = Json.newObject();

        WorkflowRun run = runnable.getWorkflowRun();
        response.put("runId", run.id);

        return response;
    }

    private static InputStream loadYamlStream(String yamlFile) throws IOException {
        File file = new File(yamlFile);
        return new FileInputStream(file);
    }

    /**
     * Generic workflow input form page.
     *
     * @param name workflow name
     */
    @Security.Authenticated(Secured.class)
    public Result workflow(String name) {
        FormDefinition form = formDefinitionForWorkflow(name);

        if (form != null) {
            return ok(
                    workflow.render(form)
            );
        } else {
            return notFound("No workflow found for name " + name);
        }
    }

    /**
     * Return the result archive containing artifacts produced by the workflow run.
     *
     * @param workflowRunId identifies the workflow run by id
     * @return the zip archive
     */
    @Security.Authenticated(Secured.class)
    public Result resultArchive(long workflowRunId) {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);
        return ok(new File(run.result.archivePath));
    }

    /**
     * Return the workflow run error log as a text file.
     *
     * @param workflowRunId identifies the workflow run by id
     * @return error log as text file
     */
    @Security.Authenticated(Secured.class)
    public Result errorLog(long workflowRunId) {
        response().setHeader("Content-Disposition", "attachment; filename=error_log.txt");
        response().setContentType("text/plain");

        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            return ok(run.result.errorText);
        } else {
            return notFound("No error log found for workflow run with id " + workflowRunId);
        }
    }

    /**
     * Return the workflow output log as a text file.
     *
     * @param workflowRunId identifies the workflow run by id
     * @return output log as text file
     */
    @Security.Authenticated(Secured.class)
    public Result outputLog(long workflowRunId) {
        response().setHeader("Content-Disposition", "attachment; filename=output_log.txt");
        response().setContentType("text/plain");

        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            return ok(run.result.outputText);
        } else {
            return notFound("No output log found for workflow run with id " + workflowRunId);
        }
    }

    /**
     * REST endpoint returns a json array containing metadata about the currently logged in user's uploaded files.
     *
     * @return json containing file upload ids and filenames
     */
    public Result listUploads() {
        List<UserUpload> uploadList = UserUpload.findUploadsByUserId(Application.getCurrentUserId());

        ObjectNode response = Json.newObject();
        ArrayNode uploads = response.putArray("uploads");

        for (UserUpload userUpload : uploadList) {
            ObjectNode upload = Json.newObject();
            upload.put("id", userUpload.id);
            upload.put("filename", userUpload.fileName);
            uploads.add(upload);
        }

        return ok(uploads);
    }

    public Result removeRun(long workflowRunId) {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            run.delete();

            WorkflowResult result = run.result;

            if (run.result != null) {
                List<ResultFile> resultFiles = result.resultFiles;

                for (ResultFile resultFile : resultFiles) {
                    resultFile.delete();
                }

                result.delete();
            }
        }

        return ok();
    }

    /** REST endopint returns a json object with metadata about the status of all of a current users workflow runs.
     *
     * @param uid
     * @return
     */
    public Result status(String uid) {
        List<WorkflowRun> workflowRuns = WorkflowRun.find.where().eq("user.id", uid).findList();

        ArrayNode response = Json.newArray();
        for (WorkflowRun run : workflowRuns) {
            ObjectNode runJson = Json.newObject();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            runJson.put("id", run.id);
            runJson.put("workflow", run.workflow.title);
            runJson.put("startTime", dateFormat.format(run.startTime));
            runJson.put("endTime", run.endTime != null ? dateFormat.format(run.endTime) : null);
            runJson.put("status", run.status);
            runJson.put("hasResult", run.result != null);
            if (run.result != null) {
                runJson.put("hasOutput", !run.result.outputText.equals(""));
                runJson.put("hasErrors", !run.result.errorText.equals(""));
            } else {
                runJson.put("hasOutput", false);
                runJson.put("hasErrors", false);
            }

            response.add(runJson);
        }

        return ok(response);

    }

    /**
     * Private helper method obtains an instance of FormDefinition from the workflow name.
     *
     * @param name workflow name
     * @return the form definition object
     */
    private static FormDefinition formDefinitionForWorkflow(String name) {
        List<FormDefinition> formDefs = loadWorkflowFormDefinitions();
        FormDefinition form = null;

        for (FormDefinition formDef : formDefs) {
            if (formDef.name.equals(name)) {
                form = formDef;
            }
        }

        return form;
    }

    /**
     * Utility method loads all of the workflow form definitions from yaml files contained in the workflow
     * directory.
     *
     * @return
     */
    public static List<FormDefinition> loadWorkflowFormDefinitions() {
//        List<FormDefinition> formDefs = new ArrayList<>();
//
//        URL path = Play.application().classloader().getResource(WORKFLOWS_PATH);
//        try {
//            File dir = new File(path.toURI());
//
//            File[] workflows = dir.listFiles(new FilenameFilter() {
//                public boolean accept(File dir, String name) {
//                    return name.toLowerCase().endsWith(".yaml");
//                }
//            });
//
//            for (File file : workflows) {
//                formDefs.add(loadFormDefinition(file.getAbsolutePath()));
//            }
//        } catch (URISyntaxException e) { /* Should not occur */ }

        List<FormDefinition> formDefs = new ArrayList<>();


        Collection<config.WorkflowConfig> workflows = ConfigManager.getInstance().getWorkflowConfigs();

        for (config.WorkflowConfig workflow : workflows) {
            FormDefinition formDef = new FormDefinition();
            formDef.name = workflow.getName();
            formDef.title = workflow.getTitle();
            formDef.yamlFile = workflow.getYaml();
            formDef.documentation = workflow.getDocumentation();
            formDef.instructions = workflow.getInstructions();
            formDef.summary = workflow.getSummary();

            for (ParameterConfig parameter : workflow.getParameters()) {
                if (parameter.isTyped()) {
                    String type = parameter.getType();

                    switch (type) {
                        case "text":
                            TextField textField = new TextField();
                            textField.name = parameter.getName();
                            textField.label = parameter.getLabel();
                            textField.tooltip = parameter.getDescription();
                            formDef.addField(textField);
                            break;
                        case "select":
                            SelectField selectField = new SelectField();
                            selectField.name = parameter.getName();
                            selectField.label = parameter.getLabel();
                            selectField.options = parameter.getOptions();
                            selectField.tooltip = parameter.getDescription();
                            formDef.addField(selectField);
                            break;
                        case "upload":
                            FileInput fileInput = new FileInput();
                            fileInput.name = parameter.getName();
                            fileInput.label = parameter.getLabel();
                            fileInput.tooltip = parameter.getDescription();
                            formDef.addField(fileInput);
                            break;
                    }
                }
            }

            formDefs.add(formDef);
        }
        return formDefs;
    }

    /**
     * Private helper method loads data from the yaml file specified and creates an instance of FormDefinition
     *
     * @param yamlFile
     * @return
     */
//    private static FormDefinition loadFormDefinition(String yamlFile) {
//        try {
//            GenericApplicationContext springContext = new GenericApplicationContext();
//            YamlBeanDefinitionReader yamlBeanReader = new YamlBeanDefinitionReader(springContext);
//            yamlBeanReader.loadBeanDefinitions(new FileInputStream(yamlFile), "-");
//            springContext.refresh();
//
//            FormDefinition formDefinition = springContext.getBean(FormDefinition.class);
//            formDefinition.yamlFile = yamlFile;
//
//            return formDefinition;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Helper method creates a temp file from the multipart form data and persists the upload file metadata to the
     * database
     *
     * @param filePart data from the form submission
     * @return an instance of UploadFile that has been persisted to the db
     */
    private static UserUpload uploadFile(Http.MultipartFormData.FilePart filePart) {
        File src = (File) filePart.getFile();
        File file = null;
        try {
            file = File.createTempFile(filePart.getFilename() + "-", ".csv");
            FileUtils.copyFile(src, file);
        } catch (IOException e) {
            throw new RuntimeException("Could not create temp file for upload", e);
        }

        UserUpload uploadFile = new UserUpload();
        uploadFile.absolutePath = file.getAbsolutePath();
        uploadFile.fileName = filePart.getFilename();
        uploadFile.user = Application.getCurrentUser();
        uploadFile.save();

        return uploadFile;
    }

    /**
     * Obtains the currently uploaded file from the session variable.
     *
     * @return uploaded file
     * @throws FileNotFoundException
     */
    private static File getCurrentUpload() throws FileNotFoundException {
        String uploadFileId = session().get("uploadFileId");
        UserUpload uploadFile = UserUpload.find.byId(Long.parseLong(uploadFileId));
        File file = new File(uploadFile.absolutePath);

        if (!file.exists()) {
            throw new FileNotFoundException("Could not load input from file.");
        }

        return file;
    }

    /**
     * Will load additional settings from the web app config. Creates a map object that contains
     * workflow parameters to be provided as input to the runner
     *
     * @param form form definition of the workflow being run
     * @return a map of the settings
     */
    private static Map<String, String> settingsFromConfig(FormDefinition form) {
        try {
            Map<String, String> settings = new HashMap<String, String>();

            // Get the workspace basedir from application.conf
            String workspace = ConfigFactory.defaultApplication().getString("jython.workspace");

            // Load workflow yaml file to check parameters
            GenericApplicationContext springContext = new GenericApplicationContext();
            YamlBeanDefinitionReader yamlBeanReader = new YamlBeanDefinitionReader(springContext);
            yamlBeanReader.loadBeanDefinitions(loadYamlStream(form.yamlFile), "-");
            springContext.refresh();

            WorkflowConfig workflowConfig = springContext.getBean(WorkflowConfig.class);

            // Create a workspace
            Path path = Paths.get(workspace, "workspace_" + UUID.randomUUID());
            path.toFile().mkdir();

            // If the "workspace" parameter is present in the workflow set it to the path defined in the config
            if (workflowConfig.getParameters().containsKey("workspace")) {
                settings.put("workspace", path.toString());
            }

            return settings;
        } catch (IOException e) {
            throw new RuntimeException("Error creating workspace directory.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error reading yaml file for parameters.", e);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result uploadWorkflow() {
        return ok(
                deploy.render()
        );
    }

    @Security.Authenticated(Secured.class)
    public Result deploy() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("filename");

        if (filePart != null) {
           ConfigManager configManager = ConfigManager.getInstance();
            try {
                configManager.unpack((File) filePart.getFile());
            } catch (Exception e) {
                flash("error", "Could not verify package.");
                return redirect(routes.Workflows.uploadWorkflow());
            }

            flash("message", "Packages zip file was successfully deployed");
            return redirect(routes.Workflows.uploadWorkflow());
        } else {
            flash("error", "Missing file");
            return badRequest();
        }
    }
}
