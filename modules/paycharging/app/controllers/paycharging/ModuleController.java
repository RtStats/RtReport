package controllers.paycharging;

import global.paycharging.ModuleBootstrap;

import java.util.Calendar;
import java.util.Date;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import utils.common.SiteUtils;
import bo.common.site.SiteBo;
import bo.common.site.SiteDao;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

import controllers.common.AuthRequiredController;

public class ModuleController extends AuthRequiredController {

    private final static String SECTION = "paycharging.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";
    public final static String VIEW_DATE_COMPARE = SECTION + "date_compare";

    public final static int PERIOD_HOUR = 0;
    public final static int PERIOD_DAY = 1;
    public final static int PERIOD_WEEK = 2;
    public final static int PERIOD_MONTH = 3;
    private final static int LAGGING = 15 * 1000;

    /*
     * Handles GET:/dashboard
     */
    public static Promise<Result> dashboard(final int period) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                if (true) {
                    String url = controllers.paycharging.routes.ModuleController.dateCompare(null,
                            null).url();
                    return redirect(url);
                }

                final String siteName = SiteUtils.extractSiteName();
                final SiteBo site = SiteDao.getSite(siteName);
                final String PRODUCT_ID = site.getProduct();
                long DURATION = 60 * 60 * 1000; // last 60 mins
                int STEPS = ICounter.STEPS_1_MIN;
                final Calendar now = Calendar.getInstance();
                now.add(Calendar.MILLISECOND, -LAGGING);
                long timestampEnd = now.getTimeInMillis();
                now.set(Calendar.MILLISECOND, 0);
                now.set(Calendar.SECOND, 0);
                ICounterFactory counterFactory = ModuleBootstrap.getCassandraCounterFactory();

                if (period == PERIOD_WEEK) {
                    DURATION = 7 * 24 * 60 * 60 * 1000; // last 7 days
                    STEPS = ICounter.STEPS_1_HOUR * 24;
                    now.set(Calendar.MINUTE, 0);
                    now.set(Calendar.HOUR_OF_DAY, 0);
                } else if (period == PERIOD_DAY) {
                    DURATION = 24 * 60 * 60 * 1000;// last 24 hours
                    STEPS = ICounter.STEPS_1_HOUR;
                    now.set(Calendar.MINUTE, 0);
                }
                long timestampStart = now.getTimeInMillis() - DURATION + STEPS * 1000;

                DataPoint[] dpVND, dpXu, dpTransaction;
                {
                    ICounter counter = counterFactory.getCounter("pc_product_vnd_" + PRODUCT_ID);
                    dpVND = counter.getSeries(timestampStart, timestampEnd, STEPS,
                            DataPoint.Type.SUM);
                }
                {
                    ICounter counter = counterFactory.getCounter("pc_product_xu_" + PRODUCT_ID);
                    dpXu = counter.getSeries(timestampStart, timestampEnd, STEPS,
                            DataPoint.Type.SUM);
                }
                {
                    ICounter counter = counterFactory.getCounter("pc_product_transaction_"
                            + PRODUCT_ID);
                    dpTransaction = counter.getSeries(timestampStart, timestampEnd, STEPS,
                            DataPoint.Type.SUM);
                }

                int numPoints = dpVND.length;
                long[] xTimestamp = new long[numPoints];
                long[] xVND = new long[numPoints];
                long[] xXu = new long[numPoints];
                long[] xTransaction = new long[numPoints];

                for (int i = 0; i < numPoints; i++) {
                    xTimestamp[i] = dpVND[i].timestamp();
                    xVND[i] = dpVND[i].value();
                    xXu[i] = dpXu[i].value();
                    xTransaction[i] = dpTransaction[i].value();
                }

                Html html = render(VIEW_DASHBOARD, period, xTimestamp, xVND, xXu, xTransaction);
                return ok(html);
            }
        });
        return promise;
    }

    private final static String DF_YYYYMMDD = "yyyy-MM-dd";

    private static Calendar startOfDay(Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    private static Calendar nextDay(Calendar cal) {
        Calendar nextDay = (Calendar) cal.clone();
        nextDay.add(Calendar.DATE, 1);
        nextDay.set(Calendar.MILLISECOND, 0);
        nextDay.set(Calendar.SECOND, 0);
        nextDay.set(Calendar.MINUTE, 0);
        nextDay.set(Calendar.HOUR_OF_DAY, 0);
        return nextDay;
    }

    private static DataPoint[] counterForDate(ICounter counter, Calendar cal) {
        Calendar startOfDay = startOfDay(cal);
        Calendar nextDay = nextDay(cal);
        {
            // TEMP FIX!
            startOfDay.add(Calendar.HOUR_OF_DAY, 7);
            nextDay.add(Calendar.HOUR_OF_DAY, 7);
        }

        DataPoint[] dpList = counter.getSeries(startOfDay.getTimeInMillis(),
                nextDay.getTimeInMillis() - 1, ICounter.STEPS_1_HOUR, DataPoint.Type.SUM);
        System.out.println("Getting DPs for [" + cal.getTime() + "]: from [" + startOfDay.getTime()
                + "] to [" + nextDay.getTime() + "]...");
        System.out.println("\tNum points: " + dpList.length);
        final String DF = "yyyy-MM-dd HH:mm";
        for (DataPoint dp : dpList) {
            Date d = new Date(dp.timestamp());
            System.out.println("\t" + DateFormatUtils.toString(d, DF) + "\t" + dp.value());
        }
        return dpList;
    }

    /*
     * Handles GET:/dateCompare
     */
    public static Promise<Result> dateCompare(final String strDate1, final String strDate2) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Calendar date1 = Calendar.getInstance(); // today
                try {
                    date1.setTime(DateFormatUtils.fromString(strDate1, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                Calendar date2 = Calendar.getInstance();
                date2.add(Calendar.DATE, -1); // yesterday
                try {
                    date2.setTime(DateFormatUtils.fromString(strDate2, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                Calendar date3 = (Calendar) date1.clone();
                date3.add(Calendar.DATE, -7); // 7 days ago

                final String siteName = SiteUtils.extractSiteName();
                final SiteBo site = SiteDao.getSite(siteName);
                final String PRODUCT_ID = site.getProduct();
                ICounterFactory counterFactory = ModuleBootstrap.getCassandraCounterFactory();
                ICounter counter = counterFactory.getCounter("pc_product_xu_" + PRODUCT_ID);
                DataPoint[] dpDate1 = counterForDate(counter, date1);
                DataPoint[] dpDate2 = counterForDate(counter, date2);
                DataPoint[] dpDate3 = counterForDate(counter, date3);

                int numPoints = dpDate1.length;
                String[] xTimestamp = new String[numPoints];
                long[] xDate1 = new long[numPoints];
                long[] xDate2 = new long[numPoints];
                long[] xDate3 = new long[numPoints];

                long sumDate1 = 0, sumDate2 = 0, sumDate3 = 0;
                for (int i = 0; i < numPoints; i++) {
                    xTimestamp[i] = i + ":00";
                    xDate1[i] = dpDate1[i].value();
                    sumDate1 += xDate1[i];
                    xDate2[i] = dpDate2[i].value();
                    sumDate2 += xDate2[i];
                    xDate3[i] = dpDate3[i].value();
                    sumDate3 += xDate3[i];
                }

                Html html = render(VIEW_DATE_COMPARE,
                        DateFormatUtils.toString(date1.getTime(), DF_YYYYMMDD),
                        DateFormatUtils.toString(date2.getTime(), DF_YYYYMMDD),
                        DateFormatUtils.toString(date3.getTime(), DF_YYYYMMDD), xTimestamp, xDate1,
                        sumDate1, xDate2, sumDate2, xDate3, sumDate3);
                return ok(html);
            }
        });
        return promise;
    }
}
