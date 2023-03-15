package fr.cimut.ged.entrant.utils;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateHelper {

	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd").withZone(DateTimeZone.forID("Europe/Paris"));

	private static final DateTimeFormatter formatter_yyyyMMddhhmmss = DateTimeFormat.forPattern("yyyyMMddhhmmss");

	private static final DateTimeFormatter formatter_ISO_8601 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(DateTimeZone.UTC);

	public static DateTime stringToDate(String strDate) throws ParseException {
		DateTime date = formatter.parseDateTime(strDate);
		return date;
	}

	public static String convert(String asText) throws ParseException {
		DateTime date = stringToDate(asText);
		return convert(date);
	}

	public static LocalDate dateToLocalDate(Date date) {
		return LocalDate.fromDateFields(date);
	}

	public static String convert(DateTime date) throws ParseException {
		DateTime dateUTC = date.withZone(DateTimeZone.UTC);
		DateTimeFormatter parser = ISODateTimeFormat.dateTime();
		return parser.print(dateUTC);
	}

	public static String toLocalTime(String strDate) throws ParseException {
		DateTime dt = formatter_ISO_8601.parseDateTime(strDate);
		DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
		return dtfOut.print(dt.withZone(DateTimeZone.forID("Europe/Paris")));
	}

	public static String getTimeStampNow() {
		DateTime date = new DateTime(new Date());
		return formatter_yyyyMMddhhmmss.print(date);
	}

	public static Date stringISOToDate(String dateISO) {
		return formatter_ISO_8601.parseDateTime(dateISO).toDate();
	}

	public static String formatyyyymmddToddmmyyyyWithSlashes(String stToFormat) {

		String format = "";
		try {
			if (StringUtils.isNotBlank(stToFormat)) {
				DateTime dt = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(stToFormat);
				if (null != dt) {
					format = DateTimeFormat.forPattern("dd/MM/yyyy").print(dt);
				}
			}
		} catch (Exception e) {
		}
		return format;
	}

}
