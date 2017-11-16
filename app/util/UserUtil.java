/** UserUtil.java
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
package util;

import models.db.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class UserUtil {
    private static final int GEN_PASS_LEN = 12; // Generated password length

    public static User authenticate(User user, String password) {
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    public static String generatePassword() {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        char[] symbols = tmp.toString().toCharArray();

        Random random = new Random();

        char[] buf = new char[GEN_PASS_LEN];

        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];

        return new String(buf);
    }

    /**
     * Utility for generating guest accounts and sql statements for adding them to the db.
     */
    public static void main(String[] args) {
       String pass = generatePassword();

        Map<String, String> users = new LinkedHashMap<>();

        for (int i = 0; i < 25; i++) {
            String uid = new Integer(i+8).toString();

            String password = generatePassword();
            String hash = BCrypt.hashpw(pass, BCrypt.gensalt());

            users.put("user_" + (i + 1), password);

            System.out.println("insert into user (id, firstname, lastname, email, username, password, affiliation, active) values(" + uid + ", '', '', '', 'user_" + (i + 1) + "', '" + hash + "', 'Kurator', TRUE);");
            System.out.println("insert into user_security_role (user_id, security_role_id) values(" + uid + ", 2);");

        }

        System.out.println();

        for (String username : users.keySet()) {
            System.out.println(username + " : " + users.get(username));
        }

        String hash = BCrypt.hashpw("password", BCrypt.gensalt());
        System.out.println(hash);
    }
}
