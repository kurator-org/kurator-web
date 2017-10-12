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

    public RunResult run(RunOptions options) throws IOException, InterruptedException {
        if (KURATOR_JAR == null) {
            // Try a JVM property
            String prop = System.getProperty("kurator.jar");

            if (prop != null) {
                KURATOR_JAR = prop;
            } else {
                throw new RuntimeException("Missing KURATOR_JAR environment variable. Configure to point to the location of your kurator jar (i.e. kurator-validation.jar)");
            }
        }

        // The process builder will run the kurator jar in a separate process
        ProcessBuilder builder = new ProcessBuilder(JAVA_BIN, "-cp", KURATOR_JAR, "org.kurator.akka.KuratorWeb");
        Map<String, String> env = builder.environment();

        // TODO: Hardcoded for now, get these variables instead of the JYTHON ones
        env.put("PYTHONPATH", "/home/lowery/IdeaProjects/kurator-validation/packages");
        env.put("LD_LIBRARY_PATH", "/home/lowery/IdeaProjects/kurator-akka/lib/native");

        System.out.println("export JYTHON_HOME=" + options.getConfig().get("jython_home"));
        System.out.println("export JYTHON_PATH=" + options.getConfig().get("jython_path"));
        System.out.println("java -jar " + KURATOR_JAR + " " + options.toCmdString());
        System.out.println();
        System.out.println(options.toJsonString());
        System.out.println();

        // Create workspace if it doesn't exist
        File workspace = new File(options.getWorkspace());
        if (!workspace.exists()) {
            workspace.mkdirs();
        }

        // Create run result, retain a copy of the input options and set initial status to running
        RunResult result = new RunResult();
        result.setOptions(options);
        result.setStatus(Status.RUNNING);

        // Store run log as file
        File log = new File(options.getWorkspace() + "/" + "output.log");
        result.setRunlog(log);
        builder.redirectError(log);

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

                // Deserialize json output and add workflow artifacts to the run result
                ObjectMapper mapper = new ObjectMapper();
                List<WorkflowArtifact> artifacts = mapper.readValue(stdout, new TypeReference<ArrayList<WorkflowArtifact>>() { });
                result.setArtifacts(artifacts);

                break;
            case 1:
            case 2:
                result.setStatus(Status.ERRORS);
                break;
        }

        System.out.println("Workflow run status: " + result.getStatus());
        return result;
    }
}