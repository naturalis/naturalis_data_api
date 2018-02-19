package nl.naturalis.nba.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Specifies a set of common data access methods that can be called against any
 * type of document within the NBA document store.
 * 
 * @author Ayco Holleman
 *
 * @param <DOCUMENT_OBJECT>
 *            The class representing the Elasticsearch document that you are
 *            given access to.
 */
public interface INbaAccess<DOCUMENT_OBJECT extends IDocumentObject> {

	/**
	 * <p>
	 * Returns the document with the specified document ID, or {@code null} if
	 * there is no document with the specified document ID. Note that the
	 * document ID is not part of the document itself. It corresponds with the
	 * Elasticsearch {@code _id} field, which is retrieved separately from the
	 * document source. You can get the value of this field through
	 * {@link IDocumentObject#getId() IDocumentObject.getId}.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/find/{id}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/find/ZMA.MAM.123456@CRS
	 * </code>
	 * </p>
	 * 
	 * @see IDocumentObject
	 * 
	 * @param id
	 *            The NBA system ID of the data model object
	 * @return
	 */
	DOCUMENT_OBJECT find(String id);

	/**
	 * <p>
	 * Returns the data model objects with the specified system IDs, or a
	 * zero-length array no specimens were found. Note that you cannot look up
	 * more than 1024 IDs at a time.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/findByIds/{id-0},{id-1},{id-2},{id-n}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/findByIds/ZMA.MAM.123,ZMA.MAM.456,ZMA.MAM.789
	 * </code>
	 * </p>
	 * 
	 * @param id
	 *            The NBA system IDs of the requested data model objects
	 * @return
	 */
	DOCUMENT_OBJECT[] findByIds(String[] ids);

	/**
	 * <p>
	 * Returns the documents conforming to the provided query specification.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET and a POST request
	 * with the following endpoint:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/query
	 * </code>
	 * </p>
	 * <p>
	 * See {@link QuerySpec} for an explanation of how to encode the
	 * {@code QuerySpec} object in the request.
	 * </p>
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<DOCUMENT_OBJECT> query(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * <p>
	 * Returns the number of documents conforming to the provided query
	 * specification. You may specify {@code null} for the {@code querySpec}
	 * argument if you simply want a total document count. Otherwise you should
	 * only set the query conditions and (possibly) the {@link LogicalOperator
	 * logical operator} on the {@code QuerySpec}. Setting anything else on the
	 * {@code QuerySpec} has no effect.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET or POST request with
	 * the following endpoint:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/count
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/taxon/count<br>
	 * http://api.biodiversitydata.nl/v2/specimen/count/?sourceSystem.code=BRAHMS
	 * </code>
	 * </p>
	 * <p>
	 * See {@link QuerySpec} for an explanation of how to encode the
	 * {@code QuerySpec} object in the request.
	 * </p>
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	long count(QuerySpec querySpec) throws InvalidQueryException;
	
	/**
	 * <p>
	 * Returns ...
	 * </p>
	 * 
	 * @param field
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	long countDistinctValues(String field, QuerySpec querySpec) throws InvalidQueryException;

	/**
   * <p>
   * Returns ...
   * </p>
   * 
   * @param forField
   * @param forGroup
   * @param querySpec
   * @return
   * @throws InvalidQueryException
 
	 */
	List<Map<String, Object>> countDistinctValuesPerGroup(String forField, String forGroup, QuerySpec querySpec)
	    throws InvalidQueryException;

	/**
	 * <p>
	 * Returns the unique values of the specified field. The result is returned
	 * as a {@link Map} with the each key specifying one of the unique values
	 * and the value specifying a document count (the number of documents for
	 * which the specified field has that value). Note that if the specified
	 * field is an {@link Collection} or array, the sum of the document counts
	 * may add up to more than the total number of documents in the index. You
	 * may specify {@code null} for the {@code querySpec} argument if you simply
	 * want a total document count. Otherwise you should only set the query
	 * conditions and (possibly) the {@link LogicalOperator logical operator} on
	 * the {@code QuerySpec}. Setting anything else on the {@code QuerySpec} has
	 * no effect.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following endpoint:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/getDistinctValues/{forField}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/getDistinctValues/recordBasis<br>
	 * </code>
	 * </p>
	 * <p>
	 * See {@link QuerySpec} for an explanation of how to encode the
	 * {@code QuerySpec} object in the request.
	 * </p>
	 * 
	 * @param forField
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	Map<String, Long> getDistinctValues(String forField, QuerySpec querySpec)
			throws InvalidQueryException;

	
	/**
	 * <p>
	 * ...
	 * </p>
	 * 
	 * @param forField
	 * @param forGroup
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	List<Map<String, Object>> getDistinctValuesPerGroup(String forField, String forGroup, QuerySpec querySpec) throws InvalidQueryException;
	
//	/**
//	 * <p>
//	 * Returns the unique values of the specified field, given the value of
//	 * another field. For example: return all localities per area type
//	 * (countries: U.S.A., Germany, France, ...; states: California, Texas, ...;
//	 * cities: New York, Chigago, ...) Here, the "areaType" field is the
//	 * groupField while the "locality" field is the valuesField. The result is
//	 * returned as a {@link Map} where each key is a group and each value is the
//	 * set of unique values for that group. Null values are excluded, both for
//	 * the {@code groupField} and for the {@code valuesField}.
//	 * </p>
//	 * <h5>REST API</h5>
//	 * <p>
//	 * The NBA REST API exposes this method through a GET request with the
//	 * following end point:
//	 * </p>
//	 * <p>
//	 * <code>
//	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/getDistinctValuesPerGroup/{groupField}/{valuesField}
//	 * </code>
//	 * </p>
//	 * <p>
//	 * For example:
//	 * </p>
//	 * <p>
//	 * <code>
//	 * http://api.biodiversitydata.nl/v2/geo/getDistinctValuesPerGroup/areaType/locality<br>
//	 * http://api.biodiversitydata.nl/v2/specimen/getDistinctValuesPerGroup/recordBasis/phaseOrStage
//	 * </code>
//	 * </p>
//	 * <p>
//	 * For consistency's sake, even though you can only pass
//	 * {@link QueryCondition conditions} to this method, when accessing this
//	 * method through the REST API you still can and should do so via the
//	 * {@code _querySpec} query parameter, as described {@link QuerySpec here}.
//	 * Only the conditions of the URL-encoded {@code QuerySpec} object are taken
//	 * into account; all other properties are ignored).
//	 * </p>
//	 * 
//	 * @param groupField
//	 * @param valuesField
//	 * @param conditions
//	 * @return
//	 * @throws InvalidQueryException
//	 */
//	Map<Object, Set<Object>> getDistinctValuesPerGroup(String groupField, String valuesField,
//			QueryCondition... conditions) throws InvalidQueryException;
	
//	/**
//	 * 
//	 * @param groupByField
//	 * @param querySpec
//	 * @return
//	 * @throws InvalidQueryException
//	 */
//	List<KeyValuePair<Object, Integer>> getGroups(String groupByField, QuerySpec querySpec)
//			throws InvalidQueryException;

}
