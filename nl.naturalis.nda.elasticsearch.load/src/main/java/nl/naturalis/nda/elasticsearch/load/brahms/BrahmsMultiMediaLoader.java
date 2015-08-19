package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;

class BrahmsMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	public BrahmsMultiMediaLoader(IndexNative indexManager)
	{
		super(indexManager, LUCENE_TYPE_MULTIMEDIA_OBJECT, 1000);
	}

	@Override
	protected IdGenerator<ESMultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<ESMultiMediaObject>() {
			@Override
			public String getId(ESMultiMediaObject obj)
			{
				return BrahmsImportAll.ID_PREFIX + obj.getUnitID();
			}
		};
	}

}
