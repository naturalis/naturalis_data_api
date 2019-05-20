package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

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

	/*
	 * Include constantScore
	 */
	@Test
	public void test_equals_02()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(false);

		assertFalse("01", q0.equals(q1));

		q1.setConstantScore(true);
		assertTrue("02", q0.equals(q1));
	}

	/*
	 * Include constantScore
	 */
	@Test
	public void test_hashCode_02()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(false);

		assertNotEquals("01", q0.hashCode(), q1.hashCode());

		q1.setConstantScore(true);
		assertEquals("02", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Include logicalOperator
	 */
	@Test
	public void test_equals_03()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.AND);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(null);

		/*
		 * null == AND !
		 */
		assertTrue("01", q0.equals(q1));

		q0.setLogicalOperator(LogicalOperator.OR);
		assertFalse("02", q0.equals(q1));

		q1.setLogicalOperator(LogicalOperator.OR);
		assertTrue("03", q0.equals(q1));
	}

	/*
	 * Include logicalOperator
	 */
	@Test
	public void test_hashCode_03()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.AND);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(null);

		/*
		 * null == AND !
		 */
		assertEquals("01", q0.hashCode(), q1.hashCode());

		q0.setLogicalOperator(LogicalOperator.OR);
		assertNotEquals("02", q0.hashCode(), q1.hashCode());

		q1.setLogicalOperator(LogicalOperator.OR);
		assertEquals("03", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Include from
	 */
	@Test
	public void test_equals_04()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(0);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(null);

		/*
		 * null == 0 !
		 */
		assertTrue("01", q0.equals(q1));

		q0.setFrom(1);
		assertFalse("02", q0.equals(q1));

		q1.setFrom(1);
		assertTrue("03", q0.equals(q1));
	}

	/*
	 * Include from
	 */
	@Test
	public void test_hashCode_04()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(0);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(null);

		/*
		 * null == 0 !
		 */
		assertEquals("01", q0.hashCode(), q1.hashCode());

		q0.setFrom(1);
		assertNotEquals("02", q0.hashCode(), q1.hashCode());

		q1.setFrom(1);
		assertEquals("03", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Include size
	 */
	@Test
	public void test_equals_05()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(null);

		/*
		 * null != 0 !
		 */
		assertFalse("01", q0.equals(q1));

		q1.setSize(67);
		assertFalse("02", q0.equals(q1));

		q1.setSize(55);
		assertTrue("03", q0.equals(q1));
	}

	/*
	 * Include size
	 */
	@Test
	public void test_hashCode_05()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(null);

		/*
		 * null != 0 !
		 */
		assertNotEquals("01", q0.hashCode(), q1.hashCode());

		q1.setSize(67);
		assertNotEquals("02", q0.hashCode(), q1.hashCode());

		q1.setSize(55);
		assertEquals("03", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Include fields
	 */
	@Test
	public void test_equals_06()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Collections.<Path> emptyList());

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(55);
		q1.setFields(null);

		/*
		 * null != empty !
		 */
		assertFalse("01", q0.equals(q1));

		q1.setFields(Arrays.asList(new Path("foo"), new Path("bar"), new Path("soap")));
		assertFalse("02", q0.equals(q1));

		q0.setFields(Arrays.asList(new Path("foo"), new Path("bar"), new Path("soap_02")));
		assertFalse("03", q0.equals(q1));

		q0.setFields(Arrays.asList(new Path("foo"), new Path("soap")));
		assertFalse("04", q0.equals(q1));

		q0.setFields(Arrays.asList(new Path("foo"), new Path("bar"), new Path("soap")));
		assertTrue("05", q0.equals(q1));
	}

	/*
	 * Include fields
	 */
	@Test
	public void test_hashCode_06()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Collections.<Path> emptyList());

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(55);
		q1.setFields(null);

		/*
		 * null != empty !
		 */
		assertNotEquals("01", q0.hashCode(), q1.hashCode());

		q1.setFields(Arrays.asList(new Path("foo"), new Path("bar"), new Path("soap")));
		assertNotEquals("02", q0.hashCode(), q1.hashCode());

		q0.setFields(Arrays.asList(new Path("foo"), new Path("bar"), new Path("soap_02")));
		assertNotEquals("03", q0.hashCode(), q1.hashCode());

		q0.setFields(Arrays.asList(new Path("foo"), new Path("soap")));
		assertNotEquals("04", q0.hashCode(), q1.hashCode());

		q0.setFields(Arrays.asList(new Path("foo"), new Path("bar"), new Path("soap")));
		assertEquals("05", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Include conditions
	 */
	@Test
	public void test_equals_07()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Arrays.asList(new Path("foo"), new Path("bar")));
		q0.setConditions(new ArrayList<QueryCondition>());

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(55);
		q1.setFields(Arrays.asList(new Path("foo"), new Path("bar")));
		q1.setConditions(null);

		/*
		 * null == empty !
		 */
		assertTrue("01", q0.equals(q1));

		q0.addCondition(new QueryCondition("foo", "=", "bar"));
		assertFalse("02", q0.equals(q1));

		q1.addCondition(new QueryCondition("foo", "=", "bar"));
		assertTrue("03", q0.equals(q1));

		q0.addCondition(new QueryCondition("foo2", "CONTAINS", "soap"));
		assertFalse("04", q0.equals(q1));

		q1.addCondition(new QueryCondition("foo2", "CONTAINS", "soap"));
		assertTrue("05", q0.equals(q1));
	}

	/*
	 * Include conditions
	 */
	@Test
	public void test_hashCode_07()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Arrays.asList(new Path("foo"), new Path("bar")));
		q0.setConditions(new ArrayList<QueryCondition>());

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(55);
		q1.setFields(Arrays.asList(new Path("foo"), new Path("bar")));
		q1.setConditions(null);

		/*
		 * null == empty !
		 */
		assertEquals("01", q0.hashCode(), q1.hashCode());

		q0.addCondition(new QueryCondition("foo", "=", "bar"));
		assertNotEquals("02", q0.hashCode(), q1.hashCode());

		q1.addCondition(new QueryCondition("foo", "=", "bar"));
		assertEquals("03", q0.hashCode(), q1.hashCode());

		q0.addCondition(new QueryCondition("foo2", "CONTAINS", "soap"));
		assertNotEquals("04", q0.hashCode(), q1.hashCode());

		q1.addCondition(new QueryCondition("foo2", "CONTAINS", "soap"));
		assertEquals("05", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Include sortFields
	 */
	@Test
	public void test_equals_08()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Arrays.asList(new Path("unitID"), new Path("kindOfUnit")));
		q0.addCondition(new QueryCondition("genus", "=", "Larus"));
		q0.setSortFields(new ArrayList<SortField>());

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(55);
		q1.setFields(Arrays.asList(new Path("unitID"), new Path("kindOfUnit")));
		q1.addCondition(new QueryCondition("genus", "=", "Larus"));
		q1.setSortFields(null);

		/*
		 * null == empty !
		 */
		assertTrue("01", q0.equals(q1));

		q1.sortBy("foo");
		assertFalse("02", q0.equals(q1));

		q0.sortBy("foo");
		assertTrue("03", q0.equals(q1));

		q1.sortBy("bar", SortOrder.DESC);
		assertFalse("04", q0.equals(q1));

		q0.sortBy("bar", SortOrder.DESC);
		assertTrue("04", q0.equals(q1));
	}

	/*
	 * Include sortFields
	 */
	@Test
	public void test_hashCode_08()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Arrays.asList(new Path("unitID"), new Path("kindOfUnit")));
		q0.addCondition(new QueryCondition("genus", "=", "Larus"));
		q0.setSortFields(new ArrayList<SortField>());

		QuerySpec q1 = new QuerySpec();
		q1.setConstantScore(true);
		q1.setLogicalOperator(LogicalOperator.OR);
		q1.setFrom(10);
		q1.setSize(55);
		q1.setFields(Arrays.asList(new Path("unitID"), new Path("kindOfUnit")));
		q1.addCondition(new QueryCondition("genus", "=", "Larus"));
		q1.setSortFields(null);

		/*
		 * null == empty !
		 */
		assertEquals("01", q0.hashCode(), q1.hashCode());

		q1.sortBy("foo");
		assertNotEquals("02", q0.hashCode(), q1.hashCode());

		q0.sortBy("foo");
		assertEquals("03", q0.hashCode(), q1.hashCode());

		q1.sortBy("bar", SortOrder.DESC);
		assertNotEquals("04", q0.hashCode(), q1.hashCode());

		q0.sortBy("bar", SortOrder.DESC);
		assertEquals("05", q0.hashCode(), q1.hashCode());
	}

	/*
	 * Check copy constructor via equals method
	 */
	@Test
	public void test_equals_09()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Arrays.asList(new Path("unitID"), new Path("kindOfUnit")));
		q0.addCondition(new QueryCondition("genus", "=", "Larus"));
		q0.sortBy("recordBasis");

		QuerySpec q1 = new QuerySpec(q0);
		assertTrue("01", q0.equals(q1));
	}

	/*
	 * Check copy constructor via hashCode method
	 */
	@Test
	public void test_hashCode_09()
	{
		QuerySpec q0 = new QuerySpec();
		q0.setConstantScore(true);
		q0.setLogicalOperator(LogicalOperator.OR);
		q0.setFrom(10);
		q0.setSize(55);
		q0.setFields(Arrays.asList(new Path("unitID"), new Path("kindOfUnit")));
		q0.addCondition(new QueryCondition("genus", "=", "Larus"));
		q0.sortBy("recordBasis");

		QuerySpec q1 = new QuerySpec(q0);
		assertEquals("01", q0.hashCode(), q1.hashCode());
	}

}
