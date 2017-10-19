/** ChangePass.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models.forms;

import dao.UserDao;
import models.db.user.User;
import util.UserUtil;

import static play.mvc.Controller.request;
import static play.mvc.Http.Context.Implicit.session;

/**
 * The change password form object (user administration page)
 */
public class ChangePass {
    private final UserDao userDao = new UserDao();

    private String oldPassword;
    private String password;
    private String confirmPassword;

    public String validate() {
        long uid = Long.parseLong(session().get("uid"));
        User user = UserUtil.authenticate(User.find.byId(uid), oldPassword);
        System.out.println(User.find.byId(uid).getUsername());
        System.out.println(oldPassword);
        if (user == null) {
            return "Current password is incorrect.";
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
