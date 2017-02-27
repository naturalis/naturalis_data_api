package nl.naturalis.nba.api;

import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;

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
	 * Returns metadata about specified fields. Each key in the returned map is
	 * one of the specified fields and its value a {@link FieldInfo} instance
	 * containing metadata about the field. If you speciy null or a zero-length
	 * array for the {@code fields} argument, all fields are returned.
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/metadata/getFieldInfo
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/metadata/getFieldInfo/?fields=field0,field1,field2
	 * </pre>
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * http://api.biodiversitydata.nl/v2/taxon/metadata/getFieldInfo
	 * http://api.biodiversitydata.nl/v2/specimen/metadata/getFieldInfo/?fields=unitID,gatheringEvent.dateTimeBegin
	 * </pre>
	 * </p>
	 * 
	 * @param fields
	 * @return
	 */
	Map<String, FieldInfo> getFieldInfo(String... fields) throws NoSuchFieldException;

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
