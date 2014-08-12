package controllers.report;

import global.common.ModuleBootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Result;

import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;

import controllers.common.BaseController;

public class JsonController extends BaseController {

    private final static long LAGGING = 5 * 1000;
    private final static long DURATION = 60 * 1000;

    /*
     * Handles GET:/json/countersForTag
     */
    public static Promise<Result> countersForTag(final String tagName) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                List<String> result = ModuleBootstrap.getMetadataDao().getCountersForTag(tagName);
                return ok(Json.toJson(result));
            }
        });
        return promise;
    }

    /*
     * Handles GET:/json/rtstats
     */
    public static Promise<Result> rtstats(final String counterName, final String counterName2) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                long timestampEnd = System.currentTimeMillis() - LAGGING;
                timestampEnd = timestampEnd - timestampEnd % 1000;
                long timestampStart = timestampEnd - DURATION;

                List<Object> result = new ArrayList<Object>();
                {
                    ICounter counter = !StringUtils.isBlank(counterName) ? ModuleBootstrap
                            .getCounter(counterName) : null;
                    if (counter != null) {
                        DataPoint[] dpList = counter.getSeries(timestampStart, timestampEnd,
                                ICounter.STEPS_1_SEC, DataPoint.Type.SUM);
                        Map<String, Object> series = new HashMap<String, Object>();
                        result.add(series);
                        series.put("id", counterName);
                        series.put("name", counterName);
                        List<long[]> dataList = new ArrayList<long[]>();
                        series.put("data", dataList);
                        for (DataPoint dp : dpList) {
                            dataList.add(new long[] { dp.timestamp(), dp.value() });
                        }
                    }
                }

                {
                    ICounter counter = !StringUtils.isBlank(counterName2) ? ModuleBootstrap
                            .getCounter(counterName2) : null;
                    if (counter != null) {
                        DataPoint[] dpList = counter.getSeries(timestampStart, timestampEnd,
                                ICounter.STEPS_1_SEC, DataPoint.Type.SUM);
                        Map<String, Object> series = new HashMap<String, Object>();
                        result.add(series);
                        series.put("id", counterName2);
                        series.put("name", counterName2);
                        List<long[]> dataList = new ArrayList<long[]>();
                        series.put("data", dataList);
                        for (DataPoint dp : dpList) {
                            dataList.add(new long[] { dp.timestamp(), dp.value() });
                        }
                    }
                }

                return ok(Json.toJson(result));
            }
        });
        return promise;
    }
}
