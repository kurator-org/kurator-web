package models.json;

/**
 * Created by lowery on 5/8/17.
 */
public class ArtifactDef {
    private String name = "";
    private String description = "";
    private String label = "";
    private String type = "";
    private String info = "";


    public ArtifactDef(String name, String description, String label, String type, String info) {
        this.name = name;
        this.description = description;
        this.label = label;
        this.type = type;
        this.info = info;
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

    public String getInfo() {
        return info;
    }
}
