package forms.input;

import forms.handlers.MultipleValueHandler;
import forms.handlers.SingleValueHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A selection list field abstraction
 */
public class SelectField extends BasicField {
    public boolean multiple;
    public Map<String, String> options;

    public String[] selected;

    public SelectField() {
        fieldHandler = new SingleValueHandler();
    }

    @Override
    public void setValue(Object obj) {
        selected = ((String[]) obj);
    }

    @Override
    public Object value() {
        return fieldHandler.transform(selected);
    }
}
