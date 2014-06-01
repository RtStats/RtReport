package truyen.common.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.db.DB;
import truyen.common.Constants;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.plommon.bo.jdbc.BaseMysqlDao;

public class CounterDao extends BaseMysqlDao {

    public final static String TABLE_COUNTER = Constants.APP_ID + "_counter";
    private final static String SQL_UPDATE = MessageFormat.format(
            "UPDATE {0} SET cvalue=cvalue+1 WHERE cname=?", TABLE_COUNTER);
    private final static String SQL_INSERT = MessageFormat.format(
            "INSERT INTO {0} (cname, cvalue) VALUES (?, 1)", TABLE_COUNTER);
    private final static String SQL_SELECT = MessageFormat.format(
            "SELECT cvalue FROM {0} WHERE cname=?", TABLE_COUNTER);

    public static long nextId(String counterName) {
        Long value = null;
        Connection conn = DB.getConnection(false);
        try {
            try {
                conn.setAutoCommit(false);
                int numUpdated = update(SQL_UPDATE, new Object[] { counterName });
                if (numUpdated < 1) {
                    insert(SQL_INSERT, new Object[] { counterName });
                }
                List<Map<String, Object>> dbRows = select(SQL_SELECT, new Object[] { counterName });
                value = DPathUtils.getValue(dbRows, "[0].cvalue", Long.class);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage(), e);
        }
        return value != null ? value.longValue() : -1;
    }
}
