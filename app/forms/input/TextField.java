package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public class TextField extends BasicField {
    public boolean textArea;

    public String value;

    @Override
    public void setValue(Object obj) {
        String val = ((String[]) obj)[0];

        this.value = val;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TextField{" +
                "value='" + value + '\'' +
                '}';
    }
}