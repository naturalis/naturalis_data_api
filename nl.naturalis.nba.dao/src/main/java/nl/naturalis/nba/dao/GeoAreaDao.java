package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.model.GeoArea;

public class GeoAreaDao extends NbaDao<GeoArea> implements IGeoAreaAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(GeoAreaDao.class);

	public GeoAreaDao()
	{
		super(GEO_AREA);
	}

	@Override
	GeoArea[] createDocumentObjectArray(int length)
	{
		return new GeoArea[length];
	}

}
