package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.TaxonomicRank.FAMILY;
import static nl.naturalis.nda.domain.TaxonomicRank.GENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.KINGDOM;
import static nl.naturalis.nda.domain.TaxonomicRank.ORDER;
import static nl.naturalis.nda.domain.TaxonomicRank.SPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBSPECIES;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.getDouble;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.load.CSVImportUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

class BrahmsImportUtil {

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsImportUtil.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


	private BrahmsImportUtil()
	{
	}


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

	static void backupCsvFiles()
	{
		String backupExtension = "." + sdf.format(new Date()) + ".imported";
		for (File f : getCsvFiles()) {
			f.renameTo(new File(f.getAbsolutePath() + backupExtension));
		}
	}


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

	static Date getDate(String year, String month, String day)
	{
		year = year.trim();
		if (year.length() == 0) {
			return null;
		}
		try {
			int yearInt = (int) Float.parseFloat(year);
			if (yearInt == 0) {
				return null;
			}
			month = month.trim();
			if (month.length() == 0) {
				month = "0";
			}
			int monthInt = (int) Float.parseFloat(month);
			if (monthInt < 0 || monthInt > 11) {
				monthInt = 0;
			}
			day = day.trim();
			if (day.length() == 0) {
				day = "1";
			}
			int dayInt = (int) Float.parseFloat(day);
			if (dayInt <= 0 || dayInt > 31) {
				dayInt = 1;
			}
			return new GregorianCalendar(yearInt, monthInt, dayInt).getTime();
		}
		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s", year, month, day, e.getMessage()));
			}
			return null;
		}
	}


	static SpecimenIdentification getSpecimenIdentification(CSVRecord record)
	{
		final SpecimenIdentification identification = new SpecimenIdentification();
		String s = CSVImportUtil.val(record, BrahmsCsvField.DETBY.ordinal());
		if (s != null) {
			identification.addIdentifier(new Agent(s));
		}
		s = CSVImportUtil.val(record, BrahmsCsvField.VERNACULAR.ordinal());
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = CSVImportUtil.val(record, BrahmsCsvField.YEARIDENT.ordinal());
		String m = CSVImportUtil.val(record, BrahmsCsvField.MONTHIDENT.ordinal());
		String d = CSVImportUtil.val(record, BrahmsCsvField.DAYIDENT.ordinal());
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(record);
		DefaultClassification dc = getDefaultClassification(record, sn);
		identification.setTaxonRank(getTaxonRank(record));
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}


	static ScientificName getScientificName(CSVRecord record)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(CSVImportUtil.val(record, BrahmsCsvField.SPECIES.ordinal()));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(CSVImportUtil.val(record, BrahmsCsvField.GENUS.ordinal()));
		sn.setSpecificEpithet(CSVImportUtil.val(record, BrahmsCsvField.SP1.ordinal()));
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


	static DefaultClassification getDefaultClassification(CSVRecord record, ScientificName sn)
	{
		DefaultClassification dc = TransferUtil.extractClassificiationFromName(sn);
		dc.setKingdom("Plantae");
		// Phylum deliberately not set
		dc.setClassName(CSVImportUtil.val(record, BrahmsCsvField.FAMCLASS.ordinal()));
		dc.setOrder(CSVImportUtil.val(record, BrahmsCsvField.ORDER.ordinal()));
		dc.setFamily(CSVImportUtil.val(record, BrahmsCsvField.FAMILY.ordinal()));
		return dc;
	}


	static List<Monomial> getSystemClassification(DefaultClassification dc)
	{
		final List<Monomial> sc = new ArrayList<>(8);
		if (dc.getKingdom() != null) {
			sc.add(new Monomial(KINGDOM, dc.getKingdom()));
		}
		if (dc.getOrder() != null) {
			sc.add(new Monomial(ORDER, dc.getOrder()));
		}
		if (dc.getFamily() != null) {
			sc.add(new Monomial(FAMILY, dc.getFamily()));
		}
		if (dc.getGenus() != null) {
			sc.add(new Monomial(GENUS, dc.getGenus()));
		}
		if (dc.getSpecificEpithet() != null) {
			sc.add(new Monomial(SPECIES, dc.getSpecificEpithet()));
		}
		if (dc.getInfraspecificEpithet() != null) {
			sc.add(new Monomial(SUBSPECIES, dc.getInfraspecificEpithet()));
		}
		return sc;
	}


	private static String getAuthor(CSVRecord record)
	{
		if (CSVImportUtil.val(record, BrahmsCsvField.SP3.ordinal()) == null) {
			if (CSVImportUtil.val(record, BrahmsCsvField.SP2.ordinal()) == null) {
				return CSVImportUtil.val(record, BrahmsCsvField.AUTHOR1.ordinal());
			}
			return CSVImportUtil.val(record, BrahmsCsvField.AUTHOR2.ordinal());
		}
		return CSVImportUtil.val(record, BrahmsCsvField.AUTHOR3.ordinal());
	}


	private static String getInfraspecificMarker(CSVRecord record)
	{
		String s = CSVImportUtil.val(record, BrahmsCsvField.RANK2.ordinal());
		return s == null ? CSVImportUtil.val(record, BrahmsCsvField.RANK1.ordinal()) : s;
	}


	private static String getInfraspecificEpithet(CSVRecord record)
	{
		String s = CSVImportUtil.val(record, BrahmsCsvField.SP3.ordinal());
		return s == null ? CSVImportUtil.val(record, BrahmsCsvField.SP2.ordinal()) : s;
	}


	private static String getTaxonRank(CSVRecord record)
	{
		if (CSVImportUtil.val(record, BrahmsCsvField.SP3.ordinal()) == null) {
			if (CSVImportUtil.val(record, BrahmsCsvField.SP2.ordinal()) == null) {
				if (CSVImportUtil.val(record, BrahmsCsvField.SP1.ordinal()) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return CSVImportUtil.val(record, BrahmsCsvField.RANK1.ordinal());
		}
		return CSVImportUtil.val(record, BrahmsCsvField.RANK2.ordinal());
	}

	private static File getDataDir()
	{
		return Registry.getInstance().getConfig().getDirectory("brahms.csv_dir");
	}


	static ESGatheringEvent getGatheringEvent(CSVRecord record)
	{
		final ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(val(record, BrahmsCsvField.CONTINENT.ordinal()));
		ge.setContinent(ge.getWorldRegion());
		ge.setCountry(val(record, BrahmsCsvField.COUNTRY.ordinal()));
		ge.setProvinceState(val(record, BrahmsCsvField.MAJORAREA.ordinal()));
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
		String locNotes = val(record, BrahmsCsvField.LOCNOTES.ordinal());
		if (locNotes != null) {
			ge.setLocality(locNotes);
			if (sb.length() != 0) {
				sb.append("; ");
			}
			sb.append(locNotes);
		}
		ge.setLocalityText(sb.toString());
		String y = val(record, BrahmsCsvField.YEAR.ordinal());
		String m = val(record, BrahmsCsvField.MONTH.ordinal());
		String d = val(record, BrahmsCsvField.DAY.ordinal());
		Date date = getDate(y, m, d);
		ge.setDateTimeBegin(date);
		ge.setDateTimeEnd(date);
		Double lat = getDouble(record, BrahmsCsvField.LATITUDE.ordinal());
		Double lon = getDouble(record, BrahmsCsvField.LONGITUDE.ordinal());
		if (lat == 0D && lon == 0D) {
			lat = null;
			lon = null;
		}
		if (lon != null && (lon < -180D || lon > 180D)) {
			BrahmsSpecimensImporter.logger.error("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90D || lat > 90D)) {
			BrahmsSpecimensImporter.logger.error("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}
		String collector = val(record, BrahmsCsvField.COLLECTOR.ordinal());
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
		return ge;
	}

}
