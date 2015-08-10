package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ETL class using CRS's OAIPMH service to extract the data, w3c DOM to parse
 * the data, and ElasticSearch's native client to save the data.
 * 
 * @author ayco_holleman
 * 
 */
public class CrsSpecimenImporter extends AbstractSpecimenImporter {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		// Set up thematic search and make sure it's configured OK.
		ThemeCache.getInstance();

		// Debug functionality: just see what a particular specimen in
		// source data looks like
		String unitIDToCheck = System.getProperty("check");
		if (unitIDToCheck != null) {
			CrsSpecimenImporter importer = new CrsSpecimenImporter(null);
			importer.checkSpecimen(unitIDToCheck);
			return;
		}

		// For real
		IndexNative index = null;
		try {
			index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			CrsSpecimenImporter importer = new CrsSpecimenImporter(index);
			importer.importSpecimens();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsSpecimenImporter.class);

	private final Index index;


	public CrsSpecimenImporter(Index index) throws Exception
	{
		super();
		this.index = index;
	}


	@Override
	protected void beforeFirst()
	{
		index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.CRS.getCode());
	}


	@Override
	protected void saveSpecimens(List<ESSpecimen> specimens, List<String> databaseIds)
	{
		for (int i = 0; i < databaseIds.size(); ++i) {
			databaseIds.set(i, CrsImportAll.ID_PREFIX + databaseIds.get(i));
		}
		index.saveObjects(LUCENE_TYPE_SPECIMEN, specimens, databaseIds);
	}


	@Override
	protected void deleteSpecimen(String databaseId)
	{
		databaseId = CrsImportAll.ID_PREFIX + databaseId;
		index.deleteDocument(LUCENE_TYPE_SPECIMEN, databaseId);
	}

}
