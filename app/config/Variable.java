package config;

/**
 * Created by lowery on 7/4/17.
 */
public class Variable {
    private final String name;
    private final String description;
    private final String actor;
    private final String parameter;
    private final String type;

    public Variable(String name, String description, String actor, String parameter, String type) {
        this.name = name;
        this.description = description;
        this.actor = actor;
        this.parameter = parameter;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getActor() {
        return actor;
    }

    public String getParameter() {
        return parameter;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", actor='" + actor + '\'' +
                ", parameter='" + parameter + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
