package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.*;
import play.libs.Json;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.data.validation.Constraints.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import views.html.*;

public class Application extends Controller {

    /**
     * Javascript routes for jQuery ajax
     */
    public static Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes",
                        controllers.routes.javascript.Application.run(),
                        controllers.routes.javascript.Application.runworms()
                )
        );
    }

    /**
     * Describes the hello form.
     */
    public static class Hello {
        @Required public String name;
        @Required @Min(1) @Max(100) public Integer repeat;
        public String color;
    }
    
    // -- Actions
  
    /**
     * Home page
     */
    public static Result index() {
        return ok(
            index.render(form(Hello.class))
        );
    }

    /**
     * Workflow page
     */
    public static Result workflow() {
        return ok(
            workflow.render()
        );
    }

    /**
     * Worms workflow page
     */
    public static Result wormsworkflow() {
        return ok(
            wormsworkflow.render()
        );
    }

    /**
     * Run workflow
     */
    public static Result run() {
       InputStream yamlStream = Play.application().classloader().getResourceAsStream("hello_file.yaml");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(buffer);
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            File file = File.createTempFile("result-", ".csv");

            Map<String, Object> settings = new HashMap<String,Object>();
            settings.put("out", file.getAbsolutePath());

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();

            ObjectNode result = Json.newObject();

            result.put("output", new String(buffer.toByteArray()));
            result.put("filename", file.getName());

            return ok(result);

        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public static Result result(String fileName) {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        return ok(file);
    }

    /** 
     * Run worms workflow.
     */
    public static Result runworms() {
        InputStream yamlStream = Play.application().classloader().getResourceAsStream("hello_worms.yaml");
        File inFile = request().body().asRaw().asFile();

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(buffer);
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            File outFile = File.createTempFile("worms_result-", ".csv");

            Map<String, Object> settings = new HashMap<String,Object>();
            settings.put("in", inFile.getAbsolutePath());
            settings.put("out", outFile.getAbsolutePath());

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();

            ObjectNode result = Json.newObject();

            result.put("output", new String(buffer.toByteArray()));
            result.put("filename", outFile.getName());

            return ok(result);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    /**
     * Handles the form submission.
     */
    public static Result sayHello() {
        Form<Hello> form = form(Hello.class).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(index.render(form));
        } else {
            Hello data = form.get();
            return ok(
                hello.render(data.name, data.repeat, data.color)
            );
        }
    }
  
}
