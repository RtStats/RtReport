package controllers.pplogin;

import global.pplogin.ModuleBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Result;

import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

import controllers.common.BaseController;

public class PpLoginController extends BaseController {

    private final static String SECTION = "pplogin.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";

    private final static long LAGGING = 5 * 1000;
    private final static long DURATION_FULL = 24 * 1000;
    private final static long DURATION_SHORT = 2 * 1000;

    /*
     * Handles GET:/dashboard
     */
    public static Promise<Result> dashboard() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION_FULL;

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
     * Handles GET:/jsonLoginSummary
     */
    public static Promise<Result> jsonLoginSummary() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION_FULL;

                List<Object> result = new ArrayList<Object>();

                ICounterFactory counterFactory = ModuleBootstrap.getCounterFactory();

                ICounter cLoginTotal = counterFactory.getCounter("login_total");
                DataPoint[] dpTotal = cLoginTotal.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);
                Map<String, Object> seriTotal = new HashMap<String, Object>();
                result.add(seriTotal);
                seriTotal.put("id", "Total");
                seriTotal.put("name", "Total");
                List<long[]> dataTotal = new ArrayList<long[]>();
                seriTotal.put("data", dataTotal);
                for (DataPoint dp : dpTotal) {
                    dataTotal.add(new long[] { dp.timestamp(), dp.value() });
                }

                ICounter cLoginSuccessful = counterFactory.getCounter("login_successful");
                DataPoint[] dpSuccessful = cLoginSuccessful.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);
                Map<String, Object> seriSuccessful = new HashMap<String, Object>();
                result.add(seriSuccessful);
                seriSuccessful.put("id", "Successful");
                seriSuccessful.put("name", "Successful");
                List<long[]> dataSuccessful = new ArrayList<long[]>();
                seriSuccessful.put("data", dataSuccessful);
                for (DataPoint dp : dpSuccessful) {
                    dataSuccessful.add(new long[] { dp.timestamp(), dp.value() });
                }

                ICounter cLoginFailed = counterFactory.getCounter("login_failed");
                DataPoint[] dpFailed = cLoginFailed.getSeries(timestampStart, timestampEnd,
                        ICounter.STEPS_1_SEC, DataPoint.Type.SUM);
                Map<String, Object> seriFailed = new HashMap<String, Object>();
                result.add(seriFailed);
                seriFailed.put("id", "Failed");
                seriFailed.put("name", "Failed");
                List<long[]> dataFailed = new ArrayList<long[]>();
                seriFailed.put("data", dataFailed);
                for (DataPoint dp : dpFailed) {
                    dataFailed.add(new long[] { dp.timestamp(), dp.value() });
                }

                return ok(Json.toJson(result));
            }
        });
        return promise;
    }
}
