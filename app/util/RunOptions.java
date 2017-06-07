package util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class RunOptions {
    private String yamlFile;
    private Map<String, String> parameters;
    private Map<String, String> config;
    private String logLevel;

    public RunOptions(String yamlFile, Map<String, String> parameters, Map<String, String> config, String logLevel) {
        this.yamlFile = yamlFile;
        this.parameters = parameters;
        this.config = config;
        this.logLevel = logLevel;
    }

    @JsonProperty("yaml")
    public String getYamlFile() {
        return yamlFile;
    }

    @JsonProperty("parameters")
    public Map<String, String> getParameters() {
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


}
