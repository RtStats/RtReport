package controllers.zcreport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.zcreport.ReportSummaryEntry;
import play.api.templates.Html;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Result;
import utils.zcreport.DateTimeUtils;
import vngup.rtreports.common.Constants;
import bo.zcreport.ReportDao;

import com.github.ddth.commons.utils.DateFormatUtils;

import controllers.common.AuthRequiredController;

public class ModuleController extends AuthRequiredController {

    private final static String SECTION = "zcreport.";
    public final static String VIEW_DASHBOARD = SECTION + "dashboard";
    public final static String VIEW_REPORT_SUMMARY = SECTION + "report_summary";

    /*
     * Handles GET:/dashboard
     */
    public static Promise<Result> dashboard() {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Html html = render(VIEW_DASHBOARD);
                return ok(html);
            }
        });
        return promise;
    }

    private final static String DF_YYYYMMDD = "yyyy-MM-dd";

    /*
     * Handles GET:/summary
     */
    public static Promise<Result> reportSummary(final String strDate1, final String strDate2) {
        Promise<Result> promise = Promise.promise(new Function0<Result>() {
            public Result apply() throws Exception {
                Calendar now = DateTimeUtils.startOfDay(Calendar.getInstance()); // today

                Calendar date1 = (Calendar) now.clone();
                date1.add(Calendar.DATE, -1); // yesterday
                Calendar date2 = (Calendar) date1.clone();

                try {
                    date1.setTime(DateFormatUtils.fromString(strDate1, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                try {
                    date2.setTime(DateFormatUtils.fromString(strDate2, DF_YYYYMMDD));
                } catch (Exception e) {
                }

                final long MAX_MONTH_MS = 30 * 24 * 3600 * (long) 1000;
                Lang lang = Lang.forCode("vi");
                List<ReportSummaryEntry> data = new ArrayList<ReportSummaryEntry>();
                if (date1.after(date2)) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.from_date_to_date");
                    flash(VIEW_REPORT_SUMMARY, msg);
                } else if (!date1.before(now)) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.from_date_now");
                    flash(VIEW_REPORT_SUMMARY, msg);
                } else if (date2.getTimeInMillis() - date1.getTimeInMillis() > MAX_MONTH_MS) {
                    String msg = Constants.FLASH_MSG_PREFIX_ERROR
                            + Messages.get(lang, "error.long_from_date_to_date");
                    flash(VIEW_REPORT_SUMMARY, msg);
                } else {
                    Calendar cal = (Calendar) date1.clone();
                    while (!cal.after(date2)) {
                        Double openingBalance = ReportDao.getRemainBalanceOpeningDay(cal);
                        Double closingBalance = ReportDao.getRemainBalanceClosingDay(cal);
                        Double income = ReportDao.getTotalIncomeForDate(cal);
                        Double payroll = ReportDao.getTotalPayrollForDate(cal);
                        String strDate = DateFormatUtils.toString(cal.getTime(), DF_YYYYMMDD);
                        ReportSummaryEntry entry = new ReportSummaryEntry();
                        data.add(entry);
                        entry.openingBalance = openingBalance;
                        entry.closingBalance = closingBalance;
                        entry.income = income;
                        entry.payroll = payroll;
                        entry.date = strDate;
                        if (openingBalance == null || openingBalance == null || income == null
                                || payroll == null) {
                            entry.error = true;
                        } else if (openingBalance + income - payroll != closingBalance) {
                            entry.error = true;
                        }
                        cal = DateTimeUtils.nextDate(cal);
                    }
                }

                Html html = render(VIEW_REPORT_SUMMARY,
                        DateFormatUtils.toString(date1.getTime(), DF_YYYYMMDD),
                        DateFormatUtils.toString(date2.getTime(), DF_YYYYMMDD), data);
                return ok(html);
            }
        });
        return promise;
    }
}
