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
import java.util.Date;

import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;

/**
 * Handles input for date fields in an Elasticsearch index. Generally this means
 * query-time input for date fields (date string in {@link QueryCondition query
 * conditions}). However this class is also useful for data imports. The date
 * format accepted by the NBA indices is yyyy-MM-dd'T'HH:mm:ssxx (for example:
 * 2017-08-21T08:40:59+0200), so date strings in {@link QuerySpec} objects need
 * to be re-formatted using this pattern. The following date formats can be used
 * in {@code QuerySpec} objects:
 * <ol>
 * <li>yyyy-MM-dd'T'HH:mm:ssxx - The date format used by the NBA indices
 * themselves, for example: 2017-08-21T08:40:59+0200
 * <li>yyyy-MM-dd'T'HH:mm[:ss][.SSS]Z - The
 * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME default format} used by
 * {@link OffsetDateTime} when parsing date strings. This format allows for
 * milliseconds and requires a colon within the time zone, for example:
 * 2017-08-21T08:40:59.880+02:00
 * <li>yyyy-MM-dd HH:mm:ss
 * <li>yyyy-MM-dd'T' HH:mm:ss
 * <li>yyyy-MM-dd HH:mm
 * <li>yyyy-MM-dd'T'HH:mm
 * <li>yyyy-MM-dd
 * <li>yyyy-MM
 * <li>yyyy
 * </ol>
 * 
 * @author Ayco Holleman
 *
 */
public final class ESDateInput {

	/**
	 * The date format used to store dates in the NBA indices:
	 * yyyy-MM-dd'T'HH:mm:ssZ
	 */
	public static final String ES_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter
			.ofPattern(ES_DATE_FORMAT);

	private static final DateTimeFormatter[] ACCEPTED_LOCAL_DATE_TIME_FORMATTERS = new DateTimeFormatter[] {
			DateTimeFormatter.ISO_LOCAL_DATE_TIME,
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]") };

	private static final DateTimeFormatter[] ACCEPTED_LOCAL_DATE_FORMATTERS = new DateTimeFormatter[] {
			DateTimeFormatter.ISO_LOCAL_DATE };

	private static final DateTimeFormatter[] ACCEPTED_YEAR_MONTH_FORMATTERS = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("yyyy-MM") };

	/**
	 * Formats the specified date according to the date format pattern used by
	 * the NBA indices. This method is null-safe; if the argument passed to it
	 * is {@code null}, {@code null} is returned.
	 * 
	 * @param odt
	 * @return
	 */
	public static String format(OffsetDateTime odt)
	{
		if (odt == null) {
			return null;
		}
		return odt.format(DEFAULT_FORMATTER);
	}

	/**
	 * Formats a &34;classic&34; {@link Date java.util.Date} string according to
	 * the date format pattern used by the NBA indices.
	 * 
	 * @param dateString
	 * @return
	 */
	public static String format(Date date)
	{
		return format(convert(date));
	}

	private final String dateString;

	public ESDateInput(String dateString)
	{
		this.dateString = dateString;
	}

	/**
	 * Returns the accepted query-time date formats. See class comments.
	 * 
	 * @return
	 */
	public static String[] getAcceptedDateFormats()
	{
		return new String[] { ES_DATE_FORMAT, "yyyy-MM-dd'T'HH:mm[:ss][.SSS]Z",
				"yyyy-MM-dd'T'HH:mm[:ss]", "yyyy-MM-dd HH:mm[:ss]", "yyyy-MM-dd", "yyyy-MM",
				"yyyy" };
	}

	/**
	 * Converts a classic {@link Date java.util.Date} to an
	 * {@link OffsetDateTime} instance.
	 * 
	 * @param date
	 * @return
	 */
	public static OffsetDateTime convert(Date date)
	{
		Instant instant = date.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
	}

	/**
	 * Re-formats the specified date string according to the date format pattern
	 * used by the NBA indices.
	 * 
	 * @param dateString
	 * @return
	 */
	public String toESFormat()
	{
		OffsetDateTime odt = parse();
		if (odt == null) {
			return null;
		}
		return format(odt);
	}

	/**
	 * Attempts to parse the specified date string into an
	 * {@link OffsetDateTime} using any of the accepted date format patterns,
	 * starting with the most detailed patterns down to the year-only pattern.
	 * 
	 * @param dateString
	 * @return
	 */
	public OffsetDateTime parse()
	{
		OffsetDateTime date;
		if (null != (date = parseAsOffsetDateTime())) {
			return date;
		}
		if (null != (date = parseAsLocalDateTime())) {
			return date;
		}
		if (null != (date = parseAsLocalDate())) {
			return date;
		}
		if (null != (date = parseAsYearMonth())) {
			return date;
		}
		if (null != (date = parseAsYear())) {
			return date;
		}
		return null;
	}

	/**
	 * Parses the specified date string using pattern yyyy-MM-dd'T'HH:mm:ssZ
	 * 
	 * @param dateString
	 * @return
	 */
	public OffsetDateTime parseAsOffsetDateTime()
	{
		try {
			return OffsetDateTime.parse(dateString, DEFAULT_FORMATTER);
		}
		catch (DateTimeParseException e) {
			try {
				return OffsetDateTime.parse(dateString);
			}
			catch (DateTimeParseException e2) {
				return null;
			}
		}
	}

	/**
	 * Parses the specified date string using the specified pattern.
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public OffsetDateTime parseAsOffsetDateTime(String pattern)
	{
		return parseAsOffsetDateTime(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * Parses the specified date string using the specified formatter.
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public OffsetDateTime parseAsOffsetDateTime(DateTimeFormatter formatter)
	{
		try {
			return OffsetDateTime.parse(dateString, formatter);
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
	public OffsetDateTime parseAsLocalDateTime()
	{
		for (DateTimeFormatter formatter : ACCEPTED_LOCAL_DATE_TIME_FORMATTERS) {
			try {
				LocalDateTime date = LocalDateTime.parse(dateString, formatter);
				return OffsetDateTime.of(date, ZoneOffset.UTC);
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
	public OffsetDateTime parseAsLocalDateTime(String pattern)
	{
		return parseAsLocalDateTime(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * Parses the specified date string using the specified formatter.
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public OffsetDateTime parseAsLocalDateTime(DateTimeFormatter formatter)
	{
		try {
			LocalDateTime date = LocalDateTime.parse(dateString, formatter);
			return OffsetDateTime.of(date, ZoneOffset.UTC);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

	/**
	 * Parses the specified date string using the accepted date patterns.
	 * 
	 * @param dateString
	 * @return
	 */
	public OffsetDateTime parseAsLocalDate()
	{
		for (DateTimeFormatter formatter : ACCEPTED_LOCAL_DATE_FORMATTERS) {
			try {
				LocalDate date = LocalDate.parse(dateString, formatter);
				return OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC);
			}
			catch (DateTimeParseException e) {}
		}
		return null;
	}

	/**
	 * Parses the specified date string using the specified pattern. The date
	 * string must represent a {@link LocalDate}, which then is converted to an
	 * {@link OffsetDateTime}.
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public OffsetDateTime parseAsLocalDate(String pattern)
	{
		return parseAsLocalDate(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * Parses the specified date string using the specified formatter. The date
	 * string must represent a {@link LocalDate}, which then is converted to an
	 * {@link OffsetDateTime}.
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public OffsetDateTime parseAsLocalDate(DateTimeFormatter formatter)
	{
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
	public OffsetDateTime parseAsYearMonth()
	{
		for (DateTimeFormatter formatter : ACCEPTED_YEAR_MONTH_FORMATTERS) {
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
	public OffsetDateTime parseAsYear()
	{
		try {
			Year year = Year.parse(dateString);
			return OffsetDateTime.of(year.atDay(1).atStartOfDay(), ZoneOffset.UTC);
		}
		catch (DateTimeParseException e) {}
		return null;
	}

	/**
	 * Parses the specified date string into a classic {@link Date
	 * java.util.Date}.
	 * 
	 * @param dateString
	 * @return
	 */
	public Date toJavaUtilDate()
	{
		OffsetDateTime odt = parse();
		if (odt == null) {
			return null;
		}
		return Date.from(odt.toInstant());
	}

}
