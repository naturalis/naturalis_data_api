package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.slf4j.Logger;

public class CoLImportAll {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
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

	private final IndexNative index;


	public CoLImportAll(IndexNative index)
	{
		this.index = index;
	}


	public void importAll() throws IOException
	{
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		CoLTaxonImporter importer = new CoLTaxonImporter(index);
		importer.importCsv(dwcaDir + "/taxa.txt");
		CoLTaxonSynonymEnricher synonymEnricher = new CoLTaxonSynonymEnricher(index);
		synonymEnricher.importCsv(dwcaDir + "/taxa.txt");
		CoLTaxonVernacularNameEnricher vernacularNameEnricher = new CoLTaxonVernacularNameEnricher(index);
		vernacularNameEnricher.importCsv(dwcaDir + "/vernacular.txt");
		CoLTaxonReferenceEnricher referenceEnricher = new CoLTaxonReferenceEnricher(index);
		referenceEnricher.importCsv(dwcaDir + "/reference.txt");
		CoLTaxonDistributionEnricher distributionEnricher = new CoLTaxonDistributionEnricher(index);
		distributionEnricher.importCsv(dwcaDir + "/distribution.txt");
	}
}
