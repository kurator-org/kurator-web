package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public class CheckBox extends BasicField {
    public boolean checked;

    public CheckBox(String name, String label) {
        this.name = name;
        this.label = label;
    }

    @Override
    public void setValue(Object obj) {
        checked = true;
    }

    @Override
    public String getValue() {
        return Boolean.toString(checked);
    }

    @Override
    public String toString() {
        return "CheckBox{" +
                "name='" + name + '\'' +
                ", checked=" + checked +
                '}';
    }
}
