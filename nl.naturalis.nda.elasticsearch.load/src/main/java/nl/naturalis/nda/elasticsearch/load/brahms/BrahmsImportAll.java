package nl.naturalis.nda.elasticsearch.load.brahms;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.StringUtil;

public class BrahmsImportAll {

	public static void main(String[] args) throws Exception
	{
		String rebuild = System.getProperty("rebuild", "false");
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType("Specimen");
			index.deleteType("MultiMediaObject");
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType("Specimen", mapping);
			mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType("MultiMediaObject", mapping);
		}
		else {
			index.deleteWhere("Specimen", "sourceSystem.code", SourceSystem.BRAHMS.getCode());
			index.deleteWhere("MultiMediaObject", "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		}
		Thread.sleep(2000);
		try {
		}
		finally {
			index.getClient().close();
		}
	}
}
