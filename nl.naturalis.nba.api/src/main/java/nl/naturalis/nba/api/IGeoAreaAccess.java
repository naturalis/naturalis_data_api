package nl.naturalis.nba.api;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.GeoPoint;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;

/**
 * <p>
 * Specifies methods for accessing geographical areas and their coordinates.
 * This interface is mainly intended for lookups of GeoArea document IDs. When
 * using operator {@link ComparisonOperator#IN IN} to retrieve specimens found
 * within a certain area, you can either provide the coordinates yourself or you
 * can provide the ID of the GeoArea document specifying the coordinates. The
 * {@link Condition query condition} would then look like this:
 * </p>
 * <p>
 * <code>
 * // Find specimens found in the Netherlands:<br>
 * Condition condition = new Condition("gatheringEvent.siteCoordinates.geoShape", IN, "1004050@GEO");
 * </code>
 * </p>
 * <p>
 * This lookup will only work for fields of type {@link GeoJsonObject}. It will
 * not work for fields of type {@link GeoPoint} (e.g.
 * {@code gatheringEvent.siteCoordinates.geoPoint}). You should not rely on the
 * document ID being stable. You should look it up using, for example,
 * {@link #getIdForLocality(String) getIdForLocality} or
 * {@link #getIdForIsoCode(String) getIdForIsoCode}.
 * </p>
 * 
 * @see IDocumentObject
 * 
 * @author Ayco Holleman
 *
 */
public interface IGeoAreaAccess extends INbaAccess<GeoArea> {

	/**
	 * <p>
	 * Returns the coordinates of the area with the specified
	 * {@link GeoArea#getLocality() locality}.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/geo/getGeoJsonForLocality/{locality}
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	GeoJsonObject getGeoJsonForLocality(String locality);


}
