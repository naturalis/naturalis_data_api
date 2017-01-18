package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_SUMMARY;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.ETLUtil;

public class NameImportAll {

	public static void main(String[] args) throws Exception
	{
		try {
			NameImportAll nameImportAll = new NameImportAll();
			nameImportAll.importNames();
		}
		finally {
			ESUtil.refreshIndex(SCIENTIFIC_NAME_SUMMARY);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(NameImportAll.class);

	public void importNames()
	{
		long start = System.currentTimeMillis();
		ESUtil.deleteIndex(SCIENTIFIC_NAME_SUMMARY.getIndexInfo());
		ESUtil.createIndex(SCIENTIFIC_NAME_SUMMARY.getIndexInfo());
		NameImporter importer = new NameImporter();
		importer.importNames();
		ETLUtil.logDuration(logger, getClass(), start);
	}

}
