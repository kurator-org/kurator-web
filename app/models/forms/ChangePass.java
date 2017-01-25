package models.forms;

import dao.UserDao;
import models.db.user.User;
import util.UserUtil;

import static play.mvc.Controller.request;

/**
 * The change password form object (user administration page)
 */
public class ChangePass {
    private final UserDao userDao = new UserDao();

    private String oldPassword;
    private String password;
    private String confirmPassword;

    public String validate() {
        User user = UserUtil.authenticate(userDao.findUserByUsername(request().username()), password);

        if (user == null) {
            return "Current password is invalid.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        return null;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
