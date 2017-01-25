package ui.handlers;

/**
 * Field handler for boolean values capable of transforming text to an instance of Boolean
 */
public class BooleanHandler implements FieldHandler<Boolean> {

    public Boolean transform(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof String) {
            return Boolean.parseBoolean((String) obj);
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
