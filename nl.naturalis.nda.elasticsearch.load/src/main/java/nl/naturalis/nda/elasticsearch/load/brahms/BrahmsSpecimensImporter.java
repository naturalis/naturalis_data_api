package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.TaxonomicRank.FAMILY;
import static nl.naturalis.nda.domain.TaxonomicRank.GENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.KINGDOM;
import static nl.naturalis.nda.domain.TaxonomicRank.ORDER;
import static nl.naturalis.nda.domain.TaxonomicRank.SPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBSPECIES;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

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
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.DocumentType;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsSpecimensImporter extends CSVImporter<ESSpecimen> {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		String rebuild = System.getProperty("rebuild", "false");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), DEFAULT_NDA_INDEX_NAME);
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_SPECIMEN);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType(LUCENE_TYPE_SPECIMEN, mapping);
		}
		else {
			index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		}

		try {
			BrahmsSpecimensImporter importer = new BrahmsSpecimensImporter(index);
			importer.importCsvFiles();
		}
		finally {
			index.getClient().close();
		}
	}

	//@formatter:off
	static enum CsvField {
		TAG,
		DEL,
		HERBARIUM,
		CATEGORY,
		SPECID,
		BRAHMS,
		ACCESSION,
		BARCODE,
		OLDBARCODE,
		PHENOLOGY,
		COLLECTOR,
		PREFIX,
		NUMBER,
		SUFFIX,
		ADDCOLL,
		ADDCOLLALL,
		TYPE,
		TYPE_OF,
		TYPEURL,
		DAY,
		MONTH,
		YEAR,
		DATERES,
		FAMCLASS,
		ORDER,
		FAMILY,
		CF,
		TAXSTAT,
		SPECIES,
		DETSTATUS,
		DETBY,
		DAYIDENT,
		MONTHIDENT,
		YEARIDENT,
		DETDATE,
		DETHISTORY,
		DETNOTES,
		CURATENOTE,
		ORIGINSTAT,
		ORIGINID,
		CONTINENT,
		REGION,
		COUNTRY,
		MAJORAREA,
		MINORAREA,
		LOCPREFIX,
		GAZETTEER,
		LOCNOTES,
		HABITATTXT,
		NOTE,
		CULTNOTES,
		LATITUDE,
		NS,
		LONGITUDE,
		EW,
		LLUNIT,
		LLRES,
		LLORIG,
		LATLONG,
		LATDEC,
		LONGDEC,
		DEGSQ,
		MINELEV,
		MAXELEV,
		ALTRES,
		ALTTEXT,
		ALTRANGE,
		GEODATA,
		PLANTDESC,
		NOTES,
		VERNACULAR,
		LANGUAGE,
		GENUS,
		SP1,
		AUTHOR1,
		RANK1,
		SP2,
		AUTHOR2,
		RANK2,
		SP3,
		AUTHOR3,
		UNIQUE,
		HSACCODE,
		GAZCODE,
		HBCODE,
		HSTYPE,
		SPTYPE,
		SPNUMBER,
		SPCODETYPE,
		CSCODE,
		ALTCS,
		ADDCSCODE,
		DETBYCODE,
		CONUMBER,
		CCID,
		ENTRYDATE,
		WHO,
		NOTONLINE,
		DATELASTM,
		IMAGELIST
	}
	//@formatter:on

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(BrahmsSpecimensImporter.class);
	private static final String ID_PREFIX = "BRAHMS-";

	private final boolean rename;


	public BrahmsSpecimensImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE_SPECIMEN);
		this.delimiter = ',';
		this.suppressErrors = true;
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty("bulkRequestSize", "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty("maxRecords", "0");
		setMaxRecords(Integer.parseInt(prop));
		prop = System.getProperty("rename", "false");
		rename = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importCsvFiles() throws Exception
	{

		// Check thematic search is configured properly
		ThematicSearchConfig.getInstance();

		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] csvFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		for (File f : csvFiles) {
			importCsv(f.getCanonicalPath());
			if (rename) {
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				f.renameTo(new File(f.getCanonicalPath() + "." + now + ".bak"));
			}
		}
	}


	@Override
	protected List<ESSpecimen> transfer(CSVRecord record, String csvRecord, int lineNo) throws Exception
	{
		String barcode = val(record, CsvField.BARCODE.ordinal());
		if (barcode == null) {
			logger.debug(String.format("Error at line %s: missing barcode", lineNo));
			return null;
		}
		try {
			checkSpData(record);
		}
		catch (Exception e) {
			logger.debug(String.format("Error at line %s: %s", lineNo, e.getMessage()));
			return null;
		}
		final ESSpecimen specimen = new ESSpecimen();
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSourceSystemId(barcode);
		specimen.setUnitID(barcode);

		specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		specimen.setOwner(SOURCE_INSTITUTION_ID);
		specimen.setSourceID("Brahms");
		specimen.setLicenceType(LICENCE_TYPE);
		specimen.setLicence(LICENCE);

		ThematicSearchConfig tsc = ThematicSearchConfig.getInstance();
		List<String> themes = tsc.getThemesForDocument(specimen.getUnitID(), DocumentType.SPECIMEN, SourceSystem.BRAHMS);
		specimen.setTheme(themes);

		String recordBasis = val(record, CsvField.CATEGORY.ordinal());
		if (recordBasis == null) {
			specimen.setRecordBasis("Preserved Specimen");
		}
		else {
			specimen.setRecordBasis(recordBasis);
		}

		specimen.setAssemblageID(ID_PREFIX + getFloatFieldAsInteger(record, CsvField.BRAHMS.ordinal()));
		specimen.setNotes(val(record, CsvField.PLANTDESC.ordinal()));
		specimen.setTypeStatus(typeStatusNormalizer.getNormalizedValue(val(record, CsvField.TYPE.ordinal())));
		String notOnline = val(record, CsvField.NOTONLINE.ordinal());
		if (notOnline == null || notOnline.equals("0")) {
			specimen.setObjectPublic(true);
		}
		else {
			specimen.setObjectPublic(false);
		}
		specimen.setGatheringEvent(getGatheringEvent(record));
		specimen.addIndentification(getSpecimenIdentification(record));
		return Arrays.asList(specimen);
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		String id = ID_PREFIX + val(record, CsvField.BARCODE.ordinal());
		return Arrays.asList(id);
	}


	static ESGatheringEvent getGatheringEvent(CSVRecord record)
	{
		final ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(val(record, CsvField.CONTINENT.ordinal()));
		ge.setContinent(ge.getWorldRegion());
		ge.setCountry(val(record, CsvField.COUNTRY.ordinal()));
		ge.setProvinceState(val(record, CsvField.MAJORAREA.ordinal()));
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
		String locNotes = val(record, CsvField.LOCNOTES.ordinal());
		if (locNotes != null) {
			ge.setLocality(locNotes);
			if (sb.length() != 0) {
				sb.append("; ");
			}
			sb.append(locNotes);
		}
		ge.setLocalityText(sb.toString());
		String y = val(record, CsvField.YEAR.ordinal());
		String m = val(record, CsvField.MONTH.ordinal());
		String d = val(record, CsvField.DAY.ordinal());
		Date date = getDate(y, m, d);
		ge.setDateTimeBegin(date);
		ge.setDateTimeEnd(date);
		Double lat = dget(record, CsvField.LATITUDE.ordinal());
		Double lon = dget(record, CsvField.LONGITUDE.ordinal());
		if (lon != null && (lon < -180 || lon > 180)) {
			logger.error("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90 || lat > 90)) {
			logger.error("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}
		String collector = val(record, CsvField.COLLECTOR.ordinal());
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
		return ge;
	}


	static SpecimenIdentification getSpecimenIdentification(CSVRecord record)
	{
		final SpecimenIdentification identification = new SpecimenIdentification();
		String s = val(record, CsvField.DETBY.ordinal());
		if (s != null) {
			identification.addIdentifier(new Agent(s));
		}
		s = val(record, CsvField.VERNACULAR.ordinal());
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = val(record, CsvField.YEARIDENT.ordinal());
		String m = val(record, CsvField.MONTHIDENT.ordinal());
		String d = val(record, CsvField.DAYIDENT.ordinal());
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
		final ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(record, CsvField.SPECIES.ordinal()));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(val(record, CsvField.GENUS.ordinal()));
		sn.setSpecificEpithet(val(record, CsvField.SP1.ordinal()));
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
		final DefaultClassification dc = TransferUtil.extractClassificiationFromName(sn);
		dc.setKingdom("Plantae");
		// Phylum deliberately not set
		dc.setClassName(val(record, CsvField.FAMCLASS.ordinal()));
		dc.setOrder(val(record, CsvField.ORDER.ordinal()));
		dc.setFamily(val(record, CsvField.FAMILY.ordinal()));
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


	static void checkSpData(CSVRecord record) throws Exception
	{
		String r = val(record, CsvField.RANK1.ordinal());
		String s = val(record, CsvField.SP2.ordinal());
		if ((r == null && s != null) || (r != null && s == null)) {
			throw new Exception("If rank1 is provided, sp2 must also be provided and vice versa");
		}
		r = val(record, CsvField.RANK2.ordinal());
		s = val(record, CsvField.SP3.ordinal());
		if ((r == null && s != null) || (r != null && s == null)) {
			throw new Exception("If rank2 is provided, sp3 must also be provided and vice versa");
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
			logger.debug(String.format("Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s", year, month, day, e.getMessage()));
			return null;
		}
	}


	private static String getAuthor(CSVRecord record)
	{
		if (val(record, CsvField.SP3.ordinal()) == null) {
			if (val(record, CsvField.SP2.ordinal()) == null) {
				return val(record, CsvField.AUTHOR1.ordinal());
			}
			return val(record, CsvField.AUTHOR2.ordinal());
		}
		return val(record, CsvField.AUTHOR3.ordinal());
	}


	private static String getInfraspecificEpithet(CSVRecord record)
	{
		if (val(record, CsvField.RANK1.ordinal()) == "subspecies") {
			return val(record, CsvField.SP2.ordinal());
		}
		return null;
	}


	private static String getTaxonRank(CSVRecord record)
	{
		if (val(record, CsvField.SP3.ordinal()) == null) {
			if (val(record, CsvField.SP2.ordinal()) == null) {
				if (val(record, CsvField.SP1.ordinal()) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return val(record, CsvField.RANK1.ordinal());
		}
		return val(record, CsvField.RANK2.ordinal());

	}


	private static Integer getFloatFieldAsInteger(CSVRecord record, int field)
	{
		String s = val(record, field);
		if (s == null) {
			return null;
		}
		try {
			return (int) Float.parseFloat(s);
		}
		catch (NumberFormatException e) {
			logger.debug(String.format("Invalid number in field %s: \"%s\"", field, s));
			return null;
		}
	}


	private static Double dget(CSVRecord record, int field)
	{
		String s = val(record, field);
		if (s == null) {
			return null;
		}
		try {
			return Double.valueOf(s);
		}
		catch (NumberFormatException e) {
			logger.debug(String.format("Invalid number in field %s: \"%s\"", field, s));
			return null;
		}
	}

}
