package nl.naturalis.nba.dao.es.transfer;

import nl.naturalis.nba.api.model.NBADomainObject;
import nl.naturalis.nba.dao.es.types.ESType;

/**
 * Interface
 * @author Ayco Holleman
 *
 * @param <API_OBJECT>
 * @param <ES_OBJECT>
 */
public interface ITransferObject<API_OBJECT extends NBADomainObject, ES_OBJECT extends ESType> {

	API_OBJECT getApiObject(ES_OBJECT esModelObject, String elasticsearchId);
	
	ES_OBJECT getEsObject(API_OBJECT apiModelObject);

}
