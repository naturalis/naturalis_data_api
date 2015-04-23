package nl.naturalis.nda.export;

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

public class ExportUtil {

	private ExportUtil()
	{
		// Static methods only
	}

	private static final Logger logger = LoggerFactory.getLogger(ExportUtil.class);

	/**
	 * Name of the file containing property file containing fundamental settings
	 * for the export programs.
	 */
	private static final String PROPERTY_FILE_NAME = "nda-export.properties";

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
			logger.debug("Searching classpath for configuration file " + PROPERTY_FILE_NAME);
			try (InputStream is = ExportUtil.class.getResourceAsStream("/" + PROPERTY_FILE_NAME)) {
				if (is == null) {
					throw new RuntimeException(String.format("Configuration file missing: %s", PROPERTY_FILE_NAME));
				}
				config = new ConfigObject(is);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
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
