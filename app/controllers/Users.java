/** Users.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import dao.UserAccessDao;
import dao.UserDao;
import models.db.user.SecurityRole;
import models.db.user.User;
import models.db.user.UserGroup;
import models.db.user.UserUpload;
import models.forms.ChangePass;
import models.forms.Login;
import models.forms.Register;
import models.forms.ResetPass;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import views.html.*;

public class Users extends Controller {
    private final UserDao userDao = new UserDao();
    private final UserAccessDao userAccessDao = new UserAccessDao();

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
    @SubjectPresent
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
            userDao.updateLastActive(loginForm.get().getUsername());

            return redirect(
                    routes.Application.test()
            );
        }
    }

    public Result checkAuth() {
        if (session().get("username") != null) {
            User user = userDao.findUserByUsername(session().get("username"));

            ObjectNode json = Json.newObject();
            json.put("uid", user.getId());
            json.put("username", user.getUsername());
            json.put("role", user.getRoles().get(0).getName());

            return ok(json);
        }

        return unauthorized();
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

        List<User> adminUsers = userDao.findUsersByRole(SecurityRole.ADMIN);

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
                routes.Users.register()
        );
    }

    @Restrict({@Group("ADMIN")})
    public Result manage() {
        Map req = request().body().asFormUrlEncoded();

        List<User> users = new ArrayList<>();

        if (req != null && req.containsKey("group")) {
            users = userAccessDao.findUsersByGroup((String) req.get("group"));
        } else {
            users = userDao.findAllUsers();
        }

        return ok(
                Json.toJson(users)
        );
    }

    @SubjectPresent
    public Result listGroups() {
        List<UserGroup> groups = userAccessDao.findAllGroups();
        if (groups.isEmpty()) {

            //response().setHeader("Content-Type", "application/json");
            return ok(Json.newArray()); // empty array
        }
        return ok(Json.toJson(groups));
    }

    @Restrict({@Group("ADMIN")})
    public Result createGroup() {
        final JsonNode json = request().body().asJson();

        String name = json.get("name").textValue();

        User user = userDao.findUserByUsername(session().get("username"));
        UserGroup group = userAccessDao.createGroup(user, name);


        return ok(
                Json.toJson(group)
        );
    }

    @Restrict({@Group("ADMIN")})
    public Result addUserToGroup() {
        final JsonNode json = request().body().asJson();
        User user = userAccessDao.addUserToGroup(json.get("user").asLong(), json.get("group").asLong());

        return ok(
                Json.toJson(user)
        );
    }

    /**
     * Process the data submitted on the user management form (user administration page). Activate or
     * deactivate the user accounts specified and assign role.
     */
    @Restrict({@Group("ADMIN")})
    public Result updateUser(Long id) {
        ObjectNode response = Json.newObject();

        JsonNode request = request().body().asJson();
        userDao.updateUserAccess(request.get("username").asText(), request.get("active").asBoolean(), request.get("role").asText());

        // TODO: Add success message to json response or handle errors

        return ok(response);
    }

    @Restrict({@Group("ADMIN")})
    public Result createWorkshop() {
        Map<String, String[]> form = request().body().asFormUrlEncoded();

        // TODO: Create a workshop and temporary guest user accounts
        String workshopName = form.get("name")[0];
        int numUsers = Integer.parseInt(form.get("numUsers")[0]);

        System.out.println("workshop: " + workshopName + ", numUsers: " + numUsers);
        flash("activate_success", "Created \"" + workshopName + "\" and assigned " + numUsers + " guest accounts");

        return redirect(
                routes.Application.admin()
        );
    }

    @SubjectPresent
    public Result reset() {
        Form form = formFactory.form(ResetPass.class);
        return ok(reset.render(form));
    }

    @SubjectPresent
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

    /**
     * REST endpoint returns a json array containing metadata about the currently logged in user's uploaded files.
     *
     * @return json containing file upload ids and filenames
     */
    public Result listUploads() {
        List<UserUpload> uploadList = userDao.findUserUploads(session().get("username"));

        ObjectNode response = Json.newObject();
        ArrayNode uploads = response.putArray("uploads");

        for (UserUpload userUpload : uploadList) {
            ObjectNode upload = Json.newObject();
            upload.put("id", userUpload.getId());
            upload.put("filename", userUpload.getFileName());
            upload.put("path", userUpload.getAbsolutePath());
            uploads.add(upload);
        }

        return ok(uploads);
    }

    public Result downloadFile(long uploadId) {
        UserUpload upload = userDao.findUserUploadById(uploadId);

        File file = new File(upload.getAbsolutePath());
        response().setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        return ok(file);
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
