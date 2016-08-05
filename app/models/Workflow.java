package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The workflow metadata
 */
@Entity
public class Workflow extends Model {
    @Id
    public Long id;

    public String name;
    public String title;

    public String yamlFile;

    public static Finder<Long, Workflow> find = new Finder<Long,Workflow>(Workflow.class);
}
