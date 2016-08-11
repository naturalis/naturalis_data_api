package nl.naturalis.nba.dao.es;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.NBADomainObject;
import nl.naturalis.nba.dao.es.transfer.ITransferObject;
import nl.naturalis.nba.dao.es.types.ESType;

abstract class AbstractDao<API_OBJECT extends NBADomainObject, ES_OBJECT extends ESType> {

	private static final Logger logger;

	static {
		logger = DaoRegistry.getInstance().getLogger(SpecimenDao.class);
	}

	private DocumentType dt;
	private ITransferObject<API_OBJECT, ES_OBJECT> transfer;

	public AbstractDao(DocumentType dt)
	{
		this.dt = dt;
	}

	public API_OBJECT find(String id)
	{
		if (logger.isDebugEnabled())
			logger.debug("find(\"{}\")", id);
		GetRequestBuilder request = client().prepareGet();
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		request.setIndex(index);
		request.setType(type);
		request.setId(id);
		GetResponse response = request.execute().actionGet();
		if (!response.isExists()) {
			if (logger.isDebugEnabled())
				logger.debug("{} with id \"{}\" not found", dt, id);
			return null;
		}
		if (logger.isDebugEnabled())
			logger.debug("Response:\n{}", response.getSourceAsString());
		Map<String, Object> data = response.getSource();
		return createApiObject(id, data);
	}

	private API_OBJECT createApiObject(String id, Map<String, Object> data)
	{
		if (logger.isDebugEnabled())
			logger.debug("Creating {} instance with id {}", dt, id);
		ObjectMapper om = dt.getObjectMapper();
		@SuppressWarnings("unchecked")
		ES_OBJECT esObject = (ES_OBJECT) om.convertValue(data, dt.getESType());
		return transfer.getApiObject(esObject, id);
	}

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}
}
