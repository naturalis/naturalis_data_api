package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.zpad;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.domainobject.util.ConfigObject;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;

/**
 * Utility class providing basic functionality for all import programs. Always
 * use this class to connect to ElasticSearch using {@link #getESClient()} or
 * {@link #getNbaIndexManager()}.
 * 
 * @author Ayco Holleman
 *
 */
public final class LoadUtil {

	private static final Logger logger = Registry.getInstance().getLogger(LoadUtil.class);
	private static final String PROPERTY_FILE_NAME = "nda-import.properties";

	private static ConfigObject config;
	private static Client esClient;


	private LoadUtil()
	{
	}


	/**
	 * Get a {@code ConfigObject} for the central NBA import configuration file
	 * (nda-import.properties).
	 * 
	 * @return
	 * 
	 * @Deprecated Use {@link Registry}
	 */
	@Deprecated()
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
	 * Get a native Java ElasticSearch {@code Client}.
	 * 
	 * @Deprecated Use {@link Registry}
	 */
	@Deprecated()
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


	/**
	 * Get an index manager for the NBA index.
	 * 
	 * @Deprecated Use {@link Registry}
	 */
	@Deprecated()
	public static IndexNative getNbaIndexManager()
	{
		return new IndexNative(getESClient(), getConfig().required("elasticsearch.index.name"));
	}


	/**
	 * Get the duration between {@code start} and now, formatted as HH:mm:ss.
	 * 
	 * @param start
	 * @return
	 */
	public static String getDuration(long start)
	{
		return getDuration(start, System.currentTimeMillis());
	}


	/**
	 * Get the duration between {@code start} and {@code end}, formatted as
	 * HH:mm:ss.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getDuration(long start, long end)
	{
		int millis = (int) (end - start);
		int hours = millis / (60 * 60 * 1000);
		millis = millis % (60 * 60 * 1000);
		int minutes = millis / (60 * 1000);
		millis = millis % (60 * 1000);
		int seconds = millis / 1000;
		return zpad(hours, 2, ":") + zpad(minutes, 2, ":") + zpad(seconds, 2);
	}


	/**
	 * Equivalent to {@code URLEncoder.encode(raw, "UTF-8")} suppressing the
	 * {@code UnsupportedEncodingException}.
	 * 
	 * @param raw
	 * @return
	 */
	public static String urlEncode(String raw)
	{
		try {
			return URLEncoder.encode(raw, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// Won't happen with UTF-8
			return null;
		}
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
