package models.forms;

import models.db.user.User;
import service.UserService;

import static play.mvc.Controller.request;
import static play.mvc.Http.Context.Implicit.session;

/**
     * The change password form object (user administration page)
     */
    public class ChangePass {
        private UserService userService = new UserService();

        public String oldPassword;
        public String password;
        public String confirmPassword;

        public String validate() {
            User user = userService.authenticate(request().username(), oldPassword);
            if (user == null) {
                return "Current password is invalid.";
            }

            if (!password.equals(confirmPassword)) {
                return "Passwords do not match";
            }
            return null;
        }
    }