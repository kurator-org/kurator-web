package forms.handlers;

/**
 * Created by lowery on 3/15/16.
 */
public interface FieldHandler<T> {
    public T transform(Object obj);
}
