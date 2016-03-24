package forms.handlers;

/**
 * Created by lowery on 3/24/16.
 */
public class BooleanHandler implements FieldHandler<Boolean> {

    @Override
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
