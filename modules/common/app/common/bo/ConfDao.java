package common.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.db.DB;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.plommon.bo.jdbc.BaseMysqlDao;
import common.Constants;

public class ConfDao extends BaseMysqlDao {

    public final static String TABLE_CONFIG = Constants.APP_ID + "_config";
    private final static String SQL_UPDATE = MessageFormat.format(
            "UPDATE {0} SET conf_value=? WHERE conf_key=?", TABLE_CONFIG);
    private final static String SQL_INSERT = MessageFormat.format(
            "INSERT INTO {0} (conf_key, conf_value) VALUES (?, ?)", TABLE_CONFIG);

    /*----------------------------------------------------------------------*/
    private static String cacheKey(String confKey) {
        return Constants.CACHE_PREFIX + "_CONF_" + confKey;
    }

    private static void invalidate(String confKey) {
        removeFromCache(cacheKey(confKey));
    }

    /*----------------------------------------------------------------------*/
    /**
     * Creates or updates a config.
     * 
     * @param confKey
     * @param confValue
     * @return
     */
    public static void createOrUpdateConf(String confKey, String confValue) {
        Connection conn = DB.getConnection(false);
        try {
            try {
                conn.setAutoCommit(false);
                int numUpdated = update(SQL_UPDATE, new Object[] { confValue, confKey });
                if (numUpdated < 1) {
                    insert(SQL_INSERT, new Object[] { confKey, confValue });
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage(), e);
        }
        invalidate(confKey);
    }

    /**
     * Creates/Updates a config.
     * 
     * @param confKey
     * @param confValue
     */
    public static void createOrUpdateConf(String confKey, boolean confValue) {
        createOrUpdateConf(confKey, confValue ? "1" : "0");
    }

    /**
     * Creates/Updates a config.
     * 
     * @param confKey
     * @param confValue
     */
    public static void createOrUpdateConf(String confKey, int confValue) {
        createOrUpdateConf(confKey, Integer.toString(confValue));
    }

    /**
     * Creates/Updates a config.
     * 
     * @param confKey
     * @param confValue
     */
    public static void createOrUpdateConf(String confKey, long confValue) {
        createOrUpdateConf(confKey, Long.toString(confValue));
    }

    /**
     * Creates/Updates a config.
     * 
     * @param confKey
     * @param confValue
     */
    public static void createOrUpdateConf(String confKey, float confValue) {
        createOrUpdateConf(confKey, Float.toString(confValue));
    }

    /**
     * Creates/Updates a config.
     * 
     * @param confKey
     * @param confValue
     */
    public static void createOrUpdateConf(String confKey, double confValue) {
        createOrUpdateConf(confKey, Double.toString(confValue));
    }

    /**
     * Deletes an existing config.
     * 
     * @param confKey
     */
    public static void deleteConf(String confKey) {
        final String[] COLUMNS = new String[] { "conf_key" };
        final Object[] VALUES = new Object[] { confKey };
        delete(TABLE_CONFIG, COLUMNS, VALUES);
        invalidate(confKey);
    }

    /**
     * Gets a config value.
     * 
     * @param confKey
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getConf(String confKey) {
        final String CACHE_KEY = cacheKey(confKey);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String[][] COLUMNS = new String[][] { { "conf_key" }, { "conf_value" } };
            List<Map<String, Object>> dbResult = select(TABLE_CONFIG, COLUMNS, "conf_key=?",
                    new Object[] { confKey });
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        String result = dbRow != null ? DPathUtils.getValue(dbRow, "conf_value", String.class)
                : null;
        return result != null ? result.trim() : "";
    }

    /**
     * Gets a config as boolean value.
     * 
     * @param confKey
     * @return
     */
    public static boolean getConfAsBoolean(String confKey) {
        String value = getConf(confKey);
        return !"0".equalsIgnoreCase(value) || "Y".equalsIgnoreCase(value)
                || "T".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value)
                || "TRUE".equalsIgnoreCase(value);
    }

    /**
     * Gets a config as int value.
     * 
     * @param confKey
     * @return
     */
    public static int getConfAsInt(String confKey) {
        String value = getConf(confKey);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gets a config as long value.
     * 
     * @param confKey
     * @return
     */
    public static long getConfAsLong(String confKey) {
        String value = getConf(confKey);
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gets a config as long value.
     * 
     * @param confKey
     * @return
     */
    public static float getConfAsFloat(String confKey) {
        String value = getConf(confKey);
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    /**
     * Gets a config as long value.
     * 
     * @param confKey
     * 
     * @return
     */
    public static double getConfAsDouble(String confKey) {
        String value = getConf(confKey);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
