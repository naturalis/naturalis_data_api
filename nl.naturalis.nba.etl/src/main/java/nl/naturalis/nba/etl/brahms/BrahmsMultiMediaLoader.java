package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.es.util.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;

/**
 * The loader component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaLoader extends ElasticSearchLoader<ESMultiMediaObject> {

	public BrahmsMultiMediaLoader(ETLStatistics stats)
	{
		super(MULTI_MEDIA_OBJECT, 1000, stats);
	}

	@Override
	protected IdGenerator<ESMultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<ESMultiMediaObject>() {

			@Override
			public String getId(ESMultiMediaObject obj)
			{
				return getElasticsearchId(BRAHMS, obj.getUnitID());
			}
		};
	}

}
