package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.FormDefinition;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.App;
import views.html.*;

/**
 * The main application controller
 */
public class Application extends Controller {

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        List<FormDefinition> workflows = Workflows.loadWorkflowFormDefinitions();

        String uid = session().get("uid");
        List<UserUpload> userUploads = UserUpload.find.where().eq("user.id", uid).findList();

        return ok(
                index.render(workflows, userUploads)
        );
    }

    /**
     * The page for the workflow builder tool.
     */
    @Security.Authenticated(Secured.class)
    public static Result builder() {
        return ok(
                builder.render()
        );
    }

    /**
     * The login form page.
     */
    public static Result login() {
        return ok(
                login.render(form(Login.class))
        );
    }

    /**
     * The logout action.
     */
    @Security.Authenticated(Secured.class)
    public static Result logout() {
        session().clear();
        return redirect(
                routes.Application.index()
        );
    }

    /**
     * Action to process the login form submission and authenticate the user.
     */
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

    /**
     * Display the register a new user form page.
     */
    public static Result register() {
        return ok(
                register.render(form(Register.class))
        );
    }

    /**
     * Action processes the submission of the new user registration form and creates a deactivated user that admin can
     * activate.
     */
    public static Result registerSubmit() {
        Form<Register> registerForm = form(Register.class).bindFromRequest();

        if(registerForm.hasErrors()) {
            return badRequest(register.render(registerForm));
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

    /**
     * The user management and admin page.
     */
    public static Result admin() {
        return ok(
                admin.render(form(ChangePass.class), User.findNonAdminUsers())
        );
    }

    /**
     * Process the data submitted on the user activation form (user administration page) and activate or
     * deactivate the user accounts specified.
     */
    public static Result activateAccount() {
        DynamicForm form = form().bindFromRequest();

        List<User> users = User.findNonAdminUsers();

        for (User user : users) {
            if (form.data().containsValue(String.valueOf(user.id))) {
                user.active = true;
            } else {
                user.active = false;
            }

            user.save();
        }

        return redirect(
                routes.Application.admin()
        );
    }

    /**
     * Action to process the change password form submission for the currently logged in user.
     */
    public static Result changePassword() {
        Form<ChangePass> changePassForm = form(ChangePass.class).bindFromRequest();
        if (changePassForm.hasErrors()) {
            return badRequest(admin.render(changePassForm, User.findNonAdminUsers()));
        }

        User user = Application.getCurrentUser();
        user.password = BCrypt.hashpw(changePassForm.get().password, BCrypt.gensalt());
        user.save();

        return redirect(
                routes.Application.admin()
        );
    }

    /**
     * The login form object.
     */
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



    public static User getCurrentUser() {
        String uid = session().get("uid");
        return User.find.byId(Long.parseLong(uid));
    }

    public static Long getCurrentUserId() {
        return Long.valueOf(session().get("uid"));
    }

    /**
     * The register form object
     */
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

    /**
     * The change password form object (user administration page)
     */
    public static class ChangePass {
        public String oldPassword;
        public String password;
        public String confirmPassword;

        public String validate() {
            User user = User.authenticate(session().get("username"), oldPassword);
            if (user == null) {
                return "Current password is invalid.";
            }

            if (!password.equals(confirmPassword)) {
                return "Passwords do not match";
            }
            return null;
        }
    }
}
