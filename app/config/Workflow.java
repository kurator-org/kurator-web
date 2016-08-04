package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import org.geotools.resources.UnmodifiableArrayList;

import java.util.*;

/**
 * Created by lowery on 8/4/16.
 */
public class Workflow {
    private final Config config;
    private final String name;

    protected Workflow(String name, Config config) {
        this.config = config;
        this.name = name;
    }

    public String getYaml() {
        return config.getString("yaml");
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

    public Collection<Artifact> getArtifacts() {
        List<Artifact> artifacts = new ArrayList<>();

        for (String name : config.getObject("artifacts").unwrapped().keySet()) {
            artifacts.add(new Artifact(name, config.atKey(name)));
        }

        return Collections.unmodifiableCollection(artifacts);
    }

    public Collection<Parameter> getParameters() {
        List<Parameter> parameters = new ArrayList<>();

        for (String name : config.getObject("parameters").unwrapped().keySet()) {
            parameters.add(new Parameter(name, config.atKey(name)));
        }

        return Collections.unmodifiableCollection(parameters);
    }
}
