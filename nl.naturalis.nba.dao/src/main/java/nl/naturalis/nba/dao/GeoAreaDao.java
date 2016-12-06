package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.util.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.query.QuerySpecTranslator;

public class GeoAreaDao extends NbaDao<GeoArea> implements IGeoAreaAccess {

	private static final Logger logger = getLogger(GeoAreaDao.class);

	public GeoAreaDao()
	{
		super(GEO_AREA);
	}

	public static String getIdForLocality(String locality)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getIdForLocality", locality));
		}
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("locality", EQUALS, locality));
		QuerySpecTranslator translator = new QuerySpecTranslator(qs, GEO_AREA);
		SearchRequestBuilder request;
		try {
			request = translator.translate();
			request.setNoFields();
		}
		catch (InvalidQueryException e) {
			// We made this one outselves, so eh ...
			throw new DaoException(e);
		}
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return null;
		}
		return hits[0].getId();
	}

	@Override
	public GeoJsonObject getGeoJsonForLocality(String locality)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getGeoJsonForLocality", locality));
		}
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("locality", EQUALS, locality));
		QueryResult<GeoArea> areas;
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
		return areas.get(0).getShape();
	}

	@Override
	GeoArea[] createDocumentObjectArray(int length)
	{
		return new GeoArea[length];
	}

}
