package bo.common.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vngup.rtreports.common.Constants;
import bo.common.CounterDao;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.plommon.bo.jdbc.BaseMysqlDao;
import com.github.ddth.plommon.bo.jdbc.ParamExpression;

public class UserDao extends BaseMysqlDao {

    public final static String TABLE_USERGROUP = Constants.APP_ID + "_usergroup";
    public final static String TABLE_USER = Constants.APP_ID + "_user";

    // private final static UserBo[] EMPTY_ARR_USER_BO = new UserBo[0];
    private final static UsergroupBo[] EMPTY_ARR_USERGROUP_BO = new UsergroupBo[0];

    /*----------------------------------------------------------------------*/
    private static String cacheKeyUsergroup(int id) {
        return Constants.CACHE_PREFIX + "_USERGROUP_" + id;
    }

    private static String cacheKey(UsergroupBo usergroup) {
        return cacheKeyUsergroup(usergroup.getId());
    }

    private static String cacheKeyAllUsergroups() {
        return Constants.CACHE_PREFIX + "_ALL_USERGROUPS";
    }

    private static void invalidate(UsergroupBo usergroup) {
        removeFromCache(cacheKey(usergroup));
        removeFromCache(cacheKeyAllUsergroups());
    }

    private static String cacheKeyUser(int id) {
        return Constants.CACHE_PREFIX + "_USER_ID_" + id;
    }

    private static String cacheKeyUserEmail(String email) {
        return Constants.CACHE_PREFIX + "_USER_" + email;
    }

    private static String cacheKey(UserBo user) {
        return cacheKeyUser(user.getId());
    }

    private static String cacheKeyAllUsers() {
        return Constants.CACHE_PREFIX + "_ALL_USERS";
    }

    private static void invalidate(UserBo user) {
        removeFromCache(cacheKey(user));
        removeFromCache(cacheKeyUserEmail(user.getEmail()));
        removeFromCache(cacheKeyAllUsers());
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new user group.
     * 
     * @param usergroup
     * @return
     */
    public static UsergroupBo create(final UsergroupBo usergroup) {
        if (usergroup.getId() < 1) {
            usergroup.setId((int) CounterDao.nextId(Constants.COUNTER_USERGROUP_ID));
        }
        final String[] COLUMNS = new String[] { UsergroupBo.COL_ID[0], UsergroupBo.COL_TITLE[0],
                UsergroupBo.COL_DESC[0] };
        final Object[] VALUES = new Object[] { usergroup.getId(), usergroup.getTitle(),
                usergroup.getDescription() };
        insertIgnore(TABLE_USERGROUP, COLUMNS, VALUES);
        invalidate(usergroup);
        return (UsergroupBo) usergroup.markClean();
    }

    /**
     * Deletes an existing usergroup.
     * 
     * @param usergroup
     */
    public static void delete(UsergroupBo usergroup) {
        final String[] COLUMNS = new String[] { UsergroupBo.COL_ID[0] };
        final Object[] VALUES = new Object[] { usergroup.getId() };
        delete(TABLE_USERGROUP, COLUMNS, VALUES);
        invalidate(usergroup);
    }

    /**
     * Gets a usergroup by id.
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static UsergroupBo getUsergroup(int id) {
        if (true) {
            return null;
        }
        final String CACHE_KEY = cacheKeyUsergroup(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] columns = { UsergroupBo.COL_ID, UsergroupBo.COL_TITLE,
                    UsergroupBo.COL_DESC };
            final String whereClause = UsergroupBo.COL_ID[0] + "=?";
            final Object[] paramValues = { id };
            List<Map<String, Object>> dbResult = select(TABLE_USERGROUP, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (UsergroupBo) new UsergroupBo().fromMap(dbRow) : null;
    }

    /**
     * Gets all usergroups as a list.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static UsergroupBo[] getAllUsergroups() {
        if (true) {
            return null;
        }
        final String CACHE_KEY = cacheKeyAllUsergroups();
        List<Map<String, Object>> dbRows = getFromCache(CACHE_KEY, List.class);
        if (dbRows == null) {
            final String[][] columns = { UsergroupBo.COL_ID };
            final String whereClause = null;
            final Object[] paramValues = null;
            dbRows = select(TABLE_USERGROUP, columns, whereClause, paramValues);
            putToCache(CACHE_KEY, dbRows);
        }
        List<UsergroupBo> result = new ArrayList<UsergroupBo>();
        if (dbRows != null) {
            for (Map<String, Object> dbRow : dbRows) {
                int id = DPathUtils.getValue(dbRow, UsergroupBo.COL_ID[1], int.class);
                UsergroupBo usergroup = getUsergroup(id);
                if (usergroup != null) {
                    result.add(usergroup);
                }
            }
        }
        return result.toArray(EMPTY_ARR_USERGROUP_BO);
    }

    /**
     * Updates an existing usergroup.
     * 
     * @param usergroup
     * @return
     */
    public static UsergroupBo update(UsergroupBo usergroup) {
        if (usergroup.isDirty()) {
            final String CACHE_KEY = cacheKey(usergroup);
            final String[] COLUMNS = new String[] { UsergroupBo.COL_TITLE[0],
                    UsergroupBo.COL_DESC[0] };
            final Object[] VALUES = new Object[] { usergroup.getTitle(), usergroup.getDescription() };
            final String[] WHERE_COLUMNS = new String[] { UsergroupBo.COL_ID[0] };
            final Object[] WHERE_VALUES = new Object[] { usergroup.getId() };
            update(TABLE_USERGROUP, COLUMNS, VALUES, WHERE_COLUMNS, WHERE_VALUES);
            Map<String, Object> dbRow = usergroup.toMap();
            putToCache(CACHE_KEY, dbRow);
        }
        return (UsergroupBo) usergroup.markClean();
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new user account.
     * 
     * @param user
     * @return
     */
    public static UserBo create(UserBo user) {
        if (user.getId() < 1) {
            user.setId((int) CounterDao.nextId(Constants.COUNTER_USER_ID));
        }
        final String[] COLUMNS = new String[] { UserBo.COL_ID[0], UserBo.COL_EMAIL[0],
                UserBo.COL_DISPLAY_NAME[0], UserBo.COL_PASSWORD[0], UserBo.COL_GROUP_ID[0],
                UserBo.COL_TIMESTAMP_CREATE[0] };
        final Object[] VALUES = new Object[] { user.getId(), user.getEmail(),
                user.getDisplayName(), user.getPassword(), user.getGroupId(),
                new ParamExpression("UTC_TIMESTAMP()") };
        insertIgnore(TABLE_USER, COLUMNS, VALUES);
        invalidate(user);
        return (UserBo) user.markClean();
    }

    /**
     * Deletes an existing user account.
     * 
     * @param user
     */
    public static void delete(UserBo user) {
        final String[] COLUMNS = new String[] { UserBo.COL_ID[0] };
        final Object[] VALUES = new Object[] { user.getId() };
        delete(TABLE_USER, COLUMNS, VALUES);
        invalidate(user);
    }

    /**
     * Gets a user account by email.
     * 
     * @param email
     * @return
     */
    @SuppressWarnings("unchecked")
    public static UserBo getUserByEmail(String email) {
        if (true) {
            return null;
        }
        final String CACHE_KEY = cacheKeyUserEmail(email);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] columns = { UserBo.COL_ID };
            final String whereClause = UserBo.COL_EMAIL[0] + "=?";
            final Object[] paramValues = { email };
            List<Map<String, Object>> dbResult = select(TABLE_USER, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        if (dbRow != null) {
            int id = DPathUtils.getValue(dbRow, UserBo.COL_ID[1], int.class);
            return getUser(id);
        } else {
            return null;
        }
    }

    /**
     * Gets a user account by id.
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static UserBo getUser(int id) {
        if (true) {
            return null;
        }
        final String CACHE_KEY = cacheKeyUser(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] columns = { UserBo.COL_ID, UserBo.COL_EMAIL, UserBo.COL_DISPLAY_NAME,
                    UserBo.COL_PASSWORD, UserBo.COL_GROUP_ID, UserBo.COL_TIMESTAMP_CREATE };
            final String whereClause = UserBo.COL_ID[0] + "=?";
            final Object[] paramValues = { id };
            List<Map<String, Object>> dbResult = select(TABLE_USER, columns, whereClause,
                    paramValues);
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (UserBo) new UserBo().fromMap(dbRow) : null;
    }

    /**
     * Updates an existing user account.
     * 
     * @param user
     * @return
     */
    public static UserBo update(UserBo user) {
        if (user.isDirty()) {
            final String CACHE_KEY = cacheKey(user);
            final String[] COLUMNS = new String[] { UserBo.COL_EMAIL[0],
                    UserBo.COL_DISPLAY_NAME[0], UserBo.COL_PASSWORD[0], UserBo.COL_GROUP_ID[0] };
            final Object[] VALUES = new Object[] { user.getEmail(), user.getDisplayName(),
                    user.getPassword(), user.getGroupId() };
            final String[] WHERE_COLUMNS = new String[] { UserBo.COL_ID[0] };
            final Object[] WHERE_VALUES = new Object[] { user.getId() };
            update(TABLE_USER, COLUMNS, VALUES, WHERE_COLUMNS, WHERE_VALUES);
            Map<String, Object> dbRow = user.toMap();
            putToCache(CACHE_KEY, dbRow);
        }
        return (UserBo) user.markClean();
    }

    /**
     * Updates email of an existing user account.
     * 
     * @param user
     * @param newEmail
     * @return
     */
    public static UserBo updateEmail(UserBo user, String newEmail) {
        removeFromCache(cacheKeyUserEmail(user.getEmail()));
        user.setEmail(newEmail);
        return update(user);
    }

    /*----------------------------------------------------------------------*/
}
