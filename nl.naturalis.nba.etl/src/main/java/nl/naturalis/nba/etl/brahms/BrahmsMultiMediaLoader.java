package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

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

	public BrahmsMultiMediaLoader(int queueSize, ETLStatistics stats)
	{
		super(MULTI_MEDIA_OBJECT, queueSize, stats);
	}

}
