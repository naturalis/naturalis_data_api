package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the NSR multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrMultiMediaLoader extends Loader<MultiMediaObject> {

	public NsrMultiMediaLoader(int queueSize, ETLStatistics stats)
	{
		super(MULTI_MEDIA_OBJECT, queueSize, stats);
	}

	@Override
	protected IdGenerator<MultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<MultiMediaObject>() {

			@Override
			public String getId(MultiMediaObject obj)
			{
				return getElasticsearchId(NSR, obj.getSourceSystemId());
			}
		};
	}

}
