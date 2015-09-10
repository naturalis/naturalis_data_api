package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.TaxonomicRank.FAMILY;
import static nl.naturalis.nda.domain.TaxonomicRank.GENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.KINGDOM;
import static nl.naturalis.nda.domain.TaxonomicRank.ORDER;
import static nl.naturalis.nda.domain.TaxonomicRank.SPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBSPECIES;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.getDouble;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.*;

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
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.TransformUtil;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

class BrahmsImportUtil {

	static final Logger logger = Registry.getInstance().getLogger(BrahmsImportUtil.class);
	static final SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("yyyyMMdd");

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

	static void backup()
	{
		String ext = "." + fileNameDateFormatter.format(new Date()) + ".imported";
		for (File f : getCsvFiles()) {
			f.renameTo(new File(f.getAbsolutePath() + ext));
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
				String fmt = "Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s";
				logger.debug(String.format(fmt, year, month, day, e.getMessage()));
			}
			return null;
		}
	}

	static SpecimenIdentification getSpecimenIdentification(CSVRecord record)
	{
		final SpecimenIdentification identification = new SpecimenIdentification();
		String s = val(record, DETBY);
		if (s != null) {
			identification.addIdentifier(new Agent(s));
		}
		s = val(record, VERNACULAR);
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = val(record, YEARIDENT);
		String m = val(record, MONTHIDENT);
		String d = val(record, DAYIDENT);
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
		sn.setFullScientificName(val(record, SPECIES));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(val(record, GENUS));
		sn.setSpecificEpithet(val(record, SP1));
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
		DefaultClassification dc = TransformUtil.extractClassificiationFromName(sn);
		dc.setKingdom("Plantae");
		// Phylum deliberately not set
		dc.setClassName(val(record, FAMCLASS));
		dc.setOrder(val(record, ORDER));
		dc.setFamily(val(record, FAMILY));
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
		if (val(record, SP3) == null) {
			if (val(record, SP2) == null) {
				return val(record, AUTHOR1);
			}
			return val(record, AUTHOR2);
		}
		return val(record, AUTHOR3);
	}

	private static String getInfraspecificMarker(CSVRecord record)
	{
		String s = val(record, RANK2);
		return s == null ? val(record, RANK1) : s;
	}

	private static String getInfraspecificEpithet(CSVRecord record)
	{
		String s = val(record, SP3);
		return s == null ? val(record, SP2) : s;
	}

	private static String getTaxonRank(CSVRecord record)
	{
		if (val(record, SP3) == null) {
			if (val(record, SP2) == null) {
				if (val(record, SP1) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return val(record, RANK1);
		}
		return val(record, RANK2);
	}

	private static File getDataDir()
	{
		return Registry.getInstance().getConfig().getDirectory("brahms.csv_dir");
	}

	static ESGatheringEvent getGatheringEvent(CSVRecord record)
	{
		final ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(val(record, CONTINENT));
		ge.setContinent(ge.getWorldRegion());
		ge.setCountry(val(record, COUNTRY));
		ge.setProvinceState(val(record, MAJORAREA));
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
		String locNotes = val(record, LOCNOTES);
		if (locNotes != null) {
			ge.setLocality(locNotes);
			if (sb.length() != 0) {
				sb.append("; ");
			}
			sb.append(locNotes);
		}
		ge.setLocalityText(sb.toString());
		String y = val(record, YEAR);
		String m = val(record, MONTH);
		String d = val(record, DAY);
		Date date = getDate(y, m, d);
		ge.setDateTimeBegin(date);
		ge.setDateTimeEnd(date);
		Double lat = getDouble(record, LATITUDE);
		Double lon = getDouble(record, LONGITUDE);
		if (lat == 0D && lon == 0D) {
			lat = null;
			lon = null;
		}
		if (lon != null && (lon < -180D || lon > 180D)) {
			BrahmsSpecimenImporter.logger.error("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90D || lat > 90D)) {
			BrahmsSpecimenImporter.logger.error("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}
		String collector = val(record, COLLECTOR);
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
		return ge;
	}

}
