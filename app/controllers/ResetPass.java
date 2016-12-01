package controllers;

import models.User;

import static play.mvc.Http.Context.Implicit.session;

/**
     * The reset password form object
     */
    public class ResetPass {
        public String username;
        public String email;

        public String validate() {
            User user = User.find.where().eq("username", username).findUnique();

            if (user == null || !user.email.equals(email) || !user.active) {
                return "No active combination of username and email found.";
            }

            return null;
        }
    }