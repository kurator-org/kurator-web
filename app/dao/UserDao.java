/** UserDao.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dao;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import models.db.user.*;
import models.db.workflow.WorkflowRun;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.util.*;

public class UserDao {

    private static final String DEFAULT_ROLE = SecurityRole.USER; // Default role for new user

    @Transactional
    public User createUser(String username, String firstName, String lastName, String email, String password,
                           String affiliation) {
        User user = new User();

        user.setUsername(username);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRoles(Collections.singletonList(SecurityRole.findByName(DEFAULT_ROLE)));
        user.setAffiliation(affiliation);
        user.setCreatedOn(new Date());

        user.save();
        return user;
    }

    @Transactional
    public User updateUser(String email, String username, String firstName, String lastName, String password,
                           String affiliation) {
        User user = User.find.where().eq("email", email).findUnique();

        user.setUsername(username);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRoles(Collections.singletonList(SecurityRole.findByName(DEFAULT_ROLE)));
        user.setAffiliation(affiliation);
        user.setLastActive(new Date());

        user.update();
        return user;
    }


    @Transactional
    public User createUser(String email, String password, UserGroup group) {
        User user = new User();

        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setEmail(email);

        if (group != null) {
            user.getGroups().add(group);
        }

        user.setRoles(Collections.singletonList(SecurityRole.findByName(DEFAULT_ROLE)));
        user.setCreatedOn(new Date());
        user.setActive(true);

        user.save();
        return user;
    }

    @Transactional
    public UserUpload createUserUpload(Long uid, String filename, String absolutePath) {
        User user = User.find.byId(uid);

        UserUpload uploadFile = new UserUpload();

        uploadFile.setUser(user);
        uploadFile.setFileName(filename);
        uploadFile.setAbsolutePath(absolutePath);

        uploadFile.save();

        return uploadFile;
    }

    public UserUpload removeUserUpload(long id) {
        UserUpload userUpload = UserUpload.find.byId(id);

        File file = new File(userUpload.getAbsolutePath());

        if (file.exists()) {
            file.delete();
        }

        userUpload.delete();

        return userUpload;
    }

    public List<User> findUsersByRole(String role) {
        return User.find.where().eq("roles.name", role).findList();
    }

    public User findUserByUsername(String username) {
        return User.find.where().eq("username", username).findUnique();
    }

    public User findUserByEmail(String email) {
        return User.find.where().eq("email", email).findUnique();
    }

    public List<User> findAllUsers() {
        List<User> users = User.find.all();

        String sql = "select user.username, count(case when status = 'SUCCESS' then status end) as count_success, " +
                          "count(case when status = 'ERRORS' then status end) as count_errors, " +
                          "count(case when status = 'RUNNING' then status end) as count_running " +
                          "from workflow_run join user on workflow_run.owner_id=user.id group by username;";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        List<SqlRow> list = sqlQuery.findList();

        Map<String, RunReport> runReports = new HashMap<>();
        for (SqlRow row : list) {
            RunReport runReport = new RunReport(row.getLong("count_success"),
                    row.getLong("count_errors"),
                    row.getLong("count_running"));

            runReports.put(row.getString("username"), runReport);
        }

        for (User user : users) {
            String username = user.getUsername();
            user.setRunReport(runReports.get(username));
        }

        return users;
    }

    public UserUpload findUserUploadById(long id) {
        return UserUpload.find.byId(id);
    }

    public List<UserUpload> findUserUploads(Long uid) {
        return UserUpload.find.where().eq("user.id", uid).findList();
    }

    @Transactional
    public void updatePassword(String username, String password) {
        User user = findUserByUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.save();
    }

    @Transactional
    public void updateLastActive(String username) {
        User user = findUserByUsername(username);
        user.setLastActive(new Date());
        user.save();
    }

    @Transactional
    public void updateUser(Long id, boolean active, String role) {
        User user = User.find.byId(id);
        user.setActive(active);
        user.setRoles(Collections.singletonList(SecurityRole.findByName(role)));
        user.save();
    }


}
