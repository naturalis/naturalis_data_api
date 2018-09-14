package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_SUPPRESS_ERRORS;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.COLLECTOR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.CONTINENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.COUNTRY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DAY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.GENUS;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.HABITATTXT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.LATITUDE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.LOCNOTES;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.LONGITUDE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MAJORAREA;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MONTH;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.TYPE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.YEAR;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Arrays;

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

	private String[] testGenera;

	BrahmsTransformer(ETLStatistics stats)
	{
		super(stats);
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		testGenera = getTestGenera();
	}

	@Override
	protected boolean skipRecord()
	{
		if (testGenera != null && !isTestSetGenus()) {
			return true;
		}
		return false;
	}

	@Override
	public String getObjectID()
	{
		return input.get(BARCODE);
	}

	SpecimenTypeStatus getTypeStatus()
	{
		try {
			return typeStatusNormalizer.map(input.get(TYPE));
		}
		catch (UnmappedValueException e) {
			if (logger.isDebugEnabled()) {
				debug(e.getMessage());
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
		ge.setBiotopeText(record.get(HABITATTXT));
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
	OffsetDateTime getDate(String year, String month, String day, boolean lastDayOfMonth)
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
				if (!lastDayOfMonth) {
					date = LocalDate.of(yearInt, monthInt, 1);
				}
				else {
					date = YearMonth.of(yearInt, monthInt).atEndOfMonth();
				}
			}
			else {
				date = LocalDate.of(yearInt, monthInt, dayInt);
			}
			return date.atStartOfDay().atOffset(ZoneOffset.UTC);
		}
		catch (Exception e) {
			if (!suppressErrors) {
				warn(String.format(MSG_INVALID_DATE, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

	OffsetDateTime getDate(String year, String month, String day)
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
			LocalDate date = LocalDate.of(yearInt, monthInt, dayInt);
			return date.atStartOfDay().atOffset(ZoneOffset.UTC);
		}
		catch (Exception e) {
			if (!suppressErrors) {
				warn(String.format(MSG_INVALID_DATE, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

	private boolean isTestSetGenus()
	{
		String genus = input.get(GENUS);
		if (genus == null) {
			return false;
		}
		genus = genus.toLowerCase();
		for (String s : testGenera) {
			if (s.equals(genus)) {
				return true;
			}
		}
		return false;
	}

}
