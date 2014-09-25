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

	protected Client esClient;
	protected String ndaIndexName;


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
