package ui.handlers;

/**
 * The interface for a field handler object.
 */
public interface FieldHandler<T> {
    public T transform(Object obj);
}
