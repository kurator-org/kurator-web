package forms.input;

import forms.handlers.MultipleValueHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class SelectField extends BasicField {
    public boolean multiple;
    public Map<String, String> options;

    public String[] selected;

    public SelectField() {
        fieldHandler = new MultipleValueHandler();
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
