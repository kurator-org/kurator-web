package ui.input;

import ui.handlers.FieldHandler;

/**
 * Abstract super class for a generic form field.
 */
public abstract class BasicField<T> {
    public FieldHandler fieldHandler;
    public String name;
    public String label;
    public String tooltip;

    public void setFieldHandler(FieldHandler fieldHandler) {
        this.fieldHandler = fieldHandler;
    }

    public abstract void setValue(T value);
    public abstract Object value();
}
