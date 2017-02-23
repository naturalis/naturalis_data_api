package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;

public class NameImportAll {

	public static void main(String[] args) throws Exception
	{
		try {
			NameImportAll nameImportAll = new NameImportAll();
			nameImportAll.importNames();
		}
		finally {
			ESUtil.refreshIndex(NAME_GROUP);
			ESClientManager.getInstance().closeClient();
		}
	}

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(NameImportAll.class);

	@SuppressWarnings("static-method")
	public void importNames()
	{
		ESUtil.deleteIndex(NAME_GROUP.getIndexInfo());
		ESUtil.createIndex(NAME_GROUP.getIndexInfo());
		NameImporter importer = new NameImporter();
		importer.importNames();
	}

}
