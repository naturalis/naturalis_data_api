package nl.naturalis.nba.dao.format.csv;

import static org.apache.commons.text.StringEscapeUtils.escapeCsv;

import static org.junit.Assert.*;

import org.junit.Test;

public class CsvRecordWriterTest {

	@Test
	public void testEscapeCsv01()
	{
		String in = "Hello\\nWorld";
		String out = escapeCsv(in);
		assertEquals("01", in, out);
	}

	@Test
	public void testEscapeCsv02()
	{
		String in = "Hello\\World";
		String out = escapeCsv(in);
		assertEquals("01", in, out);
	}

}
