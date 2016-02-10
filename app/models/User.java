package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by lowery on 2/1/2016.
 */
@Entity
public class User extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String firstname;
    public String lastname;

    public String email;

    public String username;
    public String password;

    public String affiliation;

    public static User authenticate(String username, String password) {
        User user = User.find.where().eq("username", username).findUnique();
        if (user.password.equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public static Finder<Long, User> find = new Finder<Long,User>(User.class);
}
