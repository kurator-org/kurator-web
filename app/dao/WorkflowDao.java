package dao;

import com.avaje.ebean.annotation.Transactional;
import models.db.user.User;
import models.db.workflow.*;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by lowery on 1/24/2017.
 */
public class WorkflowDao {

    @Transactional
    public WorkflowRun createWorkflowRun(Workflow workflow, User user, Date startTime) {
        WorkflowRun run = new WorkflowRun();
        run.setUser(user);
        run.setWorkflow(workflow);
        run.setStartTime(startTime);
        run.setStatus(Status.RUNNING);
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
    public ResultFile createResultFile(String label, String filename) {
        ResultFile resultFile = new ResultFile();
        resultFile.setLabel(label);
        resultFile.setFileName(filename);
        return resultFile;
    }

    public WorkflowResult findResultByWorkflowId(long id) {
        return WorkflowResult.find.where().eq("workflow.id", id).findUnique();
    }

    public List<WorkflowRun> findUserWorkflowRuns(String uid) {
        return WorkflowRun.find.where().eq("user.uid", uid).findList();
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
}
