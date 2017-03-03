package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;

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
		finally {
			ESUtil.refreshIndex(NAME_GROUP);
			ESClientManager.getInstance().closeClient();
		}
	}

	@SuppressWarnings("static-method")
	public void importNames() throws BulkIndexException
	{
		ESUtil.deleteIndex(NAME_GROUP.getIndexInfo());
		ESUtil.createIndex(NAME_GROUP.getIndexInfo());
		boolean suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		String prop = System.getProperty("batchSize", "1000");
		int batchSize = 0;
		try {
			batchSize = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			System.err.println("Invalid batch size: " + prop);
			System.exit(1);
		}
		prop = System.getProperty("timeout", "30000");
		int timeout = 0;
		try {
			timeout = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			System.err.println("Invalid timeout: " + prop);
			System.exit(1);
		}
		TaxonNameImporter importer0=new TaxonNameImporter();
		importer0.setSuppressErrors(suppressErrors);
		importer0.setBatchSize(batchSize);
		importer0.setTimeout(timeout);
		importer0.importNames();
		
		SpecimenNameImporter importer = new SpecimenNameImporter();
		importer.setSuppressErrors(suppressErrors);
		importer.setBatchSize(batchSize);
		importer.setTimeout(timeout);
		importer.importNames();
	}

}
