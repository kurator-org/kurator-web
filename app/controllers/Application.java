package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Task;
import models.User;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import play.*;
import play.libs.Json;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.data.validation.Constraints.*;

import java.io.*;
import java.util.Date;
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
                        controllers.routes.javascript.Workflow.runhello(),
                        controllers.routes.javascript.Workflow.runworms()
                )
        );
    }

    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }

    public static Result createaccount() {
        return ok(
                createaccount.render(form(Register.class))
        );
    }

    public static Result register() {
        Form<Register> registerForm = form(Register.class).bindFromRequest();

        if(registerForm.hasErrors()) {
            return badRequest(createaccount.render(registerForm));
        }

        User user = new User();
        user.username = registerForm.get().username;
        user.firstname = registerForm.get().firstName;
        user.lastname = registerForm.get().lastName;
        user.email = registerForm.get().email;
        user.password = registerForm.get().password;
        user.affiliation = registerForm.get().affiliation;
        user.save();

        return redirect(
                routes.Application.index()
        );
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().username);
            return redirect(
                    routes.Application.index()
            );
        }
    }

    public static Result logout() {
        session().clear();

        return redirect(
                routes.Application.index()
        );
    }

    // -- Actions
  
    /**
     * Home page
     */
    public static Result index() {
        return ok(
            index.render()
        );
    }

    public static class Login {

        public String username;
        public String password;

        public String validate() {
            if (User.authenticate(username, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static class Register {

        public String email;
        public String firstName;
        public String lastName;
        public String username;
        public String password;
        public String confirmPassword;
        public String affiliation;

        public String validate() {
            if (!password.equals(confirmPassword)) {
                return "Passwords do not match";
            }
            return null;
        }
    }
}
