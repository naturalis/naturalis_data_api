package nl.naturalis.nda.elasticsearch.client;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.domainobject.util.ExceptionUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.indices.IndexMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around ElasticSearch's Native (Java) client. Since the API seems to
 * have some quirks, we may have to resort to the {@link IndexREST}, which
 * "simply" uses the REST API of ElasticSearch.
 * 
 * @author ayco_holleman
 * 
 */
public class IndexNative implements Index {

	private static Logger logger = LoggerFactory.getLogger(IndexNative.class);


	public static void main(String[] args)
	{
		IndexNative esjc = new IndexNative("nda");
		if (esjc.exists()) {
			esjc.delete();
		}
		esjc.create();
		//		URL url = NDASchemaManager.class.getResource("/elasticsearch/specimen.type.json");
		//		String mappings = FileUtil.getContents(url);
		//		esjc.createType("specimen", mappings);
	}

	final Client esClient;
	final IndicesAdminClient admin;
	final String indexName;


	/**
	 * Create an instance manipulating the specified index using a default Java
	 * Client. If elasticsearch.yml in the classpath, it will be used to
	 * configure the cluster, node and client.
	 * 
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 */
	public IndexNative(String indexName)
	{
		this.indexName = indexName;
		this.esClient = nodeBuilder().node().client();
		admin = esClient.admin().indices();
	}


	/**
	 * reate an instance manipulating the specified index using the specified
	 * client.
	 * 
	 * @param client The client
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 */
	public IndexNative(Client client, String indexName)
	{
		this.indexName = indexName;
		this.esClient = client;
		admin = esClient.admin().indices();
	}

	/*
	 * Inner class only used to be thrown out of the action listener within the
	 * exists() method to signal that the index exists.
	 */
	@SuppressWarnings("serial")
	private class ExistsException extends RuntimeException {
	}


	public boolean exists()
	{
		logger.info(String.format("Verifying existence of index \"%s\"", indexName));
		IndicesExistsRequest request = new IndicesExistsRequest(indexName);
		try {
			admin.exists(request, new ActionListener<IndicesExistsResponse>() {
				public void onResponse(IndicesExistsResponse response)
				{
					if (response.isExists()) {
						throw new ExistsException();
					}
				}


				public void onFailure(Throwable e)
				{
					ExceptionUtil.smash(e);
				}
			});
		}
		catch (ExistsException e) {
			return true;
		}
		return false;
	}


	@Override
	public String describe()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String describeAllIndices()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void create()
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		CreateIndexResponse response = admin.create(request).actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexException("Failed to delete index " + indexName);
		}
		logger.info("Index created");
	}


	@Override
	public void create(String mappings)
	{
		// TODO Auto-generated method stub

	}


	public void createType(String name, String mapping)
	{
		logger.info(String.format("Creating type \"%s\"", name));
		CreateIndexRequestBuilder cirb = admin.prepareCreate(indexName).addMapping(name, mapping);
		CreateIndexResponse response = cirb.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexException(String.format("Failed to create type \"%s\"", name));
		}
	}


	/**
	 * Deletes the index for which this client was set up.
	 * 
	 * @return {@code true} if the index existed and was successfully deleted;
	 *         {@code false} if the index did not exist.
	 */
	public boolean delete()
	{
		logger.info("Deleting index " + indexName);
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		try {
			DeleteIndexResponse response = admin.delete(request).actionGet();
			if (!response.isAcknowledged()) {
				throw new IndexException("Failed to delete index " + indexName);
			}
			logger.info("Index deleted");
			return true;
		}
		catch (IndexMissingException e) {
			logger.info("No such index: " + indexName);
			return false;
		}
	}


	@Override
	public void deleteAllIndices()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void addDocument(String type, Object obj)
	{
		// TODO Auto-generated method stub

	}

}
