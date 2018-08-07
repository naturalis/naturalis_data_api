package nl.naturalis.nba.api;

import java.io.OutputStream;
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
   * Retrieves a {@link Specimen} by its UnitID. Since the UnitID is not strictly specified to be
   * unique across all of the NBA's data sources, a theoretical chance exists that multiple
   * specimens are retrieved for a given UnitID. Therefore this method returns an array of
   * specimens. If no specimen with the specified UnitID exists, a zero-length array is returned.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * @param unitID The UnitID of the specimen occurence
   * @return
   */
  Specimen[] findByUnitID(String unitID);

  /**
   * <p>
   * Returns whether or not the specified string is a valid UnitID (i&#46;e&#46; is the UnitID of at
   * least one specimen record).
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * Writes a DarwinCore Archive with taxa satisfying the specified query specification to the
   * specified output stream.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET and POST request with the following end
   * point:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/specimen/dwca/query
   * </code>
   * </p>
   * <p>
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
   * </p>
   * 
   * @param querySpec
   * @param out
   * @throws InvalidQueryException
   */
  void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException;

  /**
   * <p>
   * Writes a DarwinCore Archive with specimens from a predefined data set to the specified output
   * stream. To get the names of all currently defined data sets, call {@link #dwcaGetDataSetNames()
   * dwcaGetDataSetNames}.
   * </p>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * @param name The name of the predefined data set
   * @param out The output stream to write to
   * @throws InvalidQueryException
   */
  void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException;

  /**
   * <p>
   * Returns the names of all predefined data sets with specimen data.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * Returns all &#34;special collections&#34; defined within the specimen dataset. These can be
   * collections from a particular collector or collections revolving around a theme (e.g.
   * &#34;Extinct Birds&#34;).
   * </p>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * Returns the document IDs of all specimens belonging to a named collection.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET and POST request with the following end
   * point:
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

  /**
   * <p>
   * Groups specimens by their scientific name. Although this method will optionally also retrieve
   * the taxa associated with a scientific name, any query conditions and sort fields specified
   * through the {@link QuerySpec} must reference {@link Specimen} fields only.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/specimen/groupByScientificName
   * </code>
   * </p>
   * 
   * @param querySpec
   * @return
   * @throws InvalidQueryException
   */
  GroupByScientificNameQueryResult groupByScientificName(GroupByScientificNameQuerySpec querySpec)
      throws InvalidQueryException;

}
