package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.StringUtil;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadUtil {

	private static final Logger logger = LoggerFactory.getLogger(LoadUtil.class);
	private static final String PROPERTY_FILE_NAME = "nda-es-loaders.properties";

	private static ConfigObject config;
	private static Client localClient;


	public static String getLuceneType(Class<?> cls)
	{
		return cls.getSimpleName();
	}


	public static String getMapping(Class<?> cls)
	{
		return StringUtil.getResourceAsString("/es-mappings/" + getLuceneType(cls) + ".json");
	}


	public static ConfigObject getConfig()
	{
		if (config == null) {
			String ndaConfDir = System.getProperty("ndaConfDir");
			if (ndaConfDir != null) {
				logger.debug("Using system property \"ndaConfDir\" to locate configuration file " + PROPERTY_FILE_NAME);
				File dir = new File(ndaConfDir);
				if (!dir.isDirectory()) {
					throw new RuntimeException(String.format("Invalid directory specified for property \"ndaConfDir\": \"%s\"", ndaConfDir));
				}
				try {
					File file = new File(dir.getCanonicalPath() + "/" + PROPERTY_FILE_NAME);
					if (!file.isFile()) {
						throw new RuntimeException(String.format("Configuration file missing: %s", file.getCanonicalPath()));
					}
					logger.debug(String.format("Using configuration file %s", file.getCanonicalPath()));
					config = new ConfigObject(file);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else {
				logger.debug("Searching classpath for configuration file " + PROPERTY_FILE_NAME);
				try (InputStream is = LoadUtil.class.getResourceAsStream("/" + PROPERTY_FILE_NAME)) {
					if (is == null) {
						throw new RuntimeException(String.format("Configuration file missing: %s", PROPERTY_FILE_NAME));
					}
					config = new ConfigObject(is);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return config;
	}


	/**
	 * Returns {@code Client} configured using elasticsearch.yml, which should
	 * be on classpath.
	 */
	@SuppressWarnings("resource")
	public static final Client getDefaultClient()
	{
		if (localClient == null) {
			ConfigObject config = getConfig();
			logger.info("Initializing ElasticSearch session");
			String cluster = config.required("elasticsearch.cluster.name");
			String host = config.required("elasticsearch.transportaddress.host");
			String port = config.required("elasticsearch.transportaddress.port");
			InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(host, Integer.parseInt(port));
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster).build();
			localClient = new TransportClient(settings).addTransportAddress(transportAddress);
			ClusterStatsRequest request = new ClusterStatsRequest();
			ClusterStatsResponse response = localClient.admin().cluster().clusterStats(request).actionGet();
			logger.debug("Cluster stats: " + response.toString());
		}
		return localClient;
	}
}
