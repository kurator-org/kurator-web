package api;

import models.Workflow;
import org.kurator.akka.data.WorkflowProduct;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lowery on 9/20/16.
 */
public interface WorkflowEngine {
    public void init(Workflow workflow);
    public void settings(Map<String, Object> settings);
    public void start();
    public void stop();
    public String errors();
    public String output();
    public List<WorkflowProduct> artifacts();
}
