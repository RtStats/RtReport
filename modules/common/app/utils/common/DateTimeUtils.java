package utils.common;

import java.util.Calendar;

public class DateTimeUtils {
    public static Calendar startOfDay(Calendar cal) {
        Calendar temp = (Calendar) cal.clone();
        temp.set(Calendar.MILLISECOND, 0);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MINUTE, 0);
        temp.set(Calendar.HOUR_OF_DAY, 0);
        return temp;
    }

    public static Calendar nextDate(Calendar cal) {
        Calendar temp = (Calendar) cal.clone();
        temp.add(Calendar.DATE, 1);
        return temp;
    }

    public static Calendar previousDate(Calendar cal) {
        Calendar temp = (Calendar) cal.clone();
        temp.add(Calendar.DATE, -1);
        return temp;
    }

}
