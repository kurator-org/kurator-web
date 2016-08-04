package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 8/4/16.
 */
public class KuratorConfig {
    private final Config config;

    protected KuratorConfig(Config config) {
        this.config = config;
    }

    public Workflow getWorkflow(String name) {
        return new Workflow(name, config.getConfig(name));
    }
}
