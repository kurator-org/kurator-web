package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.File;

/**
 * Created by lowery on 2/25/16.
 */
@Entity
public class WorkflowResult extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // TODO: add hasErrors boolean and check after running to display error to the user

    public String errorText;
    public String outputText;

    public String resultFile;

    public String getResultFileName() {
        if (resultFile != null) {
            File file = new File(resultFile);
            return file.getName();
        }

        return null;
    }

}
