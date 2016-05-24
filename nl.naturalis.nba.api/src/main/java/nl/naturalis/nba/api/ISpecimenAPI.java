package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Specifies methods for accessing specimen-related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ISpecimenAPI {

	/**
	 * Returns the {@link Specimen} with the specified system id, or
	 * {@code null} if there is no such specimen.
	 * 
	 * @param id
	 *            The NBA system ID of the specimen
	 * @return
	 */
	Specimen find(String id);

	/**
	 * Returns the {@link Specimen}s with the specified system ids, or a
	 * zero-length array no specimens were found.
	 * 
	 * @param id
	 *            The NBA system IDs of the requested specimens
	 * @return
	 */
	Specimen[] find(String[] ids);

	/**
	 * Retrieves a {@link Specimen} by its UnitID. Since the UnitID is not
	 * strictly specified to be unique across all of the NBA's data sources, a
	 * theoretical chance exists that multiple specimens are retrieved for a
	 * given UnitID. Therefore this method returns an array of specimens. If no
	 * specimen with the specified UnitID exists, a zero-length array is
	 * returned.
	 * 
	 * @param unitID
	 *            The UnitID of the specimen occurence
	 * @return
	 */
	Specimen[] findByUnitID(String unitID);

	/**
	 * Returns whether or not the specified string is a valid UnitID
	 * (i&#46;e&#46; is the UnitID of at least one specimen record).
	 * 
	 * @param unitID
	 * @return
	 */
	boolean exists(String unitID);

	/**
	 * Returns specimens according to the provided query specification.
	 * 
	 * @param querySpec
	 * @return
	 */
	Specimen[] query(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * Returns all &#34;special collections&#34; defined within the specimen
	 * dataset. These can be collections from a particular collector or
	 * collections revolving around a theme (e.g. Extinct Birds).
	 * 
	 * @return
	 */
	String[] getNamedCollections();

	/**
	 * Returns the ids of all specimens belonging to a named collection.
	 * 
	 * @param collectionName
	 * @return
	 */
	String[] getIdsInCollection(String collectionName);

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
