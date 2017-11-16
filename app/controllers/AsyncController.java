package controllers;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.ConfigFactory;
import config.ConfigManager;
import dao.UserDao;
import dao.WorkflowDao;
import models.db.user.User;
import models.db.user.UserUpload;
import models.db.workflow.*;
import models.json.ArtifactDef;
import models.json.WorkflowDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kurator.akka.data.WorkflowProduct;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import ui.input.BasicField;
import util.RunOptions;
import util.RunResult;
import util.WorkflowArtifact;
import util.WorkflowRunner;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AsyncController extends Controller {
    // Get jython home and path directories relative to the current directory
    //private static final String JYTHON_HOME = new File("jython").getAbsolutePath();
    //private static final String JYTHON_PATH = new File("packages").getAbsolutePath();

    // Get the workspace basedir relative to the current directory
    private static final String WORKSPACE_DIR = new File("workspace").getAbsolutePath();

    private final ActorSystem system;
    private final ExecutionContextExecutor exec;

    private final WorkflowDao workflowDao = new WorkflowDao();
    private final UserDao userDao = new UserDao();

    @Inject
    public AsyncController(ActorSystem system, ExecutionContextExecutor exec) {
        this.system = system;
        this.exec = exec;
    }

    public Result scheduleRun(String name) {
        WorkflowDefinition workflowDef = Workflows.formDefinitionForWorkflow(name);

        // TODO: save user object in global state somehow
        // Get the current logged in user
        User user = User.find.byId(Long.parseLong(session().get("uid")));
        Map<String, Object> settings = Workflows.settingsFromConfig(workflowDef, user);

        // Update date user was last active
        user.setLastActive(new Date());
        user.save();

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

        // Update the workflow model object and persist to the db
        Workflow workflow = workflowDao.updateWorkflow(workflowDef.getName(), workflowDef.getTitle(),
                workflowDef.getYamlFile());

        // Run the workflow
        long runId = runYamlWorkflow(runName, workflow, workflowDef, settings, user);

        // The response json contains the workflow run id for later reference
        ObjectNode response = Json.newObject();
        response.put("runId", runId);

        return ok(response);
    }

    private long runYamlWorkflow(String runName, Workflow workflow, WorkflowDefinition workflowDef, Map<String, Object> parameters, User user) {
        // Set up workflow runner configuration
        Map<String, String> config = new HashMap<>();
        //config.put("jython_home", JYTHON_HOME);
        //config.put("jython_path", JYTHON_PATH);

        ConfigManager configManager = ConfigManager.getInstance();

        String pythonPath = ConfigFactory.defaultApplication().getString("python.path");
        String libraryPath = ConfigFactory.defaultApplication().getString("library.path");

        config.put("python_path", pythonPath);
        config.put("ld_library_path", libraryPath);

        Date startTime = new Date(); // Workflow run start time
        WorkflowRun run = workflowDao.createWorkflowRun(runName, workflow, user, startTime);
        final long runId = run.getId();

        String yamlFile = workflow.getYamlFile();

        // Create a workspace
        Path path = Paths.get(WORKSPACE_DIR, "workspace_" + UUID.randomUUID());
        path.toFile().mkdir();

        //Map<String, String> parameters = new HashMap<>();

        parameters.put("workspace", path.toString());
        //parameters.put("inputfile", "/home/lowery/IdeaProjects/kurator-validation/packages/kurator_dwca/data/tests/test_onslow_vertnet.csv");
        //parameters.put("format", "txt");
        //parameters.put("fieldlist", "country|stateProvince");

        String logLevel = "DEBUG";

        Runnable workflowTask = () -> {
            final WorkflowRun workflowRun = run;

            System.out.println("Started workflow run...");
            WorkflowRunner runner = new WorkflowRunner();

            try {
                RunOptions options = new RunOptions(yamlFile, parameters, config, logLevel);
                RunResult result = runner.run(options, workflowRun);

                System.out.println(result.getOptions().toJsonString());
                System.out.println(result.getWorkspaceDirectory().getAbsolutePath());

                for (WorkflowArtifact artifact : result.getArtifacts()) {
                    System.out.println(artifact.getName() + " - " + artifact.getPath());
                }

                processResults(workflowDef, result, runId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Ended workflow run...");
        };

        system.dispatcher().execute(workflowTask);

        return runId;
    }

    public void processResults(WorkflowDefinition workflowDef, RunResult result, long runId) {
        WorkflowDao workflowDao = new WorkflowDao();
        WorkflowRun run = workflowDao.findWorkflowRunById(runId);

        List<ResultFile> resultFiles = new ArrayList<>();

        Map<String, String> resultDefs = new HashMap<>();
        Map<String, String> otherDefs = new HashMap<>();

        for (ArtifactDef artifactDef : workflowDef.getResultArtifacts().values()) {
            resultDefs.put(artifactDef.getName(), artifactDef.getDescription());
        }

        for (ArtifactDef artifactDef : workflowDef.getOtherArtifacts().values()) {
            otherDefs.put(artifactDef.getName(), artifactDef.getDescription());
        }

        Date endTime = new Date(); // Workflow run end time
        String dqReportFile = "";

        try {
            // Process workflow artifacts
            for (WorkflowArtifact artifact : result.getArtifacts()) {
                String fileName = String.valueOf(artifact.getPath());
                File file = new File(fileName);
                if (file.exists()) {
                    // if one of the products is a dq report save it for later
                    if (artifact.getType().equals("DQ_REPORT")) {
                        dqReportFile = file.getAbsolutePath();
                    }

                    // Include descriptive text
                    String description = "";

                    if (resultDefs.containsKey(artifact.getName())) {
                        description = resultDefs.get(artifact.getName());
                    } else if (otherDefs.containsKey(artifact.getName())) {
                        description = otherDefs.get(artifact.getName());
                    }

                    // Create a result file from the workflow product and persist it to the db
                    ResultFile resultFile = workflowDao.createResultFile(artifact.getName(), file.getName(), description, artifact.getPath());
                    resultFiles.add(resultFile);

                } else {
                    Logger.error("artifact specified does not exist: " + fileName);
                }
            }

            // TODO: yaml file should be workflow yaml and not web app version
            //File yamlFile = new File(yamlFile);

            // Create output error log file
            //ResultFile log = workflowDao.createResultFile("runlog.txt", "runlog", "The output log for this run", result.getRunlog().getAbsolutePath());
            //resultFiles.add(log);

            // Create readme file
            resultFiles.add(createReadmeFile(workflowDef.getName(), run.getStartTime(), endTime));

            // Package result files in archive
            File archive = createArchive(resultFiles);
            System.out.println("created archive");

            // TODO: this is a hack for now, modify WorkflowResult to hold a reference to log file path instead
            String outputText = logFileToString(result.getRunlog().getAbsolutePath());

            // Persist the result to the db and update the workflow run
            WorkflowResult workflowResult = workflowDao.createWorkflowResult(resultFiles, archive.getAbsolutePath(),
                    dqReportFile, outputText, "");

            // Default status is success unless errors are present in the error log
            Status status = result.getStatus();

            workflowDao.updateRunResult(run, workflowResult, status, endTime);
        } catch (Exception e) {
            throw new RuntimeException("Failure during processing of workflow result", e);
        }
    }

    private String logFileToString(String logfile) {
        try {
            return IOUtils.toString(new FileInputStream(logfile));
        } catch (IOException e) {
            return "";
        }
    }

    private ResultFile createReadmeFile(String workflow, Date startTime, Date endTime) throws IOException {
        File readmeFile = File.createTempFile("README_", ".txt");
        FileWriter readme = new FileWriter(readmeFile);

        readme.append("Workflow: " + workflow + "\n");
        readme.append("Start time: " + startTime + "\n");
        readme.append("End time: " + endTime + "\n");

        readme.close();

        return workflowDao.createResultFile("readme","readme", "", readmeFile.getAbsolutePath());
    }

    private File createArchive(List<ResultFile> resultFiles) throws IOException {
        System.out.println("about to create archive");
        File archive = File.createTempFile("artifacts_", ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));

        for (ResultFile resultFile : resultFiles) {
            File file = new File(resultFile.getFileName());
            writeFile(file, out);
        }

        out.close();
        return archive;
    }

    private void writeFile(File file, ZipOutputStream out) throws IOException {
        FileInputStream in = new FileInputStream(file);

        out.putNextEntry(new ZipEntry(file.getName()));

        byte[] b = new byte[1024];
        int count;

        while ((count = in.read(b)) > 0) {
            out.write(b, 0, count);
        }

        in.close();
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
}
