package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.systypes.CoLTaxon;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class TaxaImporter extends CSVImporter<CoLTaxon> {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

		index.deleteType(LUCENE_TYPE);
		Thread.sleep(2000);		

		String mapping = StringUtil.getResourceAsString("/es-mappings/CoLTaxon.json");
		index.addType(LUCENE_TYPE, mapping);
		
		TaxaImporter importer = new TaxaImporter(index);
		importer.importCsv("C:/test/col-dwca/taxa.txt");

		index.getClient().close();
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

	private static final String LUCENE_TYPE = "CoLTaxon";


	public TaxaImporter(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected CoLTaxon transfer(CSVRecord record)
	{
		final CoLTaxon taxon = new CoLTaxon();
		taxon.setAcceptedNameUsageID(getInt(record, CsvField.acceptedNameUsageID.ordinal()));
		taxon.setClassRank(record.get(CsvField.classRank.ordinal()));
		taxon.setDatasetID(record.get(CsvField.datasetID.ordinal()));
		taxon.setDatasetName(record.get(CsvField.datasetName.ordinal()));
		taxon.setDescription(record.get(CsvField.description.ordinal()));
		taxon.setFamily(record.get(CsvField.family.ordinal()));
		taxon.setGenericName(record.get(CsvField.genericName.ordinal()));
		taxon.setGenus(record.get(CsvField.genus.ordinal()));
		taxon.setIdentifier(record.get(CsvField.identifier.ordinal()));
		taxon.setInfraspecificEpithet(record.get(CsvField.infraspecificEpithet.ordinal()));
		taxon.setKingdom(record.get(CsvField.kingdom.ordinal()));
		taxon.setModified(record.get(CsvField.modified.ordinal()));
		taxon.setNameAccordingTo(record.get(CsvField.nameAccordingTo.ordinal()));
		taxon.setNamePublishedIn(record.get(CsvField.namePublishedIn.ordinal()));
		taxon.setOrder(record.get(CsvField.order.ordinal()));
		taxon.setParentNameUsageID(getInt(record, CsvField.parentNameUsageID.ordinal()));
		taxon.setPhylum(record.get(CsvField.phylum.ordinal()));
		taxon.setReferences(record.get(CsvField.references.ordinal()));
		taxon.setScientificName(record.get(CsvField.scientificName.ordinal()));
		taxon.setScientificNameAuthorship(record.get(CsvField.scientificNameAuthorship.ordinal()));
		taxon.setScientificNameID(record.get(CsvField.scientificNameID.ordinal()));
		taxon.setSource(record.get(CsvField.source.ordinal()));
		taxon.setSpecificEpithet(record.get(CsvField.specificEpithet.ordinal()));
		taxon.setSubgenus(record.get(CsvField.subgenus.ordinal()));
		taxon.setSuperfamily(record.get(CsvField.superfamily.ordinal()));
		taxon.setTaxonID(getInt(record, CsvField.taxonID.ordinal()));
		taxon.setTaxonConceptID(getInt(record, CsvField.taxonConceptID.ordinal()));
		taxon.setTaxonomicStatus(record.get(CsvField.taxonomicStatus.ordinal()));
		taxon.setTaxonRank(record.get(CsvField.taxonRank.ordinal()));
		taxon.setVerbatimTaxonRank(record.get(CsvField.verbatimTaxonRank.ordinal()));
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
		return record.get(CsvField.taxonID.ordinal());
	}

}
