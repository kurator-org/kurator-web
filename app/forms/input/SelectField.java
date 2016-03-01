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
    public List<Option> options;
    public Map<String, Option> optionsMap = new HashMap<>();

    public List<Option> value = new ArrayList<>();

    public SelectField(String name, String label, List<Option> options, boolean multiple) {
        this.name = name;
        this.label = label;
        this.options = options;

        for (Option option : options) {
            this.optionsMap.put(option.name, option);
        }

        this.multiple = multiple;
    }

    @Override
    public void setValue(Object obj) {
        String[] values = ((String[]) obj);

        for(String val : values) {
            value.add(optionsMap.get(val));
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
