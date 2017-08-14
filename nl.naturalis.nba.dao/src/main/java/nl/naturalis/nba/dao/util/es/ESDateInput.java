package nl.naturalis.nba.dao.util.es;

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
 * Handles input for date fields in an Elasticsearch index. The input can be
 * index-time date strings or query-time date strings. Both are re-formatted to
 * the date format accepted by NBA indices (yyyy-MM-dd'T'HH:mm:ss.SSSZ). Input
 * for date fields may have any of the following date format patterns:
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
public class ESDateInput {

	private static final String ES_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private static final String[] ACCEPTED_LOCAL_DATE_TIME_FORMATS = new String[] {
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm" };

	private static final String[] ACCEPTED_LOCAL_DATE_FORMATS = new String[] { "yyyy-MM-dd" };

	private static final String[] ACCEPTED_YEAR_MONTH_FORMATS = new String[] { "yyyy-MM" };

	public static String[] getAcceptedDateFormats()
	{
		ArrayList<String> formats = new ArrayList<>();
		formats.add(ES_DATE_FORMAT);
		formats.addAll(Arrays.asList(ACCEPTED_LOCAL_DATE_TIME_FORMATS));
		formats.addAll(Arrays.asList(ACCEPTED_LOCAL_DATE_FORMATS));
		formats.addAll(Arrays.asList(ACCEPTED_YEAR_MONTH_FORMATS));
		formats.add("yyyy");
		return formats.toArray(new String[formats.size()]);
	}

	/**
	 * Reformats the specified date string according to the date format pattern
	 * used by the NBA indices.
	 * 
	 * @param dateString
	 * @return
	 */
	public String toESFormat(String dateString)
	{
		OffsetDateTime odt = parse(dateString);
		if (odt == null) {
			return null;
		}
		return format(odt);
	}

	/**
	 * Formats the specified date string according to the date format pattern
	 * used by the NBA indices.
	 * 
	 * @param dateString
	 * @return
	 */
	public String toESFormat(Date date)
	{
		return format(convert(date));
	}

	/**
	 * Formats the specified date string according to the date format pattern
	 * used by the NBA indices.
	 * 
	 * @param dateString
	 * @return
	 */
	@SuppressWarnings("static-method")
	public String toESFormat(OffsetDateTime date)
	{
		return format(date);
	}

	/**
	 * Parses the specified date string into a classic {@link Date
	 * java.util.Date}.
	 * 
	 * @param dateString
	 * @return
	 */
	public Date toJavaUtilDate(String dateString)
	{
		OffsetDateTime odt = parse(dateString);
		if (odt == null) {
			return null;
		}
		return Date.from(odt.toInstant());
	}

	/**
	 * Parses the specified date string into an {@link OffsetDateTime}.
	 * 
	 * @param dateString
	 * @return
	 */
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

	private static String format(OffsetDateTime odt)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ES_DATE_FORMAT);
		return odt.format(formatter);
	}

}
