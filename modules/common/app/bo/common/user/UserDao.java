package bo.common.user;

import global.common.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.ddth.plommon.bo.nosql.BaseNosqlDao;

public class UserDao extends BaseNosqlDao {

    public final static String TABLE_USERGROUP = "config_user";
    public final static String TABLE_USER = "config_user";

    /**
     * Loads a user account data.
     * 
     * @param username
     * @return
     */
    public static UserBo getUser(String username) {
        Map<String, Object> result = null;
        Map<Object, Object> dbRows = loadAsMap(Registry.datasourceName(), TABLE_USER, username);
        if (dbRows != null) {
            result = new HashMap<String, Object>();
            for (Entry<Object, Object> entry : dbRows.entrySet()) {
                result.put(entry.getKey().toString(), entry.getValue());
            }
            result.put(UserBo.COL_USERNAME, username);
        }
        return result != null ? (UserBo) (new UserBo().fromMap(result)) : null;
    }
}
