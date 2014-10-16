package nl.naturalis.nda.elasticsearch.load.col;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.*;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoLImportAll {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		String dwcaDir = System.getProperty("dwcaDir");
		String rebuild = System.getProperty("rebuild", "false");
		if (dwcaDir == null) {
			throw new Exception("Missing property \"dwcaDir\"");
		}

		IndexNative index = new IndexNative(LoadUtil.getDefaultClient(), DEFAULT_NDA_INDEX_NAME);

		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_TAXON);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE_TAXON, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_TAXON)) {
				index.deleteWhere(LUCENE_TYPE_TAXON, "sourceSystem.code", SourceSystem.COL.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
				index.addType(LUCENE_TYPE_TAXON, mapping);
			}
		}

		try {
			CoLTaxonImporter importer = new CoLTaxonImporter(index);
			importer.importCsv(dwcaDir + "/taxa.txt");
			CoLTaxonSynonymEnricher synonymEnricher = new CoLTaxonSynonymEnricher(index);
			synonymEnricher.importCsv(dwcaDir + "/taxa.txt");
			CoLTaxonVernacularNameEnricher vernacularNameEnricher = new CoLTaxonVernacularNameEnricher(index);
			vernacularNameEnricher.importCsv(dwcaDir + "/vernacular.txt");
			CoLTaxonReferenceEnricher referenceEnricher = new CoLTaxonReferenceEnricher(index);
			referenceEnricher.importCsv(dwcaDir + "/reference.txt");
		}
		finally {
			index.getClient().close();
		}
		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(CoLImportAll.class);
}
