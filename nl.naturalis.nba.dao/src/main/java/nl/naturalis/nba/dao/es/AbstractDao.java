package nl.naturalis.nba.dao.es;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.transfer.ITransferObject;
import nl.naturalis.nba.dao.es.types.ESType;

abstract class AbstractDao<API_OBJECT extends IDocumentObject, ES_OBJECT extends ESType> {

	private static final Logger logger;

	static {
		logger = DaoRegistry.getInstance().getLogger(SpecimenDao.class);
	}

	private DocumentType<ES_OBJECT> dt;
	private ITransferObject<API_OBJECT, ES_OBJECT> transfer;

	AbstractDao(DocumentType<ES_OBJECT> dt)
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

	public String save(API_OBJECT apiObject, boolean immediate)
	{
		String id = apiObject.getId();
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		if (logger.isDebugEnabled()) {
			String pattern = "New save request (index={};type={};id={})";
			logger.debug(pattern, index, type, id);
		}
		IndexRequestBuilder request = client().prepareIndex(index, type, id);
		ES_OBJECT esObject = transfer.getEsObject(apiObject);
		byte[] source = JsonUtil.serialize(esObject);
		request.setSource(source);
		IndexResponse response = request.execute().actionGet();
		if (immediate) {
			IndicesAdminClient iac = client().admin().indices();
			RefreshRequestBuilder rrb = iac.prepareRefresh(index);
			rrb.execute().actionGet();
		}
		apiObject.setId(response.getId());
		return response.getId();
	}

	public boolean delete(String id, boolean immediate)
	{
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		DeleteRequestBuilder request = client().prepareDelete(index, type, id);
		DeleteResponse response = request.execute().actionGet();
		return response.isFound();
	}

	protected SearchRequestBuilder newSearchRequest()
	{
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		if (logger.isDebugEnabled()) {
			String pattern = "New search request (index={};type={})";
			logger.debug(pattern, index, type);
		}
		SearchRequestBuilder request = client().prepareSearch(index);
		request.setTypes(type);
		return request;
	}

	private API_OBJECT createApiObject(String id, Map<String, Object> data)
	{
		if (logger.isDebugEnabled())
			logger.debug("Creating {} instance with id {}", dt, id);
		ObjectMapper om = dt.getObjectMapper();
		ES_OBJECT esObject = om.convertValue(data, dt.getESType());
		return transfer.getApiObject(esObject, id);
	}

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}
}
