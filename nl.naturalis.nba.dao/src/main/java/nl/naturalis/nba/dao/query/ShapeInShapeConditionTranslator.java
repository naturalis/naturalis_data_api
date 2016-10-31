package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.common.geo.builders.ShapeBuilder.newMultiPolygon;
import static org.elasticsearch.common.geo.builders.ShapeBuilder.newPolygon;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.geo.builders.MultiPolygonBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import com.vividsolutions.jts.geom.Coordinate;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class ShapeInShapeConditionTranslator extends ConditionTranslator {

	ShapeInShapeConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		GeoShapeQueryBuilder query = geoShapeQuery(condition.getField(), getShape());
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
	}

	private ShapeBuilder getShape() throws InvalidConditionException
	{
		Object value = condition.getValue();
		Class<?> cls = value.getClass();
		if (cls == Polygon.class) {
			Polygon polygon = (Polygon) value;
			PolygonBuilder shape = newPolygon();
			shape.points(convert(polygon.getCoordinates()));
			return shape;
		}
		else if (cls == MultiPolygon.class) {
			MultiPolygon polygon = (MultiPolygon) value;
			MultiPolygonBuilder shape = newMultiPolygon();
			List<List<List<LngLatAlt>>> multiPolygonCoords = polygon.getCoordinates();
			for (List<List<LngLatAlt>> polygonCoords : multiPolygonCoords) {
				shape.polygon(newPolygon().points(convert(polygonCoords)));
			}
			return shape;
		}
		throw new InvalidConditionException("Unsupported geo shape: " + cls.getSimpleName());
	}

	private static Coordinate[] convert(List<List<LngLatAlt>> polyonCoords)
	{
		List<Coordinate> coordinates = new ArrayList<>();
		for (List<LngLatAlt> lngLatAlts : polyonCoords) {
			for (LngLatAlt lngLatAlt : lngLatAlts) {
				double lon = lngLatAlt.getLongitude();
				double lat = lngLatAlt.getLatitude();
				coordinates.add(new Coordinate(lon, lat));
			}
		}
		return coordinates.toArray(new Coordinate[coordinates.size()]);
	}
}
