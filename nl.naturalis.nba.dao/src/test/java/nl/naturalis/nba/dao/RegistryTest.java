package nl.naturalis.nba.dao;

import static org.junit.Assert.assertNotNull;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.nba.utils.ConfigObject;

@SuppressWarnings("static-method")
public class RegistryTest {

	private static final Logger logger = DaoRegistry.getInstance().getLogger(RegistryTest.class);

	@BeforeClass
	public static void init()
	{
		logger.info("Starting tests");
	}

	@Test
	public void testGetConfig()
	{
		DaoRegistry registry = DaoRegistry.getInstance();
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
