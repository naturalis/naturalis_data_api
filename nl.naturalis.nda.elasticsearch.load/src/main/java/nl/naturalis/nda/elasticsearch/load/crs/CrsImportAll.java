package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_SPECIMEN;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrsImportAll {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), DEFAULT_NDA_INDEX_NAME);

		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_SPECIMEN);
			index.deleteType(LUCENE_TYPE_MULTIMEDIA_OBJECT);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType(LUCENE_TYPE_SPECIMEN, mapping);
			mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_SPECIMEN)) {
				index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.CRS.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
				index.addType(LUCENE_TYPE_SPECIMEN, mapping);
			}
			if (index.typeExists(LUCENE_TYPE_MULTIMEDIA_OBJECT)) {
				index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.CRS.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
				index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
			}
		}

		try {
			CrsSpecimenImporter specimenImporter = new CrsSpecimenImporter(index);
			specimenImporter.importSpecimens();
			CrsMultiMediaImporter multimediaImporter = new CrsMultiMediaImporter(index);
			multimediaImporter.importMultiMedia();
		}
		finally {
			index.getClient().close();
		}

		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsImportAll.class);
}
