package nl.naturalis.nba.dao.util.es;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.util.es.ESDateInput;

@SuppressWarnings("static-method")
public class ESDateInputTest {

	private static final Logger logger = DaoRegistry.getInstance().getLogger(ESDateInputTest.class);

	@BeforeClass
	public static void before()
	{
		logger.info("Start");
	}

	@BeforeClass
	public static void after()
	{
		logger.info("Finish");
	}

	/*
	 * Test with valid ISO8601 date string
	 */
	@Test
	public void test_parse01()
	{
		String date = "1204-06-10T08:10:11.888+04:00";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertEquals("01", "1204-06-10T08:10:11.888+04:00", odt.toString());
	}

	/*
	 * Test with valid ISO8601 date string (no time zone)
	 */
	@Test
	public void test_parse02()
	{
		String date = "1204-06-10T08:10:11.888Z";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertEquals("01", "1204-06-10T08:10:11.888Z", odt.toString());
	}

	/*
	 * Test with valid ISO8601 date string (no millis)
	 */
	@Test
	public void test_parse03()
	{
		String date = "1204-06-10T08:10:11Z";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertEquals("01", "1204-06-10T08:10:11Z", odt.toString());
	}

	/*
	 * Test with invalid ISO8601 date string (bad month)
	 */
	@Test
	public void test_parse04()
	{
		String date = "1204-6-10T08:10:11Z";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertNull("01", odt);
	}

	/*
	 * Test with valid date string: yyyy-MM-dd HH:mm:ss
	 */
	@Test
	public void test_parse05()
	{
		String date = "1204-06-10 08:10:11";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertEquals("01", "1204-06-10T08:10:11Z", odt.toString());
	}

	/*
	 * Test with invalid date string (bad seconds)
	 */
	@Test
	public void test_parse06()
	{
		String date = "1204-06-10 08:10:1";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertNull("01", odt);
	}

	/*
	 * Test with year and month only
	 */
	@Test
	public void test_parse07()
	{
		String date = "1204-06";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertEquals("01", "1204-06-01T00:00Z", odt.toString());
	}

	/*
	 * Test with year and month only (bad month)
	 */
	@Test
	public void test_parse08()
	{
		String date = "1204-13";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertNull("01", odt);
	}

	/*
	 * Test with year only
	 */
	@Test
	public void test_parse09()
	{
		String date = "2017";
		OffsetDateTime odt = new ESDateInput().parse(date);
		assertEquals("01", "2017-01-01T00:00Z", odt.toString());
	}
}
