package controllers.pplogin;

import global.pplogin.ModuleBootstrap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

import controllers.common.BaseController;

public class ModuleController extends BaseController {

    private final static String SECTION = "pplogin.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";
    public final static String VIEW_LOGIN_SUMMARY_RT = SECTION + "login_summary_rt";
    public final static String VIEW_LOGIN_ACTIONID_RT = SECTION + "login_actionid_rt";

    public final static String VIEW_LOGIN_SUMMARY = SECTION + "login_summary";

    private final static long LAGGING = 5 * 1000;
    private final static long DURATION = 24 * 1000;

    /*
     * Handles GET:/dashboard
     */
    public static Promise<Result> dashboard() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION;

                ICounterFactory counterFactory = ModuleBootstrap.getRedisCounterFactory();

                ICounter cLoginTotal = counterFactory.getCounter("login_total");
                DataPoint[] dpTotal = cLoginTotal.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);

                ICounter cLoginSuccessful = counterFactory.getCounter("login_successful");
                DataPoint[] dpSuccessful = cLoginSuccessful.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);

                ICounter cLoginFailed = counterFactory.getCounter("login_failed");
                DataPoint[] dpFailed = cLoginFailed.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);

                int numPoints = dpTotal.length;
                long[] xTimestamp = new long[numPoints];
                long[] xTotal = new long[numPoints];
                long[] xFailed = new long[numPoints];
                long[] xSuccessful = new long[numPoints];

                for (int i = 0; i < numPoints; i++) {
                    xTimestamp[i] = dpTotal[i].timestamp();
                    xTotal[i] = dpTotal[i].value();
                    xFailed[i] = dpFailed[i].value();
                    xSuccessful[i] = dpSuccessful[i].value();
                }

                Html html = render(VIEW_DASHBOARD, xTimestamp, xTotal, xSuccessful, xFailed);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/loginSummaryRt
     */
    public static Promise<Result> loginSummaryRt() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION;

                ICounterFactory counterFactory = ModuleBootstrap.getRedisCounterFactory();

                ICounter cLoginTotal = counterFactory.getCounter("login_total");
                DataPoint[] dpTotal = cLoginTotal.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);

                ICounter cLoginSuccessful = counterFactory.getCounter("login_successful");
                DataPoint[] dpSuccessful = cLoginSuccessful.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);

                ICounter cLoginFailed = counterFactory.getCounter("login_failed");
                DataPoint[] dpFailed = cLoginFailed.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);

                int numPoints = dpTotal.length;
                long[] xTimestamp = new long[numPoints];
                long[] xTotal = new long[numPoints];
                long[] xFailed = new long[numPoints];
                long[] xSuccessful = new long[numPoints];

                for (int i = 0; i < numPoints; i++) {
                    xTimestamp[i] = dpTotal[i].timestamp();
                    xTotal[i] = dpTotal[i].value();
                    xFailed[i] = dpFailed[i].value();
                    xSuccessful[i] = dpSuccessful[i].value();
                }

                Html html = render(VIEW_LOGIN_SUMMARY_RT, xTimestamp, xTotal, xSuccessful, xFailed);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/loginSummary
     */
    public static Promise<Result> loginSummary() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Calendar cal = Calendar.getInstance();
                Date toDate = cal.getTime();

                cal.add(Calendar.HOUR, -1);
               
                Date fromDate = cal.getTime();
                final String dateFormat = "MM/dd/yyyy hh:00 a";
                SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                Date dfFromDate= df.parse(df.format(fromDate));
                Date dfToDate=df.parse(df.format(toDate));
                
                
                long timestampEnd = dfToDate.getTime();
                long timestampStart = dfFromDate.getTime();
               
                ICounterFactory counterFactory = ModuleBootstrap.getCassandraCounterFactory();

                ICounter cLoginTotal = counterFactory.getCounter("login_total");
                DataPoint[] dpTotal = cLoginTotal.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_MIN, DataPoint.Type.SUM);

                ICounter cLoginSuccessful = counterFactory.getCounter("login_successful");
                DataPoint[] dpSuccessful = cLoginSuccessful.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_MIN, DataPoint.Type.SUM);

                ICounter cLoginFailed = counterFactory.getCounter("login_failed");
                DataPoint[] dpFailed = cLoginFailed.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_MIN, DataPoint.Type.SUM);

                int numPoints = dpTotal.length;
                long[] xTimestamp = new long[numPoints];
                long[] xTotal = new long[numPoints];
                long[] xFailed = new long[numPoints];
                long[] xSuccessful = new long[numPoints];

                for (int i = 0; i < numPoints; i++) {
                    xTimestamp[i] = dpTotal[i].timestamp();
                    xTotal[i] = dpTotal[i].value();
                    xFailed[i] = dpFailed[i].value();
                    xSuccessful[i] = dpSuccessful[i].value();
                }

                
                String fromDateStr = DateFormatUtils.toString(dfFromDate, dateFormat);
                String toDateStr = DateFormatUtils.toString(dfToDate, dateFormat);
                Html html = render(VIEW_LOGIN_SUMMARY,fromDateStr,toDateStr, xTimestamp, xTotal, xSuccessful, xFailed);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/loginActionIdRt
     */
    public static Promise<Result> loginActionIdRt() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_LOGIN_ACTIONID_RT);
                return ok(html);
            }
        });
        return promise;
    }
}
