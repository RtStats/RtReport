package controllers.pplogin;

import global.pplogin.ModuleBootstrap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Result;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.plommon.utils.PlayAppUtils;
import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

import controllers.common.BaseController;

public class JsonController extends BaseController {

    private final static long LAGGING = 5 * 1000;
    private final static long DURATION = 24 * 1000;

    /*
     * Handles GET:/jsonLoginSummaryRt
     */
    public static Promise<Result> jsonLoginSummaryRt() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION;

                List<Object> result = new ArrayList<Object>();

                ICounterFactory counterFactory = ModuleBootstrap.getRedisCounterFactory();

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

    public static Promise<Result> jsonLoginSummary() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                String datetime = PlayAppUtils.queryString("datetime");
                String[] arrDatetime = datetime.split("-");
                String fromDateStr = arrDatetime[0];
                String toDateStr = arrDatetime[1];
                Date fromDate = DateFormatUtils.fromString(fromDateStr, "MM/dd/yyyy hh:mm a");

                Date toDate = DateFormatUtils.fromString(toDateStr, "MM/dd/yyyy hh:mm a");

                long timestampEnd = toDate.getTime();
                long timestampStart = fromDate.getTime();
                long duration = timestampEnd - timestampStart;
                int steps = ICounter.STEPS_1_MIN;
                if (duration < 2 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_5_MINS;
                } else if (duration >= 2 * 60 * 60 * (long) 1000
                        && duration < 5 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_5_MINS;
                } else if (duration >= 5 * 60 * 60 * (long) 1000
                        && duration < 10 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_10_MINS;
                } else if (duration >= 10 * 60 * 60 * (long) 1000
                        && duration < 18 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_15_MINS;
                } else if (duration >= 18 * 60 * 60 * (long) 1000
                        && duration < 24 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_30_MINS;
                } else if (duration >= 24 * 60 * 60 * (long) 1000
                        && duration < 2 * 24 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_1_HOUR;
                } else if (duration >= 2 * 24 * 60 * 60 * (long) 1000
                        && duration < 31 * 24 * 60 * 60 * (long) 1000) {
                    steps = ICounter.STEPS_1_HOUR * 24;
                } else {
                    return badRequest("Thời gian report tối đa là 1 tháng");
                }

                List<Object> result = new ArrayList<Object>();

                ICounterFactory counterFactory = ModuleBootstrap.getCassandraCounterFactory();

                ICounter cLoginTotal = counterFactory.getCounter("login_total");
                DataPoint[] dpTotal = cLoginTotal.getSeries(timestampStart, timestampEnd, steps,
                        DataPoint.Type.SUM);
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
                        steps, DataPoint.Type.SUM);
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
                DataPoint[] dpFailed = cLoginFailed.getSeries(timestampStart, timestampEnd, steps,
                        DataPoint.Type.SUM);
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

    private final static int[] ACTION_ID_LIST = { 1, 0, 2, 1000, 1001, 2005, 3, 4, 5 };

    private static String actionIdToStr(int actionId) {
        switch (actionId) {
        case 0:
            return "Error";
        case 1:
            return "Ok";
        case 2:
            return "Failed";
        case 1000:
            return "InvalidAccount";
        case 1001:
            return "InvalidPassword";
        case 2005:
            return "WrongPassword";
        case 3:
            return "Banned";
        case 4:
            return "NoAccount";
        case 5:
            return "InvalidParam";
        default:
            return "_" + actionId;
        }
    }

    /*
     * Handles GET:/jsonLoginActionIdRt
     */
    public static Promise<Result> jsonLoginActionIdRt() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION;

                List<Object> result = new ArrayList<Object>();

                ICounterFactory counterFactory = ModuleBootstrap.getRedisCounterFactory();

                for (int actionId : ACTION_ID_LIST) {
                    String counterName = "login_action_" + actionId;
                    ICounter counter = counterFactory.getCounter(counterName);
                    DataPoint[] dpTotal = counter.getSeries(timestampStart, timestampEnd,
                            ICounter.STEPS_1_SEC, DataPoint.Type.SUM);
                    Map<String, Object> seriTotal = new HashMap<String, Object>();
                    result.add(seriTotal);
                    String _temp = actionIdToStr(actionId);
                    seriTotal.put("id", _temp);
                    seriTotal.put("name", _temp);
                    List<long[]> dataTotal = new ArrayList<long[]>();
                    seriTotal.put("data", dataTotal);
                    for (DataPoint dp : dpTotal) {
                        dataTotal.add(new long[] { dp.timestamp(), dp.value() });
                    }
                }

                return ok(Json.toJson(result));
            }
        });
        return promise;
    }
}
