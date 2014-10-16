package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
		String ndaConfDir = System.getProperty("ndaConfDir");
		if (ndaConfDir != null) {
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
				return new ConfigObject(file);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		logger.debug("Searching classpath for configuration file " + PROPERTY_FILE_NAME);
		InputStream is = LoadUtil.class.getResourceAsStream("/" + PROPERTY_FILE_NAME);
		if (is == null) {
			throw new RuntimeException(String.format("Configuration file missing: %s", PROPERTY_FILE_NAME));
		}
		ConfigObject result = new ConfigObject(is);
		try {
			is.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Returns {@code Client} configured using elasticsearch.yml, which should
	 * be on classpath.
	 */
	@SuppressWarnings("resource")
	public static final Client getDefaultClient()
	{
		if (localClient == null) {
			logger.info("Initializing ElasticSearch session");
			Properties props = new Properties();
			String ndaConfDir = System.getProperty("ndaConfDir");
			if (ndaConfDir == null) {
				throw new RuntimeException("Missing system property: \"ndaConfDir\"");
			}
			File dir = new File(ndaConfDir);
			if (!dir.isDirectory()) {
				throw new RuntimeException(String.format("Invalid path specified for system property \"ndaConfDir\": \"%s\"", ndaConfDir));
			}
			try {
				File file = new File(dir.getCanonicalPath() + "/nda-es-loaders.properties");
				props.load(new FileInputStream(file));
			}
			catch (IOException e1) {
				throw new RuntimeException("Missing file \"nda-es-loaders.properties\" under directory " + ndaConfDir);
			}
			String cluster = props.getProperty("elasticsearch.cluster.name");
			if (cluster == null) {
				throw new RuntimeException("Missing property in nl.naturalis.nda.elasticsearch.load.properties: elasticsearch.cluster.name");
			}
			String host = props.getProperty("elasticsearch.transportaddress.host");
			if (host == null) {
				throw new RuntimeException("Missing property in nl.naturalis.nda.elasticsearch.load.properties: elasticsearch.transportaddress.host");
			}
			String port = props.getProperty("elasticsearch.transportaddress.port");
			if (port == null) {
				throw new RuntimeException("Missing property in nl.naturalis.nda.elasticsearch.load.properties: elasticsearch.transportaddress.port");
			}
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
