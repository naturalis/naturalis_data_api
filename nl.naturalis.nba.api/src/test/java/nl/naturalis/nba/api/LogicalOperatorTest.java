package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class LogicalOperatorTest {

	@Test
	public void parse_01()
	{
		assertNull("01", LogicalOperator.parse(null));
		assertNull("02", LogicalOperator.parse(""));
	}
	
	@Test
	public void parse_02()
	{
		assertEquals("01", LogicalOperator.AND, LogicalOperator.parse("AND"));
		assertEquals("02", LogicalOperator.AND, LogicalOperator.parse("and"));
		assertEquals("03", LogicalOperator.AND, LogicalOperator.parse("&&"));
	}
	
	@Test
	public void parse_03()
	{
		assertEquals("01", LogicalOperator.OR, LogicalOperator.parse("OR"));
		assertEquals("02", LogicalOperator.OR, LogicalOperator.parse("or"));
		assertEquals("03", LogicalOperator.OR, LogicalOperator.parse("||"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParse_01()
	{
		LogicalOperator.parse("not_good");
	}
	
}
