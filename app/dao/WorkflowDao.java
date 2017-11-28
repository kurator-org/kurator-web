/** WorkflowDao.java
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
package dao;

import com.avaje.ebean.annotation.Transactional;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import models.db.user.User;
import models.db.workflow.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WorkflowDao {

    @Transactional
    public WorkflowRun createWorkflowRun(String name, Workflow workflow, User user, Date startTime) {
        WorkflowRun run = new WorkflowRun();
        run.setOwner(user);
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
        return WorkflowRun.find.where().eq("owner.id", uid).findList();
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
    public void removeWorkflowRun(long workflowRunId) throws IOException {
        WorkflowRun run = WorkflowRun.find.byId(workflowRunId);

        if (run != null) {
            // if status is running then kill the process
            try {
                Runtime rt = Runtime.getRuntime();
                if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
                    rt.exec("taskkill " + run.getPid());
                else
                    rt.exec("kill -9 " + run.getPid());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // if the workspace directory exists, delete it
            File workspace = new File(run.getWorkspace());
            if (workspace.exists()) {
                FileUtils.deleteDirectory(workspace);
            }

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
