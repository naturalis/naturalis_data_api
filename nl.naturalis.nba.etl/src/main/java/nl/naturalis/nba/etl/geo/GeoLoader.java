package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.api.model.SourceSystem.GEO;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

class GeoLoader extends Loader<GeoArea> {

	GeoLoader(ETLStatistics stats, int treshold)
	{
		super(GEO_AREA, treshold, stats);
	}

	@Override
	protected IdGenerator<GeoArea> getIdGenerator()
	{
		return new IdGenerator<GeoArea>() {

			@Override
			public String getId(GeoArea obj)
			{
				return getElasticsearchId(GEO, obj.getSourceSystemId());
			}
		};
	}

}
