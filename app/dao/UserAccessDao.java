package dao;

import models.db.user.Group;
import com.avaje.ebean.annotation.Transactional;
import models.db.user.SharedAccess;
import models.db.user.User;
import models.db.workflow.WorkflowRun;

import java.util.List;

public class UserAccessDao {

    @Transactional
    public SharedAccess createSharedAccess(WorkflowRun resource, List<User> users, List<Group> groups) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public SharedAccess createGroup(User owner, Group group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public User addUserToGroup(User user, Group group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public User removeUserFromGroup(User user, Group group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<User> findUsersByGroup(String role) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<Group> findAllGroups() {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<Group> findGroupsByOwner(User owner) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<WorkflowRun> findSharedRunsByUser() {
        // TODO: implement me
        return null;
    }

}
