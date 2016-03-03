package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

/**
 * Created by lowery on 2/11/16.
 */
@Entity
public class WorkflowRun extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String workflow;
    public Date startTime;
    public Date endTime;

    @OneToOne(cascade=CascadeType.ALL)
    public WorkflowResult result;

    @ManyToOne
    public User user;

    public static Finder<Long, WorkflowRun> find = new Finder<Long,WorkflowRun>(WorkflowRun.class);
}