package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

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

    public boolean active;

    public static void main(String[] args) {}

    public static User authenticate(String username, String password) {
        User user = User.find.where().eq("username", username).findUnique();

        if (user != null && user.active && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
    }

    public static Finder<Long, User> find = new Finder<Long,User>(User.class);

    public static List<User> findNonAdminUsers() {
        List<User> users = User.find.where().ne("id", 1).findList();
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (firstname != null ? !firstname.equals(user.firstname) : user.firstname != null) return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return affiliation != null ? affiliation.equals(user.affiliation) : user.affiliation == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        return result;
    }
}
