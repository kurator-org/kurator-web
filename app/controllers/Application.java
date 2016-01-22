package controllers;

import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.data.validation.Constraints.*;

import java.io.*;
import views.html.*;

public class Application extends Controller {
    
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
     * Run workflow
     */
    public static Result workflow() {
        InputStream yamlStream = Play.application().classloader().getResourceAsStream("hello_file.yaml");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream outStream = new PrintStream(new ByteArrayOutputStream());
            WorkflowRunner runner = new YamlStreamWorkflowRunner(yamlStream);
            runner.apply(null)
                    .outputStream(outStream)
                    .errorStream(System.out)
                    .run();
            return ok(
                    workflow.render(new String(buffer.toByteArray()))
            );
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
