package org.conqueror.common.utils.date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateTimeUtils {

    public enum TimeUnit {YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND}

    private static final int ONE_DAY = 1000 * 60 * 60 * 24;
    private static final int ONE_HOUR = 1000 * 60 * 60;
    private static final int ONE_MINUTE = 1000 * 60;
    private static final int ONE_SECOND = 1000;

    private DateTimeUtils() {
    }

    public static Date plusSecond(Date date, int seconds) {
        return plusDate(date, seconds, Calendar.SECOND);
    }

    public static Date plusMinute(Date date, int minutes) {
        return plusDate(date, minutes, Calendar.MINUTE);
    }

    public static Date plusHour(Date date, int hours) {
        return plusDate(date, hours, Calendar.HOUR_OF_DAY);
    }

    public static Date plusDay(Date date, int days) {
        return plusDate(date, days, Calendar.DATE);
    }

    public static Date plusMonth(Date date, int months) {
        return plusDate(date, months, Calendar.MONTH);
    }

    public static Date plusYear(Date date, int years) {
        return plusDate(date, years, Calendar.YEAR);
    }

    public static Date plusDate(Date date, int diff, TimeUnit unit) {
        int field;
        switch (unit) {
            case YEAR:
                field = Calendar.YEAR;
                break;
            case MONTH:
                field = Calendar.MONTH;
                break;
            case DAY:
                field = Calendar.DATE;
                break;
            case HOUR:
                field = Calendar.HOUR;
                break;
            case MINUTE:
                field = Calendar.MINUTE;
                break;
            case SECOND:
                field = Calendar.SECOND;
                break;
            default:
                field = Calendar.MILLISECOND;
                diff = 0;
        }

        return plusDate(date, diff, field);
    }

    private static Date plusDate(Date date, int diff, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, diff);

        return cal.getTime();
    }

    public static DateTime plusDateTime(DateTime dateTime, int diff, TimeUnit unit) {
        switch (unit) {
            case YEAR:
                return dateTime.plusYears(diff);
            case MONTH:
                return dateTime.plusMonths(diff);
            case DAY:
                return dateTime.plusDays(diff);
            case HOUR:
                return dateTime.plusHours(diff);
            case MINUTE:
                return dateTime.plusMinutes(diff);
            case SECOND:
                return dateTime.plusSeconds(diff);
            default:
                return dateTime;
        }
    }

    /*
     * 단위 시간 비교 : 해당 단위 시간 아랫 단위를 제외한 시간 차이 비교
     */
    public static int getTimeUnitDiff(Date after, Date before, TimeUnit unit) {
        Calendar afterCal = Calendar.getInstance();
        Calendar beforeCal = Calendar.getInstance();
        long diff;

        afterCal.setTime(after);
        beforeCal.setTime(before);

        if (TimeUnit.DAY.compareTo(unit) >= 0) {
            afterCal.set(Calendar.HOUR, 0);
            beforeCal.set(Calendar.HOUR, 0);
        }

        if (TimeUnit.HOUR.compareTo(unit) >= 0) {
            afterCal.set(Calendar.MINUTE, 0);
            beforeCal.set(Calendar.MINUTE, 0);
        }

        if (TimeUnit.MINUTE.compareTo(unit) >= 0) {
            afterCal.set(Calendar.SECOND, 0);
            beforeCal.set(Calendar.SECOND, 0);
        }

        afterCal.set(Calendar.MILLISECOND, 0);
        beforeCal.set(Calendar.MILLISECOND, 0);

        diff = afterCal.getTimeInMillis() - beforeCal.getTimeInMillis();

        switch (unit) {
            case SECOND:
                diff /= ONE_SECOND;
                break;
            case MINUTE:
                diff /= ONE_MINUTE;
                break;
            case HOUR:
                diff /= ONE_HOUR;
                break;
            case DAY:
                diff /= ONE_DAY;
                break;
            case MONTH:
                diff = (long) ((afterCal.get(Calendar.YEAR) - beforeCal.get(Calendar.YEAR)) * 12
                    + (afterCal.get(Calendar.MONTH) - beforeCal.get(Calendar.MONTH)));
                break;
            case YEAR:
                diff = (long) (afterCal.get(Calendar.YEAR) - beforeCal.get(Calendar.YEAR));
                break;
            case WEEK:
                int mondayDiff = Calendar.MONDAY - afterCal.get(Calendar.DAY_OF_WEEK);
                if (mondayDiff > 0) mondayDiff = -6;
                afterCal.add(Calendar.DATE, mondayDiff);
                mondayDiff = Calendar.MONDAY - beforeCal.get(Calendar.DAY_OF_WEEK);
                if (mondayDiff > 0) mondayDiff = -6;
                beforeCal.add(Calendar.DATE, mondayDiff);
                diff = getTimeUnitDiff(afterCal.getTime(), beforeCal.getTime(), TimeUnit.DAY) / 7;
                break;
        }

        return (int) diff;
    }

    public static Date min(Date date1, Date date2) {
        int ret = date1.compareTo(date2);
        return ret > 0 ? date2 : date1;
    }

    public static DateTime min(DateTime date1, DateTime date2) {
        int ret = date1.compareTo(date2);
        return ret > 0 ? date2 : date1;
    }

    public static Date max(Date date1, Date date2) {
        int ret = date1.compareTo(date2);
        return ret < 0 ? date2 : date1;
    }

    public static DateTime max(DateTime date1, DateTime date2) {
        int ret = date1.compareTo(date2);
        return ret < 0 ? date2 : date1;
    }

    public static Date getFirstDateTime(Date date, TimeUnit unit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.MILLISECOND, 0);
        if (unit.compareTo(TimeUnit.MINUTE) <= 0) cal.set(Calendar.SECOND, 0);
        if (unit.compareTo(TimeUnit.HOUR) <= 0) cal.set(Calendar.MINUTE, 0);
        if (unit.compareTo(TimeUnit.DAY) <= 0) cal.set(Calendar.HOUR_OF_DAY, 0);
        if (unit.compareTo(TimeUnit.MONTH) <= 0) cal.set(Calendar.DAY_OF_MONTH, 1);
        if (unit.compareTo(TimeUnit.YEAR) == 0) cal.set(Calendar.MONTH, 0);

        return cal.getTime();
    }

    public static DateTime getFirstDateTime(DateTime dt, TimeUnit unit) {
        dt = dt.withField(DateTimeFieldType.millisOfSecond(), dt.millisOfSecond().getMinimumValue());
        if (unit.compareTo(TimeUnit.MINUTE) <= 0) {
            dt = dt.withField(DateTimeFieldType.secondOfMinute(), dt.secondOfMinute().getMinimumValue());
        }
        if (unit.compareTo(TimeUnit.HOUR) <= 0) {
            dt = dt.withField(DateTimeFieldType.minuteOfHour(), dt.minuteOfHour().getMinimumValue());
        }
        if (unit.compareTo(TimeUnit.DAY) <= 0) {
            dt = dt.withField(DateTimeFieldType.hourOfDay(), dt.hourOfDay().getMinimumValue());
        }
        if (unit.compareTo(TimeUnit.MONTH) <= 0) {
            dt = dt.withField(DateTimeFieldType.dayOfMonth(), dt.dayOfMonth().getMinimumValue());
        }
        if (unit.compareTo(TimeUnit.YEAR) == 0) {
            dt = dt.withField(DateTimeFieldType.monthOfYear(), dt.monthOfYear().getMinimumValue());
        }

        return dt;
    }

    public static Date getLastDateTime(Date date, TimeUnit unit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.MILLISECOND, 999);
        if (unit.compareTo(TimeUnit.MINUTE) <= 0) cal.set(Calendar.SECOND, 59);
        if (unit.compareTo(TimeUnit.HOUR) <= 0) cal.set(Calendar.MINUTE, 59);
        if (unit.compareTo(TimeUnit.DAY) <= 0) cal.set(Calendar.HOUR_OF_DAY, 23);
        if (unit.compareTo(TimeUnit.MONTH) <= 0) cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
        if (unit.compareTo(TimeUnit.YEAR) == 0) cal.set(Calendar.MONTH, Calendar.DECEMBER);

        return cal.getTime();
    }

    public static DateTime getLastDateTime(DateTime dt, TimeUnit unit) {
        dt = dt.withField(DateTimeFieldType.millisOfSecond(), dt.millisOfSecond().getMaximumValue());
        if (unit.compareTo(TimeUnit.MINUTE) <= 0)
            dt = dt.withField(DateTimeFieldType.secondOfMinute(), dt.secondOfMinute().getMaximumValue());
        if (unit.compareTo(TimeUnit.HOUR) <= 0)
            dt = dt.withField(DateTimeFieldType.minuteOfHour(), dt.minuteOfHour().getMaximumValue());
        if (unit.compareTo(TimeUnit.DAY) <= 0)
            dt = dt.withField(DateTimeFieldType.hourOfDay(), dt.hourOfDay().getMaximumValue());
        if (unit.compareTo(TimeUnit.MONTH) <= 0)
            dt = dt.withField(DateTimeFieldType.dayOfMonth(), dt.dayOfMonth().getMaximumValue());
        if (unit.compareTo(TimeUnit.YEAR) == 0)
            dt = dt.withField(DateTimeFieldType.monthOfYear(), dt.monthOfYear().getMaximumValue());
        return dt;
    }

    public static Date[] getRangeDates(Date from, Date to, TimeUnit unit) {
        List<Date> dates = new ArrayList<>();
        for (Date date = from; !date.after(to); date = plusDate(date, 1, unit)) {
            dates.add(date);
        }
        return dates.toArray(new Date[0]);
    }

    public static DateTime[] getRangeDates(DateTime from, DateTime to, TimeUnit unit) {
        List<DateTime> dates = new ArrayList<>();
        for (DateTime date = from; !date.isAfter(to); date = plusDateTime(date, 1, unit)) {
            dates.add(date);
        }
        return dates.toArray(new DateTime[0]);
    }

    public static String getDurationString(long seconds) {

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    private static String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        } else if (number < 10) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

}
