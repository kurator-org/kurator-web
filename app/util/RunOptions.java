/** RunOptions.java
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RunOptions {
    private String yamlFile;
    private Map<String, Object> parameters;
    private Map<String, String> config;
    private String logLevel;
    private String workspace;

    public RunOptions(String yamlFile, Map<String, Object> parameters, Map<String, String> config, String logLevel) {
        this.yamlFile = yamlFile;
        this.parameters = parameters;
        this.config = config;
        this.logLevel = logLevel;

        if (parameters.containsKey("workspace")) {
            this.workspace = (String) parameters.get("workspace");
        }
    }

    @JsonProperty("yaml")
    public String getYamlFile() {
        return yamlFile;
    }

    @JsonProperty("parameters")
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @JsonProperty("config")
    public Map<String, String> getConfig() {
        return config;
    }

    @JsonProperty("loglevel")
    public String getLogLevel() {
        return logLevel;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public String toCmdString() {
        StringBuilder sb = new StringBuilder();

        sb.append("-f ")
                .append(yamlFile);

        for (String param : parameters.keySet()) {
            sb.append(" -p ")
                    .append(param)
                    .append('=')
                    .append(parameters.get(param));
        }

        sb.append(" -l ")
                .append(logLevel);

        return sb.toString();
    }

    public String getWorkspace() {
        return workspace;
    }

    /**
     * Helper method resolves a path relative to the packages directory and returns an
     * absolute path as a String
     *
     * @param path
     * @return absolute path
     */
    public static String resolve(String path) {
        Path packageDir = Paths.get("packages/kurator_dwca");
        File file = packageDir.resolve(path).toFile();

        if (!file.exists()) {
            throw new RuntimeException("File not found: " + file.getAbsolutePath());
        }

        return file.getAbsolutePath();
    }
}
