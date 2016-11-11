package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.query.QuerySpecTranslator;

public class GeoAreaDao extends NbaDao<GeoArea> implements IGeoAreaAccess {

	private static final Logger logger = getLogger(GeoAreaDao.class);

	private static LinkedHashMap<String, String> localities;
	private static LinkedHashMap<String, String> isoCodes;

	public GeoAreaDao()
	{
		super(GEO_AREA);
	}

	@Override
	public Map<String, String> getLocalities()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getLocalities()");
		}
		if (localities == null) {
			QuerySpec qs = new QuerySpec();
			qs.addFields("locality");
			qs.sortBy("locality");
			qs.setSize(2500); // Something big
			QuerySpecTranslator translator = new QuerySpecTranslator(qs, GEO_AREA);
			SearchRequestBuilder request;
			try {
				request = translator.translate();
			}
			catch (InvalidQueryException e) {
				// We made this one outselves, so eh ...
				throw new DaoException(e);
			}
			SearchResponse response = request.execute().actionGet();
			SearchHit[] hits = response.getHits().getHits();
			localities = new LinkedHashMap<>(hits.length + 8, 1F);
			for (SearchHit hit : hits) {
				Object value = hit.getSource().get("locality");
				if (value == null) {
					continue;
				}
				localities.put(value.toString(), hit.getId());
			}
		}
		return localities;
	}

	@Override
	public Map<String, String> getIsoCodes()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsoCodes()");
		}
		if (isoCodes == null) {
			QuerySpec qs = new QuerySpec();
			qs.addFields("isoCode");
			qs.sortBy("isoCode");
			qs.setSize(2500); // Something big
			QuerySpecTranslator translator = new QuerySpecTranslator(qs, GEO_AREA);
			SearchRequestBuilder request;
			try {
				request = translator.translate();
			}
			catch (InvalidQueryException e) {
				// We made this one outselves, so eh ...
				throw new DaoException(e);
			}
			SearchResponse response = request.execute().actionGet();
			SearchHit[] hits = response.getHits().getHits();
			isoCodes = new LinkedHashMap<>(hits.length + 8, 1F);
			for (SearchHit hit : hits) {
				Object value = hit.getSource().get("isoCode");
				if (value == null) {
					continue;
				}
				isoCodes.put(value.toString(), hit.getId());
			}
		}
		return isoCodes;
	}

	@Override
	GeoArea[] createDocumentObjectArray(int length)
	{
		return new GeoArea[length];
	}
}
