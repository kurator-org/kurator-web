package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import config.ConfigManager;
import config.ParameterConfig;
import dao.UserDao;
import dao.WorkflowDao;
import models.db.user.UserUpload;
import org.apache.commons.io.FileUtils;
import ui.input.BasicField;
import ui.input.FileInput;
import ui.input.SelectField;
import ui.input.TextField;
import models.PackageData;
import models.db.user.User;
import models.db.workflow.Workflow;
import models.db.workflow.WorkflowResult;
import models.db.workflow.WorkflowRun;
import models.json.RunResult;
import models.json.WorkflowDefinition;
import org.datakurator.postprocess.FFDQPostProcessor;
import org.kurator.akka.WorkflowConfig;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import org.restflow.yaml.spring.YamlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.AsyncWorkflowRunnable;
import util.ClasspathStreamHandler;
import util.ConfigurableStreamHandlerFactory;
import util.WorkflowPackageVerifier;

import javax.inject.Singleton;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import views.html.*;
import views.html.admin.*;

/**
 * This controller deals with actions related to running a workflow, uploading files, checking the status of a run, and
 * obtaining artifacts produced by the run.
 *
 */
@Singleton
public class Workflows extends Controller {
    // Get jython home and path directories relative to the current directory
    private static final String JYTHON_HOME = new File("jython").getAbsolutePath();
    private static final String JYTHON_PATH = new File("packages").getAbsolutePath();

    // Get the workspace basedir relative to the current directory
    private static final String WORKSPACE_DIR = new File("workspace").getAbsolutePath();

    private final WorkflowDao workflowDao = new WorkflowDao();
    private final UserDao userDao = new UserDao();

    // For handling classpath url scheme in workflows.conf files
    static {
        try {
            URL.setURLStreamHandlerFactory(new ConfigurableStreamHandlerFactory("classpath",
                    new ClasspathStreamHandler()));
        } catch (Error e) {
            // if the stream handler was already set ignore the "factory already defined" error
        }
    }

    public Result list() {
        List<WorkflowDefinition> workflowDefs = loadWorkflowFormDefinitions();
        Collections.sort(workflowDefs);

        return ok(
                Json.toJson(workflowDefs)
        );
    }

    @Security.Authenticated(Secured.class)
    public Result deletePackage(String name) {
        boolean success = ConfigManager.getInstance().deletePacakge(name);

        if (success) {
            flash("message", "Successfully deleted package " + name);
        } else {
            flash("error", "Unable to delete package " + name);
        }
        return redirect(routes.Workflows.deploy());
    }

    /**
     * Start the workflow run asynchronously.
     *
     * @param name The name of the workflow
     * @return json response containing id
     */
    @Security.Authenticated(Secured.class)
    public Result runWorkflow(String name) {
        WorkflowDefinition workflowDef = formDefinitionForWorkflow(name);

        // Process form submission as multipart form data
        Http.MultipartFormData body = request().body().asMultipartFormData();

        for (Object obj : body.getFiles()) {
            Http.MultipartFormData.FilePart filePart = (Http.MultipartFormData.FilePart) obj;
            UserUpload userUpload = uploadFile(filePart);

            BasicField fileInputField = workflowDef.getField(filePart.getKey());
            fileInputField.setValue(userUpload);
        }

        //  Set the form definition field values from the request data
        Map<String, String[]> data = body.asFormUrlEncoded();
        for (String key : data.keySet()) {
            BasicField field = workflowDef.getField(key);
            field.setValue(data.get(key));
        }

        // Transfer form field data to workflow settings map
        Map<String, Object> settings = new HashMap<>();

        for (BasicField field : workflowDef.getFields()) {
            settings.put(field.name, field.value());
        }

        settings.putAll(settingsFromConfig(workflowDef));

        // Update the workflow model object and persist to the db
        Workflow workflow = workflowDao.updateWorkflow(workflowDef.getName(), workflowDef.getTitle(),
                workflowDef.getYamlFile());

        // Run the workflow
        long runId = runYamlWorkflow(workflow, settings);

        // The response json contains the workflow run id for later reference
        ObjectNode response = Json.newObject();
        response.put("runId", runId);

        /*return redirect(
                routes.Application.index()
        );*/

        return ok(response);
    }

    /**
     * Helper method creates a temp file from the multipart form data and persists the upload file metadata to the
     * database
     *
     * @param filePart data from the form submission
     * @return an instance of UploadFile that has been persisted to the db
     */
    private UserUpload uploadFile(Http.MultipartFormData.FilePart filePart) {
        File src = (File) filePart.getFile();
        File file = null;
        try {
            file = File.createTempFile(filePart.getFilename() + "-", ".csv");
            FileUtils.copyFile(src, file);
        } catch (IOException e) {
            throw new RuntimeException("Could not create temp file for upload", e);
        }

        UserUpload uploadFile = userDao.createUserUpload(request().username(), filePart.getFilename(),
                file.getAbsolutePath());


        return uploadFile;
    }

    /**
     * Helper method for running yaml workflows using an instance of WorkflowRunner.
     *
     * @param workflow Workflow definition object
     * @param settings A map of the settings provided as input to the runner
     * @return json containing the id of this run
     */
    private long runYamlWorkflow(Workflow workflow, Map<String, Object> settings) {
        // Load workflow yaml
        InputStream yamlStream = loadYamlStream(workflow.getYamlFile());

        // Initialize streams for output and error logging
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();

        // This instance of runnable will be executed at the end of a workflow run
        AsyncWorkflowRunnable runnable = new AsyncWorkflowRunnable();

        try {
            // Set up workflow runner configuration
            Map<String, Object> config = new HashMap<String, Object>();
            config.put("jython_home", JYTHON_HOME);
            config.put("jython_path", JYTHON_PATH);

            // TODO: save user object in global state somehow
            // Get the current logged in user
            User user = userDao.findUserByUsername(request().username());

            // Initialize and run the yaml workflow
            WorkflowRunner runner = new YamlStreamWorkflowRunner()
                    .yamlStream(yamlStream).configure(config);

            runnable.init(workflow, user, runner, errStream, outStream);

            runner.apply(settings)
                    .outputStream(new PrintStream(outStream))
                    .errorStream(new PrintStream(errStream))
                    .runAsync(runnable);
        } catch (Exception e) {
            throw new RuntimeException("Error running workflow: " + workflow.getTitle(), e);
        }

        return runnable.getRunId();
    }

    /**
     * Generic workflow input form page.
     *
     * @param name workflow name
     */
    @Security.Authenticated(Secured.class)
    public Result workflow(String name) {
        WorkflowDefinition workflowDef = formDefinitionForWorkflow(name);

        if (workflowDef != null) {
            return ok(
                    workflow.render(workflowDef)
            );
        } else {
            return notFound("No workflow found for name " + name);
        }
    }

    /**
     * Return the result archive containing artifacts produced by the workflow run.
     *
     * @param runId identifies the workflow run by id
     * @return the zip archive
     */
    @Security.Authenticated(Secured.class)
    public Result resultArchive(long runId) {
        WorkflowResult result = workflowDao.findResultByWorkflowId(runId);
        return ok(new File(result.getArchivePath()));
    }

    /**
     * Return the workflow run error log as a text file.
     *
     * @param runId identifies the workflow run by id
     * @return error log as text file
     */
    @Security.Authenticated(Secured.class)
    public Result errorLog(long runId) {
        response().setHeader("Content-Disposition", "attachment; filename=error_log.txt");
        response().setHeader("Content-Type", "text/plain");

        WorkflowResult result = workflowDao.findResultByWorkflowId(runId);

        if (result != null) {
            return ok(result.getErrorText());
        } else {
            return notFound("No error log found for workflow run with id " + runId);
        }
    }

    /**
     * Return the workflow output log as a text file.
     *
     * @param runId identifies the workflow run by id
     * @return output log as text file
     */
    @Security.Authenticated(Secured.class)
    public Result outputLog(long runId) {
        response().setHeader("Content-Disposition", "attachment; filename=output_log.txt");
        response().setHeader("Content-Type", "text/plain");

        WorkflowResult result = workflowDao.findResultByWorkflowId(runId);

        if (result != null) {
            return ok(result.getOutputText());
        } else {
            return notFound("No output log found for workflow run with id " + runId);
        }
    }

    public Result removeRun(long workflowRunId) {
        workflowDao.removeWorkflowRun(workflowRunId);
        return ok();
    }

    /** REST endopint returns a json object with metadata about the status of all of a current users workflow runs.
     *
     * @param uid
     * @return
     */
    public Result status(String uid) {
        List<RunResult> results = new ArrayList<>();
        List<WorkflowRun> workflowRuns = workflowDao.findUserWorkflowRuns(uid);

        for (WorkflowRun run : workflowRuns) {
            //boolean hasReport = run.getResult().getDqReport() != null;
            boolean hasOutput = run.getResult() != null && run.getResult().getOutputText() != null;
            boolean hasErrors = run.getResult() != null && run.getResult().getErrorText() != null;

            RunResult result = new RunResult(run.getId(), run.getWorkflow().getTitle(), run.getStartTime(),
                    run.getEndTime(), hasOutput, hasErrors, run.getStatus());

            results.add(result);
        }

        return ok(Json.toJson(results));
    }

    /**
     * Private helper method obtains an instance of WorkflowDefinition from the workflow name.
     *
     * @param name workflow name
     * @return the form definition object
     */
    private WorkflowDefinition formDefinitionForWorkflow(String name) {
        List<WorkflowDefinition> workflowDefs = loadWorkflowFormDefinitions();

        for (WorkflowDefinition workflowDef : workflowDefs) {
            if (workflowDef.getName().equals(name)) {
                return workflowDef;
            }
        }

        return null; // no such workflow by name
    }

    /**
     * Utility method loads all of the workflow form definitions from yaml files contained in the workflow
     * directory.
     *
     * @return
     */
    public static List<WorkflowDefinition> loadWorkflowFormDefinitions() {
        List<WorkflowDefinition> workflowDefs = new ArrayList<>();

        Collection<config.WorkflowConfig> workflows = ConfigManager.getInstance().getWorkflowConfigs();

        for (config.WorkflowConfig workflow : workflows) {
            WorkflowDefinition workflowDef = new WorkflowDefinition(workflow);

            for (ParameterConfig parameter : workflow.getParameters()) {
                if (parameter.isTyped()) {
                    String type = parameter.getType();
                    BasicField field = null;

                    switch (type) {
                        case "text":
                            field = new TextField();
                            break;
                        case "select":
                            SelectField selectField = new SelectField();
                            selectField.options = parameter.getOptions();
                            field = selectField;
                            break;
                        case "upload":
                            field = new FileInput();
                            break;
                        default:
                            throw new RuntimeException("Unsupported parameter type in workflow config: "
                                    + workflow.getName());
                    }

                    // Set properties common to all fields
                    field.name = parameter.getName();
                    field.label = parameter.getLabel();
                    field.tooltip = parameter.getDescription();

                    workflowDef.addField(field);
                }
            }

            workflowDefs.add(workflowDef);
        }
        return workflowDefs;
    }

    private InputStream loadYamlStream(String yamlFile) {
        try {
            return new FileInputStream(yamlFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not load workflow from yaml file.", e);
        }
    }

    /**
     * Will load additional settings from the web app config. Creates a map object that contains
     * workflow parameters to be provided as input to the runner
     *
     * @param form form definition of the workflow being run
     * @return a map of the settings
     */
    private Map<String, String> settingsFromConfig(WorkflowDefinition form) {
        try {
            Map<String, String> settings = new HashMap<String, String>();

            // Load workflow yaml file to check parameters
            GenericApplicationContext springContext = new GenericApplicationContext();
            YamlBeanDefinitionReader yamlBeanReader = new YamlBeanDefinitionReader(springContext);
            yamlBeanReader.loadBeanDefinitions(loadYamlStream(form.getYamlFile()), "-");
            springContext.refresh();

            WorkflowConfig workflowConfig = springContext.getBean(WorkflowConfig.class);

            // Create a workspace
            Path path = Paths.get(WORKSPACE_DIR, "workspace_" + UUID.randomUUID());
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
    public Result deploy() {
        List<PackageData> packages = ConfigManager.getInstance().listPackages();

        return ok(
                Json.toJson(packages)
        );
    }

    @Security.Authenticated(Secured.class)
    public Result deployWorkflows() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("filename");

        if (filePart != null) {
           ConfigManager configManager = ConfigManager.getInstance();

            char[] keystorePassword = ConfigFactory.defaultApplication().getString("keystore.password").toCharArray();
            String keystoreLocation = ConfigFactory.defaultApplication().getString("keystore.location");

            try {
                File zipFile = (File) filePart.getFile();
                WorkflowPackageVerifier verifier = new WorkflowPackageVerifier();
                verifier.checkIntegrity(zipFile, keystoreLocation, keystorePassword);

                configManager.unpack((File) filePart.getFile());
            } catch (Exception e) {
                flash("error", "Could not verify package.");
                return redirect(routes.Workflows.deploy());
            }

            flash("message", "Packages zip file was successfully deployed");
            return redirect(routes.Workflows.deploy());
        } else {
            flash("error", "Missing file");
            return badRequest();
        }
    }

    public Result report(long workflowRunId) throws IOException {
        // TODO: this works for demonstration purposes but should be implemented properly
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        FFDQPostProcessor postProcessor = new FFDQPostProcessor(new FileInputStream(run.getResult().getDqReport()),
                AsyncWorkflowRunnable.class.getResourceAsStream("/ev-assertions.json"));

        String json = postProcessor.measureSummary();
        System.out.println(json);

        return ok(json);
    }
}
