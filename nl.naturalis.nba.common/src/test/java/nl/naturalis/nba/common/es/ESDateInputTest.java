package nl.naturalis.nba.common.es;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.time.OffsetDateTime;

import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("static-method")
public class ESDateInputTest {

	@BeforeClass
	public static void before()
	{
	}

	@BeforeClass
	public static void after()
	{
	}

	/*
	 * Important b/c this is how we parse dates coming back from Elasticsearch.
	 * See OffsetDateTimeDeserializer.
	 */
	@Test
	public void test_parseAsOffsetDateTimeWithESDateFormat()
	{
		String date = "1204-06-10T08:10:11+0000";
		ESDateInput input = new ESDateInput(date);
		OffsetDateTime odt = input.parseAsOffsetDateTime(ESDateInput.ES_DATE_FORMAT);
		assertNotNull("01", odt);
		assertEquals("01", "1204-06-10T08:10:11Z", odt.toString());
	}

	/*
	 * Test with valid date string, parsable by
	 * DateFormatter.ISO_OFFSET_DATE_TIME
	 */
	@Test
	public void test_parse01()
	{
		String date = "1204-06-10T08:10:11.888+04:00";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "1204-06-10T08:10:11.888+04:00", odt.toString());
	}

	/*
	 * Test with valid date string, parsable by ESDateInput.ES_DATE_FORMAT
	 */
	@Test
	public void test_parse01a()
	{
		String date = "1204-06-10T08:10:11+0400";
		OffsetDateTime odt = new ESDateInput(date).parse();
		/*
		 * NB The default ISO_OFFSET_DATE_TIME formatter puts a colon in the
		 * time zone.
		 */
		assertEquals("01", "1204-06-10T08:10:11+04:00", odt.toString());
	}

	/*
	 * Test with valid date string (no time zone)
	 */
	@Test
	public void test_parse02()
	{
		String date = "1204-06-10T08:10:11.888Z";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "1204-06-10T08:10:11.888Z", odt.toString());
	}

	/*
	 * Test with valid date string (no millis)
	 */
	@Test
	public void test_parse03()
	{
		String date = "1204-06-10T08:10:11Z";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "1204-06-10T08:10:11Z", odt.toString());
	}

	/*
	 * Test with invalid ISO8601 date string (bad month)
	 */
	@Test
	public void test_parse04()
	{
		String date = "1204-6-10T08:10:11Z";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertNull("01", odt);
	}

	/*
	 * Test with valid date string, parsable with pattern yyyy-MM-dd HH:mm:ss
	 */
	@Test
	public void test_parse05()
	{
		String date = "1204-06-10 08:10:11";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "1204-06-10T08:10:11Z", odt.toString());
	}

	/*
	 * Test with valid date string, parsable with pattern yyyy-MM-dd HH:mm
	 */
	@Test
	public void test_parse05a()
	{
		String date = "1204-06-10 08:10";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "1204-06-10T08:10Z", odt.toString());
	}

	/*
	 * Test with invalid date string (bad seconds)
	 */
	@Test
	public void test_parse06()
	{
		String date = "1204-06-10 08:10:1";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertNull("01", odt);
	}

	/*
	 * Test with year and month only
	 */
	@Test
	public void test_parse07()
	{
		String date = "1204-06";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "1204-06-01T00:00Z", odt.toString());
	}

	/*
	 * Test with year and month only (bad month)
	 */
	@Test
	public void test_parse08()
	{
		String date = "1204-13";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertNull("01", odt);
	}

	/*
	 * Test with year only
	 */
	@Test
	public void test_parse09()
	{
		String date = "2017";
		OffsetDateTime odt = new ESDateInput(date).parse();
		assertEquals("01", "2017-01-01T00:00Z", odt.toString());
	}
}
