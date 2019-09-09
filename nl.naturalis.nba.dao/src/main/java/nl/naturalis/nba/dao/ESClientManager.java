package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
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

  private static final ESClientManager instance =
      new ESClientManager(DaoRegistry.getInstance().getConfiguration());

  /**
   * Returns an instance of an {@link ESClientManager}.
   */
  public static ESClientManager getInstance() {
    return instance;
  }

  private final ConfigObject config;

  private Client client;

  private ESClientManager(ConfigObject config) {
    this.config = config;
  }

  /**
   * Returns an Elasticsearch {@link Client} instance.
   * 
   * @return
   */
  public synchronized Client getClient() {
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
      logger.info("Connected");
    }
    ping();
    return client;
  }

  /**
   * Closes the Elasticsearch client. If you disconnect from Elasticsearch this way, the next call
   * to {@link #getClient()} is guaranteed to return a new Elasticsearch {@link Client} instance.
   */
  public synchronized void closeClient() {
    if (client != null) {
      logger.info("Disconnecting from Elasticsearch cluster");
      try {
        client.close();
      } finally {
        client = null;
      }
    }
  }

  private Client createClient() {
    Builder builder = Settings.builder();
    String cluster = config.required("elasticsearch.cluster.name");
    logger.info("Cluster: {}", cluster);
    builder.put("cluster.name", cluster);
    builder.put("client.transport.ping_timeout", "20s");
    Settings settings = builder.build();
    return new PreBuiltTransportClient(settings);
  }

  private InetAddress[] getHosts() {
    String s = config.required("elasticsearch.transportaddress.host");
    logger.info("Host(s): " + s);
    String names[] = s.trim().split(",");
    InetAddress[] addresses = new InetAddress[names.length];
    for (int i = 0; i < names.length; ++i) {
      String name = names[i].trim();
      try {
        addresses[i] = InetAddress.getByName(name);
      } catch (UnknownHostException e) {
        String msg = "Unknown host: \"" + name + "\"";
        throw new ConnectionFailureException(msg);
      }
    }
    return addresses;
  }

  private int[] getPorts(int numHosts) {
    int[] ports = new int[numHosts];
    String s = config.get("elasticsearch.transportaddress.port");
    logger.info("Port(s): " + s);
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

  private void ping() {
    try {
      // Do some request
      client.admin().indices().prepareGetIndex().get();
    } catch (NoNodeAvailableException e) {
      String cluster = config.required("elasticsearch.cluster.name");
      String host = config.required("elasticsearch.transportaddress.host");
      String port = config.get("elasticsearch.transportaddress.port");
      String fmt = "Cluster: %s. Host: %s. Port: %s. Message: %s. Is Elasticsearch down?";
      String msg = String.format(fmt, cluster, host, port, e.getMessage());
      logger.error(msg);
      throw new ConnectionFailureException(msg);
    }
  }
}
