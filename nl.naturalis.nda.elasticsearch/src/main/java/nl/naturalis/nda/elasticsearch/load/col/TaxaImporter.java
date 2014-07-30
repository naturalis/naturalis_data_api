package nl.naturalis.nda.elasticsearch.load.col;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.CoLTaxon;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxaImporter {

	public static void main(String[] args) throws IOException
	{
		Index index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		index.deleteType("CoLCommonName");
		index.deleteType("CoLReference");
		index.deleteType(LUCENE_TYPE);
		String mapping = StringUtil.getResourceAsString("/es-mappings/CoLTaxon.json");
		index.addType(LUCENE_TYPE, mapping);
		TaxaImporter harvester = new TaxaImporter(index);
		harvester.harvest();
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

	private static final Logger logger = LoggerFactory.getLogger(TaxaImporter.class);
	private static final int BULK_REQUEST_BATCH_SIZE = 5000;
	private static final String LUCENE_TYPE = "CoLTaxon";

	private final Index index;


	public TaxaImporter(Index index)
	{
		this.index = index;
	}


	public void harvest() throws IOException
	{
		logger.info("Processing taxa.txt");
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader("C:/test/col-dwca/taxa.txt"));
		int processed = 0;
		String line;
		try {
			lnr.readLine(); // Skip header
			List<CoLTaxon> taxa = new ArrayList<CoLTaxon>(BULK_REQUEST_BATCH_SIZE);
			List<String> ids = new ArrayList<String>(BULK_REQUEST_BATCH_SIZE);
			while ((line = lnr.readLine()) != null) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
				if (line.trim().length() == 0) {
					logger.info("Skipping empty line: " + processed);
				}
				try {
					CSVRecord record = CSVParser.parse(line, format).iterator().next();
					CoLTaxon taxon = transfer(record);
					taxa.add(taxon);
					ids.add(String.valueOf(taxon.getTaxonID()));

					if (taxa.size() == BULK_REQUEST_BATCH_SIZE) {
						index.saveObjects(LUCENE_TYPE, taxa, ids);
						taxa.clear();
						ids.clear();
					}
				}
				catch (Throwable t) {
					logger.error("Error at line " + processed, t);
				}
			}
			if (!taxa.isEmpty()) {
				index.saveObjects(LUCENE_TYPE, taxa, ids);
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Ready");
	}


	private static CoLTaxon transfer(CSVRecord record)
	{
		final CoLTaxon taxon = new CoLTaxon();
		taxon.setAcceptedNameUsageID(getInt(record, CsvField.acceptedNameUsageID));
		taxon.setClassRank(record.get(CsvField.classRank.ordinal()));
		taxon.setDatasetID(record.get(CsvField.datasetID.ordinal()));
		taxon.setDatasetName(record.get(CsvField.classRank.ordinal()));
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
		taxon.setParentNameUsageID(getInt(record, CsvField.parentNameUsageID));
		taxon.setPhylum(record.get(CsvField.phylum.ordinal()));
		taxon.setReferences(record.get(CsvField.references.ordinal()));
		taxon.setScientificName(record.get(CsvField.scientificName.ordinal()));
		taxon.setScientificNameAuthorship(record.get(CsvField.scientificNameAuthorship.ordinal()));
		taxon.setScientificNameID(record.get(CsvField.scientificNameID.ordinal()));
		taxon.setSource(record.get(CsvField.source.ordinal()));
		taxon.setSpecificEpithet(record.get(CsvField.specificEpithet.ordinal()));
		taxon.setSubgenus(record.get(CsvField.subgenus.ordinal()));
		taxon.setSuperfamily(record.get(CsvField.superfamily.ordinal()));
		taxon.setTaxonID(getInt(record, CsvField.taxonID));
		taxon.setTaxonConceptID(getInt(record, CsvField.taxonConceptID));
		taxon.setTaxonomicStatus(record.get(CsvField.taxonomicStatus.ordinal()));
		taxon.setTaxonRank(record.get(CsvField.taxonRank.ordinal()));
		taxon.setVerbatimTaxonRank(record.get(CsvField.verbatimTaxonRank.ordinal()));
		return taxon;
	}


	private static int getInt(CSVRecord record, CsvField field)
	{
		String s = record.get(field.ordinal());
		if (s.trim().length() == 0) {
			return 0;
		}
		return Integer.parseInt(s);
	}
}
