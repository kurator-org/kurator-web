/** WorkflowDefinition.java
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
package models.json;

import config.Artifact;
import config.WorkflowAlternativeConfig;
import config.WorkflowConfig;
import ui.input.BasicField;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The form definition object contains a list of form fields and their values. It also contains metadata about the
 * workflow for display on the form.
 */
public class WorkflowDefinition implements Comparable<WorkflowDefinition> {
    private Map<String, ArtifactDef> results = new HashMap<>();
    private Map<String, ArtifactDef> other = new HashMap<>();
    private List<BasicField> fields = new ArrayList<>();
    private String title;
    private String name;
    private String documentation;  // A link to documentation for this workflow
    private String yamlFile;
    private String instructions;
    private String summary;

    public WorkflowDefinition() {
        // Default constructor
    }

    public WorkflowDefinition(String name, String title, String documentation, String summary, String instructions,
                              String yamlFile, Collection<Artifact> resultArtifacts, Collection<Artifact> otherArtifacts) {
        this.name = name;
        this.title = title;
        this.yamlFile = yamlFile;
        this.documentation = documentation;
        this.instructions = instructions;
        this.summary = summary;

        for (Artifact artifact : resultArtifacts) {
            ArtifactDef artifactDef = new ArtifactDef(artifact.getName(), artifact.getDescription(), artifact.getLabel(), artifact.getType(), artifact.getInfo());
            this.results.put(artifact.getName(), artifactDef);
        }

        for (Artifact artifact : otherArtifacts) {
            ArtifactDef artifactDef = new ArtifactDef(artifact.getName(), artifact.getDescription(), artifact.getLabel(), artifact.getType(), artifact.getInfo());
            this.other.put(artifact.getName(), artifactDef);
        }
    }

    public void addField(BasicField field) {
        fields.add(field);
    }

    public BasicField getField(String name) {
        for (BasicField field : fields) {
            if (field.name.equals(name)) {
                return field;
            }
        }

        return null;
    }

    public List<BasicField> getFields() {
        return fields;
    }

    public void setFields(List<BasicField> fields) {
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getYamlFile() {
        return yamlFile;
    }

    public void setYamlFile(String yamlFile) {
        this.yamlFile = yamlFile;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<String, ArtifactDef> getResultArtifacts() {
        return results;
    }

    public void setResultArtifacts(Map<String, ArtifactDef> artifacts) {
        this.results = artifacts;
    }


    public Map<String, ArtifactDef> getOtherArtifacts() {
        return other;
    }

    public void setOtherArtifacts(Map<String, ArtifactDef> artifacts) {
        this.other = artifacts;
    }


    @Override
    public int compareTo(@NotNull WorkflowDefinition o) {
        return title.compareTo(o.title);
    }
}
