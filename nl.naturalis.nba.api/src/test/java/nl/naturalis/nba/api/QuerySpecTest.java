package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


@SuppressWarnings("static-method")
public class QuerySpecTest {


	@Test
	public void test_equals_01()
	{
		QuerySpec q0 = new QuerySpec();
		QuerySpec q1 = new QuerySpec();
		assertTrue("01", q0.equals(q1));
	}

	@Test
	public void test_hashCode_01()
	{
		QuerySpec q0 = new QuerySpec();
		QuerySpec q1 = new QuerySpec();
		assertEquals("01", q0.hashCode(), q1.hashCode());
	}

}
