package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_CRS;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;

import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

/**
 * The loader component for the CRS multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	private static IndexManagerNative indexManager()
	{
		return ETLRegistry.getInstance().getNbaIndexManager();
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
