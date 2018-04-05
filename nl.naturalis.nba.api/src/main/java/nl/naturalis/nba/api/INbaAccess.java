package nl.naturalis.nba.api;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Specifies a set of common data access methods that can be called against any type of document
 * within the NBA document store.
 * 
 * @author Ayco Holleman
 *
 * @param <DOCUMENT_OBJECT> The class representing the Elasticsearch document that you are given
 *        access to.
 */
public interface INbaAccess<DOCUMENT_OBJECT extends IDocumentObject> {

  /**
   * <p>
   * Returns the document with the specified document ID, or {@code null} if there is no document
   * with the specified document ID. Note that the document ID is not part of the document itself.
   * It corresponds with the Elasticsearch {@code _id} field, which is retrieved separately from the
   * document source. You can get the value of this field through {@link IDocumentObject#getId()
   * IDocumentObject.getId}.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * @param id The NBA system ID of the data model object
   * @return
   */
  DOCUMENT_OBJECT find(String id);

  /**
   * <p>
   * Returns the data model objects with the specified system IDs, or a zero-length array no
   * specimens were found. Note that you cannot look up more than 1024 IDs at a time.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
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
   * @param id  The NBA system IDs of the requested data model objects
   * @return
   */
  DOCUMENT_OBJECT[] findByIds(String[] ids);

  /**
   * <p>
   * Returns the documents conforming to the provided query specification.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET and a POST request with the following
   * endpoint:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/query
   * </code>
   * </p>
   * <p>
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
   * </p>
   * 
   * @param querySpec
   * @return
   * @throws InvalidQueryException
   */
  QueryResult<DOCUMENT_OBJECT> query(QuerySpec querySpec) throws InvalidQueryException;

  /**
   * <p>
   * Writes the output satisfying the specified query
   * specification to the specified output stream.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET and POST request with
   * the following end point:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/download
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
  void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException;
  
  
  /**
   * <p>
   * Returns the number of documents conforming to the provided query specification. You may specify
   * {@code null} for the {@code querySpec} argument if you simply want a total document count.
   * Otherwise you should only set the query conditions and (possibly) the {@link LogicalOperator
   * logical operator} on the {@code QuerySpec}. Setting anything else on the {@code QuerySpec} has
   * no effect.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET or POST request with the following endpoint:
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
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
   * </p>
   * 
   * @param querySpec
   * @return
   * @throws InvalidQueryException
   */
  long count(QuerySpec querySpec) throws InvalidQueryException;

  /**
   * <p>
   * Returns the distinct number of values for the given field. You may specify a {@code querySpec}
   * argument if you're interested in just the distinct values of a (limited) set of documents. You
   * may specify {@code null} for the {@code querySpec} argument if you simply want a total document
   * count. Otherwise you should only set the query conditions and (possibly) the
   * {@link LogicalOperator logical operator} on the {@code QuerySpec}. Setting anything else on the
   * {@code QuerySpec} has no effect.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET or POST request with the following endpoint:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/countDistinctValues/{forField}
   * </code>
   * </p>
   * <p>
   * For example:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/taxon/countDistinctValues/defaultClassification.genus<br>
   * http://api.biodiversitydata.nl/v2/specimen/countDistinctValues/collectionType/?sourceSystem.code=CRS
   * </code>
   * </p>
   * <p>
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
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
   * Returns the distinct number of values for the given field (<i>forField</i>), grouped by the
   * second field (<i>forGroup</i>) you have specified. The result is a {@link List} in which each
   * item consists of 2 {@link Map}s: the first containing the group name (key) and the
   * distinct group value (value); the second containing the field name (key) and the number of 
   * distinct values for this field (value), within this group.
   * </p>
   * 
   * <pre>
   * [
   *   {"&lt;<i>forGroup</i>&gt;":"&lt;<i>distinct value a</i>&gt;","&lt;<i>forField</i>&gt;":&lt;<i>distinct count 1</i>&gt;},
   *   {"&lt;<i>forGroup</i>&gt;":"&lt;<i>distinct value b</i>&gt;","&lt;<i>forField</i>&gt;":&lt;<i>distinct count 2</i>&gt;},
   *   [...]
   *   {"&lt;<i>forGroup</i>&gt;":"&lt;<i>distinct value z</i>&gt;","&lt;<i>forField</i>&gt;":&lt;<i>distinct count n</i>&gt;}
   * ]
   * </pre>
   * 
   * <p>
   * You may specify a {@code querySpec} argument if you're interested in just the distinct values
   * of a specific set of documents. If you don't include a {@code querySpec} argument, you will 
   * get a summary of all documents.
   * </p>
   * 
   * <p>
   * By default, the result will be sorted descending by the distinct field value count. You may
   * choose to change the sorting by including the field you're using to group the results, as the
   * sort field in the {@code querySpec}:
   * </p>
   * 
   * <pre>
   * "sortFields" : [ { "path" : "[<i>forGroup</i>]", "sortOrder" : "ASC|DESC" } ]
   * </pre>
   * 
   * Default result size is 10. You can override this by including the desired result size in the 
   * {@code querySpec}:
   * 
   * <pre>
   * "size" : <i>n</i>
   * </pre>
   * 
   * If you have a large result set, you may choose to combine {@code size} with {@code from}.
   * This will allow you to page through the results, by choosing the start ({@code from}) 
   * and the end ({@code size}) of the results you'd like to see. Include {@code from} and 
   * {@code size} in the {@code querySpec}:
   * 
   * <pre>
   * "from" : <i>n</i>,
   * "size" : <i>m</i>
   * </pre>
   * 
   * <p>
   * NOTE: limiting the result view like this, will not reduce the response time. At least, it does
   * at the top end of the result list, but not at the bottom end. A request for the final results
   * of the set, will take approximately as long as a request for the entire result set. The gain
   * you do have, is the recuced amount of data that will be returned by your request.</p>
   * 
   * <p>Furthermore, the maximum resultsize is 10 000. If you try to go past that, for instance by
   * using {@code from} and {@code size}, a {@code InvalidQueryException} will be thrown.</p>
   * 
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET or POST request with the following endpoint:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/countDistinctValuesPerGroup/{forGroup}/{forField}
   * </code>
   * </p>
   * <p>
   * For example:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/taxon/countDistinctValuesPerGroup/[<i>field1</i>]/[<i>field2</i>]<br>
   * http://api.biodiversitydata.nl/v2/specimen/countDistinctValuesPerGroup/collectionType/sourceSystem.code
   * </code>
   * </p>
   * <p>
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
   * </p>
   * 
   * @param forGroup
   * @param forField
   * @param querySpec
   * @return
   * @throws InvalidQueryException
   * 
   */
  List<Map<String, Object>> countDistinctValuesPerGroup(String forGroup, String forField,
      QuerySpec querySpec) throws InvalidQueryException;

  /**
   * <p>
   * Returns the unique values of the specified field. The result is returned as a {@link Map} with
   * each key specifying one of the unique values and its value the document count (the number of
   * documents for which the specified field has that value).
   * <p>
   * 
   * <p>
   * You may specify {@code null} for the {@code querySpec} argument if you simply want a total
   * document count. Otherwise you should only set the query conditions and (possibly) the
   * {@link LogicalOperator logical operator} on the {@code QuerySpec}.
   * </p>
   * 
   * <p>
   * By default, the result will be sorted descending by document count. You can choose to sort the
   * result by the value of the field name by including that as sort field in the {@code querySpec}:
   * </p>
   * 
   * <pre>
   * "sortFields" : [ { "path" : "[<i>forField</i>]", "sortOrder" : "ASC|DESC" } ]
   * </pre>
   * 
   * <p>
   * Note that if the specified field is a {@link Collection} or an array, the sum of the document
   * counts may add up to more than the total number of documents in the index.
   * </p>
   * 

   * <h5>REST API</h5>
   * 
   * <p>
   * The NBA REST API exposes this method through a GET request with the following endpoint:
   * </p>
   * 
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
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
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
   * Returns the distinct values and their document count of the specified field (<i>forField</i>),
   * grouped by a second specified field (<i>forGroup</i>). The result is returned as a {@link List}
   * of {@link Map}s. Each {@code List} item contains 3 {@code Map}s. The first map has as key the
   * name of the field used to group the result, and its value as value. The second map has as key 
   * the label "count", and the document count as value. The last map of the {@code List} has as 
   * key the label "values", and as value a {@code List} containing the distinct values and their 
   * document counts. The structure of the latter {@code List} is similar to that of the 
   * {@code List} returned by a {@code countDistinctValuesPerGroup()}: each list item consists of 
   * 2 {@code Map}s: the first, having the <i>forField</i> as key, and a distinct value as its 
   * value; the second, the label "count" as key, and the document count as value.
   * </p>
   * 
   * <pre>
   * [
   *  {
   *   "&lt;<i>forGroup</i>&gt;":"&lt;<i>distinct group value A</i>&gt;",
   *   "count": &lt;<i>distinct group count 1</i>&gt;,
   *   "values": [
   *      {
   *        "&lt;<i>forField</i>&gt;":"&lt;<i>distinct value a</i>&gt;",
   *        "count": &lt;<i>distinct count 1</i>&gt;
   *      },
   *      {
   *        "&lt;<i>forField</i>&gt;":"&lt;<i>distinct value b</i>&gt;",
   *        "count": &lt;<i>distinct count 2</i>&gt;
   *      },
   *      [...]
   *      {
   *        "&lt;<i>forField</i>&gt;":"&lt;<i>distinct value z</i>&gt;",
   *        "count": &lt;<i>distinct count n</i>&gt;
   *      },
   *   ],
   *   "&lt;<i>forGroup</i>&gt;":"&lt;<i>distinct group value B</i>&gt;",
   *   "count": &lt;<i>distinct group count 2</i>&gt;,
   *   "values": [
   *      {
   *        "&lt;<i>forField</i>&gt;":"&lt;<i>distinct value a</i>&gt;",
   *        "count": &lt;<i>distinct count 1</i>&gt;
   *      },
   *      {
   *        "&lt;<i>forField</i>&gt;":"&lt;<i>distinct value b</i>&gt;",
   *        "count": &lt;<i>distinct count 2</i>&gt;
   *      },
   *      [...]
   *      {
   *        "&lt;<i>forField</i>&gt;":"&lt;<i>distinct value z</i>&gt;",
   *        "count": &lt;<i>distinct count n</i>&gt;
   *      } 
   *   ],
   *   [...]
   * }
   * </pre>
   * 
   * <p>NOTE: you may think that the sum of all counts of the distinct field values, will add up
   * to the count of the distinct field value used to group the results. This is not true though! 
   * At least, not necessarily. The count of both summaries is independent of each other. When each 
   * record exactly one occurence of both fields (<i>forField</i> and <i>forGroup</i>), then the 
   * counts will add up</p>. If the number of occurences of the specified fields differ, then the
   * counts are unrelated.</p>
   * 
   * <p>
   * You may specify a {@code querySpec} argument if you're interested in just the distinct values
   * of a specific set of documents. If you don't include a {@code querySpec} argument, you will 
   * get a summary of all documents.
   * </p>
   * 
   * <p>
   * By default, the result will be sorted descending by the distinct field value count. You can
   * choose to change the sorting by including the <i>forField</i> and/or the <i>forGroup</i> as
   * sort field(s) in the {@code querySpec}:
   * </p>
   *  
   * <pre>
   * "sortFields" : [ { "path" : "[<i>forGroup</i>]", "sortOrder" : "ASC|DESC" } ]
   * </pre>
   * 
   * <p>The default result size is 10. You can override this by including the desired result 
   * size in the {@code querySpec}:
   * </p>
   * 
   * <pre>
   * "size" : <i>n</i>
   * </pre>
   * 
   * If you have a large result set, you may choose to combine {@code size} with {@code from}.
   * This will allow you to page through the results, by choosing the start ({@code from}) 
   * and the end ({@code size}) of the results you'd like to see. Include {@code from} and 
   * {@code size} in the {@code querySpec}:
   * 
   * <pre>
   * "from" : <i>n</i>,
   * "size" : <i>m</i>
   * </pre>
   * 
   * <p>
   * NOTE: limiting the result view like this, will not reduce the response time. At least, it does
   * at the top end of the result list, but not at the bottom end. A request for the final results
   * of the set, will take approximately as long as a request for the entire result set. The gain
   * you do have, is the recuced amount of data that will be returned by your request.</p>
   * 
   * <p>Furthermore, the maximum resultsize is 10 000. If you try to go past that, for instance by
   * using {@code from} and {@code size}, a {@code InvalidQueryException} will be thrown.</p>
   * 
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following endpoint:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/getDistinctValuesPerGroup/{forGroup}/{forField}
   * </code>
   * </p>
   * <p>
   * For example:
   * </p>
   * <p>
   * <code>
   * http://api.biodiversitydata.nl/v2/specimen/getDistinctValuesPerGroup/sourceSystem.code/recordBasis<br>
   * </code>
   * </p>
   * <p>
   * See {@link QuerySpec} for an explanation of how to encode the {@code QuerySpec} object in the
   * request.
   * </p>
   * 
   * @param forGroup
   * @param forField
   * @param querySpec
   * @return
   * @throws InvalidQueryException
   */
  List<Map<String, Object>> getDistinctValuesPerGroup(String forGroup, String forField,
      QuerySpec querySpec) throws InvalidQueryException;

}
