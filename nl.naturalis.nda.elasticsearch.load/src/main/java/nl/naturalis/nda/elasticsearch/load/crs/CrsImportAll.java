package nl.naturalis.nda.elasticsearch.load.crs;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrsImportAll {

	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		IndexNative index = null;
		try {
			index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			CrsImportAll crsImportAll = new CrsImportAll(index);
			crsImportAll.importAll();
		}
		finally {
			index.getClient().close();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsImportAll.class);

	private final IndexNative index;


	public CrsImportAll(IndexNative index)
	{
		this.index = index;
	}


	public void importAll() throws Exception
	{
		CrsSpecimenImporter specimenImporter = new CrsSpecimenImporter(index);
		specimenImporter.importSpecimens();
		CrsMultiMediaImporter multimediaImporter = new CrsMultiMediaImporter(index);
		multimediaImporter.importMultiMedia();
	}
}
