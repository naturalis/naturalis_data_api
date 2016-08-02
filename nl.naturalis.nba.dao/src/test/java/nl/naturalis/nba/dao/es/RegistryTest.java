package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static org.junit.Assert.assertNotNull;

import org.domainobject.util.ConfigObject;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("static-method")
public class RegistryTest {

	@BeforeClass
	public static void init()
	{
		if (SPECIMEN.getIndexInfo().getName().equals("nba")) {
			/*
			 * Unit tests are run against the nba_test index. However, some
			 * performance test are run against the nba index, which contains
			 * real data imported by the ETL programs. For that purpose we let
			 * nba-test.properties temporarily point to the nba index.
			 * Unfornately, we forgot once too often to reset
			 * nba-test.properties before running the unit tests again, wiping
			 * out the data in the nba index. So here some bare-knuckle way to
			 * prevent this. Note that RegistryTest is the first of the unit
			 * tests within the test suite.
			 */
			System.out.println("UPDATE nba-test.properties FIRST !!!!");
			System.exit(1);
		}
	}

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
