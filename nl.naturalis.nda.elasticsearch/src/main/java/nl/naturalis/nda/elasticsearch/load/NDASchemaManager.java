package nl.naturalis.nda.elasticsearch.load;

import java.net.URL;

import nl.naturalis.nda.elasticsearch.client.ElasticSearchHttpClient;

import org.domainobject.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NDASchemaManager {
	
	public static void main(String[] args) {
		NDASchemaManager nsm = new NDASchemaManager();
		nsm.bootstrap();
	}

	/**
	 * The default name of the ElasticSearch index for the Naturalis Data API.
	 */
	public static final String DEFAULT_NDA_INDEX_NAME = "nda";

	private static final Logger logger = LoggerFactory.getLogger(NDASchemaManager.class);

	private final ElasticSearchHttpClient client;


	/**
	 * Creates a {@code NDASchemaManager} managing the default NDA schema (i.e.
	 * the schema with name {@link #DEFAULT_NDA_INDEX_NAME} in the local cluster
	 * ({@link ElasticSearchHttpClient#LOCAL_CLUSTER}).
	 */
	public NDASchemaManager()
	{
		logger.info(String.format("Creating client for index %s in cluster %s", DEFAULT_NDA_INDEX_NAME, ElasticSearchHttpClient.LOCAL_CLUSTER));
		client = new ElasticSearchHttpClient(DEFAULT_NDA_INDEX_NAME, ElasticSearchHttpClient.LOCAL_CLUSTER);
	}


	/**
	 * Creates a {@code NDASchemaManager} managing the default NDA schema (i.e.
	 * the schema with name {@link #DEFAULT_NDA_INDEX_NAME} in the specified
	 * cluster.
	 * 
	 * @param clusterUrl The URL for the cluster (e.g. http://localhost:9200).
	 */
	public NDASchemaManager(String clusterUrl)
	{
		logger.info(String.format("Creating client for index %s in cluster %s", DEFAULT_NDA_INDEX_NAME, clusterUrl));
		client = new ElasticSearchHttpClient(DEFAULT_NDA_INDEX_NAME, clusterUrl);
	}


	/**
	 * Creates a {@code NDASchemaManager} managing the specified index in the
	 * specified cluster. Ordinarily the schema name would be
	 * {@link #DEFAULT_NDA_INDEX_NAME}, but for test or upgrade purposes it
	 * might be another name.
	 * 
	 * @param indexName
	 */
	public NDASchemaManager(String indexName, String clusterUrl)
	{
		logger.info(String.format("Creating client for index %s in cluster %s", indexName, clusterUrl));
		client = new ElasticSearchHttpClient(indexName, clusterUrl);
	}


	/**
	 * Creates the NDA schema from scratch. Will delete the entire index
	 * (mappings and documents) and then re-create it. WATCH OUT!
	 */
	public void bootstrap()
	{
		logger.info("Bootstrapping NDA index!");
		try {
			//client.deleteIndex();
			client.createIndex();
			URL url = NDASchemaManager.class.getResource("/elasticsearch/specimen.type.json");
			String mappings = FileUtil.getContents(url);
			client.addType("specimen", mappings);
			logger.info(client.describe());
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
