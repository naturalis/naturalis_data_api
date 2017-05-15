package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;

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

	public void importNames() throws BulkIndexException, IOException
	{
		long start = System.currentTimeMillis();
		ESUtil.truncate(SCIENTIFIC_NAME_GROUP);
		TaxonNameImporter taxonNameImporter = new TaxonNameImporter();
		taxonNameImporter.importNames();
		SpecimenNameImporter2 specimenNameImporter = new SpecimenNameImporter2();
		specimenNameImporter.importNames();
		logDuration(logger, getClass(), start);
	}

}
