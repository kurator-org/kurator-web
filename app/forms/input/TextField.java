package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public class TextField extends BasicField {
    public boolean textArea;

    public String value;

    @Override
    public void setValue(Object obj) {
        this.value = ((String[]) obj)[0];
    }

    @Override
    public Object getValue() {
        return fieldHandler.transform(value);
    }
}