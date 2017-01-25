package models.db.workflow;

import com.avaje.ebean.Model;
import models.db.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * The model object that represents a single run of a workflow.
 */
@Entity
public class WorkflowRun extends Model {
    public static Finder<Long, WorkflowRun> find = new Finder<Long,WorkflowRun>(WorkflowRun.class);

    @Id
    private Long id;

    @ManyToOne
    private Workflow workflow;

    private Date startTime;
    private Date endTime;

    private Status status;

    @OneToOne(cascade=CascadeType.ALL)
    private WorkflowResult result;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public WorkflowResult getResult() {
        return result;
    }

    public void setResult(WorkflowResult result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
