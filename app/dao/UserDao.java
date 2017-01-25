package dao;

import com.avaje.ebean.annotation.Transactional;
import models.db.user.User;
import models.db.user.UserUpload;
import models.db.user.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.List;

/**
 * Created by lowery on 1/24/2017.
 */
public class UserDao {

    private static final Role DEFAULT_ROLE = Role.USER; // Default role for new user

    @Transactional
    public User createUser(String username, String firstName, String lastName, String email, String password,
                           String affiliation) {
        User user = new User();

        user.setUsername(username);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole(DEFAULT_ROLE);
        user.setAffiliation(affiliation);
        user.setCreatedOn(new Date());

        user.save();
        return user;
    }

    public List<User> findByRole(Role role) {
        return User.find.where().eq("role", role).findList();
    }

    public User findByUsername(String username) {
        return User.find.where().eq("username", username).findUnique();
    }

    public User findByEmail(String email) {
        return User.find.where().eq("email", email).findUnique();
    }

    public List<User> findAll() {
        return User.find.all();
    }

    public List<UserUpload> findUserUploads(String username) {
        return UserUpload.find.where().eq("user.username", username).findList();
    }

    @Transactional
    public void updatePassword(String username, String password) {
        User user = findByUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.save();
    }

    @Transactional
    public void updateActiveStatus(String username, boolean active) {
        User user = findByUsername(username);
        user.setActive(active);
        user.save();
    }

    @Transactional
    public void updateLastActive(String username) {
        User user = findByUsername(username);
        user.setLastActive(new Date());
        user.save();
    }
}
