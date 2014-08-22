package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class TaxaImporterNew extends CSVImporter<ESTaxon> {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

		index.deleteType(LUCENE_TYPE);
		index.deleteType("CoLTaxon");
		Thread.sleep(2000);

		String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
		index.addType(LUCENE_TYPE, mapping);

		try {
			TaxaImporterNew importer = new TaxaImporterNew(index);
			importer.importCsv("C:/test/col-dwca/taxa.txt");

		}
		finally {
			index.getClient().close();
		}
	}

	//@formatter:off
	static enum CsvField {
		taxonID
		, identifier
		, datasetID
		, datasetName
		, acceptedNameUsageID
		, parentNameUsageID
		, taxonomicStatus
		, taxonRank
		, verbatimTaxonRank
		, scientificName
		, kingdom
		, phylum
		, classRank
		, order
		, superfamily
		, family
		, genericName
		, genus
		, subgenus
		, specificEpithet
		, infraspecificEpithet
		, scientificNameAuthorship
		, source
		, namePublishedIn
		, nameAccordingTo
		, modified
		, description
		, taxonConceptID
		, scientificNameID
		, references	
	}
	//@formatter:on

	private static final String LUCENE_TYPE = "Taxon";


	public TaxaImporterNew(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected ESTaxon transfer(CSVRecord record)
	{
		final ESTaxon taxon = new ESTaxon();

		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.setSourceSystemId(record.get(CsvField.taxonID.ordinal()));

		ScientificName sn = new ScientificName();
		taxon.setAcceptedName(sn);
		sn.setFullScientificName(record.get(CsvField.scientificName.ordinal()));
		sn.setGenusOrMonomial(record.get(CsvField.genus.ordinal()));
		sn.setSpecificEpithet(record.get(CsvField.specificEpithet.ordinal()));
		sn.setInfraspecificEpithet(record.get(CsvField.infraspecificEpithet.ordinal()));
		sn.setAuthorshipVerbatim(record.get(CsvField.scientificNameAuthorship.ordinal()));

		DefaultClassification dc = new DefaultClassification();
		taxon.setDefaultClassification(dc);
		dc.setKingdom(record.get(CsvField.kingdom.ordinal()));
		dc.setPhylum(record.get(CsvField.phylum.ordinal()));
		dc.setClassName(record.get(CsvField.classRank.ordinal()));
		dc.setOrder(record.get(CsvField.order.ordinal()));
		dc.setSuperFamily(record.get(CsvField.superfamily.ordinal()));
		dc.setFamily(record.get(CsvField.family.ordinal()));
		dc.setGenus(record.get(CsvField.genus.ordinal()));
		dc.setSubgenus(record.get(CsvField.subgenus.ordinal()));
		dc.setSpecificEpithet(record.get(CsvField.specificEpithet.ordinal()));
		dc.setInfraspecificEpithet(record.get(CsvField.infraspecificEpithet.ordinal()));

		String description = record.get(CsvField.description.ordinal()).trim();
		if (description.length() != 0) {
			taxon.setNumDescriptions(1);
			TaxonDescription td = new TaxonDescription();
			td.setDescription(description);
			taxon.setDescription00(td);
		}

		return taxon;
	}


	protected boolean skipRecord(CSVRecord record)
	{
		if (getInt(record, CsvField.acceptedNameUsageID.ordinal()) == 0) {
			return false;
		}
		return true;
	}


	@Override
	protected String getId(CSVRecord record)
	{
		return "COL-" + record.get(CsvField.taxonID.ordinal());
	}

}
