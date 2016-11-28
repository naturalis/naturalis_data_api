package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of
 * type {@link GeoJsonObject} and with a {@link Condition#getValue() search term
 * of type {@link String}, supposedly specifying a geographical name like
 * "Amsterdam" or "France".
 * 
 * @author Ayco Holleman
 *
 */
class ShapeInGeoAreaConditionTranslator extends ConditionTranslator {

	ShapeInGeoAreaConditionTranslator(Condition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		String id = condition.getValue().toString();
		GeoShapeQueryBuilder query = geoShapeQuery(field, id, GEO_AREA.getName());
		//query.indexedShapeIndex(GEO_AREA.getIndexInfo().getName());
		query.indexedShapeIndex("nba");
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
	}

}
