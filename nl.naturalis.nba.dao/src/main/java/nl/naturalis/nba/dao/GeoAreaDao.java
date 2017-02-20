package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import org.apache.logging.log4j.Logger;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchResult;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.dao.exception.DaoException;

public class GeoAreaDao extends NbaDao<GeoArea> implements IGeoAreaAccess {

	private static final Logger logger = getLogger(GeoAreaDao.class);

	public GeoAreaDao()
	{
		super(GEO_AREA);
	}

	@Override
	public GeoJsonObject getGeoJsonForLocality(String locality)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getGeoJsonForLocality", locality));
		}
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition("locality", EQUALS, locality));
		SearchResult<GeoArea> areas;
		try {
			areas = query(qs);
		}
		catch (InvalidQueryException e) {
			// We made this one outselves, so eh ...
			throw new DaoException(e);
		}
		if (areas.size() == 0) {
			return null;
		}
		return areas.iterator().next().getItem().getShape();
	}

	@Override
	GeoArea[] createDocumentObjectArray(int length)
	{
		return new GeoArea[length];
	}

}
