package nl.naturalis.nba.etl.col;

import nl.naturalis.nba.dao.DaoRegistry;

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
		CoLImportAll importer = new CoLImportAll();
		importer.importAll();
	}

	public CoLImportAll()
	{
	}

	/**
	 * Imports CoL taxa, synonyms, vernacular names and literature references.
	 * 
	 */
	@SuppressWarnings("static-method")
	public void importAll()
	{
		String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
		new CoLTaxonImporter().importCsv(dwcaDir + "/taxa.txt");
		//new CoLSynonymImporter().importCsv(dwcaDir + "/taxa.txt");
		//new CoLVernacularNameImporter().importCsv(dwcaDir + "/vernacular.txt");
		new CoLReferenceBatchImporter().importCsv(dwcaDir + "/reference.txt");
	}
}
