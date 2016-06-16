package nl.naturalis.nba.dao.es.util;

import static org.junit.Assert.assertNotNull;

import org.elasticsearch.client.Client;
import org.junit.Test;

import nl.naturalis.nba.dao.es.ESClientManager;

public class ESClientManagerTest {

	@Test
	public void testGetClient()
	{
		Client client = ESClientManager.getInstance().getClient();
		assertNotNull("01", client);
	}

}
