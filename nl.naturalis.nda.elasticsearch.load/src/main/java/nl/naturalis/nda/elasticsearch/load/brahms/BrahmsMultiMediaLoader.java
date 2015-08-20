package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import nl.naturalis.nda.elasticsearch.load.Registry;

class BrahmsMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	private static IndexNative indexManager()
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
