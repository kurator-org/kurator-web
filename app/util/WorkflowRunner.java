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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WorkflowRunner {
    final static Logger logger = LoggerFactory.getLogger(WorkflowRunner.class);

    private static String JAVA_BIN = System.getProperty("java.home") + "/bin/java";
    private static String KURATOR_JAR = "/home/lowery/IdeaProjects/kurator-validation/target/kurator-validation-1.0.1-SNAPSHOT-jar-with-dependencies.jar";

    public static void main(String[] args) throws IOException, InterruptedException {
        String yamlFile = "/home/lowery/IdeaProjects/kurator-validation/packages/kurator_dwca/workflows/file_term_values.yaml";

        // Create a workspace
        Path path = Paths.get("/home/lowery/workspace", "workspace_" + UUID.randomUUID());
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
        runner.run(options);
    }

    public int run(RunOptions options) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(JAVA_BIN, "-cp", KURATOR_JAR, "org.kurator.akka.KuratorWeb");

        // Redirect the stderr and stdout logs to a temp file
        File runlog = File.createTempFile("kurator-runlog-", ".log");
        builder.redirectError(runlog);

        Process process = builder.start();

        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        String opt = mapper.writeValueAsString(options);
        System.out.println("input options: " + opt);

        stdin.write(opt.getBytes());
        stdin.flush();
        stdin.close();

        process.waitFor();

        System.out.println("exit code: " + process.exitValue());
        System.out.println("run log: " + runlog.getAbsolutePath());

        List<WorkflowArtifact> artifacts = mapper.readValue(stdout, new TypeReference<ArrayList<WorkflowArtifact>>() { });


        for (WorkflowArtifact artifact : artifacts) {
            System.out.println(artifact.getName() + " - " + artifact.getPath());
        }

        return process.exitValue();
    }
}