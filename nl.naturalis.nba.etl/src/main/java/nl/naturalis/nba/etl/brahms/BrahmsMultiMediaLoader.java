package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_BRAHMS;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;

/**
 * The loader component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	public BrahmsMultiMediaLoader(ETLStatistics stats)
	{
		super(indexManager(), LUCENE_TYPE_MULTIMEDIA_OBJECT, 1000, stats);
	}

	@Override
	protected IdGenerator<ESMultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<ESMultiMediaObject>() {
			@Override
			public String getId(ESMultiMediaObject obj)
			{
				return ES_ID_PREFIX_BRAHMS + obj.getUnitID();
			}
		};
	}

}
