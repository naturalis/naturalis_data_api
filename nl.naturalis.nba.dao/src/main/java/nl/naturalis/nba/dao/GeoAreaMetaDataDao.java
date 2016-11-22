package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;

import nl.naturalis.nba.api.model.GeoArea;

public class GeoAreaMetaDataDao extends MetaDataDao<GeoArea> {

	public GeoAreaMetaDataDao()
	{
		super(GEO_AREA);
	}

}
