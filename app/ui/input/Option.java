package ui.input;

/**
 * Represents an option as part of a radio group field
 */
public class Option {
    public String name;
    public String label;
    public String value;

    @Override
    public String toString() {
        return value;
    }
}
