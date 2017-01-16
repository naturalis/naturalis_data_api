package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.geoPolygonQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.List;

import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

@Deprecated
class PointInShapeConditionTranslator extends ConditionTranslator {

	PointInShapeConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
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
		if (nestedPath == null || forSortField) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		if (condition.getValue().getClass() != Polygon.class) {
			String msg = "Search term must be GeoJSON object of type \"polygon\"";
			throw new InvalidConditionException(msg);
		}
	}
}
