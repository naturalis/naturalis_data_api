package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;
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
			ESUtil.refreshIndex(NAME);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(NameImportAll.class);

	public void importNames()
	{
		long start = System.currentTimeMillis();
		ESUtil.deleteIndex(NAME.getIndexInfo());
		ESUtil.createIndex(NAME.getIndexInfo());
		NameImporter importer = new NameImporter();
		importer.importNames();
		ETLUtil.logDuration(logger, getClass(), start);
	}

}
