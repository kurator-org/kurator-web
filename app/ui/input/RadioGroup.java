package ui.input;

import ui.handlers.SingleValueHandler;

import java.util.Map;

/**
 * Radio group form field
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
