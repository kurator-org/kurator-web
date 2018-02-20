package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.db.workflow.Status;
import models.db.workflow.WorkflowRun;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static util.RunOptions.resolve;

/**
 * Created by lowery on 7/13/17.
 */
@RunWith(Parameterized.class)
public class IntegrationTests {
    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();
    private RunOptions options;

    public IntegrationTests(RunOptions options) {
        this.options = options;

        env.set("KURATOR_JAR", "/home/lowery/IdeaProjects/kurator-validation/target/kurator-validation-1.0.1-SNAPSHOT-jar-with-dependencies.jar");
    }

    @Test
    public void runWorkflow() throws IOException, InterruptedException {
        WorkflowRunner runner = new WorkflowRunner();
        WorkflowRun workflowRun = new WorkflowRun();

        RunResult result = runner.run(options, workflowRun);
        assertEquals(Status.SUCCESS, result.getStatus());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> options() {
        List<RunOptions> options = new ArrayList<>();

        String logLevel = "DEBUG";

        Map<String, String> config = new HashMap<String, String>() {{
            put("jython_home", new File("jython").getAbsolutePath());
            put("jython_path", new File("packages").getAbsolutePath());
        }};

        String yamlFile;
        Map<String, Object> parameters;

        // Base workspaces directory for test runs
        File workspaceDir = new File("test_runs").getAbsoluteFile();

        if (!workspaceDir.exists()) {
            workspaceDir.mkdirs();
        }

        Path workspace = workspaceDir.toPath();

        /////////////////////////
        // darwinize_file.yaml //
        /////////////////////////

        options.add(new RunOptions(

                resolve("workflows/darwinize_file.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("darwinize_file_workspace").toString());
                    put("inputfile", resolve("data/tests/test_barcelona1_clean.txt"));
                    put("format", "txt");
                    put("dwcnamespace", "y");
                }},

                config, logLevel

        ));

        ////////////////////////////////////////
        // dwca_controlled_term_assessor.yaml //
        ////////////////////////////////////////
        
        options.add(new RunOptions(

                resolve("workflows/dwca_controlled_term_assessor.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("dwca_controlled_term_assessor_workspace").toString());
                    put("dwca_url", "http://ipt.vertnet.org:8080/ipt/archive.do?r=ccber_mammals");
                }},

                config, logLevel

        ));

        //////////////////////////////
        // dwca_date_validator.yaml //
        //////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/dwca_date_validator.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("dwca_date_validator_workspace").toString());
                    put("dwca_url", "http://ipt.vertnet.org:8080/ipt/archive.do?r=ccber_mammals");
                }},

                config, logLevel

        ));

        //////////////////////////////////
        // dwca_geography_assessor.yaml //
        //////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/dwca_geography_assessor.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("dwca_geography_assessor_workspace").toString());
                    put("dwca_url", "http://ipt.vertnet.org:8080/ipt/archive.do?r=ccber_mammals");
                }},

                config, logLevel

        ));

        /////////////////////////////////
        // dwca_geography_cleaner.yaml //
        /////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/dwca_geography_cleaner.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("dwca_geography_cleaner_workspace").toString());
                    put("dwca_url", "http://ipt.vertnet.org:8080/ipt/archive.do?r=ccber_mammals");
                }},

                config, logLevel

        ));

        ////////////////////////////////
        // dwca_georef_validator.yaml //
        ////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/dwca_georef_validator.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("dwca_georef_validator").toString());
                    put("dwca_url", "http://ipt.vertnet.org:8080/ipt/archive.do?r=ccber_mammals");
                }},

                config, logLevel

        ));

        ///////////////////////////
        // dwca_term_values.yaml //
        ///////////////////////////

        options.add(new RunOptions(

                resolve("workflows/dwca_term_values.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("dwca_term_values").toString());
                    put("dwca_url", "http://ipt.vertnet.org:8080/ipt/archive.do?r=ccber_mammals");
                    put("format", "txt");
                    put("fieldlist", "genus|specificepithet");
                }},

                config, logLevel

        ));

        //////////////////////////
        // file_aggregator.yaml //
        //////////////////////////

        options.add(new RunOptions(

                resolve("workflows/file_aggregator.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("file_aggregator_workspace").toString());
                    put("inputfile1", resolve("data/tests/test_aggregate_1.csv"));
                    put("inputfile2", resolve("data/tests/test_aggregate_2.csv"));
                    put("outputfile", "aggregated_file.csv");
                    put("format", "csv");
                }},

                config, logLevel

        ));

        ////////////////////////////////////////
        // file_controlled_term_assessor.yaml //
        ////////////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/file_controlled_term_assessor.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("file_controlled_term_assessor_workspace").toString());
                    put("inputfile", resolve("data/tests/test_barcelona1_clean.txt"));
                    put("format", "txt");
                }},

                config, logLevel

        ));

        //////////////////////////////
        // file_date_validator.yaml //
        //////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/file_date_validator.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("file_date_validator").toString());
                    put("inputfile", resolve("data/tests/test_mcz.txt"));
                    put("format", "tsv");
                    put("dwcnamespace", "y");
                }},

                config, logLevel

        ));

        ////////////////////////////// //
        // file_geography_cleaner.yaml //
        /////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/file_geography_cleaner.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("file_geography_cleaner_workspace").toString());
                    put("inputfile", resolve("data/tests/test_onslow_vertnet.csv"));
                    put("outputfile", "cleaneddata.csv");
                    put("format", "csv");
                }},

                config, logLevel

        ));

        ////////////////////////////////
        // file_georef_validator.yaml //
        ////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/file_georef_validator.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("file_georef_validator_workspace").toString());
                    put("inputfile", resolve("data/tests/test_mcz.txt"));
                    put("format", "tsv");
                    put("dwcnamespace", "y");
                }},

                config, logLevel

        ));

        ////////////////////////////////
        // file_term_values.yaml  //
        ////////////////////////////////

        options.add(new RunOptions(

                resolve("workflows/file_term_values.yaml"),

                new HashMap<String, Object>() {{
                    put("workspace", workspace.resolve("file_term_values_workspace").toString());
                    put("inputfile", resolve("data/tests/test_barcelona1_clean.txt"));
                    put("format", "txt");
                    put("fieldlist", "genus|specificepithet");
                }},

                config, logLevel

        ));

        Collection<Object[]> result = new ArrayList<>();
        for (RunOptions opt : options) {
            result.add(new Object[] { opt });
        }

        return result;
    }
}
