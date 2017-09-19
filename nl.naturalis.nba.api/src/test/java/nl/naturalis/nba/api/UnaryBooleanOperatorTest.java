package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

@SuppressWarnings("static-method")
public class UnaryBooleanOperatorTest {

	@Test
	public void test_parse_01()
	{
		assertNull("01", UnaryBooleanOperator.parse(null));
		assertNull("01", UnaryBooleanOperator.parse(""));
	}
	
	@Test
	public void test_parse_02()
	{
		assertEquals("01", UnaryBooleanOperator.NOT, UnaryBooleanOperator.parse("NOT"));
		assertEquals("01", UnaryBooleanOperator.NOT, UnaryBooleanOperator.parse("not"));
		assertEquals("01", UnaryBooleanOperator.NOT, UnaryBooleanOperator.parse("!"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_parse_03()
	{
		UnaryBooleanOperator.parse("wrong");
	}
}
