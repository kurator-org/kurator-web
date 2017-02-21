package dao;

import com.avaje.ebean.annotation.Transactional;
import models.db.user.SecurityRole;
import models.db.user.User;
import models.db.user.UserUpload;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by lowery on 1/24/2017.
 */
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
    public UserUpload createUserUpload(String username, String filename, String absolutePath) {
        User user = findUserByUsername(username);

        UserUpload uploadFile = new UserUpload();

        uploadFile.setUser(user);
        uploadFile.setFileName(filename);
        uploadFile.setAbsolutePath(absolutePath);

        uploadFile.save();

        return uploadFile;
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
        return User.find.all();
    }

    public UserUpload findUserUploadById(long id) {
        return UserUpload.find.byId(id);
    }

    public List<UserUpload> findUserUploads(String username) {
        return UserUpload.find.where().eq("user.username", username).findList();
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
    public void updateUserAccess(String username, boolean active, String role) {
        User user = findUserByUsername(username);
        user.setActive(active);
        user.setRoles(Collections.singletonList(SecurityRole.findByName(role)));
        user.save();
    }
}
