package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("static-method")
public class ComparisonOperatorTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testParse_01()
	{
		ComparisonOperator.parse(null);
	}
	
	@Test
	public void testParse_02()
	{
		assertEquals("01", ComparisonOperator.EQUALS, ComparisonOperator.parse("EQUALS"));
		assertEquals("02", ComparisonOperator.NOT_STARTS_WITH_IC, ComparisonOperator.parse("NOT_STARTS_WITH_IC"));
	}

	@Test
	public void testParse_03()
	{
		assertEquals("01", ComparisonOperator.EQUALS, ComparisonOperator.parse("equals"));
		assertEquals("02", ComparisonOperator.NOT_STARTS_WITH_IC, ComparisonOperator.parse("not_starts_with_ic"));
	}
	
	@Test
	public void testParse_04()
	{
		assertEquals("01", ComparisonOperator.EQUALS, ComparisonOperator.parse("="));
		assertEquals("02", ComparisonOperator.NOT_EQUALS, ComparisonOperator.parse("!="));
		assertEquals("03", ComparisonOperator.LT, ComparisonOperator.parse("<"));
		assertEquals("04", ComparisonOperator.LTE, ComparisonOperator.parse("<="));
		assertEquals("05", ComparisonOperator.GT, ComparisonOperator.parse(">"));
		assertEquals("06", ComparisonOperator.GTE, ComparisonOperator.parse(">="));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParse_05()
	{
		ComparisonOperator.parse("NOT_EXISTING_OPERATOR");
	}

}
