package nl.naturalis.nda.elasticsearch.dao.dao;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for all ElasticSearch data access objects.
 * 
 * @author ayco_holleman
 * 
 */
public abstract class AbstractDao {

	private static ObjectMapper objectMapper;


	protected static ObjectMapper getObjectMapper()
	{
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected final Client esClient;
	protected final String ndaIndexName;


	public AbstractDao(String ndaIndexName)
	{
		esClient = nodeBuilder().node().client();
		esClient.admin().cluster().prepareHealth().setWaitForGreenStatus().execute().actionGet();
		this.ndaIndexName = ndaIndexName;
	}


	public AbstractDao(Client esClient, String ndaIndexName)
	{
		this.esClient = esClient;
		this.ndaIndexName = ndaIndexName;
	}


	protected SearchRequestBuilder newSearchRequest()
	{
		return esClient.prepareSearch(ndaIndexName);
	}
}
