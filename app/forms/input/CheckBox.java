package forms.input;

import forms.handlers.BooleanHandler;

/**
 * The checkbox field abstraction.
 */
public class CheckBox extends BasicField {
    public boolean checked;

    public CheckBox() {
        fieldHandler = new BooleanHandler();
    }

    public void setValue(Object obj) {
        checked = true;
    }

    @Override
    public Object value() {
        return fieldHandler.transform(checked);
    }
}
