package nl.naturalis.nda.elasticsearch.load.col;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.StringUtil;

public class ImportAll {

	public static void main(String[] args) throws Exception
	{
		String dwcaDir = System.getProperty("dwcaDir");
		String rebuild = System.getProperty("rebuild", "false");
		if (dwcaDir == null) {
			throw new Exception("Missing property \"dwcaDir\"");
		}
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType("Taxon");
			Thread.sleep(2000);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType("Taxon", mapping);
		}
		try {
			TaxaImporter importer = new TaxaImporter(index);
			importer.importCsv(dwcaDir + "/taxa.txt");
			TaxonSynonymsEnricher enricher0 = new TaxonSynonymsEnricher(index);
			enricher0.importCsv(dwcaDir + "/taxa.txt");
			TaxonVernacularNamesEnricher enricher1 = new TaxonVernacularNamesEnricher(index);
			enricher1.importCsv(dwcaDir + "/vernacular.txt");
		}
		finally {
			index.getClient().close();
		}
		System.out.println("Done");
	}

}
