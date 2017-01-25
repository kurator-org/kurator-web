package models.forms;

import dao.UserDao;
import models.db.user.User;

/**
     * The register form object
     */
    public class Register {
        private UserDao userDao = new UserDao();

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

            if (userDao.findByUsername(username) != null) {
                return "A user with that name already exists!";
            }

            if (userDao.findByEmail(email) != null) {
                return "A user with that email already exists!";
            }
            return null;
        }
    }