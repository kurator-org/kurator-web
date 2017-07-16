package dao;

import be.objectify.deadbolt.java.actions.Group;
import com.avaje.ebean.Expr;
import models.db.user.UserGroup;
import com.avaje.ebean.annotation.Transactional;
import models.db.user.User;
import models.db.workflow.WorkflowRun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserAccessDao {

    @Transactional
    public WorkflowRun createSharedAccess(WorkflowRun resource, List<Long> userIds, List<Long> groupIds) {
        // Get groups and users by ids
        List<UserGroup> groups = UserGroup.find.where().idIn(groupIds).findList();
        List<User> users = User.find.where().idIn(userIds).findList();

        resource.setGroups(groups);
        resource.setUsers(users);
        resource.save();

        return resource;
    }

    @Transactional
    public UserGroup createGroup(User owner, String name) {
        UserGroup group = new UserGroup();
        group.setCreatedOn(new Date());
        group.setName(name);
        group.setOwner(owner);

        group.save();

        return group;
    }

    @Transactional
    public User addUserToGroup(Long userId, Long groupId) {
        UserGroup group = UserGroup.find.byId(groupId);
        User user = User.find.byId(userId);

        boolean found = false;
        for (UserGroup g : user.getGroups()) {
            if (g.getName().equals(group.getName())) {
                found = true;
            }
        }

        if (!found) {
            user.getGroups().add(group);
            user.update();
        }

        return user;
    }

    @Transactional
    public User removeUserFromGroup(User user, UserGroup group) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<User> findUsersByGroup(String group) {
        return User.find.where().eq("groups.name", group).findList();
    }

    @Transactional
    public List<UserGroup> findAllGroups() {
        return UserGroup.find.all();
    }

    @Transactional
    public List<UserGroup> findGroupsByOwner(User owner) {
        // TODO: implement me
        return null;
    }

    @Transactional
    public List<WorkflowRun> findSharedRunsByUser(Long uid) {
        User user = User.find.byId(uid);

        // Get a list of the user's groups
        List<Long> groupIds = new ArrayList<Long>();
        for (UserGroup group : user.getGroups()) {
            groupIds.add(group.getId());
        }

        List<WorkflowRun> shareList = WorkflowRun.find.where().disjunction().add(Expr.eq("users.id", uid)).add(Expr.in("groups.id", groupIds)).findList();

        return shareList;
    }

    public UserGroup findGroupById(Long groupId) {
        return UserGroup.find.byId(groupId);
    }
}
