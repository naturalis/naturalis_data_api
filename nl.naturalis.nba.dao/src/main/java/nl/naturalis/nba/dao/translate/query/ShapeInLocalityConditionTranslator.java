package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.searchTermHasWrongType;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.exception.DaoException;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of
 * type {@link GeoJsonObject} and with a {@link QueryCondition#getValue() search term
 * of type {@link String}, supposedly specifying a geographical name like
 * "Amsterdam" or "France".
 * 
 * @author Ayco Holleman
 *
 */
class ShapeInLocalityConditionTranslator extends ConditionTranslator {

	private static final Logger logger = getLogger(ShapeInLocalityConditionTranslator.class);

	ShapeInLocalityConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		Object value = condition.getValue();
		if (value instanceof String) {
			String id = getIdForLocality(value.toString());
			GeoShapeQueryBuilder query = geoShapeQuery(field, id, GEO_AREA.getName());
			query.indexedShapeIndex(GEO_AREA.getIndexInfo().getName());
			String nestedPath = getNestedPath(condition, mappingInfo);
			if (nestedPath == null) {
				return query;
			}
			return nestedQuery(nestedPath, query, ScoreMode.None);
		}
		Collection<?> localities;
		if (value.getClass().isArray()) {
			localities = Arrays.asList((Object[]) value);
		}
		else if (value instanceof Collection) {
			localities = (Collection<?>) value;
		}
		else {
			throw searchTermHasWrongType(condition);
		}
		BoolQueryBuilder boolQuery = boolQuery();
		for (Object loc : localities) {
			String id = getIdForLocality(loc.toString());
			GeoShapeQueryBuilder query = geoShapeQuery(field, id, GEO_AREA.getName());
			query.indexedShapeIndex(GEO_AREA.getIndexInfo().getName());
			boolQuery.must(query);
		}
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null || forSortField) {
			return boolQuery;
		}
		return nestedQuery(nestedPath, boolQuery, ScoreMode.None);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
	}

	private static String getIdForLocality(String locality) throws InvalidConditionException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up document ID for locality \"{}\"", locality);
		}
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition("locality", EQUALS, locality));
		QuerySpecTranslator translator = new QuerySpecTranslator(qs, GEO_AREA);
		SearchRequestBuilder request;
		try {
			request = translator.translate();
			request.setFetchSource(false);
		}
		catch (InvalidQueryException e) {
			// We made this one ourselves, so eh ...
			throw new DaoException(e);
		}
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			String fmt = "No such locality: \"%s\"";
			String msg = String.format(fmt, locality);
			throw new InvalidConditionException(msg);
		}
		return hits[0].getId();
	}

}