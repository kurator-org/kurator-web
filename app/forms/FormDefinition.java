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
    public Map<String, BasicField> fields = new HashMap<>();
    public String title;
    public String name;

    public void addField(BasicField field) {
        fields.put(field.name, field);
    }

    public BasicField getField(String name) {
        return fields.get(name);
    }
}
