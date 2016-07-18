package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.api.model.SourceSystem.GEO;
import static nl.naturalis.nba.dao.es.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.es.types.ESGeoArea;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

class GeoLoader extends Loader<ESGeoArea> {

	GeoLoader(ETLStatistics stats, int treshold)
	{
		super(GEO_AREA, treshold, stats);
	}

	@Override
	protected IdGenerator<ESGeoArea> getIdGenerator()
	{
		return new IdGenerator<ESGeoArea>() {

			@Override
			public String getId(ESGeoArea obj)
			{
				return getElasticsearchId(GEO, obj.getAreaId());
			}
		};
	}

}
