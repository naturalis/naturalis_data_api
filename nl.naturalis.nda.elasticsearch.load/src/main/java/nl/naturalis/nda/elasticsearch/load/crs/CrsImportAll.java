package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
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

		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));

		index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.CRS.getCode());
		index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.CRS.getCode());

		try {
			CrsImportAll crsImportAll = new CrsImportAll(index);
			crsImportAll.importOai();
		}
		finally {
			index.getClient().close();
		}

		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsImportAll.class);

	private final IndexNative index;


	public CrsImportAll(IndexNative index)
	{
		this.index = index;
	}


	public void importOai() throws Exception
	{
		CrsSpecimenImporter specimenImporter = new CrsSpecimenImporter(index);
		specimenImporter.importSpecimens();
		CrsMultiMediaImporter multimediaImporter = new CrsMultiMediaImporter(index);
		multimediaImporter.importMultiMedia();
	}
}
