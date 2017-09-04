package nl.naturalis.nba.api;

import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.api.model.metadata.NbaSetting;

/**
 * Specifies methods for retrieving metadata about an Elasticsearch document
 * type and the index containing it.
 * 
 * @author Ayco Holleman
 *
 * @param <DOCUMENT_OBJECT>
 *            The class representing the Elasticsearch document about which you
 *            get through an implementation of this interface.
 */
public interface INbaDocumentMetaData<DOCUMENT_OBJECT extends IDocumentObject> {

	/**
	 * Returns the value of a configuration setting related to the Elasticsearch
	 * document type or the index containing it. Note that document-independent
	 * settings are retrieved using {@link INbaMetaData#getSetting(NbaSetting)}
	 * or {@link INbaMetaData#getSettings()}.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/metadata/getSetting/{name}
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	Object getSetting(NbaSetting setting);

	/**
	 * <p>
	 * Returns all configuration settings for the Elasticsearch document type
	 * and the index containing it. Note that document-independent settings are
	 * retrieved using {@link INbaMetaData#getSetting(NbaSetting)} or
	 * {@link INbaMetaData#getSettings()}.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/&lt;document-type&gt;/metadata/getSettings
	 * </code>
	 * </p>
	 * <p>
	 * Examples:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/multimedia/metadata/getSettings<br>
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	Map<NbaSetting, Object> getSettings();

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
