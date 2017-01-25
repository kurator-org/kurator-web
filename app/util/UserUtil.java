package util;

import models.db.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Random;

/**
 * Created by lowery on 1/25/2017.
 */
public class UserUtil {
    private static final int GEN_PASS_LEN = 16; // Generated password length

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
}
