package forms.input;

import forms.handlers.SingleValueHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class RadioGroup extends BasicField {
    public Map<String, String> options;

    public String selected;

    public RadioGroup() {
        fieldHandler = new SingleValueHandler();
    }

    @Override
    public void setValue(Object obj) {
        selected = ((String[]) obj)[0];
    }

    @Override
    public Object value() {
        return fieldHandler.transform(selected);
    }
}
