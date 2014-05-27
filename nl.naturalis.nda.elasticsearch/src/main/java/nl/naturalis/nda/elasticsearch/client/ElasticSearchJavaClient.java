package nl.naturalis.nda.elasticsearch.client;

import java.net.URL;

import nl.naturalis.nda.elasticsearch.load.SchemaCreator;

import org.domainobject.util.FileUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around ElasticSearch's Java client. Since the documentation for the
 * Java client sucks so badly and since {@link #deleteIndex()} seems to not
 * always work, we can't really use it for the moment. Instead we use
 * {@link ElasticSearchHttpClient}.
 * 
 * @author ayco_holleman
 * 
 */
public class ElasticSearchJavaClient {

	private static Logger logger = LoggerFactory.getLogger(ElasticSearchJavaClient.class);

	@SuppressWarnings("serial")
	public class Exception extends RuntimeException {
		public Exception(String message)
		{
			super(message);
		}
	}


	public static void main(String[] args)
	{
		ElasticSearchJavaClient esjc = new ElasticSearchJavaClient("nda");
		if (esjc.indexExists()) {
			esjc.deleteIndex();
		}
		esjc.createIndex();
		URL url = SchemaCreator.class.getResource("/elasticsearch/specimen.type.json");
		String mappings = FileUtil.getContents(url);
		esjc.createType("specimen", mappings);
	}

	final Client esClient;
	final IndicesAdminClient adminClient;
	final String indexName;


	public ElasticSearchJavaClient(String indexName)
	{
		this.indexName = indexName;
		esClient = getTransportClient();
		adminClient = esClient.admin().indices();
	}


	/**
	 * Deletes the index for which this client was set up.
	 * 
	 * @return {@code true} if the index existed and was successfully deleted;
	 *         {@code false} if the index did not exist.
	 */
	public boolean deleteIndex()
	{
		logger.info("Deleting index " + indexName);
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		try {
			DeleteIndexResponse response = adminClient.delete(request).actionGet();
			if (!response.isAcknowledged()) {
				throw new Exception("Failed to delete index " + indexName);
			}
			logger.info("Index deleted");
			return true;
		}
		catch (IndexMissingException e) {
			logger.info("No such index: " + indexName);
			return false;
		}
	}


	public boolean indexExists()
	{
		logger.info(String.format("Verifying existence of index \"%s\"", indexName));
		IndicesExistsRequest request = new IndicesExistsRequest(indexName);
		IndicesExistsResponse response = adminClient.exists(request).actionGet();
		if (response.isExists()) {
			logger.info("Index exists");
			return true;
		}
		logger.info("No such index");
		return false;
	}


	public void createIndex()
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		IndicesAdminClient iac = esClient.admin().indices();
		CreateIndexResponse response = iac.create(request).actionGet();
		if (!response.isAcknowledged()) {
			throw new Exception("Failed to delete index " + indexName);
		}
		logger.info("Index created");
	}


	public void createType(String type, String json)
	{
		logger.info(String.format("Creating type \"%s\"", type));
		CreateIndexRequestBuilder cirb = adminClient.prepareCreate(indexName).addMapping(type, json);
		CreateIndexResponse response = cirb.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new Exception(String.format("Failed to create type \"%s\"", type));
		}
	}


	private Client getTransportClient()
	{
		TransportClient transportClient = new TransportClient();
		transportClient.addTransportAddress(new InetSocketTransportAddress("10.80.0.61", 9300));
		return transportClient;
	}

}
