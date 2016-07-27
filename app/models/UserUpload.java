package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * File uploaded by a user
 */
@Entity
public class UserUpload extends Model {
    @Id
    public Long id;

    public String absolutePath;
    public String fileName;

    @ManyToOne
    public User user;

    public static Finder<Long, UserUpload> find = new Finder<Long,UserUpload>(UserUpload.class);

    public static List<UserUpload> findUploadsByUser(User user) {
        return find.where().eq("user.id", user.id).findList();
    }

    public static List<UserUpload> findUploadsByUserId(Long uid) {
        return find.where().eq("user.id", uid).findList();
    }
}
