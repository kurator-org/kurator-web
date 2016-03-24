package forms.input;

/**
 * Created by lowery on 2/29/2016.
 */
public class CheckBox extends BasicField {
    public boolean checked;

    public void setValue(Object obj) {
        checked = true;
    }

    @Override
    public Object getValue() {
        return fieldHandler.transform(checked);
    }
}
