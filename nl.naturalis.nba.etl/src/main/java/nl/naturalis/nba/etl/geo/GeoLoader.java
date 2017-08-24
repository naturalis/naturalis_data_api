package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

class GeoLoader extends Loader<GeoArea> {

	GeoLoader(ETLStatistics stats, int treshold)
	{
		super(GEO_AREA, treshold, stats);
	}

}
