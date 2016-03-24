package forms.input;

import forms.handlers.BooleanHandler;

/**
 * Created by lowery on 2/29/2016.
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
