package forms;

import forms.input.BasicField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class FormDefinition {
    public List<BasicField> fields = new ArrayList<>();
    public String title;
    public String name;
    public String outputFormat;

    public String yamlFile;

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
