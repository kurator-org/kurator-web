package forms;

import forms.input.BasicField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The form definition object contains a list of form fields and their values. It also contains metadata about the
 * workflow for display on the form.
 */
public class FormDefinition {
    public List<BasicField> fields = new ArrayList<>();
    public String title;
    public String name;
    public String outputFormat;
    public String documentation;  // A link to documentation for this workflow
    public String yamlFile;
    public String instructions;
    public String summary;

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
}
