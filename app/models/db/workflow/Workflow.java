package models.db.workflow;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The workflow metadata
 */
@Entity
public class Workflow extends Model {
    public static Finder<Long, Workflow> find = new Finder<Long,Workflow>(Workflow.class);

    @Id
    private Long id;

    private String name;
    private String title;

    private String yamlFile;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYamlFile() {
        return yamlFile;
    }

    public void setYamlFile(String yamlFile) {
        this.yamlFile = yamlFile;
    }
}
