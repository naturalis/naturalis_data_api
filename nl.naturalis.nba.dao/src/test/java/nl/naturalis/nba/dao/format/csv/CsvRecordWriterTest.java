package nl.naturalis.nba.dao.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import static org.junit.Assert.*;

import org.junit.Test;

public class CsvRecordWriterTest {

	@Test
	public void testEscapeCsv01()
	{
		String in = "Hello\\nWorld";
		String out = escapeCsv(in);
		assertEquals("01", in, out);
		System.out.println(out);
	}

	@Test
	public void testEscapeCsv02()
	{
		String in = "Hello\\World";
		String out = escapeCsv(in);
		assertEquals("01", in, out);
		System.out.println(out);
	}

}
