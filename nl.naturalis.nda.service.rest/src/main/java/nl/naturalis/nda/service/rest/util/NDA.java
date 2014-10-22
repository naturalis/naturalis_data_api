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

	private static final Logger logger = LoggerFactory.getLogger(NDA.class);
	private static final String SYSPROP_CONFIG_DIR = "nl.naturalis.nda.conf.dir";
	private static final String CONFIG_FILE_NAME = "nda.properties";

	private final ConfigObject config;


	public NDA()
	{
		config = loadConfig();
	}


	@SuppressWarnings("resource")
	public Client getESClient()
	{
		logger.info("Initializing ElasticSearch session");
		String cluster = config.required("elasticsearch.cluster.name");
		String host = config.required("elasticsearch.transportaddress.host");
		String port = config.required("elasticsearch.transportaddress.port");
		InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(host, Integer.parseInt(port));
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster).build();
		Client client = new TransportClient(settings).addTransportAddress(transportAddress);
		ClusterStatsRequest request = new ClusterStatsRequest();
		ClusterStatsResponse response = client.admin().cluster().clusterStats(request).actionGet();
		logger.debug("Cluster stats: " + response.toString());
		return client;
	}


	public String getIndexName()
	{
		return config.required("elasticsearch.index.name");
	}


	public ConfigObject getConfig()
	{
		return config;
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
