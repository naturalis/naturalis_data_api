/**
 * 
 */
package nl.naturalis.nba.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.utils.DevNullOutputStream;

/**
 * @author hettling
 *
 */
public class TaxonClientTest {

	private String baseUrl = "http://localhost:8080/v2";
	private TaxonClient client;

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
		client = session.getTaxonClient();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	/*
	 * Test with non-existent field
	 */
	@Test(expected = InvalidQueryException.class)
	public void test_dwcaQuery() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("FOO", "=", "BAR"));
		client.dwcaQuery(query, new DevNullOutputStream());
	}

	@Test(expected = NoSuchDataSetException.class)
	public void test_dwcaGetDataSet01() throws NoSuchDataSetException
	{
		client.dwcaGetDataSet("FOO", new DevNullOutputStream());
	}

	@Test
	public void test_dwcaGetDataSet02() throws NoSuchDataSetException
	{
		client.dwcaGetDataSet("nsr", new DevNullOutputStream());
	}

}
