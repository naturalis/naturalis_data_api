package nl.naturalis.nba.dao.es;

import static org.junit.Assert.assertNotNull;

import org.domainobject.util.ConfigObject;
import org.junit.Test;

import nl.naturalis.nba.dao.Registry;

public class RegistryTest {

	@Test
	public void testGetConfig()
	{
		Registry registry = Registry.getInstance();
		ConfigObject config = registry.getConfig();
		assertNotNull("01", config.get("elasticsearch.cluster.name"));
		assertNotNull("02", config.get("elasticsearch.transportaddress.host"));
		assertNotNull("03", config.get("elasticsearch.transportaddress.port"));
	}

	@Test
	public void testGetConfDir()
	{
	}

	@Test
	public void testGetFile()
	{
	}

}
