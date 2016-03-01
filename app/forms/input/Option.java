package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public class Option {
    public String name;
    public String label;
    public String value;

    public Option(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Option(String name, String value, String label) {
        this(name, value);
        this.label = label;
    }

    @Override
    public String toString() {
        return value;
    }
}
