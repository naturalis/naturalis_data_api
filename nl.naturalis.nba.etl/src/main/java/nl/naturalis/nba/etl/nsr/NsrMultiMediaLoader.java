package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.dao.es.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the NSR multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrMultiMediaLoader extends Loader<ESMultiMediaObject> {

	public NsrMultiMediaLoader(int treshold, ETLStatistics stats)
	{
		super(MULTI_MEDIA_OBJECT, treshold, stats);
	}

	@Override
	protected IdGenerator<ESMultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<ESMultiMediaObject>() {

			@Override
			public String getId(ESMultiMediaObject obj)
			{
				return getElasticsearchId(NSR, obj.getSourceSystemId());
			}
		};
	}

}
