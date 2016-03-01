package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by lowery on 2/25/16.
 */
@Entity
public class UserUpload extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String absolutePath;
    public String fileName;

    @ManyToOne
    public User user;

    public static Finder<Long, UserUpload> find = new Finder<Long,UserUpload>(UserUpload.class);

    public static List<UserUpload> findUploadsByUser(User user) {
        return find.where().eq("user.id", user.id).findList();
    }
}
