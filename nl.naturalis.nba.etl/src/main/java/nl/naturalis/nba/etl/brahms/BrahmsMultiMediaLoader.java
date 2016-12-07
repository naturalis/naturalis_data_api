package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaLoader extends Loader<MultiMediaObject> {

	private static final IdGenerator<MultiMediaObject> ID_GENERATOR = new IdGenerator<MultiMediaObject>() {

		@Override
		public String getId(MultiMediaObject obj)
		{
			return getElasticsearchId(BRAHMS, obj.getUnitID());
		}
	};

	public BrahmsMultiMediaLoader(ETLStatistics stats)
	{
		super(MULTI_MEDIA_OBJECT, 1000, stats);
	}

	@Override
	protected IdGenerator<MultiMediaObject> getIdGenerator()
	{
		return ID_GENERATOR;
	}

}
