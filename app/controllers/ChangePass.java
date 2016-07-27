package controllers;

import models.User;
import play.mvc.Http;

/**
     * The change password form object (user administration page)
     */
    public class ChangePass {
        public String oldPassword;
        public String password;
        public String confirmPassword;

        public String validate() {
            Http.Session session = Http.Context.current().session();
            User user = User.authenticate(session.get("username"), oldPassword);
            if (user == null) {
                return "Current password is invalid.";
            }

            if (!password.equals(confirmPassword)) {
                return "Passwords do not match";
            }
            return null;
        }
    }