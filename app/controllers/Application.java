package controllers;

import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.*;
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

            Map<String, Object> settings = new HashMap<String,Object>();

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();
            return ok(new String(buffer.toByteArray()));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public static Result result() {
        File file = Play.application().getFile("hello_out.csv");
        return ok(file);
    }

    /** 
     * Run worms workflow.
     */
    public static Result runworms() {
       InputStream yamlStream = Play.application().classloader().getResourceAsStream("hello_worms.yaml");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(buffer);
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);

            Map<String, Object> settings = new HashMap<String,Object>();

            runner.apply(settings)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();
            return ok(new String(buffer.toByteArray()));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
    public static Result resultworms() {
        File file = Play.application().getFile("worms_out.csv");
        return ok(file);
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
