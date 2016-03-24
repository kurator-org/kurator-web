package forms.input;

import forms.handlers.FieldHandler;

/**
 * Created by lowery on 2/29/2016.
 */
public abstract class BasicField<T> {
    public FieldHandler fieldHandler;
    public String name;
    public String label;

    public void setFieldHandler(FieldHandler fieldHandler) {
        this.fieldHandler = fieldHandler;
    }

    public abstract void setValue(T value);
    public abstract Object getValue();
}
