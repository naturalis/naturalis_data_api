package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import nl.naturalis.nba.dao.exception.ConnectionFailureException;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * A factory for Elasticsearch {@link Client} instances.
 * 
 * @author Ayco Holleman
 *
 */
public class ESClientManager {

	private static final Logger logger = getLogger(ESClientManager.class);

	private static ESClientManager instance;

	/**
	 * Returns an instance of an {@link ESClientManager}.
	 */
	public static ESClientManager getInstance()
	{
		if (instance == null) {
			ConfigObject cfg = DaoRegistry.getInstance().getConfiguration();
			instance = new ESClientManager(cfg);
		}
		return instance;
	}

	private final ConfigObject config;

	private Client client;

	private ESClientManager(ConfigObject config)
	{
		this.config = config;
	}

	/**
	 * Returns an Elasticsearch {@link Client} instance.
	 * 
	 * @return
	 */
	public Client getClient()
	{
		if (client == null) {
			logger.info("Connecting to Elasticsearch cluster");
			InetAddress[] hosts = getHosts();
			int[] ports = getPorts(hosts.length);
			client = createClient();
			for (int i = 0; i < hosts.length; ++i) {
				InetAddress host = hosts[i];
				int port = ports[i];
				InetSocketTransportAddress addr;
				addr = new InetSocketTransportAddress(host, port);
				((TransportClient) client).addTransportAddress(addr);
			}
			//ping();
		}
		return client;
	}

	/**
	 * Closes the Elasticsearch client. If you disconnect from Elasticsearch
	 * this way, the next call to {@link #getClient()} is guaranteed to return
	 * a new Elasticsearch {@link Client} instance.
	 */
	public void closeClient()
	{
		if (client != null) {
			logger.info("Disconnecting from Elasticsearch cluster");
			try {
				client.close();
			}
			finally {
				client = null;
			}
		}
	}

	private Client createClient()
	{
		Builder builder = Settings.builder();
		String cluster = config.required("elasticsearch.cluster.name");
		builder.put("cluster.name", cluster);
		builder.put("client.transport.ping_timeout", "20s");
		Settings settings = builder.build();
		return new PreBuiltTransportClient(settings);
	}

	private InetAddress[] getHosts()
	{
		String s = config.required("elasticsearch.transportaddress.host");
		String names[] = s.trim().split(",");
		InetAddress[] addresses = new InetAddress[names.length];
		for (int i = 0; i < names.length; ++i) {
			String name = names[i].trim();
			try {
				addresses[i] = InetAddress.getByName(name);
			}
			catch (UnknownHostException e) {
				String msg = "Unknown host: \"" + name + "\"";
				throw new ConnectionFailureException(msg);
			}
		}
		return addresses;
	}

	private int[] getPorts(int numHosts)
	{
		int[] ports = new int[numHosts];
		String s = config.get("elasticsearch.transportaddress.port");
		if (s == null) {
			Arrays.fill(ports, 9300);
			return ports;
		}
		String[] chunks = s.trim().split(",");
		if (chunks.length == 1) {
			int port = Integer.parseInt(chunks[0].trim());
			Arrays.fill(ports, port);
			return ports;
		}
		if (chunks.length == numHosts) {
			for (int i = 0; i < numHosts; ++i) {
				int port = Integer.parseInt(chunks[i].trim());
				ports[i] = port;
			}
			return ports;
		}
		String msg = "Number of ports must be either one or match number of hosts";
		throw new ConnectionFailureException(msg);
	}

	@SuppressWarnings("unused")
	private void ping()
	{
		ClusterStatsRequest request = new ClusterStatsRequest();
		ClusterStatsResponse response = null;
		try {
			response = client.admin().cluster().clusterStats(request).actionGet();
		}
		catch (NoNodeAvailableException e) {
			String cluster = config.required("elasticsearch.cluster.name");
			String hosts = config.required("elasticsearch.transportaddress.host");
			String ports = config.get("elasticsearch.transportaddress.port");
			String msg = "Ping resulted in NoNodeAvailableException\n" + "* Check configuration:\n"
					+ "  > elasticsearch.cluster.name={}\n"
					+ "  > elasticsearch.transportaddress.host={}\n"
					+ "  > elasticsearch.transportaddress.port={}\n"
					+ "* Make sure Elasticsearch is running\n"
					+ "* Make sure client version matches server version";
			logger.error(msg, cluster, hosts, ports);
			throw new ConnectionFailureException(e);
		}
		if (response.getStatus().equals(ClusterHealthStatus.RED)) {
			throw new ConnectionFailureException("Elasticsearch cluster in bad health");
		}
	}
}
