package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.*;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoLImportAll {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));

		String rebuild = System.getProperty("rebuild", "false");
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
			CoLImportAll colImportAll = new CoLImportAll(index);
			colImportAll.importAll();
		}
		finally {
			index.getClient().close();
		}
		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(CoLImportAll.class);

	private final IndexNative index;


	public CoLImportAll(IndexNative index)
	{
		this.index = index;
	}


	public void importAll() throws IOException
	{
		String dwcaDir = LoadUtil.getConfig().required("col.csv_dir");
		CoLTaxonImporter importer = new CoLTaxonImporter(index);
		importer.importCsv(dwcaDir + "/taxa.txt");
		CoLTaxonSynonymEnricher synonymEnricher = new CoLTaxonSynonymEnricher(index);
		synonymEnricher.importCsv(dwcaDir + "/taxa.txt");
		CoLTaxonVernacularNameEnricher vernacularNameEnricher = new CoLTaxonVernacularNameEnricher(index);
		vernacularNameEnricher.importCsv(dwcaDir + "/vernacular.txt");
		CoLTaxonReferenceEnricher referenceEnricher = new CoLTaxonReferenceEnricher(index);
		referenceEnricher.importCsv(dwcaDir + "/reference.txt");
	}
}
