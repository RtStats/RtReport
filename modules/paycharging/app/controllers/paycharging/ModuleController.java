package controllers.paycharging;

import global.paycharging.ModuleBootstrap;

import java.util.Calendar;

import play.api.templates.Html;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import utils.common.SiteUtils;
import bo.common.site.SiteBo;
import bo.common.site.SiteDao;

import com.github.ddth.tsc.DataPoint;
import com.github.ddth.tsc.ICounter;
import com.github.ddth.tsc.ICounterFactory;

import controllers.common.AuthRequiredController;

public class ModuleController extends AuthRequiredController {

    private final static String SECTION = "paycharging.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";
    public final static String VIEW_LOGIN_SUMMARY_RT = SECTION + "login_summary_rt";
    public final static String VIEW_LOGIN_ACTIONID_RT = SECTION + "login_actionid_rt";

    public final static String VIEW_LOGIN_SUMMARY = SECTION + "login_summary";

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
}
