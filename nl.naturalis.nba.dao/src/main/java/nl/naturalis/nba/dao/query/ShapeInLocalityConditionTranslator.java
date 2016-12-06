package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.query.TranslatorUtil.*;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.Arrays;
import java.util.Collection;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.GeoAreaDao;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of
 * type {@link GeoJsonObject} and with a {@link Condition#getValue() search term
 * of type {@link String}, supposedly specifying a geographical name like
 * "Amsterdam" or "France".
 * 
 * @author Ayco Holleman
 *
 */
class ShapeInLocalityConditionTranslator extends ConditionTranslator {

	ShapeInLocalityConditionTranslator(Condition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		Object value = condition.getValue();
		GeoAreaDao dao = new GeoAreaDao();
		if (value instanceof String) {
			String id = dao.getIdForLocality(value.toString());
			GeoShapeQueryBuilder query = geoShapeQuery(field, id, GEO_AREA.getName());
			query.indexedShapeIndex(GEO_AREA.getIndexInfo().getName());
			String nestedPath = getNestedPath(condition, mappingInfo);
			if (nestedPath == null) {
				return query;
			}
			return nestedQuery(nestedPath, query);
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
			String id = dao.getIdForLocality(loc.toString());
			GeoShapeQueryBuilder query = geoShapeQuery(field, id, GEO_AREA.getName());
			query.indexedShapeIndex(GEO_AREA.getIndexInfo().getName());
			boolQuery.must(query);
		}
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null) {
			return boolQuery;
		}
		return nestedQuery(nestedPath, boolQuery);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
	}
	

}
