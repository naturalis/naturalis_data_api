package nl.naturalis.nba.api;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.LogicalOperator;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Specifies a common set of data access methods that can be called against any
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
	 * Returns the data model object with the specified system ID, or
	 * {@code null} if there is no data model object with the specified system
	 * ID. The system ID is not part of the Elasticsearch document from which
	 * the data model object was created. It corresponds with the {@code _id}
	 * field, which is retrieved separately from the document source. You can
	 * get the value of this field through {@link IDocumentObject#getId()
	 * IDocumentObject.getId}.
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
	 * zero-length array no specimens were found.
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
	DOCUMENT_OBJECT[] find(String[] ids);

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
	 * {@code QuerySpec} object in the URL (for GET requests) or in the request
	 * body (for POST requests). When using a POST request you actually have two
	 * options:
	 * <ol>
	 * <li>Set the Content-Type header of the request to
	 * application/x-www-form-urlencoded (or leave it empty) and encode the
	 * {@code QuerySpec} object in the request body using form parameters as
	 * described in {@link here}.
	 * <li>Set the Content-Type header of the request to application/json and
	 * set the request body to the JSON represention of the {@code QuerySpec}
	 * object (<i>without</i> using the {@code _querySpec} form parameter). In
	 * other words, the request body consists of nothing but the JSON
	 * representing the {@code QuerySpec} object.
	 * </ol>
	 * </p>
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<DOCUMENT_OBJECT> query(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * <p>
	 * Returns the raw JSON source, converted to a
	 * <code>Map&lt;String,Object&gt;</code> instance, of the documents
	 * conforming to the provided query specification. This is especially useful
	 * if you are only interested in a few fields within the document. Use
	 * {@link QuerySpec#setFields(java.util.List) QuerySpec.setFields} or
	 * {@link QuerySpec#addFields(String...) QuerySpec.addFields} to select the
	 * fields you are interested in. Note that clients can still convert the raw
	 * JSON source to "strongly typed" data model objects (e.g. {@link Taxon} or
	 * {@link Specimen} instances) using <code>JsonUtil.convert</code> in the
	 * nl.naturalis.common.json package. This package is distributed with the
	 * Java client. Also note that the system ID of a document (the _id field of
	 * a search hit) is not part of the document itself. However you can still
	 * select it by calling <code>querySpec.addField("id")</code>. The system ID
	 * will then be added with key "id" to the map containing the selected
	 * fields.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET and a POST request
	 * with the following endpoint:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/queryData
	 * </code>
	 * </p>
	 * <p>
	 * See {@link QuerySpec} for an explanation of how to encode the
	 * {@code QuerySpec} object in the URL (for GET requests) or in the request
	 * body (for POST requests). When using a POST request you actually have two
	 * options:
	 * <ol>
	 * <li>Set the Content-Type header of the request to
	 * application/x-www-form-urlencoded (or leave it empty) and encode the
	 * {@code QuerySpec} object in the request body using form parameters as
	 * described in {@link here}.
	 * <li>Set the Content-Type header of the request to application/json and
	 * set the request body to the JSON represention of the {@code QuerySpec}
	 * object (<i>without</i> using the {@code _querySpec} form parameter). In
	 * other words, the request body consists of nothing but the JSON
	 * representing the {@code QuerySpec} object.
	 * </ol>
	 * </p>
	 * 
	 * @param spec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<Map<String, Object>> queryData(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * Writes CSV records extracted from documents satisfying the specified
	 * query specification to the specified output stream.
	 * 
	 * @param querySpec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void csvQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException;

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
	 * The NBA REST API exposes this method through a GET request with the
	 * following endpoint:
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
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	long count(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * <p>
	 * Returns the unique values of the specified field. The result is returned
	 * as a {@link Map} with the each key specifying a unique value and the
	 * value specifying a document counte. Note that if the specified field is a
	 * multi-valued field (i.e. it translates into a {@link Collection} or
	 * array), the sum of the document counts may add up to more than the total
	 * number of documents for the applicable document type. You may specify
	 * {@code null} for the {@code querySpec} argument if you simply want a
	 * total document count. Otherwise you should only set the query conditions
	 * and (possibly) the {@link LogicalOperator logical operator} on the
	 * {@code QuerySpec}. Setting anything else on the {@code QuerySpec} has no
	 * effect.
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
	 * Returns the unique values of the specified field, grouping them using
	 * another field . The field on which to group is specified using the
	 * {@code groupField} argument. The field to collect the values from is
	 * specified using the {@code valuesField}. The result is returned as a
	 * {@link Map} where each key is a group and each value is the set of unique
	 * values for that group. Null values are excluded, both for the
	 * {@code groupField} and for the {@code valuesField}.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/getDistinctValuesPerGroup/{groupField}/{valuesField}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/geo/getDistinctValuesPerGroup/areaType/locality<br>
	 * http://api.biodiversitydata.nl/v2/specimen/getDistinctValuesPerGroup/recordBasis/phaseOrStage
	 * </code>
	 * </p>
	 * <p>
	 * For consistency's sake, even though you can only pass {@link Condition
	 * conditions} to this method, when accessing this method through the REST
	 * API you still can and should do so via the {@code _querySpec} query
	 * parameter, as described {@link QuerySpec here}. Only the conditions of
	 * the URL-encoded {@code QuerySpec} object are taken into account; all
	 * other properties are ignored).
	 * </p>
	 * 
	 * @param groupField
	 * @param valuesField
	 * @param conditions
	 * @return
	 * @throws InvalidQueryException
	 */
	Map<Object, Set<Object>> getDistinctValuesPerGroup(String groupField, String valuesField,
			Condition... conditions) throws InvalidQueryException;

	List<KeyValuePair<Object, Integer>> getGroups(String groupByField, QuerySpec querySpec)
			throws InvalidQueryException;

}
