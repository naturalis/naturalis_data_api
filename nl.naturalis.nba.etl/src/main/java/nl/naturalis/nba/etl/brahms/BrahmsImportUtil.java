package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.*;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nba.api.model.*;
import nl.naturalis.nba.dao.es.types.ESGatheringEvent;
import nl.naturalis.nba.dao.es.types.ESGatheringSiteCoordinates;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.TransformUtil;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.joda.time.LocalDate;

/**
 * Provides common functionality related to the Brahms ETL cycle.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsImportUtil {

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsImportUtil.class);
	private static final SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("yyyyMMdd");
	private static final boolean suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");

	private static final String MSG_INVALID_NUMBER = "Invalid number in field %s: \"%s\" (value set to 0)";

	private BrahmsImportUtil()
	{
	}

	/**
	 * Provides a list of CSV files to process. Only files whose name end with
	 * {@code .csv} (case-insensitive) will be processed.
	 * 
	 * @return
	 */
	static File[] getCsvFiles()
	{
		File[] files = getDataDir().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		return files;
	}

	/**
	 * Creates a backup of successfully processed CSV files by appending a
	 * datetime stamp and a {@code .imported} file extension to their name.
	 */
	static void backup()
	{
		String ext = "." + fileNameDateFormatter.format(new Date()) + ".imported";
		for (File f : getCsvFiles()) {
			f.renameTo(new File(f.getAbsolutePath() + ext));
		}
	}

	/**
	 * Removes the {@code .imported} file extension from files that have it,
	 * causing them to be re-processed the next time an import is started.
	 */
	static void removeBackupExtension()
	{
		File dir = getDataDir();
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".imported");
			}
		});
		for (File file : files) {
			int pos = file.getName().toLowerCase().indexOf(".csv");
			String chopped = file.getName().substring(0, pos + 4);
			System.out.println(file.getName() + " ---> " + chopped);
			chopped = dir.getAbsolutePath() + "/" + chopped;
			file.renameTo(new File(chopped));
		}
	}

	/**
	 * Constructs a {@code Date} object from the date fields in a Brahms export
	 * file. If {@code year} or {@code month} are empty or zero, {@code null} is
	 * returned. If {@code day} is empty or zero, the date is rounded to the
	 * first day of the month. If {@code year}, {@code month} or {@code day} are
	 * not numeric, a warning is logged, and {@code null} is returned. If
	 * {@code month} or {@code day} are out-of-range (e.g. 13 for month), the
	 * result is undefined.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	static Date getDate(String year, String month, String day)
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
				String fmt = "Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s";
				logger.warn(String.format(fmt, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

	/**
	 * Constructs a {@code Date} object from the date fields in a Brahms export
	 * file. Used to construct a begin and end date from problematic gathering
	 * event dates in the source data. If {@code year} is empty or zero,
	 * {@code null} is returned. If {@code month} is empty or zero, the month is
	 * set to january. If {@code day} is empty or zero, the day is set to the
	 * first day or the last day of the month depending on the value of the
	 * {@code lastDayOfMonth} argument. If {@code year}, {@code month} or
	 * {@code day} are not numeric, a warning is logged, and {@code null} is
	 * returned. If {@code month} or {@code day} are out-of-range (e.g. 13 for
	 * month), the result is undefined.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param lastDayOfMonth
	 * @return
	 */
	static Date getDate(String year, String month, String day, boolean lastDayOfMonth)
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
				String fmt = "Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s";
				logger.warn(String.format(fmt, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

	/**
	 * Extracts a {@code SpecimenIdentification} instance from a raw CSV record.
	 * 
	 * @param record
	 * @return
	 */
	static SpecimenIdentification getSpecimenIdentification(CSVRecordInfo<BrahmsCsvField> record)
	{
		SpecimenIdentification identification = new SpecimenIdentification();
		String s = record.get(DETBY);
		if (s != null)
			identification.addIdentifier(new Agent(s));
		s = record.get(VERNACULAR);
		if (s != null)
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		String y = record.get(YEARIDENT);
		String m = record.get(MONTHIDENT);
		String d = record.get(DAYIDENT);
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(record);
		DefaultClassification dc = getDefaultClassification(record, sn);
		identification.setTaxonRank(getTaxonRank(record));
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}

	/**
	 * Extracts a {@code ScientificName} instance from a raw CSV record.
	 * 
	 * @param record
	 * @return
	 */
	static ScientificName getScientificName(CSVRecordInfo<BrahmsCsvField> record)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(record.get(SPECIES));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(record.get(GENUS));
		sn.setSpecificEpithet(record.get(SP1));
		sn.setInfraspecificMarker(getInfraspecificMarker(record));
		sn.setInfraspecificEpithet(getInfraspecificEpithet(record));
		if (sn.getFullScientificName() == null) {
			StringBuilder sb = new StringBuilder();
			if (sn.getGenusOrMonomial() != null) {
				sb.append(sn.getGenusOrMonomial()).append(' ');
			}
			if (sn.getSubgenus() != null) {
				sb.append(sn.getSubgenus()).append(' ');
			}
			if (sn.getSpecificEpithet() != null) {
				sb.append(sn.getSpecificEpithet()).append(' ');
			}
			if (sn.getInfraspecificMarker() != null) {
				sb.append(sn.getInfraspecificMarker()).append(' ');
			}
			if (sn.getInfraspecificEpithet() != null) {
				sb.append(sn.getInfraspecificEpithet()).append(' ');
			}
			if (sn.getAuthorshipVerbatim() != null) {
				if (sn.getAuthorshipVerbatim().charAt(0) != '(') {
					sb.append('(');
				}
				sb.append(sn.getAuthorshipVerbatim());
				if (sn.getAuthorshipVerbatim().charAt(sn.getAuthorshipVerbatim().length() - 1) != ')') {
					sb.append(')');
				}
			}
			if (sb.length() != 0) {
				sn.setFullScientificName(sb.toString().trim());
			}
		}
		return sn;
	}

	/**
	 * Constructs a {@code DefaultClassification} from a raw CSV record and a
	 * {@code ScientificName} instance (presumably extracted via
	 * {@link #getScientificName(CSVRecord) getScientificName}).
	 * 
	 * @param record
	 * @param sn
	 * @return
	 */
	static DefaultClassification getDefaultClassification(CSVRecordInfo<BrahmsCsvField> record,
			ScientificName sn)
	{
		DefaultClassification dc = TransformUtil.extractClassificiationFromName(sn);
		dc.setKingdom("Plantae");
		// Phylum deliberately not set
		dc.setClassName(record.get(FAMCLASS));
		dc.setOrder(record.get(ORDER));
		dc.setFamily(record.get(FAMILY));
		return dc;
	}

	/**
	 * Extracts a {@code ESGatheringEvent} instance from a raw CSV record.
	 * 
	 * @param record
	 * @return
	 */
	static ESGatheringEvent getGatheringEvent(CSVRecordInfo<BrahmsCsvField> record)
	{
		final ESGatheringEvent ge = new ESGatheringEvent();
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
			logger.error("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90D || lat > 90D)) {
			logger.error("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}
		String collector = record.get(COLLECTOR);
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
		return ge;
	}

	/**
	 * Converts a {@code DefaultClassification} instance to a system
	 * classification (which is just a list of {@code Monomial}s).
	 * 
	 * @param dc
	 * @return
	 */
	static List<Monomial> getSystemClassification(DefaultClassification dc)
	{
		List<Monomial> sc = new ArrayList<>(8);
		if (dc.getKingdom() != null) {
			sc.add(new Monomial(TaxonomicRank.KINGDOM, dc.getKingdom()));
		}
		if (dc.getOrder() != null) {
			sc.add(new Monomial(TaxonomicRank.ORDER, dc.getOrder()));
		}
		if (dc.getFamily() != null) {
			sc.add(new Monomial(TaxonomicRank.FAMILY, dc.getFamily()));
		}
		if (dc.getGenus() != null) {
			sc.add(new Monomial(TaxonomicRank.GENUS, dc.getGenus()));
		}
		if (dc.getSpecificEpithet() != null) {
			sc.add(new Monomial(TaxonomicRank.SPECIES, dc.getSpecificEpithet()));
		}
		if (dc.getInfraspecificEpithet() != null) {
			sc.add(new Monomial(TaxonomicRank.SUBSPECIES, dc.getInfraspecificEpithet()));
		}
		return sc;
	}

	private static String getAuthor(CSVRecordInfo<BrahmsCsvField> record)
	{
		if (record.get(SP3) == null) {
			if (record.get(SP2) == null) {
				return record.get(AUTHOR1);
			}
			return record.get(AUTHOR2);
		}
		return record.get(AUTHOR3);
	}

	private static String getInfraspecificMarker(CSVRecordInfo<BrahmsCsvField> record)
	{
		String s = record.get(RANK2);
		return s == null ? record.get(RANK1) : s;
	}

	private static String getInfraspecificEpithet(CSVRecordInfo<BrahmsCsvField> record)
	{
		String s = record.get(SP3);
		return s == null ? record.get(SP2) : s;
	}

	private static String getTaxonRank(CSVRecordInfo<BrahmsCsvField> record)
	{
		if (record.get(SP3) == null) {
			if (record.get(SP2) == null) {
				if (record.get(SP1) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return record.get(RANK1);
		}
		return record.get(RANK2);
	}

	public static Double getDouble(CSVRecordInfo<BrahmsCsvField> record, BrahmsCsvField field)
	{
		String s = record.get(field);
		if (s == null)
			return null;
		try {
			return Double.valueOf(s);
		}
		catch (NumberFormatException e) {
			if (!suppressErrors)
				logger.warn(String.format(MSG_INVALID_NUMBER, field.ordinal(), s));
			return null;
		}
	}

	public static Float getFloat(CSVRecordInfo<BrahmsCsvField> record, BrahmsCsvField field)
	{
		String s = record.get(field);
		if (s == null)
			return null;
		try {
			return Float.valueOf(s);
		}
		catch (NumberFormatException e) {
			if (!suppressErrors)
				logger.warn(String.format(MSG_INVALID_NUMBER, field.ordinal(), s));
			return null;
		}
	}

	private static File getDataDir()
	{
		return Registry.getInstance().getConfig().getDirectory("brahms.data_dir");
	}

}
