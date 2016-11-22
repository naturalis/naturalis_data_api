package nl.naturalis.nba.api;

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
	 * <h3>REST API</h3>
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

}
