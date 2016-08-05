package config;

import com.typesafe.config.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by lowery on 8/4/16.
 */
public class ParameterConfig {
    private final Config config;

    private String name;
    private String description;
    private String label;
    private String type;

    private String value;
    private String options;

    protected ParameterConfig(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public boolean isTyped() {
        return config.hasPath("type");
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

    public String getValue() {
        return config.getString("value");
    }

    public Map<String, Object> getOptions() {
        return Collections.unmodifiableMap(config.getObject("options").unwrapped());
    }
}
