package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;

import java.util.Collection;

import org.apache.logging.log4j.Logger;
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
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.utils.ClassUtil;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of
 * type {@link GeoJsonObject} and with a {@link QueryCondition#getValue()
 * search term of type {@link String}, supposedly specifying a geographical name
 * like "Amsterdam" or "France".
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
		String[] localities = getLocalities(condition.getValue());
		if (localities.length == 1) {
			return createQueryForLocality(localities[0]);
		}
		BoolQueryBuilder query = boolQuery();
		for (String locality : localities) {
			QueryBuilder qb = createQueryForLocality(locality);
			query.should(qb);
		}
		return query;
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
	}

	private String[] getLocalities(Object value) throws InvalidConditionException
	{
		if (value instanceof CharSequence) {
			return new String[] { value.toString() };
		}
		if (value.getClass().isArray()) {
			if (ClassUtil.isA(value.getClass().getComponentType(), String.class)) {
				return (String[]) value;
			}
			Object[] values = (Object[]) value;
			String[] localities = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				if (values[i] == null || !(values[i] instanceof CharSequence)) {
					throw invalidLocality(values[i]);
				}
				localities[i] = values[i].toString();
			}
			return localities;
		}
		if (value instanceof Collection) {
			Collection<?> values = (Collection<?>) value;
			String[] localities = new String[values.size()];
			int i = 0;
			for (Object obj : values) {
				if (obj == null || !(obj instanceof CharSequence)) {
					throw invalidLocality(obj);
				}
				localities[i++] = obj.toString();
			}
		}
		throw invalidLocality(value);
	}

	private InvalidConditionException invalidLocality(Object value) throws InvalidConditionException
	{
		String msg = "Invalid locality: " + value;
		throw new InvalidConditionException(condition, msg);
	}

	private QueryBuilder createQueryForLocality(String locality) throws InvalidConditionException
	{
		String field = condition.getField().toString();
		String id = getIdForLocality(locality);
		String index = GEO_AREA.getIndexInfo().getName();
		String type = GEO_AREA.getName();
		GeoShapeQueryBuilder query = geoShapeQuery(field, id, type);
		query.indexedShapeIndex(index);
		return query;
	}

	private static String getIdForLocality(String locality) throws InvalidConditionException
	{
		String idSuffix = '@' + SourceSystem.GEO.getCode();
		if (locality.endsWith(idSuffix)) {
			return locality;
		}
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
