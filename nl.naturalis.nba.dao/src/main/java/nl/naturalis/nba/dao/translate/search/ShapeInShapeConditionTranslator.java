package nl.naturalis.nba.dao.translate.search;

import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.geo.builders.MultiPolygonBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import com.vividsolutions.jts.geom.Coordinate;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.exception.DaoException;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of
 * type {@link GeoJsonObject} and with a {@link SearchCondition#getValue()
 * search term that is also a {@link GeoJsonObject} (or a JSON string that
 * deserializes into a {@link GeoJsonObject}).
 * 
 * @author Ayco Holleman
 *
 */
class ShapeInShapeConditionTranslator extends ConditionTranslator {

	ShapeInShapeConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getFields().iterator().next();
		try {
			return geoShapeQuery(path.toString(), getShape());
		}
		catch (IOException e) {
			throw new DaoException(e);
		}
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}

	private ShapeBuilder getShape() throws InvalidConditionException
	{
		Object value = condition.getValue();
		Class<?> cls = value.getClass();
		if (cls == Polygon.class) {
			Polygon polygon = (Polygon) value;
			PolygonBuilder shape = ShapeBuilders.newPolygon(convert(polygon.getCoordinates()));
			return shape;
		}
		else if (cls == MultiPolygon.class) {
			MultiPolygon polygon = (MultiPolygon) value;
			MultiPolygonBuilder shape = ShapeBuilders.newMultiPolygon();
			List<List<List<LngLatAlt>>> multiPolygonCoords = polygon.getCoordinates();
			for (List<List<LngLatAlt>> polygonCoords : multiPolygonCoords) {
				shape.polygon(ShapeBuilders.newPolygon(convert(polygonCoords)));
			}
			return shape;
		}
		throw new InvalidConditionException("Unsupported geo shape: " + cls.getSimpleName());
	}

	private static List<Coordinate> convert(List<List<LngLatAlt>> polyonCoords)
	{
		List<Coordinate> coordinates = new ArrayList<>();
		for (List<LngLatAlt> lngLatAlts : polyonCoords) {
			for (LngLatAlt lngLatAlt : lngLatAlts) {
				double lon = lngLatAlt.getLongitude();
				double lat = lngLatAlt.getLatitude();
				coordinates.add(new Coordinate(lon, lat));
			}
		}
		return coordinates;
	}
}
