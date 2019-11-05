package nl.naturalis.nba.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;

import org.junit.Test;

import nl.naturalis.nba.dao.ESClientManager;

public class ESClientManagerTest {

	@Test
	public void testGetClient() throws IOException {
	  
	  RestHighLevelClient client = ESClientManager.getInstance().getClient();
		assertNotNull("01", client);
		
		boolean ping = false;
    ping = client.ping(RequestOptions.DEFAULT);
    assertTrue("02", ping);
	}
	
	@Test
	public void testClusterHealth() throws UnsupportedOperationException, IOException {
	  
	  Request request = new Request("GET", "/_cluster/health"); 
	  RestHighLevelClient client = ESClientManager.getInstance().getClient();
	  Response response = client.getLowLevelClient().performRequest(request); 

	  ClusterHealthStatus healthStatus;
	  try (InputStream is = response.getEntity().getContent()) { 
	      Map<String, Object> map = XContentHelper.convertToMap(XContentType.JSON.xContent(), is, true);
	      healthStatus = ClusterHealthStatus.fromString((String) map.get("status")); 
	  }
	  if (healthStatus != ClusterHealthStatus.GREEN) { }
	  assertEquals(ClusterHealthStatus.GREEN, healthStatus);
	}

}
