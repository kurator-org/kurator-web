/** ResetPass.java
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