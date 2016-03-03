package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_CRS;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import nl.naturalis.nda.elasticsearch.load.Registry;

/**
 * The loader component for the CRS multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	CrsMultiMediaLoader(ETLStatistics stats, int treshold)
	{
		super(indexManager(), LUCENE_TYPE_MULTIMEDIA_OBJECT, treshold, stats);
	}

	@Override
	protected IdGenerator<ESMultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<ESMultiMediaObject>() {
			@Override
			public String getId(ESMultiMediaObject obj)
			{
				return ES_ID_PREFIX_CRS + obj.getUnitID();
			}
		};
	}

}
