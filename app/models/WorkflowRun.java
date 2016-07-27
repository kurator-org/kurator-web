package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

/**
 * The model object that represents a single run of a workflow.
 */
@Entity
public class WorkflowRun extends Model {
    @Id
    public Long id;

    @ManyToOne
    public Workflow workflow;
    public Date startTime;
    public Date endTime;

    @OneToOne(cascade=CascadeType.ALL)
    public WorkflowResult result;

    @ManyToOne
    public User user;

    public static Finder<Long, WorkflowRun> find = new Finder<Long,WorkflowRun>(WorkflowRun.class);
}
