package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.PURL_SERVER_BASE_URL;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDate;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSpecimenIdentification;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.DocumentType;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsSpecimensImporter extends CSVImporter<ESSpecimen> {

	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		/*
		 * Check thematic search is configured properly
		 */
		ThematicSearchConfig.getInstance();
		IndexNative index = null;
		try {
			index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			BrahmsSpecimensImporter importer = new BrahmsSpecimensImporter(index);
			importer.importCsvFiles();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
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


	public BrahmsSpecimensImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE_SPECIMEN);
		this.delimiter = ',';
		this.charset = Charset.forName("Windows-1252");
		//this.suppressErrors = true;
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty(BrahmsImportAll.SYSPROP_BATCHSIZE, "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty(BrahmsImportAll.SYSPROP_MAXRECORDS, "0");
		setMaxRecords(Integer.parseInt(prop));
	}


	public void importCsvFiles() throws Exception
	{
		ThematicSearchConfig.getInstance().resetMatchCounters();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No new CSV files to import");
			return;
		}
		index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		for (File f : csvFiles) {
			importCsv(f.getCanonicalPath());
		}
		ThematicSearchConfig.getInstance().logMatchInfo();
	}


	@Override
	protected List<ESSpecimen> transfer(CSVRecord record, String csvRecord, int lineNo) throws Exception
	{
		String barcode = val(record, CsvField.BARCODE.ordinal());
		if (barcode == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Error at line %s: missing barcode", lineNo));
			}
			return null;
		}

		//		According to Marian vd Meij and Jeroen Creuwels, this check should not be done
		//		(even though it's in the QAW templates for GBIF/IPT.
		//		try {
		//			checkSpData(record);
		//		}
		//		catch (Exception e) {
		//			logger.debug(String.format("Error at line %s: %s", lineNo, e.getMessage()));
		//			return null;
		//		}

		final ESSpecimen specimen = new ESSpecimen();
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSourceSystemId(barcode);
		specimen.setUnitID(barcode);
		specimen.setUnitGUID(PURL_SERVER_BASE_URL + "/naturalis/specimen/" + urlEncode(barcode));

		specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		specimen.setOwner(SOURCE_INSTITUTION_ID);
		specimen.setSourceID("Brahms");
		specimen.setLicenceType(LICENCE_TYPE);
		specimen.setLicence(LICENCE);
		specimen.setCollectionType("Botany");

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

		specimen.setAssemblageID(BrahmsImportAll.ID_PREFIX + getFloatFieldAsInteger(record, CsvField.BRAHMS.ordinal()));
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
		String id = BrahmsImportAll.ID_PREFIX + val(record, CsvField.BARCODE.ordinal());
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
		String collector = val(record, CsvField.COLLECTOR.ordinal());
		if (collector != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(collector)));
		}
		return ge;
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
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Invalid number in field %s: \"%s\"", field, s));
			}
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
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Invalid number in field %s: \"%s\"", field, s));
			}
			return null;
		}
	}


	private static String urlEncode(String raw)
	{
		try {
			return URLEncoder.encode(raw, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// Won't happen with UTF-8
			return null;
		}
	}

}