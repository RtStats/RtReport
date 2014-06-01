package truyen.common;

import truyen.common.bo.ConfDao;
import truyen.common.bo.user.UserBo;
import truyen.common.bo.user.UserDao;

import com.github.ddth.commons.utils.IdGenerator;
import com.github.ddth.plommon.utils.SessionUtils;

public class Application {

    public static IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

    /**
     * Gets the currently logged in user.
     * 
     * @return
     */
    public static UserBo currentUser() {
        Object userId = SessionUtils.getSession(Constants.SESSION_USER_ID, true);
        UserBo user = userId != null ? UserDao.getUser(Integer.parseInt(userId.toString())) : null;
        return user;
    }

    public static void logout() {
        SessionUtils.removeSession(Constants.SESSION_USER_ID);
    }

    public static void login(UserBo user) {
        login(user.getId());
    }

    private static void login(int userId) {
        SessionUtils.setSession(Constants.SESSION_USER_ID, userId, 7 * 24 * 3600);
    }

    public static String appConfig(String key) {
        try {
            return ConfDao.getConf(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static boolean appConfigAsBoolean(String key) {
        return ConfDao.getConfAsBoolean(key);
    }
}
