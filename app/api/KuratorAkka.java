package api;

import models.Workflow;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import org.kurator.akka.data.WorkflowProduct;
import util.AsyncWorkflowRunnable;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by lowery on 9/20/16.
 */
public class KuratorAkka implements WorkflowEngine {
    private WorkflowRunner runner;
    private Map<String, Object> settings;
    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;

    private String errorText;
    private String outputText;
    private Workflow workflow;

    private AsyncWorkflowRunnable runnable;

    public KuratorAkka(InputStream yamlStream) throws Exception {
        runner = new YamlStreamWorkflowRunner()
                .yamlStream(yamlStream);

        outStream = new ByteArrayOutputStream();
        errStream = new ByteArrayOutputStream();

        settings = new HashMap<>();

        runnable = new AsyncWorkflowRunnable();
    }

    @Override
    public void init(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public void settings(Map<String, Object> settings) {
        this.settings.putAll(settings);
    }

    @Override
    public void start() {
        try {
            runnable.init(workflow, runner, errStream, outStream);

            runner.apply(settings)
                    .outputStream(new PrintStream(outStream))
                    .errorStream(new PrintStream(errStream))
                    .run();
        } catch (Exception e) {
            e.printStackTrace();

            // Log exceptions as part of the workflow error log
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));

            errorText = writer.toString();
            outputText = new String(outStream.toByteArray());
        }
    }

    @Override
    public void stop() {
        // TODO: Add support for stopping a running workflow
    }

    @Override
    public String errors() {
        return new String(errStream.toByteArray());
    }

    @Override
    public String output() {
        return new String(outStream.toByteArray());
    }

    @Override
    public List<WorkflowProduct> artifacts() {
        return runner.getWorkflowProducts();
    }
}
