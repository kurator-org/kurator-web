/** Login.java
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
 * The login form object.
 */
public class Login {
    private final UserDao userDao = new UserDao();

    private String username;
    private String password;

    public String validate() {
        User user = UserUtil.authenticate(userDao.findUserByUsername(username), password);

        if (user == null) {
            return "Invalid username or password";
        } else if (!user.isActive()) {
            return "User account is currently inactive. An admin will activate your account shortly.";
        } else {
            Http.Session session = Http.Context.current().session();
            session.clear();
            session.put("uid", Long.toString(user.getId()));
            session.put("user_role", user.getRoles().get(0).getName());
            session.put("username", user.getUsername());

            // TODO: token based auth and session timeout
        }
        return null;
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
}