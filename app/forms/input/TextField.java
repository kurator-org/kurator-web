package forms.input;

import forms.handlers.SingleValueHandler;

/**
 * Created by lowery on 2/29/2016.
 */
public class TextField extends BasicField {
    public boolean textArea;

    public String value;

    public TextField() {
        fieldHandler = new SingleValueHandler();
    }

    @Override
    public void setValue(Object obj) {
        this.value = ((String[]) obj)[0];
    }

    @Override
    public Object value() {
        return fieldHandler.transform(value);
    }
}