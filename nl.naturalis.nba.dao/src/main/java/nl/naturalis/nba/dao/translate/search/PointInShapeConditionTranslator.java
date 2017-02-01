package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoPolygonQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

@Deprecated
class PointInShapeConditionTranslator extends ConditionTranslator {

	PointInShapeConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Polygon polygon = (Polygon) condition.getValue();
		List<GeoPoint> points = new ArrayList<>(128);
		for (List<LngLatAlt> lngLatAlts : polygon.getCoordinates()) {
			for (LngLatAlt coord : lngLatAlts) {
				points.add(new GeoPoint(coord.getLatitude(), coord.getLongitude()));
			}
		}
		Path path = condition.getFields().iterator().next();
		QueryBuilder query = geoPolygonQuery(path.toString(),points);
		
		if(forSortField) {
			return query;
		}
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null) {
			query = nestedQuery(nestedPath, query, ScoreMode.None);
		}
		if (condition.isFilter().booleanValue()) {
			query = constantScoreQuery(query);
		}
		else if (condition.getBoost() != null) {
			query.boost(condition.getBoost());
		}
		return query;
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
