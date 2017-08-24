package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;


/**
 * The loader component for the CRS multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsMultiMediaLoader extends Loader<MultiMediaObject> {

	CrsMultiMediaLoader(int queueSize, ETLStatistics stats)
	{
		super(MULTI_MEDIA_OBJECT, queueSize, stats);
	}

}
