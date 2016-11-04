package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.util.ESUtil.getElasticsearchId;

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

	CrsMultiMediaLoader(ETLStatistics stats, int treshold)
	{
		super(MULTI_MEDIA_OBJECT, treshold, stats);
	}

	@Override
	protected IdGenerator<MultiMediaObject> getIdGenerator()
	{
		return new IdGenerator<MultiMediaObject>() {
			@Override
			public String getId(MultiMediaObject obj)
			{
				return getElasticsearchId(CRS, obj.getUnitID());
			}
		};
	}

}
