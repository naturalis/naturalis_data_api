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
	private static final String PROPERTY_FILE_NAME = "nda-import.properties";

	private static ConfigObject config;
	private static Client esClient;


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


	public static final Client getESClient()
	{
		// Make sure configuration is loaded
		getConfig();
		if (esClient == null) {
			logger.info("Initializing ElasticSearch session");
			String cluster = config.required("elasticsearch.cluster.name");
			String[] hosts = config.required("elasticsearch.transportaddress.host").trim().split(",");
			String[] ports = getPorts(hosts.length);
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster).build();
			esClient = new TransportClient(settings);
			for (int i = 0; i < hosts.length; ++i) {
				String host = hosts[i].trim();
				int port = Integer.parseInt(ports[i].trim());
				logger.info(String.format("Adding transport address \"%s:%s\"", host, port));
				InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(host, port);
				((TransportClient) esClient).addTransportAddress(transportAddress);
			}
			if (logger.isDebugEnabled()) {
				ClusterStatsRequest request = new ClusterStatsRequest();
				ClusterStatsResponse response = esClient.admin().cluster().clusterStats(request).actionGet();
				logger.debug("Cluster stats: " + response.toString());
			}
		}
		return esClient;
	}

	private static String[] getPorts(int numHosts)
	{
		String port = config.get("elasticsearch.transportaddress.port", true);
		String[] ports = port == null ? new String[] { "9300" } : port.trim().split(",");
		if (ports.length > 1 && ports.length != numHosts) {
			throw new RuntimeException("Error creating ES client: number of ports does not match number of hosts");
		}
		else if (ports.length == 1 && numHosts > 1) {
			port = ports[0];
			ports = new String[numHosts];
			for (int i = 0; i < ports.length; ++i) {
				ports[i] = port;
			}
		}
		return ports;
	}
}
