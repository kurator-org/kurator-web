package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

/**
 * Created by lowery on 2/11/16.
 */
@Entity
public class WorkflowRun extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String workflow;
    public Date startTime;
    public Date endTime;

    public String outputText;
    public String resultFile;

    public String getResultFileName() {
        if (resultFile != null) {
            File file = new File(resultFile);
            return file.getName();
        }

        return null;
    }

    @ManyToOne
    public User user;

    public static Finder<Long, WorkflowRun> find = new Finder<Long,WorkflowRun>(WorkflowRun.class);

    @Override
    public String toString() {
        return "WorkflowRun{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", outputText='" + outputText + '\'' +
                ", resultFile='" + resultFile + '\'' +
                ", user=" + user +
                '}';
    }
}
