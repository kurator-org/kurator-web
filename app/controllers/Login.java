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

        public String username;
        public String password;

        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            User user = User.authenticate(username, password);
            if (user == null) {
                errors.add(new ValidationError("user", "Invalid user or password"));
            } else {

                Http.Session session = Http.Context.current().session();
                session.clear();
                session.put("uid", Long.toString(user.id));
                session.put("username", user.username);
            }

            return errors.isEmpty() ? null : errors;
        }

    }