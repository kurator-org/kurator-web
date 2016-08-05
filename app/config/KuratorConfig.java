package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import java.util.*;

/**
 * Created by lowery on 8/4/16.
 */
public class KuratorConfig {
    private final Config config;

    protected KuratorConfig(Config config) {
        this.config = config;
    }

    public WorkflowConfig getWorkflow(String name) {
        return new WorkflowConfig(name, config.getConfig(name));
    }

    public Collection<WorkflowConfig> getAllWorkflows() {
        List<WorkflowConfig> workflows = new ArrayList<>();

        for (String name : config.root().keySet()) {
            workflows.add(new WorkflowConfig(name, config.getConfig(name)));
        }

        return Collections.unmodifiableCollection(workflows);
    }
}
