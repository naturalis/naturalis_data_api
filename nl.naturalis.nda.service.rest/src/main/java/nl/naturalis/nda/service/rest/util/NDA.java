package nl.naturalis.nda.service.rest.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.domainobject.util.ConfigObject;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NDA {

	public static final String SYSPROP_CONFIG_DIR = "nl.naturalis.nda.conf.dir";
	public static final String SESSION_ID_PARAM = "_SESSION_ID";
	
	private static final String CONFIG_FILE_NAME = "nda.properties";

	private static final Logger logger = LoggerFactory.getLogger(NDA.class);

	private final ConfigObject config;
	private Client esClient = null;


	public NDA()
	{
		config = loadConfig();
	}


	public Client getESClient()
	{
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


	public String getIndexName()
	{
		return config.required("elasticsearch.index.name");
	}


	public ConfigObject getConfig()
	{
		return config;
	}


	private String[] getPorts(int numHosts)
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


	private static ConfigObject loadConfig()
	{
		String confDir = System.getProperty(SYSPROP_CONFIG_DIR);
		if (confDir != null) {
			logger.debug("Using system property \"" + SYSPROP_CONFIG_DIR + "\" to locate configuration file " + CONFIG_FILE_NAME);
			File dir = new File(confDir);
			if (!dir.isDirectory()) {
				throw new RuntimeException(String.format("Invalid directory specified for property \"%s\": \"%s\"", SYSPROP_CONFIG_DIR, confDir));
			}
			try {
				File file = new File(dir.getCanonicalPath() + "/" + CONFIG_FILE_NAME);
				if (!file.isFile()) {
					throw new RuntimeException(String.format("Configuration file missing: %s", file.getCanonicalPath()));
				}
				logger.debug(String.format("Using configuration file %s", file.getCanonicalPath()));
				return new ConfigObject(file);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		logger.debug("Searching classpath for configuration file " + CONFIG_FILE_NAME);
		try (InputStream is = NDA.class.getResourceAsStream("/" + CONFIG_FILE_NAME)) {
			if (is == null) {
				throw new RuntimeException(String.format("Configuration file missing: %s", CONFIG_FILE_NAME));
			}
			return new ConfigObject(is);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
