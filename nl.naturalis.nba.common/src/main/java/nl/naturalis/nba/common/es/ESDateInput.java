package nl.naturalis.nba.common.es;

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
 * the date format accepted by NBA indices (yyyy-MM-dd'T'HH:mm:ssZ). Input for
 * date fields may have any of the following date format patterns:
 * <ol>
 * <li>yyyy-MM-dd'T'HH:mm:ssZ (the date format used by the NBA indices
 * themselves)
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

	/**
	 * The date format used to store dates in the NBA indices.
	 */
	public static final String ES_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static final String[] ACCEPTED_LOCAL_DATE_TIME_FORMATS = new String[] {
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm" };

	private static final String[] ACCEPTED_LOCAL_DATE_FORMATS = new String[] { "yyyy-MM-dd" };

	private static final String[] ACCEPTED_YEAR_MONTH_FORMATS = new String[] { "yyyy-MM" };

	/**
	 * Formats the specified date according to the date format pattern used by
	 * the NBA indices (&34;yyyy-MM-dd'T'HH:mm:ssZ&34;).
	 * 
	 * @param odt
	 * @return
	 */
	public static String format(OffsetDateTime odt)
	{
		if (odt == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ES_DATE_FORMAT);
		return odt.format(formatter);
	}

	/**
	 * Returns the accepted query-time date formats. See class comments.
	 * 
	 * @return
	 */
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
	 * used by the NBA indices (&34;yyyy-MM-dd'T'HH:mm:ssZ&34;).
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
	 * Formats a &34;classic&34; {@link Date java.util.Date} string according to
	 * the date format pattern used by the NBA indices
	 * (&34;yyyy-MM-dd'T'HH:mm:ssZ&34;).
	 * 
	 * @param dateString
	 * @return
	 */
	public String toESFormat(Date date)
	{
		return format(convert(date));
	}

	/**
	 * Formats the specified date according to the date format pattern used by
	 * the NBA indices (&34;yyyy-MM-dd'T'HH:mm:ssZ&34;).
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
	 * Attempts to parse the specified date string into an
	 * {@link OffsetDateTime} using any of the accepted date format pattern,
	 * starting with the most detailed pattern down to the year-only pattern.
	 * 
	 * @param dateString
	 * @return
	 */
	public OffsetDateTime parse(String dateString)
	{
		OffsetDateTime date;
		if (null != (date = parseAsIso8601(dateString))) {
			return date;
		}
		if (null != (date = parseAsLocalDateTime(dateString))) {
			return date;
		}
		if (null != (date = parseAsLocalDate(dateString))) {
			return date;
		}
		if (null != (date = parseAsYearMonth(dateString))) {
			return date;
		}
		if (null != (date = parseAsYear(dateString))) {
			return date;
		}
		return null;
	}

	/**
	 * Converts a classic {@link Date java.util.Date} to an
	 * {@link OffsetDateTime} instance.
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime convert(Date date)
	{
		Instant instant = date.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
	}

	/**
	 * Parses the specified date string using pattern yyyy-MM-dd'T'HH:mm:ssZ
	 * 
	 * @param dateString
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime parseAsIso8601(String dateString)
	{
		try {
			return OffsetDateTime.parse(dateString);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

	/**
	 * Parses the specified date string using the accepted date+time patterns.
	 * 
	 * @param dateString
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime parseAsLocalDateTime(String dateString)
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

	/**
	 * Parses the specified date string using the accepted date patterns.
	 * 
	 * @param dateString
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime parseAsLocalDate(String dateString)
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

	/**
	 * Parses the specified date string using the specified pattern.
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime parseAsLocalDate(String dateString, String pattern)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		try {
			LocalDate date = LocalDate.parse(dateString, formatter);
			return OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

	/**
	 * Parses the specified date string using the accepted year+month patterns.
	 * 
	 * @param dateString
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime parseAsYearMonth(String dateString)
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

	/**
	 * Parses the specified date string using the accepted pattern yyyy.
	 * 
	 * @param dateString
	 * @return
	 */
	@SuppressWarnings("static-method")
	public OffsetDateTime parseAsYear(String dateString)
	{
		try {
			Year year = Year.parse(dateString);
			return OffsetDateTime.of(year.atDay(1).atStartOfDay(), ZoneOffset.UTC);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

}
