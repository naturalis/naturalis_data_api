package nl.naturalis.nda.elasticsearch.load.col;

import nl.naturalis.nda.elasticsearch.load.Registry;

/**
 * Manages the import of CoL taxa, synonyms, vernacular names and literature
 * references.
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
	public void importAll()
	{
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		new CoLTaxonImporter().importCsv(dwcaDir + "/taxa.txt");
		new CoLSynonymImporter().importCsv(dwcaDir + "/taxa.txt");
		new CoLVernacularNameImporter().importCsv(dwcaDir + "/vernacular.txt");
		new CoLReferenceImporter().importCsv(dwcaDir + "/reference.txt");
//		CoLTaxonDistributionEnricher distributionEnricher = new CoLTaxonDistributionEnricher(index);
//		distributionEnricher.importCsv(dwcaDir + "/distribution.txt");
	}
}
