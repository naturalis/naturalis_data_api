package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import nl.naturalis.nda.elasticsearch.load.Registry;

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
