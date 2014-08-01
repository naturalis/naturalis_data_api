package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.CoLSynonym;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class SynonymsImporter extends CSVImporter<CoLSynonym> {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);		
		index.deleteType(LUCENE_TYPE);
		Thread.sleep(2000);
		String mapping = StringUtil.getResourceAsString("/es-mappings/CoLSynonym.json");
		index.addType(LUCENE_TYPE, mapping);
		SynonymsImporter importer = new SynonymsImporter(index);
		importer.setBatchSize(500);
		importer.importCsv("C:/test/col-dwca/taxa.txt");
		index.getClient().close();
	}

	private static final String LUCENE_TYPE = "CoLSynonym";


	public SynonymsImporter(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected CoLSynonym transfer(CSVRecord record)
	{
		final CoLSynonym synonym = new CoLSynonym();
		
		// Take note here
		synonym.setSynonymID(getInt(record, TaxaImporter.CsvField.taxonID.ordinal()));
		synonym.setTaxonID(getInt(record, TaxaImporter.CsvField.acceptedNameUsageID.ordinal()));
		
		synonym.setDatasetID(record.get(TaxaImporter.CsvField.datasetID.ordinal()));
		synonym.setDescription(record.get(TaxaImporter.CsvField.description.ordinal()));
		synonym.setGenus(record.get(TaxaImporter.CsvField.genus.ordinal()));
		synonym.setIdentifier(record.get(TaxaImporter.CsvField.identifier.ordinal()));
		synonym.setInfraspecificEpithet(record.get(TaxaImporter.CsvField.infraspecificEpithet.ordinal()));
		synonym.setKingdom(record.get(TaxaImporter.CsvField.kingdom.ordinal()));
		synonym.setModified(record.get(TaxaImporter.CsvField.modified.ordinal()));
		synonym.setNameAccordingTo(record.get(TaxaImporter.CsvField.nameAccordingTo.ordinal()));
		synonym.setNamePublishedIn(record.get(TaxaImporter.CsvField.namePublishedIn.ordinal()));
		synonym.setReferences(record.get(TaxaImporter.CsvField.references.ordinal()));
		synonym.setScientificName(record.get(TaxaImporter.CsvField.scientificName.ordinal()));
		synonym.setScientificNameAuthorship(record.get(TaxaImporter.CsvField.scientificNameAuthorship.ordinal()));
		synonym.setScientificNameID(record.get(TaxaImporter.CsvField.scientificNameID.ordinal()));
		synonym.setSource(record.get(TaxaImporter.CsvField.source.ordinal()));
		synonym.setSpecificEpithet(record.get(TaxaImporter.CsvField.specificEpithet.ordinal()));
		synonym.setSubgenus(record.get(TaxaImporter.CsvField.subgenus.ordinal()));
		synonym.setTaxonConceptID(getInt(record, TaxaImporter.CsvField.taxonConceptID.ordinal()));
		synonym.setTaxonomicStatus(record.get(TaxaImporter.CsvField.taxonomicStatus.ordinal()));
		synonym.setTaxonRank(record.get(TaxaImporter.CsvField.taxonRank.ordinal()));
		synonym.setVerbatimTaxonRank(record.get(TaxaImporter.CsvField.verbatimTaxonRank.ordinal()));
		return synonym;
	}


	protected boolean skipRecord(CSVRecord record)
	{
		if (getInt(record, TaxaImporter.CsvField.acceptedNameUsageID.ordinal()) == 0) {
			return true;
		}
		return false;
	}


	@Override
	protected String getId(CSVRecord record)
	{
		return record.get(TaxaImporter.CsvField.taxonID.ordinal());
	}

	@Override
	protected String getParentId(CSVRecord record)
	{
		return record.get(TaxaImporter.CsvField.acceptedNameUsageID.ordinal());
	}

}
