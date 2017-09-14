package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.SortOrder.ASC;
import static nl.naturalis.nba.api.SortOrder.DESC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("static-method")
public class SortFieldTest {

	@Test
	public void test_equals_01()
	{
		SortField sf0 = new SortField("foo");
		SortField sf1 = new SortField("foo");
		assertTrue("01", sf0.equals(sf1));
	}

	@Test
	public void test_hashCode_01()
	{
		SortField sf0 = new SortField("foo");
		SortField sf1 = new SortField("foo");
		assertEquals("01", sf0.hashCode(), sf1.hashCode());
	}

	@Test
	public void test_equals_02()
	{
		SortField sf0 = new SortField("foo");
		SortField sf1 = new SortField("foo.bar.soap");
		assertFalse("01", sf0.equals(sf1));
	}

	@Test
	public void test_hashCode_02()
	{
		SortField sf0 = new SortField("foo");
		SortField sf1 = new SortField("foo.bar.soap");
		assertNotEquals("01", sf0.hashCode(), sf1.hashCode());
	}

	/*
	 * Ensure null sortOrder is taken to be sortOrder ASC
	 */
	@Test
	public void test_equals_03()
	{
		SortField sf0 = new SortField("foo.bar.soap", ASC);
		SortField sf1 = new SortField("foo.bar.soap", null);
		assertTrue("01", sf0.equals(sf1));
	}

	/*
	 * Ensure null sortOrder is taken to be sortOrder ASC
	 */
	@Test
	public void test_hashCode_03()
	{
		SortField sf0 = new SortField("foo.bar.soap", ASC);
		SortField sf1 = new SortField("foo.bar.soap", null);
		assertEquals("01", sf0.hashCode(), sf1.hashCode());
	}

	/*
	 * Ensure null sortOrder is taken to be sortOrder ASC
	 */
	@Test
	public void test_equals_04()
	{
		SortField sf0 = new SortField("foo.bar.soap", DESC);
		SortField sf1 = new SortField("foo.bar.soap", null);
		assertFalse("01", sf0.equals(sf1));
	}

	/*
	 * Ensure null sortOrder is taken to be sortOrder ASC
	 */
	@Test
	public void test_hashCode_04()
	{
		SortField sf0 = new SortField("foo.bar.soap", DESC);
		SortField sf1 = new SortField("foo.bar.soap", null);
		assertNotEquals("01", sf0.hashCode(), sf1.hashCode());
	}

	@Test
	public void test_equals_05()
	{
		SortField sf0 = new SortField("foo.bar.soap", DESC);
		SortField sf1 = new SortField("foo.bar.soap", DESC);
		assertTrue("01", sf0.equals(sf1));
	}

	@Test
	public void test_hashCode_05()
	{
		SortField sf0 = new SortField("foo.bar.soap", DESC);
		SortField sf1 = new SortField("foo.bar.soap", DESC);
		assertEquals("01", sf0.hashCode(), sf1.hashCode());
	}


}
