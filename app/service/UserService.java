package service;

import dao.UserDao;
import models.db.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Random;

/**
 * Created by lowery on 1/24/2017.
 */
public class UserService {
    private static UserDao userDao = new UserDao();

    private static final int GEN_PASS_LEN = 16; // Generated password length

    public User authenticate(String username, String password) {
        User user = userDao.findByUsername(username);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    public String generatePassword() {
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
