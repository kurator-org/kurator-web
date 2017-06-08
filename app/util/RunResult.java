/** RunResult.java
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
package util;

import models.db.workflow.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Created by lowery on 6/8/17.
 */
public class RunResult {
    private Status status;
    private File runlog;
    private RunOptions options;
    private List<WorkflowArtifact> artifacts;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public File getRunlog() {
        return runlog;
    }

    public void setRunlog(File runlog) {
        this.runlog = runlog;
    }

    public RunOptions getOptions() {
        return options;
    }

    public void setOptions(RunOptions options) {
        this.options = options;
    }

    public List<WorkflowArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<WorkflowArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    protected File createWorkspaceFile(String filename) throws IOException {
        Path path = Paths.get(options.getWorkspace(), filename);
        File file = path.toFile();

        file.createNewFile();
        return file;
    }

    public File getWorkspaceDirectory() {
        return new File(options.getWorkspace());
    }
}
