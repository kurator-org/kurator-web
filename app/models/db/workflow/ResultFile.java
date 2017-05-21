package models.db.workflow;


import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A single artifact produced by a workflow run.
 */
@Entity
public class ResultFile extends Model {
    public static Finder<Long, ResultFile> find = new Finder<Long,ResultFile>(ResultFile.class);

    @Id
    private Long id;

    private String label;
    private String fileName;
    private String description;
    private String name;

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
