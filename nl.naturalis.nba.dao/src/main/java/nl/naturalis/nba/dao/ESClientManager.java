package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

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
  
  private static final ESClientManager instance = new ESClientManager(DaoRegistry.getInstance().getConfiguration());

  /**
   * Returns an instance of an {@link ESClientManager}.
   */
  public static ESClientManager getInstance() {
    return instance;
  }

  private final ConfigObject config;

  private RestHighLevelClient client;

  private ESClientManager(ConfigObject config) {
    this.config = config;
  }

  /**
   * Returns an Elasticsearch {@link RestHighLevelClient} instance.
   * 
   * @return RestHighLevelClient
   */
  public synchronized RestHighLevelClient getClient() {
    if (client == null) {
      logger.info("Connecting to Elasticsearch cluster");      
      // client = new RestHighLevelClient(RestClient.builder(hosts));
      // or, more elaborate:
      HttpHost[] hosts = getHosts();
      RestClientBuilder builder = RestClient.builder(hosts);
      builder.setRequestConfigCallback(
          new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                RequestConfig.Builder requestConfigBuilder) {
                  return requestConfigBuilder
                    .setConnectTimeout(5000)
                    .setSocketTimeout(0);
            }
          }
      );
      client = new RestHighLevelClient(builder);
      logger.info("Connected");
    }
    ping();
    return client;
  }

  
  /**
   * Closes the Elasticsearch client. If you disconnect from Elasticsearch this way, the next call
   * to {@link #getClient()} is guaranteed to return a new Elasticsearch {@link RestHighLevelClient} instance.
   * @throws IOException 
   */
  public synchronized void closeClient() {
    if (client != null) {
      logger.info("Disconnecting from Elasticsearch cluster");
      try {
        client.close();
      } catch (IOException e) {
        throw new ConnectionFailureException(String.format("Failed to close the Elasticsearch client: %s", e.getMessage()));
      } finally {
        client = null;
      }
      
    }
  }

  private HttpHost[] getHosts() {
    
    String s = config.required("elasticsearch.transportaddress.host");
    logger.info("Host(s): " + s);
    String names[] = s.trim().split(",");    
    int numberOfHosts = names.length;
    HttpHost[] hosts = new HttpHost[numberOfHosts];
    int[] ports = getPorts(numberOfHosts);
    
    for (int i = 0; i < names.length; i++) {
      String name = names[i].trim();
      int port = ports[i];
      HttpHost host = new HttpHost(name, port);
      hosts[i] = host;
    }
    return hosts;
  }
  
  private int[] getPorts(int numHosts) {
    int[] ports = new int[numHosts];
    String s = config.get("elasticsearch.transportaddress.port");
    logger.info("Port(s): " + s);
    if (s == null) {
      Arrays.fill(ports, 9200);
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
      client.ping(RequestOptions.DEFAULT);
    } catch (IOException e) {
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
