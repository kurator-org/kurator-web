package models.forms;

import dao.UserDao;
import models.db.user.User;

/**
 * The reset password form object
 */
public class ResetPass {
    private final UserDao userDao = new UserDao();

    private String username;
    private String email;

    public String validate() {
        User user = userDao.findUserByUsername(username);

        if (user == null || !user.getEmail().equals(email) || !user.isActive()) {
            return "No active combination of username and email found.";
        }

        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}