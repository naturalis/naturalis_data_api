package nl.naturalis.nda.elasticsearch.load.brahms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.GatheringEvent;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsSpecimensImporter extends CSVImporter<Specimen> {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
			
			index.deleteType(LUCENE_TYPE);
			Thread.sleep(2000);
			String mapping = LoadUtil.getMapping(Specimen.class);
			index.addType(LUCENE_TYPE, mapping);						
			
			BrahmsSpecimensImporter importer = new BrahmsSpecimensImporter(index);
			importer.importCsv("C:/brahms-dumps/20140704_U_DARWINCORE_21-05-2014_at_08-55-44.CSV");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BrahmsSpecimensImporter.class);
	private static final String LUCENE_TYPE = "Specimen";
	private static final String ID_PREFIX = "BRAHMS-";

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

	public BrahmsSpecimensImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE);
		this.delimiter = ',';
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected List<Specimen> transfer(CSVRecord record) throws Exception
	{
		final Specimen specimen = new Specimen();
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		String s = get(record, CsvField.barcode.ordinal());
		if (s == null) {
			throw new Exception("Missing barcode");
		}
		specimen.setSourceSystemId(s);
		specimen.setUnitID(s);
		specimen.setRecordBasis(get(record, CsvField.basisofrec.ordinal()));
		specimen.setSetID(ID_PREFIX + get(record, CsvField.brahms.ordinal()));
		specimen.setNotes(get(record, CsvField.plantdesc.ordinal()));
		specimen.setGatheringEvent(getGatheringEvent(record));
		specimen.addIndentification(getSpecimenIdentification(record));
		return Arrays.asList(specimen);
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		String id = ID_PREFIX + get(record, CsvField.barcode.ordinal());
		return Arrays.asList(id);
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


	private static SpecimenIdentification getSpecimenIdentification(CSVRecord record)
	{
		final SpecimenIdentification identification = new SpecimenIdentification();
		String s = get(record, CsvField.ident_by.ordinal());
		if (s != null) {
			identification.addIdentifier(new Agent(s));
		}
		s = get(record, CsvField.vernacular.ordinal());
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
		catch (Exception e) {
			logger.warn(String.format("Unable to construct date for year=\"%s\";month=\"%s\";day=\"%s\"", year, month, day));
			return null;
		}
	}
}
