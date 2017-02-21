package models.db.user;

import be.objectify.deadbolt.java.models.Permission;
import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserPermission extends Model implements Permission {
    @Id
    private Long id;

    @Column(name = "permission_value")
    private String value;

    public static final Model.Finder<Long, UserPermission> find = new Model.Finder<>(Long.class,
            UserPermission.class);

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}