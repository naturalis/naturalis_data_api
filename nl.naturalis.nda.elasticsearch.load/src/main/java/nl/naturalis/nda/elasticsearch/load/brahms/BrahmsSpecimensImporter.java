package nl.naturalis.nda.elasticsearch.load.brahms;

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
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

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
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType(LUCENE_TYPE);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType(LUCENE_TYPE, mapping);
		}
		else {
			index.deleteWhere(LUCENE_TYPE, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
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

	static final Logger logger = LoggerFactory.getLogger(BrahmsSpecimensImporter.class);
	static final String LUCENE_TYPE = "Specimen";
	static final String ID_PREFIX = "BRAHMS-";

	private final boolean rename;


	public BrahmsSpecimensImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE);
		this.delimiter = ',';
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty("bulkRequestSize", "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty("rename", "false");
		rename = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importCsvFiles() throws Exception
	{
		String csvDir = System.getProperty("csvDir");
		if (csvDir == null) {
			throw new Exception("Missing -DcsvDir argument");
		}
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] xmlFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		if (xmlFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		for (File f : xmlFiles) {
			importCsv(f.getCanonicalPath());
			if (rename) {
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				f.renameTo(new File(f.getCanonicalPath() + "." + now + ".bak"));
			}
		}
	}


	@Override
	protected List<ESSpecimen> transfer(CSVRecord record) throws Exception
	{
		String barcode = get(record, CsvField.BARCODE.ordinal());
		if (barcode == null) {
			throw new Exception("Missing barcode");
		}
		checkSpData(record);
		final ESSpecimen specimen = new ESSpecimen();
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSourceSystemId(barcode);
		specimen.setUnitID(barcode);
		specimen.setRecordBasis("PreservedSpecimen");
		specimen.setAssemblageID(ID_PREFIX + get(record, CsvField.BRAHMS.ordinal()));
		specimen.setNotes(get(record, CsvField.PLANTDESC.ordinal()));
		String notOnline = get(record, CsvField.NOTONLINE.ordinal());
		if(notOnline == null || notOnline.equals("0")) {
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
		String id = ID_PREFIX + get(record, CsvField.BARCODE.ordinal());
		return Arrays.asList(id);
	}


	static ESGatheringEvent getGatheringEvent(CSVRecord record)
	{
		final ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(get(record, CsvField.CONTINENT.ordinal()));
		ge.setContinent(ge.getWorldRegion());
		ge.setCountry(get(record, CsvField.COUNTRY.ordinal()));
		ge.setProvinceState(get(record, CsvField.MAJORAREA.ordinal()));
		String y = get(record, CsvField.YEAR.ordinal());
		String m = get(record, CsvField.MONTH.ordinal());
		String d = get(record, CsvField.DAY.ordinal());
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
		String collector = get(record, CsvField.COLLECTOR.ordinal());
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
		return ge;
	}


	static SpecimenIdentification getSpecimenIdentification(CSVRecord record)
	{
		final SpecimenIdentification identification = new SpecimenIdentification();
		String s = get(record, CsvField.DETBY.ordinal());
		if (s != null) {
			identification.addIdentifier(new Agent(s));
		}
		s = get(record, CsvField.VERNACULAR.ordinal());
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = get(record, CsvField.YEARIDENT.ordinal());
		String m = get(record, CsvField.MONTHIDENT.ordinal());
		String d = get(record, CsvField.DAYIDENT.ordinal());
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
		sn.setFullScientificName(get(record, CsvField.SPECIES.ordinal()));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(get(record, CsvField.GENUS.ordinal()));
		sn.setSpecificEpithet(get(record, CsvField.SP1.ordinal()));
		sn.setInfraspecificEpithet(getInfraspecificEpithet(record));
		return sn;
	}


	static DefaultClassification getDefaultClassification(CSVRecord record, ScientificName sn)
	{
		final DefaultClassification dc = new DefaultClassification();
		dc.setKingdom("Plantae");
		dc.setPhylum(null);
		dc.setClassName(get(record, CsvField.FAMCLASS.ordinal()));
		dc.setOrder(get(record, CsvField.ORDER.ordinal()));
		dc.setFamily(get(record, CsvField.FAMILY.ordinal()));
		dc.setGenus(sn.getGenusOrMonomial());
		dc.setSpecificEpithet(sn.getSpecificEpithet());
		dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());
		return dc;
	}


	static List<Monomial> getSystemClassification(DefaultClassification dc)
	{
		final List<Monomial> sc = new ArrayList<Monomial>(8);
		if (dc.getKingdom() != null) {
			sc.add(new Monomial("kingdom", dc.getKingdom()));
		}
		if (dc.getPhylum() != null) {
			sc.add(new Monomial("division", dc.getPhylum()));
		}
		if (dc.getOrder() != null) {
			sc.add(new Monomial("order", dc.getOrder()));
		}
		if (dc.getFamily() != null) {
			sc.add(new Monomial("family", dc.getFamily()));
		}
		if (dc.getGenus() != null) {
			sc.add(new Monomial("genus", dc.getGenus()));
		}
		if (dc.getSpecificEpithet() != null) {
			sc.add(new Monomial("species", dc.getSpecificEpithet()));
		}
		if (dc.getInfraspecificEpithet() != null) {
			sc.add(new Monomial("subspecies", dc.getInfraspecificEpithet()));
		}
		return sc;
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
			logger.warn(String.format("Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\": %s", year, month, day, e.getMessage()));
			return null;
		}
	}


	private static String getAuthor(CSVRecord record)
	{
		if (get(record, CsvField.SP3.ordinal()) == null) {
			if (get(record, CsvField.SP2.ordinal()) == null) {
				return get(record, CsvField.AUTHOR1.ordinal());
			}
			return get(record, CsvField.AUTHOR2.ordinal());
		}
		return get(record, CsvField.AUTHOR3.ordinal());
	}


	private static String getInfraspecificEpithet(CSVRecord record)
	{
		if (get(record, CsvField.RANK1.ordinal()) == "subspecies") {
			return get(record, CsvField.SP2.ordinal());
		}
		return null;
	}


	private static String getTaxonRank(CSVRecord record)
	{
		if (get(record, CsvField.SP3.ordinal()) == null) {
			if (get(record, CsvField.SP2.ordinal()) == null) {
				if (get(record, CsvField.SP1.ordinal()) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return get(record, CsvField.RANK1.ordinal());
		}
		return get(record, CsvField.RANK2.ordinal());

	}


	private static void checkSpData(CSVRecord record) throws Exception
	{
		String r = get(record, CsvField.RANK1.ordinal());
		String s = get(record, CsvField.SP2.ordinal());
		if ((r == null && s != null) || (r != null && s == null)) {
			throw new Exception("If rank1 is provided, sp2 must also be provided and vice versa");
		}
		r = get(record, CsvField.RANK2.ordinal());
		s = get(record, CsvField.SP3.ordinal());
		if ((r == null && s != null) || (r != null && s == null)) {
			throw new Exception("If rank2 is provided, sp3 must also be provided and vice versa");
		}
	}


	private static String get(CSVRecord record, int field)
	{
		String s = record.get(field).trim();
		return s.length() == 0 ? null : s;
	}


	private static Double dget(CSVRecord record, int field)
	{
		String s = get(record, field);
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
