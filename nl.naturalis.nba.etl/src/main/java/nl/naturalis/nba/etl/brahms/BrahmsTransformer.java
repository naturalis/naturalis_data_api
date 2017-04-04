package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.COLLECTOR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.CONTINENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.COUNTRY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DAY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.LATITUDE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.LOCNOTES;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.LONGITUDE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MAJORAREA;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MONTH;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.TYPE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.YEAR;

import java.util.Arrays;
import java.util.Date;

import org.joda.time.LocalDate;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;
import nl.naturalis.nba.etl.normalize.UnmappedValueException;
import nl.naturalis.nba.utils.ConfigObject;

abstract class BrahmsTransformer<T extends IDocumentObject>
		extends AbstractCSVTransformer<BrahmsCsvField, T> {

	static final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	static final ThemeCache themeCache;

	private static final String MSG_INVALID_NUMBER = "Invalid number in field %s: \"%s\" (value set to 0)";
	private static final String MSG_INVALID_DATE = "Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s";;

	static {
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
	}

	BrahmsTransformer(ETLStatistics stats)
	{
		super(stats);
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
	}

	@Override
	protected String getObjectID()
	{
		return input.get(BARCODE);
	}

	SpecimenTypeStatus getTypeStatus()
	{
		try {
			return typeStatusNormalizer.map(input.get(TYPE));
		}
		catch (UnmappedValueException e) {
			if (!suppressErrors) {
				warn(e.getMessage());
			}
			return null;
		}
	}

	void populateGatheringEvent(GatheringEvent ge, CSVRecordInfo<BrahmsCsvField> record)
	{
		ge.setWorldRegion(record.get(CONTINENT));
		ge.setContinent(ge.getWorldRegion());
		ge.setCountry(record.get(COUNTRY));
		ge.setProvinceState(record.get(MAJORAREA));
		StringBuilder sb = new StringBuilder(50);
		if (ge.getWorldRegion() != null) {
			sb.append(ge.getWorldRegion());
		}
		if (ge.getCountry() != null) {
			if (sb.length() != 0) {
				sb.append("; ");
			}
			sb.append(ge.getCountry());
		}
		if (ge.getProvinceState() != null) {
			if (sb.length() != 0) {
				sb.append("; ");
			}
			sb.append(ge.getProvinceState());
		}
		String locNotes = record.get(LOCNOTES);
		if (locNotes != null) {
			ge.setLocality(locNotes);
			if (sb.length() != 0) {
				sb.append("; ");
			}
			sb.append(locNotes);
		}
		ge.setLocalityText(sb.toString());
		String y = record.get(YEAR);
		String m = record.get(MONTH);
		String d = record.get(DAY);
		ge.setDateTimeBegin(getDate(y, m, d, false));
		ge.setDateTimeEnd(getDate(y, m, d, true));
		Double lat = getDouble(record, LATITUDE);
		Double lon = getDouble(record, LONGITUDE);
		if (lat == 0D && lon == 0D) {
			lat = null;
			lon = null;
		}
		if (lon != null && (lon < -180D || lon > 180D)) {
			error("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90D || lat > 90D)) {
			error("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new GatheringSiteCoordinates(lat, lon)));
		}
		String collector = record.get(COLLECTOR);
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
	}

	Double getDouble(CSVRecordInfo<BrahmsCsvField> record, BrahmsCsvField field)
	{
		String s = record.get(field);
		if (s == null)
			return null;
		try {
			return Double.valueOf(s);
		}
		catch (NumberFormatException e) {
			if (!suppressErrors)
				warn(String.format(MSG_INVALID_NUMBER, field.ordinal(), s));
			return null;
		}
	}

	Float getFloat(CSVRecordInfo<BrahmsCsvField> record, BrahmsCsvField field)
	{
		String s = record.get(field);
		if (s == null)
			return null;
		try {
			return Float.valueOf(s);
		}
		catch (NumberFormatException e) {
			if (!suppressErrors) {
				warn(String.format(MSG_INVALID_NUMBER, field.ordinal(), s));
			}
			return null;
		}
	}

	/*
	 * Constructs a Date object from the date fields in a Brahms export file.
	 * Used to construct a begin and end date from problematic gathering event
	 * dates in the source data. If year is empty or zero, null is returned. If
	 * month is empty or zero, the month is set to january. If day is empty or
	 * zero, the day is set to the first day or the last day of the month
	 * depending on the value of the lastDayOfMonth argument. If year, month or
	 * day are not numeric, a warning is logged, and null is returned. If month
	 * or day are out-of-range (e.g. 13 for month), the result is undefined.
	 */
	Date getDate(String year, String month, String day, boolean lastDayOfMonth)
	{
		try {

			if ((year = year.trim()).length() == 0)
				return null;
			int yearInt = (int) Float.parseFloat(year);
			if (yearInt == 0)
				return null;

			int monthInt;
			if ((month = month.trim()).length() == 0)
				monthInt = 1;
			else {
				monthInt = (int) Float.parseFloat(month);
				if (monthInt == 0)
					monthInt = 1;
			}

			int dayInt;
			if ((day = day.trim()).length() == 0)
				dayInt = -1;
			else {
				dayInt = (int) Float.parseFloat(day);
				if (dayInt == 0)
					dayInt = -1;
			}
			LocalDate date;
			if (dayInt == -1) {
				date = new LocalDate(yearInt, monthInt, 1);
				if (lastDayOfMonth)
					date = date.dayOfMonth().withMaximumValue();
			}
			else {
				date = new LocalDate(yearInt, monthInt, dayInt);
			}
			return date.toDate();
		}
		catch (Exception e) {
			if (!suppressErrors) {
				warn(String.format(MSG_INVALID_DATE, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

	Date getDate(String year, String month, String day)
	{
		try {
			if ((year = year.trim()).length() == 0)
				return null;
			int yearInt = (int) Float.parseFloat(year);
			if (yearInt == 0)
				return null;

			if ((month = month.trim()).length() == 0)
				return null;
			int monthInt = (int) Float.parseFloat(month);
			if (monthInt == 0)
				return null;

			int dayInt;
			if ((day = day.trim()).length() == 0)
				dayInt = 1;
			else {
				dayInt = (int) Float.parseFloat(day);
				if (dayInt == 0)
					dayInt = 1;
			}
			LocalDate date = new LocalDate(yearInt, monthInt, dayInt);
			return date.toDate();
		}
		catch (Exception e) {
			if (!suppressErrors) {
				warn(String.format(MSG_INVALID_DATE, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

}
