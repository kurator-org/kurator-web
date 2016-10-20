package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import forms.FormDefinition;
import models.User;
import models.UserUpload;
import org.kurator.util.SystemClasspathManager;
import org.mindrot.jbcrypt.BCrypt;
import play.api.Play;
import play.mvc.*;
import play.data.*;

import java.net.URL;
import java.util.*;

import util.ClasspathStreamHandler;
import util.ConfigurableStreamHandlerFactory;

import views.html.*;
import views.html.admin.*;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The main application controller
 */
@Singleton
public class Application extends Controller {

    private static final String DEFAULT_USER_ROLE = User.ROLE_USER;
    @Inject
    FormFactory formFactory;

    /**
     * Index page.
     */
    @Security.Authenticated(Secured.class)
    public Result index() {
        List<FormDefinition> workflows = Workflows.loadWorkflowFormDefinitions();
        Collections.sort(workflows);

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
    public Result builder() {
        return ok(
                builder.render()
        );
    }

    /**
     * The login form page.
     */
    public Result login() {
        return ok(
                login.render(formFactory.form(Login.class))
        );
    }

    /**
     * The logout action.
     */
    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();

        flash("message", "User successfully logged out.");
        return redirect(
                routes.Application.login()
        );
    }

    /**
     * Action to process the login form submission and authenticate the user.
     */
    public Result authenticate() {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();


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
    public Result register() {
        return ok(
                register.render(formFactory.form(Register.class))
        );
    }

    /**
     * Action processes the submission of the new user registration form and creates a deactivated user that admin can
     * activate.
     */
    public Result registerSubmit() {
        Form<Register> registerForm = formFactory.form(Register.class).bindFromRequest();

        if(registerForm.hasErrors()) {
            return badRequest(register.render(registerForm));
        }

        User user = new User();
        user.username = registerForm.get().username;
        user.firstname = registerForm.get().firstName;
        user.lastname = registerForm.get().lastName;
        user.email = registerForm.get().email;
        user.password = BCrypt.hashpw(registerForm.get().password, BCrypt.gensalt());
        user.role = DEFAULT_USER_ROLE;
        user.affiliation = registerForm.get().affiliation;
        user.save();

        flash("message", "New user registration successful! The admin will send an email notification when your account has been activated.");

        return redirect(
                routes.Application.login()
        );
    }

    public Result changePass() {
        return ok(
                changepw.render(formFactory.form(ChangePass.class))
        );
    }

    public Result viewUserRuns() {
        List<User> users = User.find.where().ne("id", 0).findList();
        return ok(
                viewruns.render(users)
        );
    }

    public Result userManagement() {
        List<User> users = User.find.where().ne("id", 0).findList();
        return ok(
                usermgmt.render(users)
        );
    }

    /**
     * Process the data submitted on the user activation form (user administration page) and activate or
     * deactivate the user accounts specified.
     */
    public Result activateAccount() {

        DynamicForm form = formFactory.form().bindFromRequest();

                    List<User> users = User.find.where().ne("id", 0).findList();

                    for (User user : users) {
                        if (form.data().containsValue(Long.toString(user.id))) {
                            user.setActive(true);
                        } else {
                            user.setActive(false);
                        }

                        user.role = form.data().get("role_" + user.username);

                        user.save();
                    }

        flash("activate_success", "Updated user(s) active status!");

        return redirect(
                routes.Application.userManagement()
        );
    }

    /**
     * Action to process the change password form submission for the currently logged in user.
     */
    public Result changePassword() {
        Form<ChangePass> changePassForm = formFactory.form(ChangePass.class).bindFromRequest();
        if (changePassForm.hasErrors()) {
            return badRequest(changepw.render(changePassForm));
        }

        User user = Application.getCurrentUser();
        user.setPassword(BCrypt.hashpw(changePassForm.get().password, BCrypt.gensalt()));
        user.save();

        flash("change_success", "Password successfully changed!");
        return redirect(
                routes.Application.changePass()
        );
    }

    public static User getCurrentUser() {
        String uid = session().get("uid");
        return User.find.byId(Long.parseLong(uid));
    }

    public static Long getCurrentUserId() {
        return Long.valueOf(session().get("uid"));
    }
}
