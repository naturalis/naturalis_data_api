package nl.naturalis.nba.dao;

import static org.junit.Assert.assertNotNull;

import org.elasticsearch.client.Client;
import org.junit.Test;

import nl.naturalis.nba.dao.ESClientManager;

public class ESClientManagerTest {

	@Test
	public void testGetClient()
	{
		Client client = ESClientManager.getInstance().getClient();
		assertNotNull("01", client);
	}

}
