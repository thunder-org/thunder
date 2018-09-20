package org.conqueror.common.utils.date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeUtils {

	public enum TimeUnit { YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND }

	private static final int ONE_DAY = 1000 * 60 * 60 * 24;
	private static final int ONE_HOUR = 1000 * 60 * 60;

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

	public static Date plusDate(Date date, int diff, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, diff);

		return cal.getTime();
	}

	public static Date plusDate(Date date, int diff, TimeUnit unit) {
		int field;
		switch (unit) {
			case YEAR: field = Calendar.YEAR; break;
			case MONTH: field = Calendar.MONTH; break;
			case DAY: field = Calendar.DATE; break;
			case HOUR: field = Calendar.HOUR; break;
			case MINUTE: field = Calendar.MINUTE; break;
			case SECOND: field = Calendar.SECOND; break;
			default: return null;
		}

		return plusDate(date, diff, field);
	}

	/*
	 * 단위 시간 비교 : 해당 단위 시간 아랫 단위를 제외한 시간 차이 비교
	 */
	public static int getTimeUnitDiff(Date after, Date before, TimeUnit unit) {
		Calendar afterCal = Calendar.getInstance();
		Calendar beforeCal = Calendar.getInstance();
		int diff = 0;

		afterCal.setTime(after);
		beforeCal.setTime(before);

		switch (unit) {
		case HOUR:
			afterCal.set(Calendar.MINUTE, 0);
			afterCal.set(Calendar.SECOND, 0);
			beforeCal.set(Calendar.MINUTE, 0);
			beforeCal.set(Calendar.SECOND, 0);
			diff = (int) ((afterCal.getTimeInMillis() - beforeCal.getTimeInMillis()) / ONE_HOUR);
			break;
		case DAY:
			afterCal.set(Calendar.HOUR, 0);
			afterCal.set(Calendar.MINUTE, 0);
			afterCal.set(Calendar.SECOND, 0);
			beforeCal.set(Calendar.HOUR, 0);
			beforeCal.set(Calendar.MINUTE, 0);
			beforeCal.set(Calendar.SECOND, 0);
			diff = (int) ((afterCal.getTimeInMillis() - beforeCal.getTimeInMillis()) / ONE_DAY);
			break;
		case MONTH:
			diff = (afterCal.get(Calendar.YEAR) - beforeCal.get(Calendar.YEAR)) * 12
					+ (afterCal.get(Calendar.MONTH) - beforeCal.get(Calendar.MONTH));
			break;
		case YEAR:
			diff = afterCal.get(Calendar.YEAR) - beforeCal.get(Calendar.YEAR);
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
		default:
			diff = -1;
			break;
		}

		return diff;
	}

	public static Date min(Date date1, Date date2) {
		int ret = date1.compareTo(date2);
		return ret > 0? date2 : date1;
	}

	public static DateTime min(DateTime date1, DateTime date2) {
		int ret = date1.compareTo(date2);
		return ret > 0? date2 : date1;
	}

	public static Date max(Date date1, Date date2) {
		int ret = date1.compareTo(date2);
		return ret < 0? date2 : date1;
	}

	public static DateTime max(DateTime date1, DateTime date2) {
		int ret = date1.compareTo(date2);
		return ret < 0? date2 : date1;
	}

	public static Date getFromDate(Date date, TimeUnit unit) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		switch (unit) {
		case YEAR:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
		case MONTH:
			cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
		case DAY:
			cal.set(Calendar.HOUR_OF_DAY, 0);
		case HOUR:
			cal.set(Calendar.MINUTE, 0);
		case MINUTE:
			cal.set(Calendar.SECOND, 0);
		case SECOND:
			cal.set(Calendar.MILLISECOND, 0);
			break;
		default:
			return null;
		}

		return cal.getTime();
	}

	public static DateTime getFromDate(DateTime dt, TimeUnit unit) {
		switch (unit) {
		case YEAR:
			dt = dt.withField(DateTimeFieldType.monthOfYear(), dt.monthOfYear().getMinimumValue());
		case MONTH:
			dt = dt.withField(DateTimeFieldType.dayOfMonth(), dt.dayOfMonth().getMinimumValue());
		case DAY:
			dt = dt.withField(DateTimeFieldType.hourOfDay(), dt.hourOfDay().getMinimumValue());
		case HOUR:
			dt = dt.withField(DateTimeFieldType.minuteOfHour(), dt.minuteOfHour().getMinimumValue());
		case MINUTE:
			dt = dt.withField(DateTimeFieldType.secondOfMinute(), dt.secondOfMinute().getMinimumValue());
		case SECOND:
			dt = dt.withField(DateTimeFieldType.millisOfSecond(), dt.millisOfSecond().getMinimumValue());
			break;
		default:
			return null;
		}
		return dt;
	}

	public static Date getToDate(Date date, TimeUnit unit) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		switch (unit) {
		case YEAR:
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
		case MONTH:
			cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
		case DAY:
			cal.set(Calendar.HOUR_OF_DAY, 23);
		case HOUR:
			cal.set(Calendar.MINUTE, 59);
		case MINUTE:
			cal.set(Calendar.SECOND, 59);
		case SECOND:
			cal.set(Calendar.MILLISECOND, 999);
			break;
		default:
			return null;
		}

		return cal.getTime();
	}

	public static DateTime getToDate(DateTime dt, TimeUnit unit) {
		switch (unit) {
		case YEAR:
			dt = dt.withField(DateTimeFieldType.monthOfYear(), dt.monthOfYear().getMaximumValue());
		case MONTH:
			dt = dt.withField(DateTimeFieldType.dayOfMonth(), dt.dayOfMonth().getMaximumValue());
		case DAY:
			dt = dt.withField(DateTimeFieldType.hourOfDay(), dt.hourOfDay().getMaximumValue());
		case HOUR:
			dt = dt.withField(DateTimeFieldType.minuteOfHour(), dt.minuteOfHour().getMaximumValue());
		case MINUTE:
			dt = dt.withField(DateTimeFieldType.secondOfMinute(), dt.secondOfMinute().getMaximumValue());
		case SECOND:
			dt = dt.withField(DateTimeFieldType.millisOfSecond(), dt.millisOfSecond().getMaximumValue());
			break;
		default:
			return null;
		}
		return dt;
	}

	public static Date[] getRangeDates(Date from, Date to, TimeUnit unit) {
		List<Date> dates = new ArrayList<>();
		for (Date date=from; !date.after(to); ) {
			dates.add(date);
			switch (unit) {
				case YEAR: date = plusYear(date, 1);
				case MONTH: date = plusMonth(date, 1);
				case DAY: date = plusDay(date, 1);
				case HOUR: date = plusHour(date, 1);
				case MINUTE: date = plusMinute(date, 1);
				case SECOND: date = plusSecond(date, 1);
			}
		}
		return dates.toArray(new Date[dates.size()]);
	}

	public static DateTime[] getRangeDates(DateTime from, DateTime to, TimeUnit unit) {
		List<DateTime> dates = new ArrayList<>();
		for (DateTime date = from; !date.isAfter(to); ) {
			dates.add(date);
			switch (unit) {
				case YEAR: date = date.plusYears(1); break;
				case MONTH: date = date.plusMonths(1); break;
				case DAY: date = date.plusDays(1); break;
				case HOUR: date = date.plusHours(1); break;
				case MINUTE: date = date.plusMinutes(1); break;
				case SECOND: date = date.plusSeconds(1); break;
			}
		}
		return dates.toArray(new DateTime[dates.size()]);
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
