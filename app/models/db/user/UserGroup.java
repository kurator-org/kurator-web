package models.db.user;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import models.db.workflow.WorkflowRun;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Entity
public class UserGroup extends Model {
    public static Finder<Long, UserGroup> find = new Finder<Long,UserGroup>(UserGroup.class);

    @Id
    private Long id;

    private String name;
    private User owner;

    private Date createdOn;

    @ManyToMany
    @JsonIgnore
    public List<WorkflowRun> workflowRuns;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
