package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.slf4j.Logger;

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
		IndexManagerNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			CoLImportAll colImportAll = new CoLImportAll(index);
			colImportAll.importAll();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
		logger.info("Ready");
	}

	public static final String ID_PREFIX = "COL-";
	public static final String SYSPROP_BATCHSIZE = "nl.naturalis.nda.elasticsearch.load.col.batchsize";
	public static final String SYSPROP_MAXRECORDS = "nl.naturalis.nda.elasticsearch.load.col.maxrecords";

	private static final Logger logger = Registry.getInstance().getLogger(CoLImportAll.class);

	private final IndexManagerNative index;

	public CoLImportAll(IndexManagerNative index)
	{
		this.index = index;
	}

	/**
	 * Imports CoL taxa, synonyms, vernacular names and literature references.
	 * 
	 * @throws IOException
	 */
	public void importAll() throws IOException
	{
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		CoLTaxonImporter importer = new CoLTaxonImporter();
		importer.importCsv(dwcaDir + "/taxa.txt");
		CoLSynonymImporter synonymEnricher = new CoLSynonymImporter();
		synonymEnricher.importCsv(dwcaDir + "/taxa.txt");
		CoLTaxonVernacularNameEnricher vernacularNameEnricher = new CoLTaxonVernacularNameEnricher(index);
		vernacularNameEnricher.importCsv(dwcaDir + "/vernacular.txt");
		CoLReferenceImporter referenceImporter = new CoLReferenceImporter();
		referenceImporter.importCsv(dwcaDir + "/reference.txt");
		CoLTaxonDistributionEnricher distributionEnricher = new CoLTaxonDistributionEnricher(index);
		distributionEnricher.importCsv(dwcaDir + "/distribution.txt");
	}
}
