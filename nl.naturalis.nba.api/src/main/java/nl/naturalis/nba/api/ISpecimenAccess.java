package nl.naturalis.nba.api;

import java.io.OutputStream;

import nl.naturalis.nba.api.model.ScientificNameGroup2;
import nl.naturalis.nba.api.model.Specimen;

/**
 * Specifies methods for accessing specimen-related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ISpecimenAccess extends INbaAccess<Specimen> {

	/**
	 * <p>
	 * Retrieves a {@link Specimen} by its UnitID. Since the UnitID is not
	 * strictly specified to be unique across all of the NBA's data sources, a
	 * theoretical chance exists that multiple specimens are retrieved for a
	 * given UnitID. Therefore this method returns an array of specimens. If no
	 * specimen with the specified UnitID exists, a zero-length array is
	 * returned.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/findByUnitID/{unitID}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/findByUnitID/ZMA.MAM.123456
	 * </code>
	 * </p>
	 * 
	 * @param unitID
	 *            The UnitID of the specimen occurence
	 * @return
	 */
	Specimen[] findByUnitID(String unitID);

	/**
	 * <p>
	 * Returns whether or not the specified string is a valid UnitID
	 * (i&#46;e&#46; is the UnitID of at least one specimen record).
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/exists/{unitID}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/exists/ZMA.MAM.123456
	 * </code>
	 * </p>
	 * 
	 * @param unitID
	 * @return
	 */
	boolean exists(String unitID);

	/**
	 * <p>
	 * Writes a DarwinCore Archive with taxa satisfying the specified query
	 * specification to the specified output stream.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET and POST request with
	 * the following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/dwca/query
	 * </code>
	 * </p>
	 * <p>
	 * See {@link QuerySpec} for an explanation of how to encode the
	 * {@code QuerySpec} object in the request.
	 * </p>
	 * 
	 * @param querySpec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException;

	/**
	 * <p>
	 * Writes a DarwinCore Archive with taxa from a predefined data set to the
	 * specified output stream. To get the names of all currently defined data
	 * sets, call {@link #dwcaGetDataSetNames() dwcaGetDataSetNames}.
	 * </p>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/dwca/getDataSet/{name}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/dwca/getDataSet/hymenoptera
	 * </code>
	 * </p>
	 * 
	 * @param name
	 *            The name of the predefined data set
	 * @param out
	 *            The output stream to write to
	 * @throws InvalidQueryException
	 */
	void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException;

	/**
	 * <p>
	 * Returns the names of all predefined data sets with specimen data.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/dwca/getDataSetNames
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	String[] dwcaGetDataSetNames();

	/**
	 * <p>
	 * Returns all &#34;special collections&#34; defined within the specimen
	 * dataset. These can be collections from a particular collector or
	 * collections revolving around a theme (e.g. &#34;Extinct Birds&#34;).
	 * </p>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/getNamedCollections
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	String[] getNamedCollections();

	/**
	 * <p>
	 * Returns the document IDs of all specimens belonging to a named
	 * collection.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET and POST request with
	 * the following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/getIdsInCollection/{name}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/getIdsInCollection/siebold
	 * </code>
	 * </p>
	 * 
	 * @param collectionName
	 * @return
	 */
	String[] getIdsInCollection(String collectionName);

	QueryResult<ScientificNameGroup2> groupByScientificName(ScientificNameGroupQuerySpec querySpec)
			throws InvalidQueryException;

	/**
	 * Saves the specified specimen to the NBA data store. N.B. although this
	 * method is part of the API, NBA clients will get an HTTP 403 (FORBIDDEN)
	 * error when calling it unless they reside on the same server as the NBA
	 * itself. If you specify an ID for the specimen (using
	 * {@link Specimen#setId(String) Specimen.setId()}), that ID will used as
	 * the Elasticsearch document ID. Otherwise an Elasticsearch-generated ID
	 * will be used <i>and</i> set (using {@link Specimen#setId(String)
	 * Specimen.setId()}) on the provided specimen instance.
	 * 
	 * @param specimen
	 *            The specimen to save
	 * @param immediate
	 *            Whether or not the specimen should become available for
	 *            retrieval immediately.
	 * @return The ID of the newly created specimen.
	 */
	String save(Specimen specimen, boolean immediate);

	/**
	 * Deletes the specimen with the specified system ID (as can be retrieved
	 * using {@link Specimen#getId()}). N.B. although this method is part of the
	 * API, NBA clients will get an HTTP 403 (FORBIDDEN) error when calling it
	 * unless they reside on the same server as the NBA itself.
	 * 
	 * @param id
	 *            The ID of the specimen
	 * @param immediate
	 *            Whether or not to refresh the index immediately after the
	 *            deletion.
	 * @return Whether or not there was a specimen with the specified ID
	 */
	boolean delete(String id, boolean immediate);

}
