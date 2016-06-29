package forms.input;

import forms.handlers.SingleValueHandler;

/**
 * Textbox field
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