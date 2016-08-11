package nl.naturalis.nba.dao.es;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;

class DaoUtil {

	private static final Logger logger;

	static {
		logger = DaoRegistry.getInstance().getLogger(DaoUtil.class);
	}

	private DaoUtil()
	{
	}

	static SearchRequestBuilder newSearchRequest(DocumentType dt)
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

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}
}
