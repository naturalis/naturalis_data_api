package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_NSR;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;

import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

/**
 * The loader component for the NSR multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	private static IndexManagerNative indexManager()
	{
		return ETLRegistry.getInstance().getNbaIndexManager();
	}

	public NsrMultiMediaLoader(int treshold, ETLStatistics stats)
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
				return ES_ID_PREFIX_NSR + obj.getSourceSystemId();
			}
		};
	}

}
