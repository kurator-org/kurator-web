package ui.input;

import ui.handlers.SingleValueHandler;

import java.util.Map;

/**
 * A selection list field abstraction
 */
public class SelectField extends BasicField {
    public boolean multiple;
    public Map<String, Object> options;

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
