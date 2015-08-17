package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.TaxonomicRank.FAMILY;
import static nl.naturalis.nda.domain.TaxonomicRank.GENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.KINGDOM;
import static nl.naturalis.nda.domain.TaxonomicRank.ORDER;
import static nl.naturalis.nda.domain.TaxonomicRank.SPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBSPECIES;

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
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.load.CSVImportUtil;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField;

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
		String s = CSVImportUtil.val(record, CsvField.DETBY.ordinal());
		if (s != null) {
			identification.addIdentifier(new Agent(s));
		}
		s = CSVImportUtil.val(record, CsvField.VERNACULAR.ordinal());
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = CSVImportUtil.val(record, CsvField.YEARIDENT.ordinal());
		String m = CSVImportUtil.val(record, CsvField.MONTHIDENT.ordinal());
		String d = CSVImportUtil.val(record, CsvField.DAYIDENT.ordinal());
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
		sn.setFullScientificName(CSVImportUtil.val(record, CsvField.SPECIES.ordinal()));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(CSVImportUtil.val(record, CsvField.GENUS.ordinal()));
		sn.setSpecificEpithet(CSVImportUtil.val(record, CsvField.SP1.ordinal()));
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
		dc.setClassName(CSVImportUtil.val(record, CsvField.FAMCLASS.ordinal()));
		dc.setOrder(CSVImportUtil.val(record, CsvField.ORDER.ordinal()));
		dc.setFamily(CSVImportUtil.val(record, CsvField.FAMILY.ordinal()));
		return dc;
	}


	static List<Monomial> getSystemClassification(DefaultClassification dc)
	{
		final List<Monomial> sc = new ArrayList<Monomial>(8);
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
		if (CSVImportUtil.val(record, CsvField.SP3.ordinal()) == null) {
			if (CSVImportUtil.val(record, CsvField.SP2.ordinal()) == null) {
				return CSVImportUtil.val(record, CsvField.AUTHOR1.ordinal());
			}
			return CSVImportUtil.val(record, CsvField.AUTHOR2.ordinal());
		}
		return CSVImportUtil.val(record, CsvField.AUTHOR3.ordinal());
	}


	private static String getInfraspecificMarker(CSVRecord record)
	{
		String s = CSVImportUtil.val(record, CsvField.RANK2.ordinal());
		return s == null ? CSVImportUtil.val(record, CsvField.RANK1.ordinal()) : s;
	}


	private static String getInfraspecificEpithet(CSVRecord record)
	{
		String s = CSVImportUtil.val(record, CsvField.SP3.ordinal());
		return s == null ? CSVImportUtil.val(record, CsvField.SP2.ordinal()) : s;
	}


	private static String getTaxonRank(CSVRecord record)
	{
		if (CSVImportUtil.val(record, CsvField.SP3.ordinal()) == null) {
			if (CSVImportUtil.val(record, CsvField.SP2.ordinal()) == null) {
				if (CSVImportUtil.val(record, CsvField.SP1.ordinal()) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return CSVImportUtil.val(record, CsvField.RANK1.ordinal());
		}
		return CSVImportUtil.val(record, CsvField.RANK2.ordinal());
	}

	private static File getDataDir()
	{
		return LoadUtil.getConfig().getDirectory("brahms.csv_dir");
	}

}
