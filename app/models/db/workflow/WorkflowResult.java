package models.db.workflow;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The workflow result and result metadata.
 */
@Entity
public class WorkflowResult extends Model {
    public static Finder<Long, WorkflowResult> find = new Finder<Long,WorkflowResult>(WorkflowResult.class);

    @Id
    private Long id;

    @Lob
    private String errorText;

    @Lob
    private String outputText;

    @OneToMany(cascade=CascadeType.ALL)
    private List<ResultFile> resultFiles = new ArrayList<>();

    private String dqReport;

    private String archivePath;

    public Long getId() {
        return id;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public List<ResultFile> getResultFiles() {
        return resultFiles;
    }

    public void setResultFiles(List<ResultFile> resultFiles) {
        this.resultFiles = resultFiles;
    }

    public String getDqReport() {
        return dqReport;
    }

    public void setDqReport(String dqReport) {
        this.dqReport = dqReport;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }
}
