package models.json;

import config.WorkflowConfig;
import forms.input.BasicField;
import models.db.workflow.Workflow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The form definition object contains a list of form fields and their values. It also contains metadata about the
 * workflow for display on the form.
 */
public class WorkflowDefinition implements Comparable<WorkflowDefinition> {
    // TODO: this morphed into a workflow definition, should refactor
    private List<BasicField> fields = new ArrayList<>();
    private String title;
    private String name;
    private String documentation;  // A link to documentation for this workflow
    private String yamlFile;
    private String instructions;
    private String summary;

    public WorkflowDefinition(WorkflowConfig workflow) {
        this.name = workflow.getName();
        this.title = workflow.getTitle();
        this.yamlFile = workflow.getYaml();
        this.documentation = workflow.getDocumentation();
        this.instructions = workflow.getInstructions();
        this.summary = workflow.getSummary();
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

    @Override
    public int compareTo(@NotNull WorkflowDefinition o) {
        return title.compareTo(o.title);
    }
}
