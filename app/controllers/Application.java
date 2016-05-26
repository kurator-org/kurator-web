package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.FormDefinition;
import models.Task;
import models.User;
import models.UserUpload;
import models.WorkflowRun;
import org.kurator.akka.WorkflowRunner;
import org.kurator.akka.YamlStreamWorkflowRunner;
import org.mindrot.jbcrypt.BCrypt;
import play.*;
import play.libs.Json;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.data.validation.Constraints.*;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import views.html.*;

public class Application extends Controller {

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

    public static Result manager() {
        List<User> users = User.find.all();

        return ok(manager.render(users));
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
        user.password = BCrypt.hashpw(registerForm.get().password, BCrypt.gensalt());
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
            return redirect(
                    routes.Application.index()
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result logout() {
        session().clear();
        return redirect(
                routes.Application.index()
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result builder() {
        return ok(
                builder.render()
        );
    }

    // -- Actions

    /**
     * Home page
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<FormDefinition> workflows = Workflows.loadWorkflowFormDefinitions();

        String uid = session().get("uid");
        List<WorkflowRun> workflowRuns = WorkflowRun.find.where().eq("user.id", uid).findList();
        List<UserUpload> userUploads = UserUpload.find.where().eq("user.id", uid).findList();

        return ok(
            index.render(workflows, workflowRuns, userUploads)
        );
    }

    public static User getCurrentUser() {
        String uid = session().get("uid");
        return User.find.byId(Long.parseLong(uid));
    }

    public static Long getCurrentUserId() {
        return Long.valueOf(session().get("uid"));
    }

    public static class Login {

        public String username;
        public String password;

        public String validate() {
            User user = User.authenticate(username, password);
            if (user == null) {
                return "Invalid user or password";
            }

            session().clear();
            session("uid", String.valueOf(user.id));
            session("username", user.username);

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
