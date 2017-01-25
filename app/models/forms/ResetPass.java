package models.forms;

import dao.UserDao;
import models.db.user.User;

/**
     * The reset password form object
     */
    public class ResetPass {
        private UserDao userDao = new UserDao();

        public String username;
        public String email;

        public String validate() {
            User user = userDao.findByUsername(username);

            if (user == null || !user.getEmail().equals(email) || !user.isActive()) {
                return "No active combination of username and email found.";
            }

            return null;
        }
    }