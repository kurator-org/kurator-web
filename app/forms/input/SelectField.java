package forms.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class SelectField extends BasicField {
    public boolean multiple;
    public Map<String, String> options;

    public List<String> value;

    @Override
    public void setValue(Object obj) {
        String[] values = ((String[]) obj);

        for(String val : values) {
           this.value.add(val);
        }
    }

    @Override
    public String getValue() {
        // TODO: add support for return of a list of values
        return value.get(0).toString();
    }

    @Override
    public String toString() {
        return "SelectField{" +
                "value=" + value +
                '}';
    }
}
