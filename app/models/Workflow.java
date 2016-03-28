package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by lowery on 3/28/2016.
 */
@Entity
public class Workflow extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String title;
    public String outputFormat;

    public static Finder<Long, Workflow> find = new Finder<Long,Workflow>(Workflow.class);
}
