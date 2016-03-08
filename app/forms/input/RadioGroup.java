package forms.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class RadioGroup extends BasicField {
    public Map<String, String> options;

    public String value = "";

    @Override
    public void setValue(Object obj) {
        String val = ((String[]) obj)[0];
        this.value = val;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    @Override
    public String toString() {
        return "RadioGroup{" +
                "value=" + value +
                '}';
    }
}
