package nl.naturalis.nba.dao.es;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;

import nl.naturalis.nba.dao.Registry;

public class ESTestUtils {

	private static final Registry registry;
	private static final Logger logger;

	static {
		registry = Registry.getInstance();
		logger = registry.getLogger(ESTestUtils.class);
	}

	public static void createIndices()
	{

	}

	public static void createIndex(String indexName)
	{
		logger.info("Creating index {}", indexName);
		Client client = registry.getESClientFactory().getClient();
		IndicesAdminClient admin = client.admin().indices();
		CreateIndexRequestBuilder request = admin.prepareCreate(indexName);
		Builder builder = Settings.settingsBuilder();
		File settingsFile = registry.getFile("es-settings.json");
		String settings = FileUtil.getContents(settingsFile);
		builder.loadFromSource(settings);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create index " + indexName);
		}
		logger.info("Created index {}", indexName);
	}
	
	public static void dropIndex(String indexName) {
		
	}
}
