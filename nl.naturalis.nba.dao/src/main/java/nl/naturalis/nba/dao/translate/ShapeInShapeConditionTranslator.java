package nl.naturalis.nba.dao.translate;

import org.locationtech.spatial4j.shape.Shape;
//import static org.elasticsearch.common.geo.builders.ShapeBuilders.newMultiPolygon;
//import static org.elasticsearch.common.geo.builders.ShapeBuilders.newPolygon;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.geo.builders.LineStringBuilder;
import org.elasticsearch.common.geo.builders.MultiPolygonBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import com.vividsolutions.jts.geom.Coordinate;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.exception.DaoException;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of
 * type {@link GeoJsonObject} and with a {@link QueryCondition#getValue() search
 * term that is also a {@link GeoJsonObject} (or a JSON string that deserializes
 * into a {@link GeoJsonObject}).
 * 
 * @author Ayco Holleman
 * 
 * 
 * TODO: checkout https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-geo-queries.html
 *
 */
class ShapeInShapeConditionTranslator extends ConditionTranslator {
  
  private static final Logger logger = getLogger(ShapeInShapeConditionTranslator.class);

	ShapeInShapeConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
	  logger.warn(">>> Translator is not ready yet!!!");
		String field = condition.getField().toString();
		logger.info(">>> field: {}", field);
		try {
			return geoShapeQuery(field, getShape());
		}
		catch (IOException e) {
			throw new DaoException(e);
		}
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}

//	private ShapeBuilder getShape() throws InvalidConditionException
//	{
//		Object value = condition.getValue();
//		Class<?> cls = value.getClass();
//		if (cls == Polygon.class) {
//			Polygon geoJsonPolygon = (Polygon) value;
//			return createESPolygon(geoJsonPolygon);
//		}
//		else if (cls == MultiPolygon.class) {
//			MultiPolygon geoJsonMulti = (MultiPolygon) value;
//			MultiPolygonBuilder elasticMulti = new MultiPolygon();
//			for (List<List<LngLatAlt>> geoJsonPolygon : geoJsonMulti.getCoordinates()) {
//				elasticMulti.polygon(createESPolygon(geoJsonPolygon));
//			}
//			return elasticMulti;
//		}
//		throw new InvalidConditionException("Unsupported geo shape: " + cls.getSimpleName());
//	}

//  private ShapeBuilder getShape() throws InvalidConditionException
//  {
//    Object value = condition.getValue();
//    Class<?> cls = value.getClass();
//    if (cls == Polygon.class) {
//      Polygon geoJsonPolygon = (Polygon) value;
//      return createESPolygon(geoJsonPolygon);
//    }
//    else if (cls == MultiPolygon.class) {
//      MultiPolygon geoJsonMulti = (MultiPolygon) value;
//      MultiPolygonBuilder elasticMulti = new MultiPolygon();
//      for (List<List<LngLatAlt>> geoJsonPolygon : geoJsonMulti.getCoordinates()) {
//        elasticMulti.polygon(createESPolygon(geoJsonPolygon));
//      }
//      return elasticMulti;
//    }
//    throw new InvalidConditionException("Unsupported geo shape: " + cls.getSimpleName());
//  }

  private ShapeBuilder getShape() throws InvalidConditionException
  {
    Object value = condition.getValue();
    Class<?> cls = value.getClass();
    
    logger.info(">>> value:\n{}", JsonUtil.toPrettyJson(value));
    
//    if (cls == Polygon.class) {
//      Polygon geoJsonPolygon = (Polygon) value;
//      return createESPolygon(geoJsonPolygon);
//    }
//    else if (cls == MultiPolygon.class) {
//      MultiPolygon geoJsonMulti = (MultiPolygon) value;
//      MultiPolygonBuilder elasticMulti = new MultiPolygon();
//      for (List<List<LngLatAlt>> geoJsonPolygon : geoJsonMulti.getCoordinates()) {
//        elasticMulti.polygon(createESPolygon(geoJsonPolygon));
//      }
//      return elasticMulti;
//    }
//    throw new InvalidConditionException("Unsupported geo shape: " + cls.getSimpleName());
    return null;
  }
	

  
  
	private static PolygonBuilder createESPolygon(Polygon geoJsonPolygon)
	{
		return createESPolygon(geoJsonPolygon.getCoordinates());
	}

	private static PolygonBuilder createESPolygon(List<List<LngLatAlt>> coords)
	{
//		Iterator<List<LngLatAlt>> rings = coords.iterator();
//		List<LngLatAlt> exterior = rings.next();
//		PolygonBuilder esPolygon = newPolygon(convertRing(exterior));
//		while (rings.hasNext()) {
//			List<LngLatAlt> hole = rings.next();
//			List<Coordinate> coordinates = convertRing(hole);
//			esPolygon.hole(new LineStringBuilder(coordinates));
//		}
//		return esPolygon;
	  return null;
	}

	private static List<Coordinate> convertRing(List<LngLatAlt> polyonCoords)
	{
		List<Coordinate> coordinates = new ArrayList<>();
		for (LngLatAlt lngLatAlt : polyonCoords) {
			double lon = lngLatAlt.getLongitude();
			double lat = lngLatAlt.getLatitude();
			coordinates.add(new Coordinate(lon, lat));
		}
		return coordinates;
	}
}
