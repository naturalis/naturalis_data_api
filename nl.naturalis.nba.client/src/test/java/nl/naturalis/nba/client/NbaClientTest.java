package nl.naturalis.nba.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;

public class NbaClientTest {

	private String baseUrl = "http://localhost:8080/v2";
	private SpecimenClient client;

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
		client = session.getSpecimenClient();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	/*
	 * Test with non-existent ID.
	 */
	@Test()
	public void test_find01()
	{
		assertNull("01", client.find("FOO"));
	}

	/*
	 * Test with valid ID.
	 */
	@Test()
	public void test_find02()
	{
		client.find("RMNH.AVES.P.1036@CRS");
	}

	/*
	 * Test with non-existent ID.
	 */
	@Test()
	public void test_findByUnitID01()
	{
		assertEquals("01", 0, client.findByUnitID("FOO").length);
	}

	/*
	 * Test with valid ID.
	 */
	@Test()
	public void test_findByUnitID02()
	{
		assertEquals("01", 1, client.findByUnitID("RMNH.AVES.P.1036").length);
	}

	/*
	 * Test with non-existent field
	 */
	@Test(expected = InvalidQueryException.class)
	public void test_query01() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("FOO", "=", "BAR"));
		client.query(query);
	}

	/*
	 * Test with non-existent value
	 */
	@Test
	public void test_query02() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("unitID", "=", "FOO"));
		QueryResult<Specimen> result = client.query(query);
		assertEquals("01", 0, result.getTotalSize());
	}

	/*
	 * Test with valid value
	 */
	@Test
	public void test_query03() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("unitID", "=", "RMNH.AVES.P.1036"));
		QueryResult<Specimen> result = client.query(query);
		assertEquals("01", 1, result.getTotalSize());
	}

	/*
	 * Test with non-existent field
	 */
	@Test(expected = InvalidQueryException.class)
	public void test_count01() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("FOO", "=", "BAR"));
		client.count(query);
	}

	/*
	 * Test with non-existent value
	 */
	@Test
	public void test_count02() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("unitID", "=", "FOO"));
		assertEquals("01", 0, client.count(query));
	}

	/*
	 * Test with non-existent value
	 */
	@Test
	public void test_count03() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("unitID", "=", "RMNH.AVES.P.1036"));
		assertEquals("01", 1, client.count(query));
	}

	/*
	 * Test without QuerySpec
	 */
	@Test
	public void test_getDistinctValues01() throws InvalidQueryException
	{
		Map<String, Long> map = client.getDistinctValues("collectionType", null);
		assertTrue("01", map.containsKey("Aves"));
	}

	/*
	 * Test with QuerySpec
	 */
	@Test
	public void test_getDistinctValues02() throws InvalidQueryException
	{
		QuerySpec query = new QuerySpec();
		query.addCondition(new QueryCondition("unitID", "=", "RMNH.AVES.P.1036"));
		assertEquals("01", 1, client.getDistinctValues("collectionType", query).size());
	}
}
