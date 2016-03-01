package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public abstract class BasicField {
    public String name;
    public String label;

    public abstract void setValue(Object obj);
    public abstract String getValue();
}
