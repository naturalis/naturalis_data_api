package nl.naturalis.nba.dao.translate;

import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import com.vividsolutions.jts.geom.Coordinate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.geometry.Geometry;
import org.elasticsearch.geometry.LinearRing;
import org.elasticsearch.geometry.Polygon;
import org.elasticsearch.geometry.MultiPolygon;
import org.elasticsearch.geometry.utils.GeographyValidator;
import org.elasticsearch.index.query.QueryBuilder;

import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;

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

  private Geometry getShape() throws InvalidConditionException
  {
    Object value = condition.getValue();
    Class<?> cls = value.getClass();
    logger.info(">>> cls: {}", cls.getName());
    logger.info(">>> value:\n{}", JsonUtil.toPrettyJson(value));
    
    if (value instanceof org.geojson.Polygon) {
      logger.info(">>> GeoShape is Polygon");
      org.geojson.Polygon polygon = (org.geojson.Polygon) value;
      return createESPolygon(polygon.getCoordinates());
    }
    else if (value instanceof org.geojson.MultiPolygon) {
      logger.info(">>> GeoShape is MultiPolygon");
      org.geojson.MultiPolygon multiPolygon = (org.geojson.MultiPolygon) value;
      return createESMultiPolygon(multiPolygon);
    }
    else {
      String msg = String.format("Unsupported geo shape: %s", value.getClass().getName());
      logger.info(msg);
      throw new InvalidConditionException(msg);
    }
  }
	
/**
 * createESPolygon  
 * @param coordinates of the Polygon
 * @return Polygon as Geometry
 */
	private static Geometry createESPolygon(List<List<LngLatAlt>> coordinates)
	{
	  Polygon polygon;
    int numberOfRings = coordinates.size();
    if (numberOfRings == 0) return null;
    
    // Outer ring
    List<LngLatAlt> outerCoordinates = coordinates.get(0);
    LinearRing linearRing = createLinearRing(outerCoordinates);
    if (numberOfRings == 1) {
      polygon = new Polygon(linearRing);
      GeographyValidator validator = new GeographyValidator(false);
      validator.validate(polygon);          
      // logger.info(">>> Polygon p:\n{}", JsonUtil.toPrettyJson(polygon));
      return polygon;
    }
    
    // Inner ring(s)
    List<LinearRing> innerRings = new ArrayList<>(numberOfRings-1);
    for (int n = 1; n < numberOfRings; n++) {
      List<LngLatAlt> coordinatesHole = coordinates.get(n);
      LinearRing ring = createLinearRing(coordinatesHole);
      innerRings.add(ring);
    }
    polygon = new Polygon(linearRing, innerRings);
    GeographyValidator validator = new GeographyValidator(false);
    validator.validate(polygon);          
    return polygon;
	}
	
	
//	private static PolygonBuilder createESPolygon(List<List<LngLatAlt>> coords)
//	{
//		Iterator<List<LngLatAlt>> rings = coords.iterator();
//		List<LngLatAlt> exterior = rings.next();
//		PolygonBuilder esPolygon = newPolygon(convertRing(exterior));
//		while (rings.hasNext()) {
//			List<LngLatAlt> hole = rings.next();
//			List<Coordinate> coordinates = convertRing(hole);
//			esPolygon.hole(new LineStringBuilder(coordinates));
//		}
//		return esPolygon;
//	  return null;
//	}

	private static Geometry createESMultiPolygon(org.geojson.MultiPolygon multiPolygonAsGeoJSON) 
	{
	  int numberOfPolygons = multiPolygonAsGeoJSON.getCoordinates().size();
	  List<Polygon> polygons = new ArrayList<>(numberOfPolygons);
	  
	  List<List<List<LngLatAlt>>> listOfPolygonCoordinates = multiPolygonAsGeoJSON.getCoordinates();
	  for (List<List<LngLatAlt>> polygonCoordinates : listOfPolygonCoordinates) {
	    Geometry g = createESPolygon(polygonCoordinates);
	    polygons.add((Polygon) g);
	  }
	  
	  MultiPolygon multiPolygon = new MultiPolygon(polygons);
	  return multiPolygon;
	}
	
	private static LinearRing createLinearRing(List<LngLatAlt> coordinates) {
	  
	  int size = coordinates.size();
	  double[] latitudes = new double[size];
	  double[] longitudes = new double[size];
	  double[] altitudes = new double[size];
	  
	  int i = 0;
	  for (LngLatAlt coord : coordinates) {
	    latitudes[i] = coord.getLatitude();
	    longitudes[i] = coord.getLongitude();
	    altitudes[i] = ( (coord.getAltitude()) > 0 || coord.getAltitude() < 0 ? coord.getAltitude() : 0);
	    i++;
	  }
	  
	  return new LinearRing(longitudes, latitudes);
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
