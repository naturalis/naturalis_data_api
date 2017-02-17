package nl.naturalis.nba.api;

import java.util.Map;
import java.util.Set;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Specifies a common set of metadata retrieval methods that can be called for
 * any type of document within the NBA document store.
 * 
 * @author Ayco Holleman
 *
 * @param <DOCUMENT_OBJECT>
 *            The class representing the Elasticsearch document about which you
 *            get through an implementation of this interface.
 */
public interface INbaMetaData<DOCUMENT_OBJECT extends IDocumentObject> {

	/**
	 * <p>
	 * Returns a JSON representation of an Elasticsearch document type mapping.
	 * This is equivalent to the Elasticsearch REST API call
	 * <code>GET &lt;index&gt;/&lt;document-type&gt;/_mapping</code>.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/getMapping
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/taxon/getMapping
	 * </code>
	 * </p>
	 * 
	 * 
	 * @return
	 */
	String getMapping();

	/**
	 * <p>
	 * Returns all fields within a document. The fields are displayed using
	 * their full path. For example: "gatheringEvent.gatheringPersons.fullName".
	 * Note that only indexed (queryable) fields are returned.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/metadata/getPaths
	 * </code>
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/multimedia/metadata/getPaths<br>
	 * http://api.biodiversitydata.nl/v2/taxon/metadata/getPaths/?sorted=true
	 * </code>
	 * </p>
	 * 
	 * @param sorted
	 *            If {@code true} paths are displayed in alphabetical order.
	 *            Otherwise they appear in the same order as in the document.
	 * @return
	 */
	String[] getPaths(boolean sorted);

	/**
	 * Returns the set of allowed operators for each of the specified fields.
	 * Each key in the returned map is one of the specified fields and its value
	 * is the set of operators that you are allowed to use in conditions on that
	 * field.
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/metadata/getAllowedOperators
	 * </code>
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/metadata/getAllowedOperators/?fields=unitID,gatheringEvent.dateTimeBegin
	 * </code>
	 * </p>
	 * 
	 * @param fields
	 * @return
	 */
	Map<String, Set<ComparisonOperator>> getAllowedOperators(String... fields);

	/**
	 * <p>
	 * Verifies that the specified operator can be used in a query condition for
	 * the specified field.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/isOperatorAllowed/{field}/{operator}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/specimen/isOperatorAllowed/unitID/BETWEEN
	 * </code>
	 * </p>
	 * 
	 * @param field
	 * @param operator
	 * @return
	 */
	boolean isOperatorAllowed(String field, ComparisonOperator operator);

}
