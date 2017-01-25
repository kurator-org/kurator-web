package models.forms;

import dao.UserDao;

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

            if (userDao.findUserByUsername(username) != null) {
                return "A user with that name already exists!";
            }

            if (userDao.findUserByEmail(email) != null) {
                return "A user with that email already exists!";
            }
            return null;
        }
    }