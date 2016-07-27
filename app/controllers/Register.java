package controllers;

import models.User;

/**
     * The register form object
     */
    public class Register {

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

            if (User.find.where().eq("username", username).findUnique() != null) {
                return "A user with that name already exists!";
            }

            if (User.find.where().eq("email", email).findUnique() != null) {
                return "A user with that email already exists!";
            }
            return null;
        }
    }