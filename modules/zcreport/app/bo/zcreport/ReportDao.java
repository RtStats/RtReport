package bo.zcreport;

import global.zcreport.ModuleBootstrap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
                if (result != null) {
                    CACHE.set(CACHE_KEY, result);
                }
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
                if (result != null) {
                    CACHE.set(CACHE_KEY, result);
                }
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
        final ICache CACHE = ModuleBootstrap.getCache("TOTAL_INCOME_DATE");
        final String CACHE_KEY = DateFormatUtils.toString(date.getTime(), "yyyyMMdd");
        Object result = CACHE.get(CACHE_KEY);
        if (result == null || !(result instanceof Double)) {
            Connection conn = getConnection();
            try {
                date = DateTimeUtils.startOfDay(date);
                Calendar nextDay = DateTimeUtils.nextDate(date);
                List<Map<String, Object>> dbRows = select(conn, SQL_GET_INCOME,
                        new Object[] { date.getTime(), nextDay.getTime() });
                result = dbRows != null && dbRows.size() > 0 ? DPathUtils.getValue(dbRows.get(0),
                        "income", Double.class) : null;
                if (result != null) {
                    CACHE.set(CACHE_KEY, result);
                }
            } finally {
                conn.close();
            }
        }
        return (Double) result;
    }

    private final static String SQL_GET_PAYROLL = "SELECT SUM(amount) AS payroll "
            + "FROM apps_summary "
            + "WHERE analyticDate>=? AND analyticDate<? AND appID NOT IN ('zing', 'admin', 'all')";

    public static Double getTotalPayrollForDate(Calendar date) throws SQLException {
        final ICache CACHE = ModuleBootstrap.getCache("TOTAL_PAYROLL_DATE");
        final String CACHE_KEY = DateFormatUtils.toString(date.getTime(), "yyyyMMdd");
        Object result = CACHE.get(CACHE_KEY);
        if (result == null || !(result instanceof Double)) {
            Connection conn = getConnection();
            try {
                date = DateTimeUtils.startOfDay(date);
                Calendar nextDay = DateTimeUtils.nextDate(date);
                List<Map<String, Object>> dbRows = select(conn, SQL_GET_PAYROLL, new Object[] {
                        date.getTime(), nextDay.getTime() });
                result = dbRows != null && dbRows.size() > 0 ? DPathUtils.getValue(dbRows.get(0),
                        "payroll", Double.class) : null;
                if (result != null) {
                    CACHE.set(CACHE_KEY, result);
                }
            } finally {
                conn.close();
            }
        }
        return (Double) result;
    }

    private final static String SQL_GET_APP_LIST_FOR_DATES = "SELECT DISTINCT appID AS app_id "
            + "FROM apps_summary "
            + "WHERE appID NOT IN ('zing','admin','all') AND analyticDate>=? AND analyticDate<? "
            + "ORDER BY app_id";

    @SuppressWarnings("unchecked")
    public static List<String> getAppListForDates(Calendar fromDate, Calendar toDate)
            throws SQLException {
        final ICache CACHE = ModuleBootstrap.getCache("APP_LIST_DATES");
        final String CACHE_KEY = DateFormatUtils.toString(fromDate.getTime(), "yyyyMMdd") + "-"
                + DateFormatUtils.toString(toDate.getTime(), "yyyyMMdd");
        Object result = CACHE.get(CACHE_KEY);
        if (result == null || !(result instanceof List)) {
            Connection conn = getConnection();
            try {
                fromDate = DateTimeUtils.startOfDay(fromDate);
                toDate = DateTimeUtils.startOfDay(toDate);
                Calendar nextDay = DateTimeUtils.nextDate(toDate);
                List<Map<String, Object>> dbRows = select(conn, SQL_GET_APP_LIST_FOR_DATES,
                        new Object[] { fromDate.getTime(), nextDay.getTime() });
                result = new ArrayList<String>();
                if (dbRows != null) {
                    for (Map<String, Object> row : dbRows) {
                        ((List<String>) result).add(row.get("app_id").toString());
                    }
                }
                CACHE.set(CACHE_KEY, result);
            } finally {
                conn.close();
            }
        }
        return (List<String>) result;
    }

    private final static String SQL_GET_ANALYTIC_FOR_DATES = "SELECT appID AS app_id, analyticDate AS analytic_date, amount AS amount "
            + "FROM apps_summary "
            + "WHERE appID NOT IN ('all', 'zing', 'admin') AND analyticDate>=? AND analyticDate<? "
            + "ORDER BY analyticDate";

    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Double>> getAnalyticForDates(Calendar fromDate,
            Calendar toDate) throws SQLException {
        final ICache CACHE = ModuleBootstrap.getCache("SQL_GET_ANALYTIC_FOR_DATES");
        final String CACHE_KEY = DateFormatUtils.toString(fromDate.getTime(), "yyyyMMdd") + "-"
                + DateFormatUtils.toString(toDate.getTime(), "yyyyMMdd");
        Object result = CACHE.get(CACHE_KEY);
        if (result == null || !(result instanceof Map)) {
            Connection conn = getConnection();
            try {
                fromDate = DateTimeUtils.startOfDay(fromDate);
                toDate = DateTimeUtils.startOfDay(toDate);
                Calendar nextDay = DateTimeUtils.nextDate(toDate);
                List<Map<String, Object>> dbRows = select(conn, SQL_GET_ANALYTIC_FOR_DATES,
                        new Object[] { fromDate.getTime(), nextDay.getTime() });
                Map<String, Map<String, Double>> temp = new HashMap<String, Map<String, Double>>();
                if (dbRows != null) {
                    for (Map<String, Object> row : dbRows) {
                        String appId = DPathUtils.getValue(row, "app_id", String.class);
                        Date analyticDate = DPathUtils.getValue(row, "analytic_date", Date.class);
                        Double amount = DPathUtils.getValue(row, "amount", Double.class);
                        Map<String, Double> appData = temp.get(appId);
                        if (appData == null) {
                            appData = new HashMap<String, Double>();
                            temp.put(appId, appData);
                        }
                        appData.put(DateFormatUtils.toString(analyticDate, "yyyy-MM-dd"), amount);
                    }
                }
                result = temp;
                CACHE.set(CACHE_KEY, result);
            } finally {
                conn.close();
            }
        }
        return (Map<String, Map<String, Double>>) result;
    }
}
