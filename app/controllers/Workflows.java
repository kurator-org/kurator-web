/** Workflows.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import config.Artifact;
import config.ConfigManager;
import config.ParameterConfig;
import config.Variable;
import dao.UserAccessDao;
import dao.UserDao;
import dao.WorkflowDao;
import models.PackageData;
import models.db.user.User;
import models.db.user.UserUpload;
import models.db.workflow.ResultFile;
import models.db.workflow.Workflow;
import models.db.workflow.WorkflowResult;
import models.db.workflow.WorkflowRun;
import models.json.ArtifactDef;
import models.json.RunResult;
import models.json.WorkflowDefinition;
import org.apache.commons.io.FileUtils;
import org.kurator.akka.WorkflowConfig;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import org.restflow.yaml.spring.YamlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import ui.input.*;
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

/**
 * This controller deals with actions related to running a workflow, uploading files, checking the status of a run, and
 * obtaining artifacts produced by the run.
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
    private final UserAccessDao userAccessDao = new UserAccessDao();

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

    @Restrict({@Group("ADMIN")})
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
    @SubjectPresent
    public Result runWorkflow(String name) {
        WorkflowDefinition workflowDef = formDefinitionForWorkflow(name);

        // TODO: save user object in global state somehow
        // Get the current logged in user
        User user = User.find.byId(Long.parseLong(session().get("uid")));
        Map<String, Object> settings = settingsFromConfig(workflowDef, user);

        // Process form submission as multipart form data
        Http.MultipartFormData body = request().body().asMultipartFormData();

        for (Object obj : body.getFiles()) {
            Http.MultipartFormData.FilePart filePart = (Http.MultipartFormData.FilePart) obj;

            BasicField fileInputField = workflowDef.getField(filePart.getKey());
            File file = createWorkspaceFile(settings, filePart);

            fileInputField.setValue(file);
        }

        //  Set the form definition field values from the request data
        Map<String, String[]> data = body.asFormUrlEncoded();
        String runName = null;

        for (String key : data.keySet()) {
            if (key.equals("run-name")) {
                runName = data.get(key)[0];
            } else {
                BasicField field = workflowDef.getField(key);
                field.setValue(data.get(key));
            }
        }

        // Transfer form field data to workflow settings map
        for (BasicField field : workflowDef.getFields()) {
            settings.put(field.name, field.value());
        }

        System.out.println(settings);

        // Update the workflow model object and persist to the db
        Workflow workflow = workflowDao.updateWorkflow(workflowDef.getName(), workflowDef.getTitle(),
                workflowDef.getYamlFile());

        // Run the workflow
        long runId = runYamlWorkflow(runName, workflow, workflowDef, settings, user);

        // The response json contains the workflow run id for later reference
        ObjectNode response = Json.newObject();
        response.put("runId", runId);

        /*return redirect(
                routes.Application.index()
        );*/

        return ok(response);
    }

    private File createWorkspaceFile(Map<String, Object> settings, Http.MultipartFormData.FilePart src) {
        String workspace = (String) settings.get("workspace");

        if (workspace == null) {
            throw new RuntimeException("Workspace parameter not present in settings! Nowhere to put the inputfile.");
        }

        try {
            File workspaceFile = Paths.get(workspace, src.getFilename()).toFile();
            FileUtils.copyFile((File) src.getFile(), workspaceFile);

            return workspaceFile;
        } catch (IOException e) {
            throw new RuntimeException("Error copying uploaded file to workspace: " + workspace, e);
        }
    }

    public Result deleteUpload(long id) {
        UserUpload userUpload = userDao.removeUserUpload(id);

        flash("message", "Successfully removed uploaded file: " + userUpload.getFileName());

        return ok("{ }");
    }

    public Result upload() {
        // Get the current logged in user
        User user = User.find.byId(Long.parseLong(session().get("uid")));

        // Process form submission as multipart form data
        Http.MultipartFormData body = request().body().asMultipartFormData();

        UserUpload userUpload = null;

        for (Object obj : body.getFiles()) {
            Http.MultipartFormData.FilePart filePart = (Http.MultipartFormData.FilePart) obj;
            userUpload = uploadFile(filePart, user);
        }

        if (userUpload != null) {
            return ok("{ 'filename' : " + userUpload.getFileName() + "}");
        } else {
            return internalServerError("{ }");
        }

    }

    /**
     * Helper method creates a temp file from the multipart form data and persists the upload file metadata to the
     * database
     *
     * @param filePart data from the form submission
     * @return an instance of UploadFile that has been persisted to the db
     */
    private UserUpload uploadFile(Http.MultipartFormData.FilePart filePart, User user) {
        File src = (File) filePart.getFile();
        File file = null;
        try {
            Path path = Paths.get(WORKSPACE_DIR, user.getUsername(), "uploaded_files");
            if (!path.toFile().exists()) {
                path.toFile().mkdirs();
            }

            file = path.resolve(filePart.getFilename()).toFile();

            // If the file exists create a new file and add a number
            int i = 0;
            while (file.exists()) {
                String filename = filePart.getFilename();
                file = path.resolve(filename.substring(0, filename.lastIndexOf('.')) +
                        '_' + i + filename.substring(filename.lastIndexOf('.'))).toFile();

                i++;
            }

            FileUtils.copyFile(src, file);
        } catch (IOException e) {
            throw new RuntimeException("Could not create temp file for upload", e);
        }

        UserUpload uploadFile = userDao.createUserUpload(Long.parseLong(session().get("uid")), file.getName(),
                file.getAbsolutePath());


        return uploadFile;
    }

    /**
     * Helper method for running yaml workflows using an instance of WorkflowRunner.
     *
     * @param workflow    Workflow definition object
     * @param workflowDef
     * @param settings    A map of the settings provided as input to the runner  @return json containing the id of this run
     */
    private long runYamlWorkflow(String name, Workflow workflow, WorkflowDefinition workflowDef, Map<String, Object> settings, User user) {
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

            // Initialize and run the yaml workflow
            WorkflowRunner runner = new YamlStreamWorkflowRunner()
                    .yamlStream(yamlStream).configure(config);


            Map<String, String> resultDefs = new HashMap<>();
            Map<String, String> otherDefs = new HashMap<>();
            for (ArtifactDef artifactDef : workflowDef.getResultArtifacts().values()) {
                resultDefs.put(artifactDef.getName(), artifactDef.getDescription());
            }

            for (ArtifactDef artifactDef : workflowDef.getOtherArtifacts().values()) {
                otherDefs.put(artifactDef.getName(), artifactDef.getDescription());
            }

            runnable.init(name, workflow, workflowDef, resultDefs, otherDefs, user, runner, errStream, outStream);

            runner.apply(settings)
                    .outputStream(new PrintStream(outStream))
                    .errorStream(new PrintStream(errStream))
                    .runAsync(runnable);
        } catch (Exception e) {
            throw new RuntimeException("Error running workflow: " + workflow.getTitle(), e);
        }

        return runnable.getRunId();
    }

    @SubjectPresent
    public Result resultArtifacts(long runId) {
        ObjectNode response = Json.newObject();

        WorkflowRun run = workflowDao.findWorkflowRunById(runId);
        WorkflowResult result = run.getResult();

        WorkflowDefinition workflowDef = formDefinitionForWorkflow(run.getWorkflow().getName());
        Map<String, ArtifactDef> resultArtifacts = workflowDef.getResultArtifacts();
        Map<String, ArtifactDef> otherArtifacts = workflowDef.getOtherArtifacts();

        response.put("id", run.getId());
        response.put("name", run.getName());
        response.put("workflow", run.getWorkflow().getTitle());
        response.put("workflowName", run.getWorkflow().getName());
        response.put("yaml", run.getWorkflow().getYamlFile());
        response.put("startTime", run.getStartTime().getTime());
        response.put("endTime", run.getEndTime().getTime());
        response.put("archive", result.getArchivePath());

        System.out.println("workflow: " + run.getWorkflow().getName());
        System.out.println();

        ArrayNode resultsArr = Json.newArray();
        ArrayNode otherArr = Json.newArray();

        for (ResultFile resultFile : result.getResultFiles()) {

            System.out.println(resultFile.getName());

            if (resultArtifacts.containsKey(resultFile.getName())) {
                ArtifactDef artifactDef = resultArtifacts.get(resultFile.getName());

                if (artifactDef != null) {
                    resultsArr.add(artifactJson(artifactDef, resultFile));
                }
            } else if (otherArtifacts.containsKey(resultFile.getName())) {
                ArtifactDef artifactDef = otherArtifacts.get(resultFile.getName());

                if (artifactDef != null) {
                    otherArr.add(artifactJson(artifactDef, resultFile));
                }
            }

        }

        ObjectNode artifactsObj = Json.newObject();
        artifactsObj.put("results", resultsArr);
        artifactsObj.put("others", otherArr);

        response.put("artifacts", artifactsObj);

        return ok(response);

        //return ok(new File(result.getArchivePath()));
    }

    private ObjectNode artifactJson(ArtifactDef artifactDef, ResultFile resultFile) {
        ObjectNode artifactObj = Json.newObject();
        artifactObj.put("type", artifactDef.getType());
        artifactObj.put("label", resultFile.getLabel());
        artifactObj.put("filename", resultFile.getFileName());
        artifactObj.put("description", artifactDef.getDescription());
        artifactObj.put("info", artifactDef.getInfo());
        artifactObj.put("id", resultFile.getId());

        return artifactObj;
    }

    @SubjectPresent
    public Result workflowYaml(String name) {
        WorkflowDefinition workflowDef = formDefinitionForWorkflow(name);
        File file = new File(workflowDef.getYamlFile());
        response().setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        return ok(file);
    }

    @SubjectPresent
    public Result resultFile(long resultFileId) {
        ResultFile resultFile = workflowDao.findResultFileById(resultFileId);
        File file = new File(resultFile.getFileName());
        response().setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        return ok(file);
    }

    /**
     * Return the result archive containing artifacts produced by the workflow run.
     *
     * @param runId identifies the workflow run by id
     * @return the zip archive
     */
    @SubjectPresent
    public Result resultArchive(long runId) {
        WorkflowRun run = workflowDao.findWorkflowRunById(runId);
        WorkflowResult result = run.getResult();

        return ok(new File(result.getArchivePath()));
    }

    /**
     * Return the workflow run error log as a text file.
     *
     * @param runId identifies the workflow run by id
     * @return error log as text file
     */
    @SubjectPresent
    public Result errorLog(long runId) {
        response().setHeader("Content-Disposition", "attachment; filename=error_log.txt");
        response().setHeader("Content-Type", "text/plain");

        WorkflowRun run = workflowDao.findWorkflowRunById(runId);
        WorkflowResult result = run.getResult();

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
    @SubjectPresent
    public Result outputLog(long runId) {
        response().setHeader("Content-Disposition", "attachment; filename=output_log.txt");
        response().setHeader("Content-Type", "text/plain");

        WorkflowRun run = workflowDao.findWorkflowRunById(runId);

            File output = Paths.get(run.getWorkspace(), "output.log").toFile();
            if (output != null && output.exists()) {
                return ok(output);
            } else {
                return notFound("No output log found for workflow run with id " + runId);
            }
    }

    @SubjectPresent
    public Result removeRuns() {
        System.out.println(request().body().asJson());
        ArrayNode runs = (ArrayNode) request().body().asJson().get("runs");

        ArrayNode response = Json.newArray();
        for (int i = 0; i < runs.size(); i++) {
            JsonNode run = runs.get(i);

            long runId = run.get("id").asLong();
            try {
                workflowDao.removeWorkflowRun(runId);
            } catch (Exception e) {
                return internalServerError(e.getMessage());
            }

            response.add(runId);
        }

        return ok(response);
    }

    /**
     * REST endopint returns a json object with metadata about the status of all of a current users workflow runs.
     *
     * @return
     */
    @SubjectPresent
    public Result status(Long uid) {
        System.out.println(uid);

        List<RunResult> results = new ArrayList<>();
        //List<WorkflowRun> userRuns = workflowDao.findUserWorkflowRuns(session("uid"));
        //List<WorkflowRun> sharedRuns = userAccessDao.findSharedRunsByUser(Long.parseLong(session("uid")));

        List<WorkflowRun> userRuns = workflowDao.findUserWorkflowRuns(uid);
        List<WorkflowRun> sharedRuns = userAccessDao.findSharedRunsByUser(uid);

        List<WorkflowRun> runs = new ArrayList<>();
        runs.addAll(userRuns);
        runs.addAll(sharedRuns);

        return ok(Json.toJson(runs));
    }

    @SubjectPresent
    public Result shareRun(Long id) {
        WorkflowRun shared = Json.fromJson(request().body().asJson(), WorkflowRun.class);
        shared.setSharedOn(new Date());
        shared.update();

        return ok(
                Json.toJson(shared)
        );
    }

    /**
     * Private helper method obtains an instance of WorkflowDefinition from the workflow name.
     *
     * @param name workflow name
     * @return the form definition object
     */
    public static WorkflowDefinition formDefinitionForWorkflow(String name) {
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

        ConfigManager configManager = ConfigManager.getInstance();
        Collection<config.WorkflowConfig> workflows = configManager.getWorkflowConfigs();

        List<Variable> variables = configManager.getVariables();
        // TODO: add ability to set workflow variables as user config

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
                        case "tokenfield":
                            field = new TokenField();
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

    private static InputStream loadYamlStream(String yamlFile) {
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
    public static Map<String, Object> settingsFromConfig(WorkflowDefinition form, User user) {
        try {
            Map<String, Object> settings = new HashMap<>();

            // Load workflow yaml file to check parameters
            GenericApplicationContext springContext = new GenericApplicationContext();
            YamlBeanDefinitionReader yamlBeanReader = new YamlBeanDefinitionReader(springContext);
            yamlBeanReader.loadBeanDefinitions(loadYamlStream(form.getYamlFile()), "-");
            springContext.refresh();

            WorkflowConfig workflowConfig = springContext.getBean(WorkflowConfig.class);

            // Create a workspace in the current user's directory
            Path path = Paths.get(WORKSPACE_DIR, user.getUsername(), "workspace_" + UUID.randomUUID());
            path.toFile().mkdirs();

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

    @Restrict({@Group("ADMIN")})
    public Result deploy() {
        List<PackageData> packages = ConfigManager.getInstance().listPackages();

        return ok(
                Json.toJson(packages)
        );
    }

    @Restrict({@Group("ADMIN")})
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

    @SubjectPresent
    public Result report(long workflowRunId) throws IOException {
        // TODO: this works for demonstration purposes but should be implemented properly
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        /*FFDQPostProcessor postProcessor = new FFDQPostProcessor(new FileInputStream(run.getResult().getDqReport()),
                Workflows.class.getResourceAsStream("/ev-assertions.json"));

        String json = postProcessor.measureSummary();*/

        return ok(new File(run.getResult().getDqReport()));
    }

    @SubjectPresent
    public Result dataset(long workflowRunId) throws IOException {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        List<ResultFile> resultFiles = run.getResult().getResultFiles();
        for (ResultFile resultFile : resultFiles) {
            if ("dq_report_xls_file".equalsIgnoreCase(resultFile.getLabel())) {
                return ok(new File(resultFile.getFileName()));
            }
        }

//        try {
//            InputStream inputStream = new FileInputStream(run.getResult().getDqReport());
//            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            StringBuilder responseStrBuilder = new StringBuilder();
//
//            String inputStr;
//            while ((inputStr = streamReader.readLine()) != null)
//                responseStrBuilder.append(inputStr);
//
//
//            return ok(responseStrBuilder.toString());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return ok();
    }
}
