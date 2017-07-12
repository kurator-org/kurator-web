package dao;

import models.db.user.UserGroup;
import com.avaje.ebean.annotation.Transactional;
import models.db.user.SharedAccess;
import models.db.user.User;
import models.db.workflow.WorkflowRun;

import java.util.List;

public class UserAccessDao {

    @Transactional
    public SharedAccess createSharedAccess(WorkflowRun resource, List<User> users, List<UserGroup> groups) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public SharedAccess createGroup(User owner, UserGroup group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public User addUserToGroup(User user, UserGroup group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public User removeUserFromGroup(User user, UserGroup group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<User> findUsersByGroup(String role) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<UserGroup> findAllGroups() {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<UserGroup> findGroupsByOwner(User owner) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<WorkflowRun> findSharedRunsByUser() {
        // TODO: implement me
        return null;
    }

}
