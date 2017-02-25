package cz.skaut.warehousemanager.helper;


import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateTimeUtils {

	private static final Locale locale = new Locale("cs", "CZ");

	private DateTimeUtils() {
		// no instances
	}

	public static Long getTimestampFromDate(String input, String inputFormat) {
		DateFormat dateFormat = new SimpleDateFormat(inputFormat, locale);
		Date date;
		try {
			date = dateFormat.parse(input);
		} catch (ParseException e) {
			Timber.e("Parse error" + e.getMessage());
			return 0L;
		}
		return date.getTime();
	}

	public static String getFormattedTimestamp(long input, String outputFormat) {
		DateFormat dateFormat = new SimpleDateFormat(outputFormat, locale);
		return dateFormat.format(new Date(input));
	}

	public static String getFormattedDate(String input) {
		if (TextUtils.isEmpty(input)) {
			return "";
		}
		DateFormat inputFormat = new SimpleDateFormat(C.DATETIME_FORMAT, locale);
		Date parsedDate;
		try {
			parsedDate = inputFormat.parse(input);
		} catch (ParseException e) {
			Timber.e("Parse error" + e.getMessage());
			return "";
		}
		return new SimpleDateFormat(C.DATE_FORMAT, locale).format(parsedDate);
	}

	public static String getFormattedDate(String input, String inputFormat, String outputFormat) {
		if (TextUtils.isEmpty(input)) {
			return "";
		}
		DateFormat inputDateFormat = new SimpleDateFormat(inputFormat, locale);
		Date parsedDate;
		try {
			parsedDate = inputDateFormat.parse(input);
		} catch (ParseException e) {
			Timber.e("Parse error" + e.getMessage());
			return "";
		}
		return new SimpleDateFormat(outputFormat, locale).format(parsedDate);
	}

	public static String getFormattedToday(String dateFormat) {
		return new SimpleDateFormat(dateFormat, locale).format(new Date());
	}

	public static long getCurrentTimestamp() {
		return new Date().getTime();
	}
}
