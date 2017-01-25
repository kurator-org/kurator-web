package models.db.workflow;


import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A single artifact produced by a workflow run.
 */
@Entity
public class ResultFile extends Model {
    @Id
    private Long id;

    private String label;
    private String fileName;

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
}
