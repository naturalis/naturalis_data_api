package nl.naturalis.nba.dao.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Converts strings (supposedly) representing dates to instances of
 * {@link OffsetDateTime}. This class has no opinion on what to do if the
 * conversion fails. Its {@link #parse(String) parse} method simply returns
 * {@code null} and it is up to clients to throw an exception, or not. The
 * following date format patterns are attempted in sequence to parse the date
 * string with:
 * <ol>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ (a.k.a. ISO8601)
 * <li>yyyy-MM-dd HH:mm:ss
 * <li>yyyy-MM-dd HH:mm
 * <li>yyyy-MM-dd
 * <li>yyyy-MM
 * <li>yyyy
 * </ol>
 * 
 * @author Ayco Holleman
 *
 */
public class DateString {

	private static final String[] ACCEPTED_LOCAL_DATE_TIME_FORMATS = new String[] {
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm" };

	private static final String[] ACCEPTED_LOCAL_DATE_FORMATS = new String[] { "yyyy-MM-dd" };

	private static final String[] ACCEPTED_YEAR_MONTH_FORMATS = new String[] { "yyyy-MM" };

	public static String[] getAcceptedDateFormats()
	{
		ArrayList<String> formats = new ArrayList<>();
		formats.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		formats.addAll(Arrays.asList(ACCEPTED_LOCAL_DATE_TIME_FORMATS));
		formats.addAll(Arrays.asList(ACCEPTED_LOCAL_DATE_FORMATS));
		formats.addAll(Arrays.asList(ACCEPTED_YEAR_MONTH_FORMATS));
		formats.add("yyyy");
		return formats.toArray(new String[formats.size()]);
	}

	@SuppressWarnings("static-method")
	public OffsetDateTime parse(String dateString)
	{
		OffsetDateTime date;
		if (null != (date = tryIso8601(dateString))) {
			return date;
		}
		if (null != (date = tryLocalDateTime(dateString))) {
			return date;
		}
		if (null != (date = tryLocalDate(dateString))) {
			return date;
		}
		if (null != (date = tryYearMonth(dateString))) {
			return date;
		}
		if (null != (date = tryYear(dateString))) {
			return date;
		}
		return null;
	}

	@SuppressWarnings("static-method")
	public OffsetDateTime convert(Date date)
	{
		Instant instant = date.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
	}

	private static OffsetDateTime tryIso8601(String dateString)
	{
		try {
			return OffsetDateTime.parse(dateString);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

	private static OffsetDateTime tryLocalDateTime(String dateString)
	{
		DateTimeFormatter formatter;
		for (String format : ACCEPTED_LOCAL_DATE_TIME_FORMATS) {
			formatter = DateTimeFormatter.ofPattern(format);
			try {
				LocalDateTime date = LocalDateTime.parse(dateString, formatter);
				return OffsetDateTime.of(date, ZoneOffset.UTC);
			}
			catch (DateTimeParseException e) {}
		}
		return null;
	}

	private static OffsetDateTime tryLocalDate(String dateString)
	{
		DateTimeFormatter formatter;
		for (String format : ACCEPTED_LOCAL_DATE_FORMATS) {
			formatter = DateTimeFormatter.ofPattern(format);
			try {
				LocalDate date = LocalDate.parse(dateString, formatter);
				return OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC);
			}
			catch (DateTimeParseException e) {}
		}
		return null;
	}

	private static OffsetDateTime tryYearMonth(String dateString)
	{
		DateTimeFormatter formatter;
		for (String format : ACCEPTED_YEAR_MONTH_FORMATS) {
			formatter = DateTimeFormatter.ofPattern(format);
			try {
				YearMonth ym = YearMonth.parse(dateString, formatter);
				return OffsetDateTime.of(ym.atDay(1).atStartOfDay(), ZoneOffset.UTC);
			}
			catch (DateTimeParseException e) {}
		}
		return null;
	}

	private static OffsetDateTime tryYear(String dateString)
	{
		try {
			Year year = Year.parse(dateString);
			return OffsetDateTime.of(year.atDay(1).atStartOfDay(), ZoneOffset.UTC);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

}
