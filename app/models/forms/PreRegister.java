/** Register.java
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
import play.mvc.Http;
import util.UserUtil;

/**
 * The register form object
 */
public class PreRegister {
    private final UserDao userDao = new UserDao();

    private String email;
    private String password;

    private String firstName;
    private String lastName;
    private String username;
    private String newPassword;
    private String confirmPassword;
    private String affiliation;

    public String validate() {
        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        if (userDao.findUserByUsername(username) != null) {
            return "A user with that name already exists!";
        }

        if (userDao.findUserByEmail(email) == null) {
            return "User pre-registration for " + email + " does not exist.";
        } else {
            User user = userDao.findUserByEmail(email);
            if (user.getUsername() != null) {
                return "User already registered!";
            }
        }

        User user = UserUtil.authenticate(userDao.findUserByEmail(email), password);
        if (user == null) {
            return "Invalid password";
        } else if (!user.isActive()) {
            return "User account is currently inactive. Please contact admin to activate your account.";
        }

        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}
