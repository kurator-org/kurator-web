package forms.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class RadioGroup extends BasicField {
    public List<Option> options;
    public Map<String, Option> optionsMap = new HashMap<>();

    public Option value;

    public RadioGroup(String name, String label, List<Option> options) {
        this.name = name;
        this.label = label;
        this.options = options;

        for (Option option : options) {
            this.optionsMap.put(option.name, option);
        }
    }

    @Override
    public void setValue(Object obj) {
        String val = ((String[]) obj)[0];
        this.value = optionsMap.get(val);
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
