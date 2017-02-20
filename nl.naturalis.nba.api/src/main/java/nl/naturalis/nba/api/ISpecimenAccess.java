package nl.naturalis.nba.api;

import java.util.zip.ZipOutputStream;

import nl.naturalis.nba.api.model.Specimen;

/**
 * Specifies methods for accessing specimen-related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ISpecimenAccess extends INbaAccess<Specimen> {

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
	 * Writes a DarwinCore Archive with specimens satisfying the specified query
	 * specification to the specified output stream.
	 * 
	 * @param querySpec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException;

	/**
	 * Writes a DarwinCore Archive with specimens from a predefined data set to
	 * the specified output stream. To get the names of all currently defined
	 * data sets, call {@link #dwcaGetDataSetNames() dwcaGetDataSetNames}.
	 * 
	 * @param name
	 *            The name of the predefined data set
	 * @param out
	 *            The output stream to write to
	 * @throws InvalidQueryException
	 */
	void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException;

	/**
	 * Returns the names of all predefined data sets with specimen/occurrence
	 * data.
	 * 
	 * @return
	 */
	String[] dwcaGetDataSetNames();

	/**
	 * Returns all &#34;special collections&#34; defined within the specimen
	 * dataset. These can be collections from a particular collector or
	 * collections revolving around a theme (e.g. &#34;Extinct Birds&#34;).
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
