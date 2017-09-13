package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.junit.Test;

@SuppressWarnings("static-method")
public class QueryConditionTest {

	@Test
	public void test_equals_01()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		QueryCondition c1 = new QueryCondition("foo", EQUALS, "bar");
		assertEquals("01", c0, c1);
	}

	@Test
	public void test_hashCode_01()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		QueryCondition c1 = new QueryCondition("foo", EQUALS, "bar");
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Make sure we identify the ALWAYS TRUE condition correctly
	 */
	@Test
	public void test_equals_02()
	{
		QueryCondition c0 = new QueryCondition(true);
		QueryCondition c1 = new QueryCondition(new Boolean(true));
		assertEquals("01", c0, c1);
	}

	/*
	 * Make sure we identify the ALWAYS TRUE condition correctly
	 */
	@Test
	public void test_hashCode_02()
	{
		QueryCondition c0 = new QueryCondition(true);
		QueryCondition c1 = new QueryCondition(new Boolean(true));
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include NOT operator
	 */
	@Test
	public void test_equals_03()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include NOT operator
	 */
	@Test
	public void test_hashCode_03()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include constantScore
	 */
	@Test
	public void test_equals_04()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.setConstantScore(false);
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		c1.setConstantScore(true);
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include constantScore
	 */
	@Test
	public void test_hashCode_04()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.setConstantScore(false);
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		c1.setConstantScore(true);
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include boost
	 */
	@Test
	public void test_equals_05()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.setConstantScore(false);
		c0.setBoost(1.7F);
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		c1.setConstantScore(true);
		c1.setBoost(1.8F);
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include boost
	 */
	@Test
	public void test_hashCode_05()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.setConstantScore(false);
		c0.setBoost(1.7F);
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		c1.setConstantScore(true);
		c1.setBoost(1.8F);
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_equals_06()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.setConstantScore(false);
		c0.setBoost(1.7F);
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		c1.setConstantScore(true);
		c1.setBoost(1.8F);
		c1.and("foo2", "CONTAINS", "bar");
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_hashCode_06()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.setConstantScore(false);
		c0.setBoost(1.7F);
		QueryCondition c1 = new QueryCondition(NOT, new Path("foo"), EQUALS, "bar");
		c1.setConstantScore(true);
		c1.setBoost(1.8F);
		c1.and("foo2", "CONTAINS", "bar");
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_equals_07()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		assertEquals("01", c0, c1);
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_hashCode_07()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_equals_08()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_hashCode_08()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_equals_09()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		assertEquals("01", c0, c1);
	}

	/*
	 * Include AND conditions
	 */
	@Test
	public void test_hashCode_09()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include nested AND conditions
	 */
	@Test
	public void test_equals_10()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 1, 3, 7, 9 }));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 2, 4, 6, 8 }));
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include nested AND conditions
	 */
	@Test
	public void test_hashCode_10()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 1, 3, 7, 9 }));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 2, 4, 6, 8 }));
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include nested AND conditions
	 */
	@Test
	public void test_equals_11()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 1, 3, 7, 9 }));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 1, 3, 7, 9 }));
		assertEquals("01", c0, c1);
	}

	/*
	 * Include nested AND conditions
	 */
	@Test
	public void test_hashCode_11()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 1, 3, 7, 9 }));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and(new QueryCondition("foo2", "CONTAINS", "bar").and("foo3", "<", 5).and("foo4", "IN",
				new int[] { 1, 3, 7, 9 }));
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_equals_12()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or("foo4", "<=", 2.0F);
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_hashCode_12()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or("foo4", "<=", 2.0F);
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_equals_13()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or("foo4", "<=", 2.0F);
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		c1.or("foo4", "<=", 2.0F);
		assertEquals("01", c0, c1);
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_hashCode_13()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or("foo4", "<=", 2.0F);
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		c1.or("foo4", "<=", 2.0F);
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_equals_14()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or(new QueryCondition("foo4", "<=", 2.0F).and(new QueryCondition("foo5", "=", 12F)));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		c1.or("foo4", "<=", 2.0F);
		assertNotEquals("01", c0, c1);
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_hashCode_14()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or(new QueryCondition("foo4", "<=", 2.0F).and(new QueryCondition("foo5", "=", 12F)));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		c1.or("foo4", "<=", 2.0F);
		assertNotEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_equals_15()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or(new QueryCondition("foo4", "<=", 2.0F)
				.and(new QueryCondition("foo5", "=", 12F).or("foo6", ">", 8)));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		c1.or(new QueryCondition("foo4", "<=", 2.0F)
				.and(new QueryCondition("foo5", "=", 12F).or("foo6", ">", 8)));
		assertEquals("01", c0, c1);
	}

	/*
	 * Include OR conditions
	 */
	@Test
	public void test_hashCode_15()
	{
		QueryCondition c0 = new QueryCondition("foo", "=", "bar");
		c0.and("foo2", "CONTAINS", "bar");
		c0.and("foo3", "NOT_EQUALS", null);
		c0.or(new QueryCondition("foo4", "<=", 2.0F)
				.and(new QueryCondition("foo5", "=", 12F).or("foo6", ">", 8)));
		QueryCondition c1 = new QueryCondition("foo", "=", "bar");
		c1.and("foo2", "CONTAINS", "bar");
		c1.and("foo3", "NOT_EQUALS", null);
		c1.or(new QueryCondition("foo4", "<=", 2.0F)
				.and(new QueryCondition("foo5", "=", 12F).or("foo6", ">", 8)));
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

	/*
	 * Make sure empty lists are treated as {@code null}.
	 */
	@Test
	public void test_equals_16()
	{
		QueryCondition c0 = new QueryCondition("foo.test", "BETWEEN", new short[] { 1, 100 });
		c0.setAnd(null);
		c0.setOr(null);
		QueryCondition c1 = new QueryCondition("foo.test", "BETWEEN", new short[] { 1, 100 });
		c1.setAnd(new ArrayList<QueryCondition>());
		c1.setOr(new ArrayList<QueryCondition>());
		assertEquals("01", c0, c1);
	}

	/*
	 * Make sure empty lists are treated as {@code null}.
	 */
	@Test
	public void test_hashCode_16()
	{
		QueryCondition c0 = new QueryCondition("foo.test", "BETWEEN", new short[] { 1, 100 });
		c0.setAnd(null);
		c0.setOr(null);
		QueryCondition c1 = new QueryCondition("foo.test", "BETWEEN", new short[] { 1, 100 });
		c1.setAnd(new ArrayList<QueryCondition>());
		c1.setOr(new ArrayList<QueryCondition>());
		assertEquals("01", c0.hashCode(), c1.hashCode());
	}

}
