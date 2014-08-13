package controllers.report;

import global.common.ModuleBootstrap;
import global.common.Registry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.api.templates.Html;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import rtreport.common.Constants;
import utils.common.DateTimeUtils;
import bo.common.metadata.IMetadataDao;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;

import controllers.common.BaseController;

public class ModuleController extends BaseController {
    private final static String SECTION = "report.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";
    public final static String VIEW_RTSTATS = SECTION + "rtstats";
    public final static String VIEW_REPORT_SINGLE = SECTION + "report_single";
    public final static String VIEW_REPORT_CROSS = SECTION + "report_cross";

    /*
     * Handles GET:/dashboard
     */
    public static Promise<Result> dashboard() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                IMetadataDao metadataDao = ModuleBootstrap.getMetadataDao();
                List<String> tags = metadataDao.getAllTags();
                List<String> counters = metadataDao.getAllCounters();

                Html html = render(VIEW_DASHBOARD, tags.toArray(ArrayUtils.EMPTY_STRING_ARRAY),
                        counters.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/rtstats
     */
    public static Promise<Result> rtstats(final String tag, final String counter,
            final String tag2, final String counter2) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                IMetadataDao metadataDao = ModuleBootstrap.getMetadataDao();
                List<String> tags = metadataDao.getAllTags();
                String tagName = tag;
                if (StringUtils.isBlank(tagName)) {
                    tagName = tags.size() > 0 ? tags.get(0) : "";
                }
                List<String> counters = !StringUtils.isBlank(tagName) ? metadataDao
                        .getCountersForTag(tagName) : new ArrayList<String>();
                String counterName = counter;
                if (StringUtils.isBlank(counterName)) {
                    counterName = counters.size() > 0 ? counters.get(0) : "";
                }

                String tagName2 = !StringUtils.isBlank(tag2) ? tag2 : "";
                List<String> counters2 = !StringUtils.isBlank(tagName2) ? metadataDao
                        .getCountersForTag(tagName2) : new ArrayList<String>();
                String counterName2 = !StringUtils.isBlank(counter2) ? counter2 : "";

                Html html = render(VIEW_RTSTATS, tags.toArray(ArrayUtils.EMPTY_STRING_ARRAY),
                        counters.toArray(ArrayUtils.EMPTY_STRING_ARRAY),
                        counters2.toArray(ArrayUtils.EMPTY_STRING_ARRAY), tagName, counterName,
                        tagName2, counterName2);
                return ok(html);
            }
        });
        return promise;
    }

    private final static String DF_YYYYMMDD = "yyyy-MM-dd";
    // private final static String DF_LABEL = "HH:mm dd/MM";
    private final static long MAX_MONTH_MS = 30 * 24 * 3600 * (long) 1000;

    /*
     * Handles GET:/reportSingle
     */
    public static Promise<Result> reportSingle(final String tag, final String counter,
            final String fromDateStr, final String toDateStr) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Calendar now = DateTimeUtils.startOfDay(Calendar.getInstance());

                Calendar dateFrom = (Calendar) now.clone();
                dateFrom.add(Calendar.DATE, -1); // yesterday
                Calendar dateTo = (Calendar) dateFrom.clone();

                try {
                    dateFrom.setTime(DateFormatUtils.fromString(fromDateStr, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                try {
                    dateTo.setTime(DateFormatUtils.fromString(toDateStr, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                IMetadataDao metadataDao = ModuleBootstrap.getMetadataDao();
                List<String> tags = metadataDao.getAllTags();
                String tagName = tag;
                if (StringUtils.isBlank(tag)) {
                    tagName = tags.size() > 0 ? tags.get(0) : "";
                }
                List<String> counters = metadataDao.getCountersForTag(tagName);
                String counterName = counter;
                if (StringUtils.isBlank(counterName)) {
                    counterName = counters.size() > 0 ? counters.get(0) : "";
                }

                long[] xTimestamp = ArrayUtils.EMPTY_LONG_ARRAY;
                long[] xDp = ArrayUtils.EMPTY_LONG_ARRAY;
                long sum = 0;
                String df = "dd-MMM HH:mm";

                Lang lang = Registry.getLanguage();
                if (dateFrom.after(dateTo)) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.from_date_to_date");
                    flash(VIEW_REPORT_SINGLE, msg);
                } else if (!dateFrom.before(now)) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.from_date_now");
                    flash(VIEW_REPORT_SINGLE, msg);
                } else if (dateTo.getTimeInMillis() - dateFrom.getTimeInMillis() > MAX_MONTH_MS) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.long_from_date_to_date");
                    flash(VIEW_REPORT_SINGLE, msg);
                } else {
                    ICounter counter = !StringUtils.isBlank(counterName) ? ModuleBootstrap
                            .getCounter(counterName) : null;
                    if (counter != null) {
                        final long timestampStart = dateFrom.getTimeInMillis();
                        final long timestampEnd = DateTimeUtils.nextDate(dateTo).getTimeInMillis() - 1;

                        int STEPS = ICounter.STEPS_1_HOUR;
                        final long duration = timestampEnd - timestampStart;
                        if (duration > 2 * 24 * 3600 * 1000) {
                            STEPS = ICounter.STEPS_1_HOUR * 24;
                            df = "dd-MMM";
                        }
                        DataPoint[] dp = counter.getSeries(timestampStart, timestampEnd, STEPS,
                                DataPoint.Type.SUM);
                        int numPoints = dp.length;
                        xTimestamp = new long[numPoints];
                        xDp = new long[numPoints];
                        for (int i = 0; i < numPoints; i++) {
                            xTimestamp[i] = dp[i].timestamp();
                            xDp[i] = dp[i].value();
                            sum += xDp[i];
                        }
                    }
                }

                Html html = render(VIEW_REPORT_SINGLE, tags.toArray(ArrayUtils.EMPTY_STRING_ARRAY),
                        counters.toArray(ArrayUtils.EMPTY_STRING_ARRAY), tagName, counterName,
                        DateFormatUtils.toString(dateFrom.getTime(), DF_YYYYMMDD),
                        DateFormatUtils.toString(dateTo.getTime(), DF_YYYYMMDD), xTimestamp, xDp,
                        sum, df);
                return ok(html);
            }
        });
        return promise;
    }

    /*
     * Handles GET:/reportCross
     */
    public static Promise<Result> reportCross(final String fromDateStr, final String toDateStr) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            @SuppressWarnings("unchecked")
            public Result apply() throws Exception {
                Collection<String> selectedCounterNames = new HashSet<String>();
                String[] counterList = request().queryString().get("c");
                if (counterList != null) {
                    for (String counterName : counterList) {
                        selectedCounterNames.add(counterName);
                    }
                }

                IMetadataDao metadataDao = ModuleBootstrap.getMetadataDao();
                List<String> counters = metadataDao.getAllCounters();
                List<Object> data = new ArrayList<Object>();

                Calendar now = DateTimeUtils.startOfDay(Calendar.getInstance());
                Calendar dateFrom = (Calendar) now.clone();
                dateFrom.add(Calendar.DATE, -1); // yesterday
                Calendar dateTo = (Calendar) dateFrom.clone();
                try {
                    dateFrom.setTime(DateFormatUtils.fromString(fromDateStr, DF_YYYYMMDD));
                } catch (Exception e) {
                }
                try {
                    dateTo.setTime(DateFormatUtils.fromString(toDateStr, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                String df = "dd-MMM HH:mm";
                long[] xTimestamp = ArrayUtils.EMPTY_LONG_ARRAY;

                Lang lang = Registry.getLanguage();
                if (dateFrom.after(dateTo)) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.from_date_to_date");
                    flash(VIEW_REPORT_CROSS, msg);
                } else if (!dateFrom.before(now)) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.from_date_now");
                    flash(VIEW_REPORT_CROSS, msg);
                } else if (dateTo.getTimeInMillis() - dateFrom.getTimeInMillis() > MAX_MONTH_MS) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.long_from_date_to_date");
                    flash(VIEW_REPORT_CROSS, msg);
                } else {
                    int STEPS = ICounter.STEPS_1_HOUR;

                    final long timestampStart = dateFrom.getTimeInMillis();
                    final long timestampEnd = DateTimeUtils.nextDate(dateTo).getTimeInMillis() - 1;
                    final long duration = timestampEnd - timestampStart;
                    if (duration > 2 * 24 * 3600 * 1000) {
                        // more than 2 days
                        STEPS = ICounter.STEPS_1_HOUR * 24;
                        df = "dd-MMM";
                    }

                    Map<String, Object> seriTotal = new HashMap<String, Object>();
                    List<long[]> seriTotalData = new ArrayList<long[]>();
                    seriTotal.put("id", "*");
                    seriTotal.put("name", "*");
                    seriTotal.put("data", seriTotalData);
                    long grandTotal = 0;
                    for (String counterName : selectedCounterNames) {
                        ICounter counter = ModuleBootstrap.getCounter(counterName);
                        if (counter != null) {
                            Map<String, Object> seri = new HashMap<String, Object>();
                            data.add(seri);
                            List<long[]> seriData = new ArrayList<long[]>();
                            seri.put("id", counterName);
                            seri.put("name", counterName);
                            seri.put("data", seriData);

                            DataPoint[] dpList = counter.getSeries(timestampStart, timestampEnd,
                                    STEPS, DataPoint.Type.SUM);
                            if (xTimestamp.length == 0) {
                                xTimestamp = new long[dpList.length];
                                for (int i = 0; i < dpList.length; i++) {
                                    xTimestamp[i] = dpList[i].timestamp();
                                }
                            }
                            long sum = 0;
                            for (DataPoint dp : dpList) {
                                sum += dp.value();
                                seriData.add(new long[] { dp.timestamp(), dp.value() });
                            }
                            seri.put("sum", sum);
                            grandTotal += sum;
                        }
                    }
                    seriTotal.put("sum", grandTotal);
                    seriTotal.put("percent", "100.0%");
                    data.add(seriTotal);

                    // calc percent
                    for (Object temp : data) {
                        if (grandTotal == 0) {
                            DPathUtils.setValue(temp, "percent", "-");
                        } else {
                            long sum = DPathUtils.getValue(temp, "sum", long.class);
                            double percent = (double) sum * 1.0 / (double) grandTotal;
                            percent *= 100.0;
                            String percentStr = String.format("%.1f", percent);
                            DPathUtils.setValue(temp, "percent", percentStr + "%");
                        }
                    }

                    // calc sum for seri "total"
                    for (int i = 0; i < xTimestamp.length; i++) {
                        long sumTotal = 0;
                        for (Object temp : data) {
                            String id = DPathUtils.getValue(temp, "id", String.class);
                            List<long[]> seriData = DPathUtils.getValue(temp, "data", List.class);
                            if ("*".equals(id)) {
                                seriData.add(new long[] { xTimestamp[i], sumTotal });
                            } else {
                                sumTotal += seriData.get(i)[1];
                            }
                        }
                    }
                }

                Html html = render(VIEW_REPORT_CROSS, selectedCounterNames,
                        counters.toArray(ArrayUtils.EMPTY_STRING_ARRAY),
                        DateFormatUtils.toString(dateFrom.getTime(), DF_YYYYMMDD),
                        DateFormatUtils.toString(dateTo.getTime(), DF_YYYYMMDD), xTimestamp,
                        data.toArray(), df);
                return ok(html);
            }
        });
        return promise;
    }
}
