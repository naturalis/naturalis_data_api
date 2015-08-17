package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonomicStatus;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVImportUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.col.CoLTaxonImporter.CsvField;
import nl.naturalis.nda.elasticsearch.load.normalize.TaxonomicStatusNormalizer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

public class CoLTaxonSynonymEnricher {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			CoLTaxonSynonymEnricher enricher = new CoLTaxonSynonymEnricher(index);
			String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
			enricher.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final TaxonomicStatusNormalizer statusNormalizer = TaxonomicStatusNormalizer.getInstance();
	private static final Logger logger = Registry.getInstance().getLogger(CoLTaxonSynonymEnricher.class);

	private final Index index;
	private final int bulkRequestSize;
	private final int maxRecords;


	public CoLTaxonSynonymEnricher(Index index)
	{
		this.index = index;
		String prop = System.getProperty(CoLImportAll.SYSPROP_BATCHSIZE, "1000");
		bulkRequestSize = Integer.parseInt(prop);
		prop = System.getProperty(CoLImportAll.SYSPROP_MAXRECORDS, "0");
		maxRecords = Integer.parseInt(prop);
	}


	public void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));

		ArrayList<ESTaxon> objects = new ArrayList<ESTaxon>(bulkRequestSize);
		ArrayList<String> ids = new ArrayList<String>(bulkRequestSize);

		int lineNo = 0;
		int processed = 0;
		int indexed = 0;
		int skipped = 0;
		int bad = 0;

		String line;
		CSVRecord record;

		try {

			++lineNo;
			lnr.readLine(); // Skip header

			ESTaxon taxon;
			while ((line = lnr.readLine()) != null) {
				++lineNo;
				if (line.trim().length() == 0) {
					logger.info("Ignoring empty line: " + lineNo);
					continue;
				}
				++processed;
				try {
					record = CSVParser.parse(line, format).iterator().next();
					if (CSVImportUtil.ival(record, CoLTaxonImporter.CsvField.acceptedNameUsageID.ordinal()) == 0) {
						// This record contains an accepted name, not a synonym
						++skipped;
					}
					else {
						String taxonId = CSVImportUtil.val(record, CsvField.acceptedNameUsageID.ordinal());
						String esId = CoLImportAll.ID_PREFIX + taxonId;

						String synonym = CSVImportUtil.val(record, CsvField.scientificName.ordinal());

						taxon = findTaxonInBatch(taxonId, objects);
						if (taxon == null) {
							taxon = index.get(LUCENE_TYPE_TAXON, esId, ESTaxon.class);
						}
						if (taxon == null) {
							logger.debug("Orphan synonym: " + synonym);
						}
						else if (taxon.getSynonyms() == null || !taxon.getSynonyms().contains(synonym)) {
							taxon.addSynonym(transfer(record));
							objects.add(taxon);
							ids.add(esId);
							if (objects.size() >= bulkRequestSize) {
								try {
									index.saveObjects(LUCENE_TYPE_TAXON, objects, ids);
									indexed += objects.size();
								}
								finally {
									objects.clear();
									ids.clear();
								}
							}
						}
						else {
							logger.debug("Synonym already exists: " + synonym);
						}
					}
				}
				catch (Throwable t) {
					++bad;
					logger.error("Error at line " + lineNo + ": " + t.getMessage());
					logger.error(line);
					if (logger.isDebugEnabled()) {
						logger.debug("Stack trace: ", t);
					}
				}
				if (maxRecords > 0 && processed >= maxRecords) {
					break;
				}
				if (processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
			}
			if (!objects.isEmpty()) {
				index.saveObjects(LUCENE_TYPE_TAXON, objects, ids);
				indexed += objects.size();
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Records processed: " + processed);
		logger.info("Records skipped: " + skipped);
		logger.info("Bad records: " + bad);
		logger.info("Documents indexed: " + indexed);
	}


	private static ScientificName transfer(CSVRecord record)
	{
		final ScientificName sn = new ScientificName();
		sn.setFullScientificName(CSVImportUtil.val(record, CsvField.scientificName.ordinal()));
		sn.setGenusOrMonomial(CSVImportUtil.val(record, CsvField.genericName.ordinal()));
		sn.setSpecificEpithet(CSVImportUtil.val(record, CsvField.specificEpithet.ordinal()));
		sn.setInfraspecificEpithet(CSVImportUtil.val(record, CsvField.infraspecificEpithet.ordinal()));
		sn.setAuthorshipVerbatim(CSVImportUtil.val(record, CsvField.scientificNameAuthorship.ordinal()));
		TaxonomicStatus status = statusNormalizer.getEnumConstant(CSVImportUtil.val(record, CsvField.taxonomicStatus.ordinal()));
		sn.setTaxonomicStatus(status);
		return sn;
	}


	private static ESTaxon findTaxonInBatch(String taxonId, ArrayList<ESTaxon> batch)
	{
		for (ESTaxon taxon : batch) {
			if (taxonId.equals(taxon.getSourceSystemId())) {
				return taxon;
			}
		}
		return null;
	}

}
