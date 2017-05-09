package dao;

import com.avaje.ebean.annotation.Transactional;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import models.db.user.User;
import models.db.workflow.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lowery on 1/24/2017.
 */
public class WorkflowDao {

    @Transactional
    public WorkflowRun createWorkflowRun(String name, Workflow workflow, User user, Date startTime) {
        WorkflowRun run = new WorkflowRun();
        run.setUser(user);
        run.setWorkflow(workflow);
        run.setStartTime(startTime);
        run.setStatus(Status.RUNNING);
        run.setName(name);
        run.save();

        return run;
    }

    @Transactional
    public WorkflowResult createWorkflowResult(List<ResultFile> resultFiles, String archivePath,
                                               String dqReport, String outputText, String errorText) {
        WorkflowResult result = new WorkflowResult();
        result.setResultFiles(resultFiles);
        result.setArchivePath(archivePath);
        result.setDqReport(dqReport);
        result.setOutputText(outputText);
        result.setErrorText(errorText);
        result.save();

        return result;
    }

    @Transactional
    public ResultFile createResultFile(String name, String label, String description, String filename) {
        ResultFile resultFile = new ResultFile();
        resultFile.setName(name);
        resultFile.setLabel(label);
        resultFile.setDescription(description);
        resultFile.setFileName(filename);
        return resultFile;
    }

    public WorkflowRun findWorkflowRunById(long id) {
        WorkflowRun run = WorkflowRun.find.where().eq("id", id).findUnique();
        return run;
    }

    public List<WorkflowRun> findUserWorkflowRuns(String uid) {
        return WorkflowRun.find.where().eq("user.id", uid).findList();
    }

    public List<WorkflowRun> findWorkflowRunsByStatus(Status status) {
        return WorkflowRun.find.where().eq("status", status).findList();
    }

    @Transactional
    public WorkflowRun updateRunResult(WorkflowRun run, WorkflowResult result, Status status, Date endTime) {
        run.setResult(result);
        run.setStatus(status);
        run.setEndTime(endTime);
        run.save();

        return run;
    }

    @Transactional
    public Workflow updateWorkflow(String name, String title, String yamlFile) {
        Workflow workflow = Workflow.find.where().eq("name", name).findUnique();

        if (workflow == null) {
            workflow = new Workflow();
        }

        workflow.setName(name);
        workflow.setTitle(title);
        workflow.setYamlFile(yamlFile);

        workflow.save();
        return workflow;
    }

    @Transactional
    public void removeWorkflowRun(long workflowRunId) {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            run.delete();

            WorkflowResult result = run.getResult();

            if (result != null) {
                List<ResultFile> resultFiles = result.getResultFiles();

                for (ResultFile resultFile : resultFiles) {
                    resultFile.delete();
                }

                result.delete();
            }
        }
    }

    public ResultFile findResultFileById(long resultFileId) {
        return ResultFile.find.where().eq("id", resultFileId).findUnique();
    }
}
