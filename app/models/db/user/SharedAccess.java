package models.db.user;

import com.avaje.ebean.Model;
import models.db.workflow.WorkflowResult;
import models.db.workflow.WorkflowRun;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

public class SharedAccess extends Model {
    public static Finder<Long, SharedAccess> find = new Finder<Long,SharedAccess>(SharedAccess.class);

    @Id
    private Long id;

    @ManyToMany
    private List<Group> groups;

    @ManyToMany
    private List<User> users;

    private WorkflowRun resource;

    public Long getId() {
        return id;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public WorkflowRun getResource() {
        return resource;
    }

    public void setResource(WorkflowRun resource) {
        this.resource = resource;
    }
}
