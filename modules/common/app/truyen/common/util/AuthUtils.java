package truyen.common.util;

import truyen.common.bo.user.UserBo;

import com.github.ddth.commons.utils.HashUtils;

public class AuthUtils {
    public static String encryptPassword(UserBo user, String rawPassword) {
        return encryptPassword(String.valueOf(user.getId()), rawPassword);
    }

    public static String encryptPassword(String salt, String rawPassword) {
        String combinedSaltAndPassword = salt + "." + rawPassword;
        return HashUtils.sha1(combinedSaltAndPassword);
    }

    /**
     * Authenticates user against a password.
     * 
     * @param user
     * @param password
     * @return
     */
    public static boolean authenticate(UserBo user, String password) {
        if (user == null) {
            return false;
        }
        String encryptedPassword = encryptPassword(user, password);
        return user.getPassword().equals(encryptedPassword);
    }
}
