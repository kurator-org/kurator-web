package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.io.File;

/**
 * Created by lowery on 2/25/16.
 */
@Entity
public class UserUpload extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String absolutePath;

    @ManyToOne
    public User user;

    public String getFileName() {
        return new File(absolutePath).getName();
    }

    public static Finder<Long, UserUpload> find = new Finder<Long,UserUpload>(UserUpload.class);
}
