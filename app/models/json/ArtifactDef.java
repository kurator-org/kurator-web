package models.json;

/**
 * Created by lowery on 5/8/17.
 */
public class ArtifactDef {
    private String name;
    private String description;
    private String label;
    private String type;

    public ArtifactDef(String name, String description, String label, String type) {
        this.name = name;
        this.description = description;
        this.label = label;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }
}
