package config;

import com.typesafe.config.Config;

/**
 * Created by lowery on 8/4/16.
 */
public class Artifact {
    private final Config config;

    private String name;
    private String description;
    private String label;
    private String type;

    protected Artifact(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return config.getString("description");
    }

    public String getLabel() {
        return config.getString("label");
    }

    public String getType() {
        return config.getString("type");
    }
}
