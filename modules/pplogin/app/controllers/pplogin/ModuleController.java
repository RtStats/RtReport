package controllers.pplogin;

import global.pplogin.ModuleBootstrap;
import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;

import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

import controllers.common.BaseController;

public class ModuleController extends BaseController {

    private final static String SECTION = "pplogin.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";
    public final static String VIEW_LOGIN_SUMMARY_RT = SECTION + "login_summary_rt";

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

                ICounterFactory counterFactory = ModuleBootstrap.getCounterFactory();

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

                ICounterFactory counterFactory = ModuleBootstrap.getCounterFactory();

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
}
