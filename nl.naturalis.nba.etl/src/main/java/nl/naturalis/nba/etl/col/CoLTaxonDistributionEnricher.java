package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.es.DocumentType.TAXON;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.elasticsearch.BulkIndexException;
import nl.naturalis.nba.etl.elasticsearch.IndexManager;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;


/**
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class CoLTaxonDistributionEnricher {

	public static void main(String[] args) throws Exception
	{
		IndexManagerNative index = null;
		try {
			index = ETLRegistry.getInstance().getIndexManager(TAXON);
			CoLTaxonDistributionEnricher enricher = new CoLTaxonDistributionEnricher(index);
			String dwcaDir = DAORegistry.getInstance().getConfiguration().required("col.csv_dir");
			enricher.importCsv(dwcaDir + "/distribution.txt");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLTaxonDistributionEnricher.class);

	private final IndexManager index;
	private final int bulkRequestSize;
	private final int maxRecords;


	/**
	 * 
	 * @param index
	 */
	public CoLTaxonDistributionEnricher(IndexManager index)
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);
		prop = System.getProperty("maxRecords", "0");
		maxRecords = Integer.parseInt(prop);
	}


	public void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));

		ArrayList<ESTaxon> objects = new ArrayList<>(bulkRequestSize);
		ArrayList<String> ids = new ArrayList<>(bulkRequestSize);

		int lineNo = 0;
		int processed = 0;
		int indexed = 0;
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
					String taxonId = null;//CSVImportUtil.val(record, CoLVernacularNameCsvField.taxonID.ordinal());
					String esId = /*CoLImportAll.ID_PREFIX +*/ taxonId;
					String loc = null;//CSVImportUtil.val(record, CoLVernacularNameCsvField.locality.ordinal());

					taxon = findTaxonInBatch(taxonId, objects);
					if (taxon == null) {
						taxon = index.get(TAXON.getName(), esId, ESTaxon.class);
					}
					if (taxon == null) {
						logger.debug("Distribution locality: " + loc);
					}
					else if (taxon.getLocalities() == null || !taxon.getLocalities().contains(loc)) {
						taxon.addLocality(loc);
						objects.add(taxon);
						ids.add(esId);
						if (objects.size() >= bulkRequestSize) {
							try {
								index.saveObjects(TAXON.getName(), objects, ids);
								indexed += objects.size();
							}
							finally {
								objects.clear();
								ids.clear();
							}
						}
					}

				}
				catch (Throwable t) {
					++bad;
					logger.error("Error at line " + lineNo + ": " + t.getMessage());
					logger.error("Line: [[" + line + "]]");
					logger.debug("Stack trace: ", t);
				}
				if (maxRecords > 0 && processed >= maxRecords) {
					break;
				}
				if (processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
			}
			if (!objects.isEmpty()) {
				try {
					index.saveObjects(TAXON.getName(), objects, ids);
				}
				catch (BulkIndexException e) {
					throw new RuntimeException(e);
				}
				indexed += objects.size();
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Records processed: " + processed);
		logger.info("Bad records: " + bad);
		logger.info("Documents indexed: " + indexed);
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
