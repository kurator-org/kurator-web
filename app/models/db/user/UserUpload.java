package models.db.user;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * File uploaded by a user
 */
@Entity
public class UserUpload extends Model {
    public static Finder<Long, UserUpload> find = new Finder<Long,UserUpload>(UserUpload.class);

    @Id
    private Long id;

    private String absolutePath;
    private String fileName;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
