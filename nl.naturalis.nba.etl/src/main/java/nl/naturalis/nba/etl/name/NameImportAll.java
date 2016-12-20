package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;

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
			ESUtil.refreshIndex(NAME);
			ESClientManager.getInstance().closeClient();
		}
	}

	@SuppressWarnings("static-method")
	public void importNames()
	{
		ESUtil.deleteIndex(NAME.getIndexInfo());
		ESUtil.createIndex(NAME.getIndexInfo());
		NameImporter<?> importer;
//		importer = new SpecimenNameImporter();
//		importer.importNames();
		importer = new TaxonNameImporter();
		importer.importNames();
//		importer = new MultiMediaObjectNameImporter();
//		importer.importNames();
	}

}
