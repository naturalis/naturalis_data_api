package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;

/**
 * Manages the import of taxa, synonyms, vernacular names and literature references from
 * the Catalogue of Life.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLImportAll {

	public static void main(String[] args) throws Exception
	{
		String prop = System.getProperty("batchSize", "1000");
		int batchSize = 0;
		try {
			batchSize = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid batch size: " + prop);
		}
		if (batchSize >= 1024) {
			// Elasticsearch ids query won't let you look up more than 1024 at once.
			throw new ETLRuntimeException("Batch size exceeds maximum of 1024");
		}
		try {
			CoLImportAll importer = new CoLImportAll();
			importer.setBatchSize(batchSize);
			importer.importAll();
		}
		catch (Throwable t) {
			logger.error("CoLImportAll terminated unexpectedly!", t);
			System.exit(1);
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(CoLImportAll.class);

	private int batchSize;

	public CoLImportAll()
	{
	}

	/**
	 * Imports CoL taxa, synonyms, vernacular names and literature references.
	 * 
	 * @throws BulkIndexException
	 * 
	 */
	public void importAll() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		String dwcaDir = DaoRegistry.getInstance().getConfiguration()
				.required("col.data.dir");
		CoLTaxonImporter cti = new CoLTaxonImporter();
		cti.importCsv(dwcaDir + "/taxa.txt");
		CoLSynonymBatchImporter csbi = new CoLSynonymBatchImporter();
		csbi.setBatchSize(batchSize);
		csbi.importCsv(dwcaDir + "/taxa.txt");
		CoLVernacularNameBatchImporter cvbi = new CoLVernacularNameBatchImporter();
		cvbi.setBatchSize(batchSize);
		cvbi.importCsv(dwcaDir + "/vernacular.txt");
		CoLReferenceBatchImporter crbi = new CoLReferenceBatchImporter();
		crbi.setBatchSize(batchSize);
		crbi.importCsv(dwcaDir + "/reference.txt");
		logDuration(logger, getClass(), start);
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

}
