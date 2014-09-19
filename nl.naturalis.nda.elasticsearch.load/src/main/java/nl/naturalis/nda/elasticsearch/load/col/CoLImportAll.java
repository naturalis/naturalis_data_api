package nl.naturalis.nda.elasticsearch.load.col;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.StringUtil;

public class CoLImportAll {

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
			CoLTaxonImporter importer = new CoLTaxonImporter(index);
			importer.importCsv(dwcaDir + "/taxa.txt");
			CoLTaxonSynonymEnricher enricher0 = new CoLTaxonSynonymEnricher(index);
			enricher0.importCsv(dwcaDir + "/taxa.txt");
			CoLTaxonVernacularNameEnricher enricher1 = new CoLTaxonVernacularNameEnricher(index);
			enricher1.importCsv(dwcaDir + "/vernacular.txt");
		}
		finally {
			index.getClient().close();
		}
		System.out.println("Done");
	}

}
