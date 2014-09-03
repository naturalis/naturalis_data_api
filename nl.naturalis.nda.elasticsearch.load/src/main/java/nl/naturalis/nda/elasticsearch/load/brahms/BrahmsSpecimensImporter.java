package nl.naturalis.nda.elasticsearch.load.brahms;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.GatheringEvent;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
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
	protected Specimen transfer(CSVRecord record) throws Exception
	{
		final Specimen specimen = new Specimen();
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		String s = get(record,CsvField.barcode.ordinal());
		if (s == null) {
			throw new Exception("Missing barcode");
		}
		specimen.setSourceSystemId(s);
		specimen.setUnitID(s);
		specimen.setRecordBasis(get(record,CsvField.basisofrec.ordinal()));
		specimen.setSetID(ID_PREFIX + get(record,CsvField.brahms.ordinal()));

		SpecimenIdentification identification = new SpecimenIdentification();
		specimen.addIndentification(identification);
		identification.setIdentifiedBy(get(record,CsvField.ident_by.ordinal()));

		ScientificName sn = new ScientificName();
		identification.setScientificName(sn);
		sn.setFullScientificName(get(record,CsvField.scientific.ordinal()));
		sn.setAuthorshipVerbatim(get(record,CsvField.authorname.ordinal()));
		sn.setGenusOrMonomial(get(record,CsvField.genus.ordinal()));
		sn.setSpecificEpithet(get(record,CsvField.species.ordinal()));
		sn.setInfraspecificEpithet(get(record,CsvField.subspecies.ordinal()));

		DefaultClassification dc = new DefaultClassification();
		identification.setDefaultClassification(dc);
		dc.setKingdom(get(record,CsvField.kingdom.ordinal()));
		dc.setPhylum(get(record,CsvField.division.ordinal()));
		dc.setOrder(get(record,CsvField.order.ordinal()));
		dc.setFamily(get(record,CsvField.family.ordinal()));
		dc.setGenus(get(record,CsvField.genus.ordinal()));
		dc.setSpecificEpithet(get(record,CsvField.species.ordinal()));
		dc.setInfraspecificEpithet(get(record,CsvField.subspecies.ordinal()));

		GatheringEvent ge = new GatheringEvent();
		specimen.setGatheringEvent(ge);
		Double longitude = dget(record,CsvField.longitude.ordinal());
		Double latitude = dget(record,CsvField.latitude.ordinal());
		ge.addSiteCoordinates(latitude, longitude);

		return specimen;
	}


	@Override
	protected String getId(CSVRecord record)
	{
		return ID_PREFIX + get(record,CsvField.barcode.ordinal());
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
