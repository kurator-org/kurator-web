package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import forms.FormDefinition;
import models.User;
import models.UserUpload;
import org.apache.commons.mail.EmailException;
import org.kurator.util.SystemClasspathManager;
import org.mindrot.jbcrypt.BCrypt;
import play.api.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
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
    @Inject
    MailerClient mailer;

    public Result summary(long runId) {
        return ok(
                summary.render(runId)
        );
    }

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

        List<User> adminUsers = User.find.where().eq("role", "ADMIN").findList();

        try {
            Email email = new Email();
            email.setSubject("New kurator-web user registration: " + user.username);
            email.setFrom("Kurator Admin <from@email.com>");

            for (User admin : adminUsers) {
                if (admin.email != null) {
                    email.addTo(admin.email);
                }
            }

            email.setBodyText("A new user, " + user.firstname + " " + user.lastname + " with username: " +
                    user.username + " and email: " + user.email + " has requested account " +
                    "authorization for kurator-web.");

            if (adminUsers.size() > 1) { // send only if there are admins registered
                //mailer.send(email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                            user.active = true;
                        } else {
                            user.active = false;
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
        user.password = BCrypt.hashpw(changePassForm.get().password, BCrypt.gensalt());
        user.save();

        flash("change_success", "Password successfully changed!");
        return redirect(
                routes.Application.changePass()
        );
    }

    public Result resetPass() {
        Form form = formFactory.form(ResetPass.class);
        return ok(reset.render(form));
    }

    public Result resetPassword() {
        Form<ResetPass> resetPassForm = formFactory.form(ResetPass.class).bindFromRequest();

        if (resetPassForm.hasErrors()) {
            return badRequest(reset.render(resetPassForm));
        }

        String pw = User.generatePassword();

        String username = resetPassForm.get().username;
        String emailAddr = resetPassForm.get().email;

        Email email = new Email();
        email.setSubject("Kurator-web password reset: " + username);
        email.setFrom("Kurator Admin <datakurator@gmail.com>");
        email.addTo(emailAddr);

        email.setBodyText("Password reset request received for user account " + username + ". Temporary password " +
                "assigned: " + pw);

        /*mailer.send(email);

        User user = User.find.where().eq("username", username).findUnique();
        user.password = pw;
        user.save();

        */

        flash("message", "Password reset email sent to: " + emailAddr);

        return redirect(
                routes.Application.resetPass()
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
