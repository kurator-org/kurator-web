package config;

public class InputConfig {
    private final String type;
    private final String format;

    public InputConfig(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }
}
