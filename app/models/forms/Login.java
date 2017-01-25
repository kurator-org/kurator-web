package models.forms;

import models.db.user.User;
import play.mvc.Http;
import service.UserService;

/**
     * The login form object.
     */
    public class Login {
        private UserService userService = new UserService();

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
            User user = userService.authenticate(username, password);
            if (user == null) {
                return "Invalid username or password";
            } else if (!user.isActive()) {
                return "User account is currently inactive. An admin will activate your account shortly.";
            } else {
                Http.Session session = Http.Context.current().session();
                session.clear();
                session.put("uid", Long.toString(user.getId()));
                session.put("user_role", user.getRole().name());
                session.put("username", user.getUsername());

                // TODO: token based auth and session timeout
            }
            return null;
        }

    }