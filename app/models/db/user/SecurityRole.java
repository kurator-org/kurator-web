package models.db.user;

import be.objectify.deadbolt.java.models.Role;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by lowery on 1/24/2017.
 */
@Entity
public class SecurityRole extends Model implements Role {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    @Id
    private Long id;

    private String name;

    public static final Finder<Long, SecurityRole> find = new Finder<>(Long.class,
            SecurityRole.class);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SecurityRole findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }
}