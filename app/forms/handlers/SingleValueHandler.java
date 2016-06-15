package forms.handlers;

import java.util.List;

/**
 * Created by lowery on 3/16/16.
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
