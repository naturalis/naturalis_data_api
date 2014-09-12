package nl.naturalis.nda.elasticsearch.load.brahms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.GatheringEvent;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsMultiMediaImporter extends CSVImporter<ESMultiMediaObject> {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

			index.deleteType(LUCENE_TYPE);
			Thread.sleep(2000);
			String mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType(LUCENE_TYPE, mapping);

			BrahmsMultiMediaImporter importer = new BrahmsMultiMediaImporter(index);
			importer.importCsv("C:/brahms-dumps/20140704_U_DARWINCORE_21-05-2014_at_08-55-44.CSV");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BrahmsMultiMediaImporter.class);
	static final String LUCENE_TYPE = "MultiMediaObject";
	static final String ID_PREFIX = "BRAHMS-";

	//@formatter:off
	static enum CsvField {
		tag,
		del,
		notonline,
		datelastm,
		institute,
		catalogue,
		barcode,
		scientific,
		basisofrec,
		kingdom,
		division,
		order,
		family,
		genus,
		species,
		subspecies,
		authorname,
		ident_by,
		yearident,
		monthident,
		dayident,
		collnumber,
		collector,
		dcollected,
		mcollected,
		ycollected,
		detstatus,
		country,
		stateprov,
		continent,
		county,
		locality,
		latitude,
		longitude,
		llres,
		minelev,
		maxelev,
		notes,
		habitattxt,
		plantdesc,
		notpublish,
		typestatus,
		vernacular,
		spnumber,
		brahms,
		specid,
		imagelist		
	}
	//@formatter:on

	public BrahmsMultiMediaImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE);
		this.delimiter = ',';
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected List<ESMultiMediaObject> transfer(CSVRecord record) throws Exception
	{
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(4);
		String s = get(record, CsvField.imagelist.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				mmos.add(transferOne(record, i, urls[i]));
			}
		}
		return mmos;
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		String base = ID_PREFIX + get(record, CsvField.barcode.ordinal());
		List<String> ids = new ArrayList<String>(4);
		String s = get(record, CsvField.imagelist.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				ids.add(base + "_" + i);
			}
		}
		return ids;
	}


	private static ESMultiMediaObject transferOne(CSVRecord record, int imageNo, String imageUrl) throws Exception
	{
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		String s = get(record, CsvField.barcode.ordinal());
		if (s == null) {
			throw new Exception("Missing barcode");
		}
		mmo.setSourceSystemId(s + "_" + imageNo);
		mmo.setSourceSystem(SourceSystem.BRAHMS);
		mmo.setDescription(get(record, CsvField.plantdesc.ordinal()));
		//mmo.setGatheringEvents(Arrays.asList(getGatheringEvent(record)));
		mmo.setIdentifications(Arrays.asList(getIdentification(record)));
		try {
			URI uri = new URI(imageUrl);
			mmo.addServiceAccessPoint(new ServiceAccessPoint(uri, null, Variant.MEDIUM_QUALITY));
		}
		catch(URISyntaxException e) {
			logger.error("Invalid URL: " + imageUrl);
			mmo.addServiceAccessPoint(new ServiceAccessPoint("http://nda.naturalis.nl/notfound", null, Variant.MEDIUM_QUALITY));
		}
		return mmo;
	}


	private static GatheringEvent getGatheringEvent(CSVRecord record)
	{
		final GatheringEvent ge = new GatheringEvent();
		ge.setWorldRegion(get(record, CsvField.continent.ordinal()));
		ge.setContinent(ge.getWorldRegion());
		ge.setCountry(get(record, CsvField.country.ordinal()));
		ge.setProvinceState(get(record, CsvField.stateprov.ordinal()));
		String y = get(record, CsvField.ycollected.ordinal());
		String m = get(record, CsvField.mcollected.ordinal());
		String d = get(record, CsvField.dcollected.ordinal());
		Date date = getDate(y, m, d);
		ge.setDateTimeBegin(date);
		ge.setDateTimeEnd(date);
		Double longitude = dget(record, CsvField.longitude.ordinal());
		Double latitude = dget(record, CsvField.latitude.ordinal());
		ge.addSiteCoordinates(latitude, longitude);
		return ge;
	}


	private static MultiMediaContentIdentification getIdentification(CSVRecord record)
	{
		final MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
		String s = get(record, CsvField.vernacular.ordinal());
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = get(record, CsvField.yearident.ordinal());
		String m = get(record, CsvField.monthident.ordinal());
		String d = get(record, CsvField.dayident.ordinal());
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(record);
		DefaultClassification dc = getDefaultClassification(record, sn);
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}


	private static ScientificName getScientificName(CSVRecord record)
	{
		final ScientificName sn = new ScientificName();
		sn.setFullScientificName(get(record, CsvField.scientific.ordinal()));
		sn.setAuthorshipVerbatim(get(record, CsvField.authorname.ordinal()));
		sn.setGenusOrMonomial(get(record, CsvField.genus.ordinal()));
		sn.setSpecificEpithet(get(record, CsvField.species.ordinal()));
		sn.setInfraspecificEpithet(get(record, CsvField.subspecies.ordinal()));
		return sn;
	}


	private static DefaultClassification getDefaultClassification(CSVRecord record, ScientificName sn)
	{
		final DefaultClassification dc = new DefaultClassification();
		dc.setKingdom(get(record, CsvField.kingdom.ordinal()));
		dc.setPhylum(get(record, CsvField.division.ordinal()));
		dc.setOrder(get(record, CsvField.order.ordinal()));
		dc.setFamily(get(record, CsvField.family.ordinal()));
		dc.setGenus(sn.getGenusOrMonomial());
		dc.setSpecificEpithet(sn.getSpecificEpithet());
		dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());
		return dc;
	}


	private static List<Monomial> getSystemClassification(DefaultClassification dc)
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


	private static Date getDate(String year, String month, String day)
	{
		year = year.trim();
		if (year.length() == 0) {
			return null;
		}
		try {
			int yearInt = Integer.parseInt(year);
			if (yearInt == 0) {
				return null;
			}
			month = month.trim();
			if (month.length() == 0) {
				month = "0";
			}
			int monthInt = Integer.parseInt(month);
			if (monthInt < 0 || monthInt > 11) {
				monthInt = 0;
			}
			day = day.trim();
			if (day.length() == 0) {
				day = "1";
			}
			int dayInt = Integer.parseInt(day);
			if (dayInt <= 0 || dayInt > 31) {
				dayInt = 1;
			}
			return new GregorianCalendar(yearInt, monthInt, dayInt).getTime();
		}
		catch (NumberFormatException e) {
			logger.warn(String.format("Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\"", year, month, day));
			return null;
		}
	}
}
