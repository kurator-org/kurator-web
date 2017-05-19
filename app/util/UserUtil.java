package util;

import models.db.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by lowery on 1/25/2017.
 */
public class UserUtil {
    private static final int GEN_PASS_LEN = 12; // Generated password length

    public static User authenticate(User user, String password) {
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
        if (user != null) {
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
    }
}
