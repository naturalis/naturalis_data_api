package nl.naturalis.nba.api;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Specifies methods for accessing geographical areas and their coordinates.
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
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/geo/getGeoJsonForLocality/Netherlands
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	GeoJsonObject getGeoJsonForLocality(String locality);

}
