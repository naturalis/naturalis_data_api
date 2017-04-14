package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Manages the import of taxa, synonyms, vernacular names and literature
 * references from the Catalogue of Life.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLImportAll {

	public static void main(String[] args) throws Exception
	{
		try {
			CoLImportAll importer = new CoLImportAll();
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

	private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLImportAll.class);

	public CoLImportAll()
	{
	}

	/**
	 * Imports CoL taxa, synonyms, vernacular names and literature references.
	 * @throws BulkIndexException 
	 * 
	 */
	public void importAll() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
		new CoLTaxonImporter().importCsv(dwcaDir + "/taxa.txt");
		new CoLSynonymImporter().importCsv(dwcaDir + "/taxa.txt");
		new CoLVernacularNameImporter().importCsv(dwcaDir + "/vernacular.txt");
		new CoLReferenceImporter().importCsv(dwcaDir + "/reference.txt");
		logDuration(logger, getClass(), start);
	}
}
