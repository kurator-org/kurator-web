package util;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lowery on 6/7/17.
 */
public class WorkflowArtifact {

    private String path;
    private String name;
    private String type;

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
