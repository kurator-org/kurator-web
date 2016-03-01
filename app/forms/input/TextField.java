package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public class TextField extends BasicField {
    public boolean isTextArea;

    public String value;

    public TextField(String name, String label, String defaultValue, boolean isTextArea) {
        this.name = name;
        this.label = label;
        this.value = defaultValue;
        this.isTextArea = isTextArea;
    }

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