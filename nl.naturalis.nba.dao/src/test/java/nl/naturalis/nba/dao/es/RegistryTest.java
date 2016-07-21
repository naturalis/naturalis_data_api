package nl.naturalis.nba.dao.es;

import static org.junit.Assert.assertNotNull;

import org.domainobject.util.ConfigObject;
import org.junit.Test;

@SuppressWarnings("static-method")
public class RegistryTest {

	@Test
	public void testGetConfig()
	{
		DAORegistry registry = DAORegistry.getInstance();
		ConfigObject config = registry.getConfiguration();
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
