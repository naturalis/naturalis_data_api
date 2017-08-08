/**
 * 
 */
package nl.naturalis.nba.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hettling
 *
 */
public class GeoAreaClientTest {

	private String baseUrl = "http://localhost:8080/v2";
	private GeoAreaClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		ClientConfig config = new ClientConfig();
		config.setBaseUrl(baseUrl);
		config.setPreferGET(true);
		NbaSession session = new NbaSession(config);
		client = session.getGeoAreaClient();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	/*
	 * Test with non-existent locality.
	 */
	@Test
	public void test_getGeoJsonForLocality01()
	{
		assertNull(client.getGeoJsonForLocality("FOO"));
	}

	/*
	 * Test with non-existent locality.
	 */
	@Test
	public void test_getGeoJsonForLocality02()
	{
		assertNotNull(client.getGeoJsonForLocality("Amsterdam"));
	}

}
