package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.utils.ConfigObject;

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

	@SuppressWarnings("static-method")
	public void importNames()
	{
		ESUtil.deleteIndex(NAME_GROUP.getIndexInfo());
		ESUtil.createIndex(NAME_GROUP.getIndexInfo());
		boolean suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		String prop = System.getProperty("batchSize", "500");
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
		NameImporter importer = new NameImporter();
		importer.setSuppressErrors(suppressErrors);
		importer.setBatchSize(batchSize);
		importer.setTimeout(timeout);
		importer.importSpecimenNames();
	}

}
