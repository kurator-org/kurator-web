package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by lowery on 2/1/2016.
 */
@Entity
public class User extends Model {
    @Id
    public Long id;

    public String firstname;
    public String lastname;

    public String username;

    public static Finder<Long, User> find = new Finder<Long,User>(User.class);
}
