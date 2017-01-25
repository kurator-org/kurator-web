package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import dao.UserDao;
import models.db.user.Role;
import models.db.user.User;
import models.db.user.UserUpload;
import models.forms.ChangePass;
import models.forms.Login;
import models.forms.Register;
import models.forms.ResetPass;
import models.json.UserManagement;
import org.apache.commons.io.FileUtils;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.UserUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import views.html.*;
import views.html.admin.*;

/**
 * Created by lowery on 1/25/2017.
 */
public class Users extends Controller {
    private final UserDao userDao = new UserDao();

    private final FormFactory formFactory;
    private final MailerClient mailerClient;

    @Inject
    public Users(FormFactory formFactory, MailerClient mailerClient) {
        this.formFactory = formFactory;
        this.mailerClient = mailerClient;
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
                routes.Users.login()
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

        Register registration = registerForm.get();

        User user = userDao.createUser(registration.getUsername(), registration.getFirstName(),
                registration.getLastName(), registration.getEmail(), registration.getPassword(),
                registration.getAffiliation());

        flash("message", "New user registration successful! The admin will send an email notification when your " +
                "account has been activated.");

        List<User> adminUsers = userDao.findUsersByRole(Role.ADMIN);

        try {
            // TODO: make this work and perhaps factor it out into a service for mailer tasks
            Email email = new Email();
            email.setSubject("New kurator-web user registration: " + user.getUsername());
            email.setFrom("Kurator Admin <from@email.com>");

            for (User admin : adminUsers) {
                if (admin.getEmail() != null) {
                    email.addTo(admin.getEmail());
                }
            }

            email.setBodyText("A new user, " + user.getFirstname() + " " + user.getLastname() + " with username: " +
                    user.getUsername() + " and email: " + user.getEmail() + " has requested account " +
                    "authorization for kurator-web.");

            if (adminUsers.size() > 1) { // send only if there are admins registered
                //mailer.send(email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return redirect(
                routes.Users.login()
        );
    }

    public Result change() {
        return ok(
                changepw.render(formFactory.form(ChangePass.class))
        );
    }

    public Result manage() {
        List<User> users = userDao.findAllUsers();
        return ok(
                usermgmt.render(users)
        );
    }

    /**
     * Process the data submitted on the user management form (user administration page). Activate or
     * deactivate the user accounts specified and assign role.
     */
    public Result manageUsers() {
        UserManagement[] request = Json.fromJson(request().body().asJson(), UserManagement[].class);

        for (UserManagement userMgmt : request) {
            userDao.updateUserAccess(userMgmt.getUsername(), userMgmt.getActive(), userMgmt.getRole());
        }

        flash("activate_success", "Updated user(s) active status!");

        return redirect(
                routes.Users.manage()
        );
    }

    /**
     * Action to process the change password form submission for the currently logged in user.
     */
    public Result changePassword() {
        Form<ChangePass> form = formFactory.form(ChangePass.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(changepw.render(form));
        }

        ChangePass changePass = form.get();
        userDao.updatePassword(request().username(), changePass.getPassword());

        flash("change_success", "Password successfully changed!");
        return redirect(
                routes.Users.change()
        );
    }

    public Result workshop() {
        return ok(workshop.render());
    }

    public Result createWorkshop() {
        Map<String, String[]> form = request().body().asFormUrlEncoded();

        // TODO: Create a workshop and temporary guest user accounts
        String workshopName = form.get("name")[0];
        int numUsers = Integer.parseInt(form.get("numUsers")[0]);

        flash("activate_success", "Created \"" + workshopName + "\" and assigned " + numUsers + " guest accounts");

        return redirect(
                routes.Users.manage()
        );
    }

    public Result reset() {
        Form form = formFactory.form(ResetPass.class);
        return ok(reset.render(form));
    }

    public Result resetPassword() {
        Form<ResetPass> form = formFactory.form(ResetPass.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(reset.render(form));
        }

        ResetPass resetPass = form.get();

        // TODO: more email tasks to make work and factor out into mailer service
        String pw = UserUtil.generatePassword();

        String username = resetPass.getUsername();
        String emailAddr = resetPass.getEmail();

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
                routes.Users.reset()
        );
    }

    public Result userRuns() {
        List<User> users = userDao.findAllUsers();
        return ok(
                viewruns.render(users)
        );
    }

    /**
     * REST endpoint returns a json array containing metadata about the currently logged in user's uploaded files.
     *
     * @return json containing file upload ids and filenames
     */
    public Result listUploads() {
        List<UserUpload> uploadList = userDao.findUserUploads(request().username());

        ObjectNode response = Json.newObject();
        ArrayNode uploads = response.putArray("uploads");

        for (UserUpload userUpload : uploadList) {
            ObjectNode upload = Json.newObject();
            upload.put("id", userUpload.getId());
            upload.put("filename", userUpload.getFileName());
            uploads.add(upload);
        }

        return ok(uploads);
    }

    /**
     * Helper method creates temp filea from the multipart form data and persists the upload file metadata to the
     * database
     *
     * @return an instance of UploadFile that has been persisted to the db
     */
    private UserUpload uploadFile() {
        Http.MultipartFormData body = request().body().asMultipartFormData();

        for (Object obj : body.getFiles()) {
            Http.MultipartFormData.FilePart filePart = (Http.MultipartFormData.FilePart) obj;

            File src = (File) filePart.getFile();

            File file = null;
            try {
                file = File.createTempFile(filePart.getFilename() + "-", ".csv");
                FileUtils.copyFile(src, file);

                UserUpload uploadFile = userDao.createUserUpload(request().username(), filePart.getFilename(),
                        file.getAbsolutePath());

                return uploadFile;
            } catch (IOException e) {
                throw new RuntimeException("Could not create temp file for upload", e);
            }
        }

        // No files present in the body of the request
        throw new RuntimeException("File part is not present in multipart form data from request");
    }

    /**
     * Obtains the currently uploaded file from the session variable.
     *
     * @return uploaded file
     * @throws FileNotFoundException
     */
    private File getCurrentUpload() throws FileNotFoundException {
        String uploadFileId = session().get("uploadFileId");
        UserUpload uploadFile = userDao.findUserUploadById(Long.parseLong(uploadFileId));
        File file = new File(uploadFile.getAbsolutePath());

        if (!file.exists()) {
            throw new FileNotFoundException("Could not load input from file.");
        }

        return file;
    }
}
