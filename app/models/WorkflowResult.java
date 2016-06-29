package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The workflow result and result metadata.
 */
@Entity
public class WorkflowResult extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // TODO: add hasErrors boolean and check after running to display error to the user

    public String errorText = "";
    public String outputText = "";

    @OneToMany(cascade=CascadeType.ALL)
    public List<ResultFile> resultFiles = new ArrayList<>();

    public String archivePath;

    /*public String getResultFileName() {
        if (resultFile != null) {
            File file = new File(resultFile);
            return file.getName();
        }

        return null;
    }*/

}
