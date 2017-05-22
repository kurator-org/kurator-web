/** WorkflowResult.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
