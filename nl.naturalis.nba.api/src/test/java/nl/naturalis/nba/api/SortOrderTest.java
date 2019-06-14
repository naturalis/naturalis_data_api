package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.SortOrder.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SortOrderTest {
	
	@Test
	public void test_parse_01()
	{
		assertEquals("01", ASC, parse(null));
		assertEquals("02", ASC, parse(""));
	}

	@Test
	public void test_parse_02()
	{
		assertEquals("01", ASC, parse("asc"));
		assertEquals("01", ASC, parse("ASC"));
	}

	@Test
	public void test_parse_03()
	{
		assertEquals("01", DESC, parse("desc"));
		assertEquals("01", DESC, parse("DESC"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_parse_04()
	{
		parse("wrong");
	}

}
