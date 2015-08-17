package nl.naturalis.nda.elasticsearch.load.crs;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.slf4j.Logger;

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
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	public static final String ID_PREFIX = "CRS-";
	public static final String SYSPROP_BATCHSIZE = "nl.naturalis.nda.elasticsearch.load.crs.batchsize";
	public static final String SYSPROP_MAXRECORDS = "nl.naturalis.nda.elasticsearch.load.crs.maxrecords";

	private static final Logger logger = Registry.getInstance().getLogger(CrsImportAll.class);

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
