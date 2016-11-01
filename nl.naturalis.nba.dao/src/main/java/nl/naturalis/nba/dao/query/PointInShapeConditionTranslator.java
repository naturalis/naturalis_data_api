package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.geoPolygonQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.List;

import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class PointInShapeConditionTranslator extends ConditionTranslator {

	PointInShapeConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		GeoPolygonQueryBuilder query = geoPolygonQuery(condition.getField());
		Polygon polygon = (Polygon) condition.getValue();
		for (List<LngLatAlt> lngLatAlts : polygon.getCoordinates()) {
			for (LngLatAlt coord : lngLatAlts) {
				query.addPoint(coord.getLatitude(), coord.getLongitude());
			}
		}
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	@Override
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
		if (condition.getValue().getClass() != Polygon.class) {
			String msg = "Search term must be GeoJSON object of type \"polygon\"";
			throw new InvalidConditionException(msg);
		}
	}
}
