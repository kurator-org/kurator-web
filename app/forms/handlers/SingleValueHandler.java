package forms.handlers;

import java.util.List;

/**
 * Handles a single value and returns a string. Treats multiple values as single value lists.
 */
public class SingleValueHandler implements FieldHandler<String> {
    @Override
    public String transform(Object obj) {
        if (obj instanceof List) {
            return ((List<String>) obj).get(0).toString(); // For single value list
        } else if (obj instanceof String[]){
            return ((String[])obj)[0];
        } else {
            return obj.toString();
        }
    }
}
