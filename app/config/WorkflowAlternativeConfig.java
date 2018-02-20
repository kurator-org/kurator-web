/** WorkflowConfig.java
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
package config;

import com.typesafe.config.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WorkflowAlternativeConfig {
    private final Config config;
    private final String name;
    private File workflowsDir;

    protected WorkflowAlternativeConfig(File workflowsDir, String name, Config config) {
        this.config = config;
        this.name = name;
        this.workflowsDir = workflowsDir;
    }

    public String getYaml() {
        return workflowsDir.getAbsolutePath() + File.separator + config.getString("yaml");
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return config.getString("summary");
    }

    public String getDocumentation() {
        return config.getString("documentation");
    }

    public String getTitle() {
        return config.getString("title");
    }

    public String getInstructions() {
        return config.getString("instructions");
    }

    public InputConfig getInputConfig() {
        Config input = config.getConfig("input");
        return new InputConfig(input.getString("type"), input.getString("format"));
    }

    public Collection<Artifact> getResultArtifacts() {
        List<Artifact> artifacts = new ArrayList<>();

        for (String name : config.getConfig("artifacts").getObject("results").keySet()) {
            artifacts.add(new Artifact(name, config.getConfig("artifacts").getConfig("results").getConfig(name)));
        }

        return Collections.unmodifiableCollection(artifacts);
    }

    public Collection<Artifact> getOtherArtifacts() {
        List<Artifact> artifacts = new ArrayList<>();

        for (String name : config.getConfig("artifacts").getObject("other").keySet()) {
            artifacts.add(new Artifact(name, config.getConfig("artifacts").getConfig("other").getConfig(name)));
        }

        return Collections.unmodifiableCollection(artifacts);
    }

    public Collection<ParameterConfig> getParameters() {
        List<ParameterConfig> parameters = new ArrayList<>();

        for (String name : config.getObject("parameters").keySet()) {
            parameters.add(new ParameterConfig(name, config.getConfig("parameters").getConfig(name)));
        }

        return Collections.unmodifiableCollection(parameters);
    }
}
