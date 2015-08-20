package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;

class BrahmsSpecimenLoader extends ElasticSearchLoader<ESSpecimen> {

	public BrahmsSpecimenLoader(IndexNative indexManager)
	{
		super(indexManager, LUCENE_TYPE_SPECIMEN, 1000);
	}

	@Override
	protected IdGenerator<ESSpecimen> getIdGenerator()
	{
		return new IdGenerator<ESSpecimen>() {
			@Override
			public String getId(ESSpecimen obj)
			{
				return ES_ID_PREFIX_BRAHMS + obj.getUnitID();
			}
		};
	}

}
