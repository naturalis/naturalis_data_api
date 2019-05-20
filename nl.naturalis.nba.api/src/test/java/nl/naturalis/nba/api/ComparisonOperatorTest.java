package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ComparisonOperatorTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testParse_01()
	{
		parse(null);
	}
	
	@Test
	public void testParse_02()
	{
		assertEquals("01", EQUALS, parse("EQUALS"));
		assertEquals("02", NOT_STARTS_WITH_IC, parse("NOT_STARTS_WITH_IC"));
	}

	@Test
	public void testParse_03()
	{
		assertEquals("01", EQUALS, parse("equals"));
		assertEquals("02", NOT_STARTS_WITH_IC, parse("not_starts_with_ic"));
	}
	
	@Test
	public void testParse_04()
	{
		assertEquals("01", EQUALS, parse("="));
		assertEquals("02", NOT_EQUALS, parse("!="));
		assertEquals("03", LT, parse("<"));
		assertEquals("04", LTE, parse("<="));
		assertEquals("05", GT, parse(">"));
		assertEquals("06", GTE, parse(">="));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParse_05()
	{
		parse("NOT_EXISTING_OPERATOR");
	}

}
