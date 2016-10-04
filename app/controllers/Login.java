package controllers;

import models.User;
import play.data.validation.ValidationError;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;

/**
     * The login form object.
     */
    public class Login {

        protected String username;
        protected String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String validate() {
            User user = User.authenticate(username, password);
            if (user == null) {
                return "Invalid user or password";
            } else {
                Http.Session session = Http.Context.current().session();
                session.clear();
                session.put("uid", Long.toString(user.id));
                session.put("user_role", user.role);
                session.put("username", user.username);

                // TODO: session timeout
            }
            return null;
        }

    }