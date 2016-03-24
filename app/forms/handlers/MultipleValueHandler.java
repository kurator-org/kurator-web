package forms.handlers;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lowery on 3/22/16.
 */
public class MultipleValueHandler implements FieldHandler<List> {

    @Override
    public List transform(Object obj) {
        if (obj instanceof List) {
            return (List) obj;
        } else if (obj instanceof Array) {
            Arrays.asList(obj);
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
        return null;
    }
}
