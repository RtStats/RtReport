package rtreport.common;

import bo.common.user.UserBo;
import bo.common.user.UserDao;

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
        Object username = SessionUtils.getSession(Constants.SESSION_USER, true);
        UserBo user = username != null ? UserDao.getUser(username.toString()) : null;
        return user;
    }

    public static void logout() {
        SessionUtils.removeSession(Constants.SESSION_USER);
    }

    public static void login(UserBo user) {
        login(user.getUsername());
    }

    private static void login(String username) {
        SessionUtils.setSession(Constants.SESSION_USER, username, 24 * 3600);
    }

    // public static String appConfig(String key) {
    // try {
    // return ConfDao.getConf(key);
    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new RuntimeException(e);
    // }
    // }
    //
    // public static boolean appConfigAsBoolean(String key) {
    // return ConfDao.getConfAsBoolean(key);
    // }
}
