package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_SUPPRESS_ERRORS;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.utils.ConfigObject;

public class NameImportAll {

	public static void main(String[] args) throws Exception
	{
		try {
			NameImportAll nameImportAll = new NameImportAll();
			nameImportAll.importNames();
		}
		catch (Exception e) {
			logger.fatal("NameImportAll aborted unexpectedly", e);
			throw e;
		}
		finally {
			ESUtil.refreshIndex(SCIENTIFIC_NAME_GROUP);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(NameImportAll.class);

	public void importNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		boolean suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		String prop = System.getProperty("batchSize", "2000");
		int batchSize = 0;
		try {
			batchSize = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			System.err.println("Invalid batch size: " + prop);
			System.exit(1);
		}
		prop = System.getProperty("timeout", "60000");
		int timeout = 0;
		try {
			timeout = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			System.err.println("Invalid timeout: " + prop);
			System.exit(1);
		}

		ESUtil.truncate(SCIENTIFIC_NAME_GROUP);
		
		TaxonNameImporter importer0 = new TaxonNameImporter();
		importer0.setSuppressErrors(suppressErrors);
		importer0.setBatchSize(batchSize);
		importer0.setTimeout(timeout);
		importer0.importNames();

		SpecimenNameImporter importer1 = new SpecimenNameImporter();
		importer1.setSuppressErrors(suppressErrors);
		importer1.setBatchSize(batchSize);
		importer1.setTimeout(timeout);
		importer1.importNames();

		logDuration(logger, getClass(), start);
	}

}
