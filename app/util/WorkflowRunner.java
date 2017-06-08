/** WorkflowRunner.java
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.db.workflow.Status;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WorkflowRunner {
    final static Logger logger = LoggerFactory.getLogger(WorkflowRunner.class);

    private static String JAVA_BIN = System.getProperty("java.home") + "/bin/java";
    private static String KURATOR_JAR = System.getenv("KURATOR_JAR");

    public static void main(String[] args) throws IOException, InterruptedException {
        String yamlFile = "/home/lowery/IdeaProjects/kurator-validation/packages/kurator_dwca/workflows/file_term_values.yaml";

        // Create a workspace
        Path path = Paths.get("/home/lowery/IdeaProjects/kurator-web/workspace", "workspace_" + UUID.randomUUID());
        path.toFile().mkdir();

        Map<String, String> parameters = new HashMap<>();

        parameters.put("workspace", path.toString());
        parameters.put("inputfile", "/home/lowery/IdeaProjects/kurator-validation/packages/kurator_dwca/data/tests/test_onslow_vertnet.csv");
        parameters.put("format", "txt");
        parameters.put("fieldlist", "country|stateProvince");

        Map<String, String> config = new HashMap<>();
        config.put("jython_home", "/home/lowery/IdeaProjects/kurator-web/jython");
        config.put("jython_path", "/home/lowery/IdeaProjects/kurator-validation/packages");

        String logLevel = "DEBUG";

        RunOptions options = new RunOptions(yamlFile, parameters, config, logLevel);

        WorkflowRunner runner = new WorkflowRunner();
        RunResult result = runner.run(options);

        System.out.println(result.getOptions().toJsonString());
        System.out.println(result.getWorkspaceDirectory().getAbsolutePath());
    }

    public RunResult run(RunOptions options) throws IOException, InterruptedException {
        // The process builder will run the kurator jar in a separate process
        ProcessBuilder builder = new ProcessBuilder(JAVA_BIN, "-cp", KURATOR_JAR, "org.kurator.akka.KuratorWeb");

        // Create run result, retain a copy of the input options and set initial status to running
        RunResult result = new RunResult();
        result.setOptions(options);
        result.setStatus(Status.RUNNING);

        // Redirect stderr and stdout to a log file in the workspace directory
        File runlog = result.createWorkspaceFile("runlog.log");
        builder.redirectError(runlog);

        // Start the workflow run as a process and get the input and output streams
        Process process = builder.start();

        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();

        // Serialize input options as json and send via stdin to the kurator process
        byte[] opt = options.toJsonString().getBytes();

        stdin.write(opt);
        stdin.flush();
        stdin.close();

        // Block until the process exits
        process.waitFor();

        // Determine run result status based on the exit code
        switch (process.exitValue()) {
            case 0:
                result.setStatus(Status.SUCCESS);
                break;
            case 1:
            case 2:
                result.setStatus(Status.ERRORS);
                break;
        }

        // Deserialize json output and add workflow artifacts to the run result
        ObjectMapper mapper = new ObjectMapper();
        List<WorkflowArtifact> artifacts = mapper.readValue(stdout, new TypeReference<ArrayList<WorkflowArtifact>>() { });
        result.setArtifacts(artifacts);

        return result;
    }
}