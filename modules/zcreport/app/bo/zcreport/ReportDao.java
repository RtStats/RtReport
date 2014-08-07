package bo.zcreport;

import global.zcreport.ModuleBootstrap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import utils.zcreport.DateTimeUtils;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.plommon.bo.jdbc.BaseJdbcDao;

public class ReportDao extends BaseJdbcDao {

    private static Connection getConnection() throws SQLException {
        Connection conn = ModuleBootstrap.getConnection();
        conn.setAutoCommit(true);
        return conn;
    }

    private final static String SQL_GET_REMAIN_BALANCE = "SELECT remainBalance AS remain_balance "
            + "FROM remain_balance "
            + "WHERE analyticDate>=? AND analyticDate<? ORDER BY analyticDate";

    public static Double getRemainBalanceOpeningDay(Calendar date) throws SQLException {
        final ICache CACHE = ModuleBootstrap.getCache("BALANCE_OPENING_DAY");
        final String CACHE_KEY = DateFormatUtils.toString(date.getTime(), "yyyyMMdd");
        Object result = CACHE.get(CACHE_KEY);
        if (result == null || !(result instanceof Double)) {
            Connection conn = getConnection();
            try {
                date = DateTimeUtils.startOfDay(date);
                Calendar prevDate = DateTimeUtils.previousDate(date);
                List<Map<String, Object>> dbRows = select(conn, SQL_GET_REMAIN_BALANCE,
                        new Object[] { prevDate.getTime(), date.getTime() });
                result = dbRows != null && dbRows.size() > 0 ? DPathUtils.getValue(dbRows.get(0),
                        "remain_balance", Double.class) : null;
                CACHE.set(CACHE_KEY, result);
            } finally {
                conn.close();
            }
        }
        return (Double) result;
    }

    public static Double getRemainBalanceClosingDay(Calendar date) throws SQLException {
        final ICache CACHE = ModuleBootstrap.getCache("BALANCE_CLOSING_DAY");
        final String CACHE_KEY = DateFormatUtils.toString(date.getTime(), "yyyyMMdd");
        Object result = CACHE.get(CACHE_KEY);
        if (result == null || !(result instanceof Double)) {
            Connection conn = getConnection();
            try {
                date = DateTimeUtils.startOfDay(date);
                Calendar nextDay = DateTimeUtils.nextDate(date);
                List<Map<String, Object>> dbRows = select(conn, SQL_GET_REMAIN_BALANCE,
                        new Object[] { date.getTime(), nextDay.getTime() });
                result = dbRows != null && dbRows.size() > 0 ? DPathUtils.getValue(dbRows.get(0),
                        "remain_balance", Double.class) : null;
                CACHE.set(CACHE_KEY, result);
            } finally {
                conn.close();
            }
        }
        return (Double) result;
    }

    private final static String SQL_GET_INCOME = "SELECT SUM(amount) AS income "
            + "FROM apps_summary "
            + "WHERE analyticDate>=? AND analyticDate<? AND appID IN ('zing', 'admin')";

    public static Double getTotalIncomeForDate(Calendar date) throws SQLException {
        Connection conn = getConnection();
        try {
            date = DateTimeUtils.startOfDay(date);
            Calendar nextDay = DateTimeUtils.nextDate(date);
            List<Map<String, Object>> dbRows = select(conn, SQL_GET_INCOME,
                    new Object[] { date.getTime(), nextDay.getTime() });
            return dbRows != null && dbRows.size() > 0 ? DPathUtils.getValue(dbRows.get(0),
                    "income", Double.class) : null;
        } finally {
            conn.close();
        }
    }

    private final static String SQL_GET_PAYROLL = "SELECT SUM(amount) AS payroll "
            + "FROM apps_summary "
            + "WHERE analyticDate>=? AND analyticDate<? AND appID NOT IN ('zing', 'admin', 'all')";

    public static Double getTotalPayrollForDate(Calendar date) throws SQLException {
        Connection conn = getConnection();
        try {
            date = DateTimeUtils.startOfDay(date);
            Calendar nextDay = DateTimeUtils.nextDate(date);
            List<Map<String, Object>> dbRows = select(conn, SQL_GET_PAYROLL,
                    new Object[] { date.getTime(), nextDay.getTime() });
            return dbRows != null && dbRows.size() > 0 ? DPathUtils.getValue(dbRows.get(0),
                    "payroll", Double.class) : null;
        } finally {
            conn.close();
        }
    }
}
