package ui.handlers;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Supports multiple value transformation (array of values to list)
 */
public class MultipleValueHandler implements FieldHandler<List> {

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
