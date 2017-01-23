package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.EnumMapping;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Web application user
 */
@Entity
public class User extends Model {
    private static final int PASSWORD_LENGTH = 16; // Generated password length

    public static String ROLE_ADMIN = "ADMIN";
    public static String ROLE_USER = "USER";
    public static String ROLE_GUEST = "GUEST";

    @Id
    public Long id;

    public String firstname;
    public String lastname;

    public String email;

    public String username;
    public String password;

    public String affiliation;

    @NotNull
    public String role;

    public boolean active;

    public static User authenticate(String username, String password) {
        User user = User.find.where().eq("username", username).findUnique();
        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
    }

    public static Finder<Long, User> find = new Finder<Long,User>(User.class);

    public static List<User> findNonAdminUsers() {
        List<User> users = User.find.where().ne("id", 0).findList();
        return users;
    }

    public static String generatePassword() {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
        char[] symbols = tmp.toString().toCharArray();

       Random random = new Random();

        char[] buf = new char[PASSWORD_LENGTH];

        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];

        return new String(buf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (active != user.active) return false;
        if (!id.equals(user.id)) return false;
        if (firstname != null ? !firstname.equals(user.firstname) : user.firstname != null) return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (!username.equals(user.username)) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return affiliation != null ? affiliation.equals(user.affiliation) : user.affiliation == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + username.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
