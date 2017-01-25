package config;

import com.typesafe.config.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by lowery on 8/4/16.
 */
public class WorkflowConfig {
    private final Config config;
    private final String name;
    private File workflowsDir;

    protected WorkflowConfig(File workflowsDir, String name, Config config) {
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

    public String getWorkspace() {
        return config.getString("workspace");
    }

    public String getWorkflows() {
        return config.getString("workflows");
    }

    public Collection<Artifact> getArtifacts() {
        List<Artifact> artifacts = new ArrayList<>();

        for (String name : config.getObject("artifacts").keySet()) {
            artifacts.add(new Artifact(name, config.getConfig("artifacts").getConfig(name)));
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
