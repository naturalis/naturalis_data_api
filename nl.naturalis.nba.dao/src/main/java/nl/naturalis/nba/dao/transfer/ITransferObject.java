package nl.naturalis.nba.dao.transfer;

import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.dao.types.ESType;

/**
 * Interface for objects that convert Elasticsearch model objects to API model
 * objects and vice versa. The API model is the data model as the client sees
 * it; the Elasticsearch model is the data model used for storing data in
 * Elasticsearch. For each Elasticsearch document type, as Java class
 * (implementing {@link ESType}) exists that exactly reflects the structure of
 * the document type.
 * 
 * @author Ayco Holleman
 *
 * @param <API_OBJECT>
 * @param <ES_OBJECT>
 */
@Deprecated
public interface ITransferObject<API_OBJECT extends INbaModelObject, ES_OBJECT extends ESType> {

	/**
	 * Converts an Elasticsearch document, deserialzed into the specified
	 * object, to an API model object.
	 */
	API_OBJECT getApiObject(ES_OBJECT esObject, String elasticsearchId);

	/**
	 * Converts an API model object to an Elasticsearch model object.
	 */
	ES_OBJECT getEsObject(API_OBJECT apiObject);

}
