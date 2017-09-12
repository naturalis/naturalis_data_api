package nl.naturalis.nba.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("static-method")
public class ApiUtilTest {

	/*
	 * Tests equals method with List arguments
	 */
	@Test
	public void test_equals_00()
	{
		List<Object> a = new ArrayList<Object>();
		List<Object> b = null;
		assertTrue("01", ApiUtil.equals(a, b));
	}

	/*
	 * Tests equals method with List arguments
	 */
	@Test
	public void test_equals_01()
	{
		List<Object> a = null;
		List<Object> b = null;
		assertTrue("01", ApiUtil.equals(a, b));
	}

	/*
	 * Tests equals method with List arguments
	 */
	@Test
	public void test_equals_02()
	{
		List<String> a = Arrays.asList("John");
		List<String> b = Arrays.asList("John");
		assertTrue("01", ApiUtil.equals(a, b));
	}

	/*
	 * Tests equals method with List arguments
	 */
	@Test
	public void test_equals_03()
	{
		List<String> a = Arrays.asList("John");
		List<String> b = Arrays.asList("John", "Smith");
		assertFalse("01", ApiUtil.equals(a, b));
	}

	/*
	 * Test equals method with arrays
	 */
	@Test
	public void test_equals_10()
	{
		Object[] a = new Object[0];
		Object[] b = null;
		assertTrue("01", ApiUtil.equals(a, b));
	}

	/*
	 * Test equals method with arrays
	 */
	@Test
	public void test_equals_11()
	{
		Object[] a = null;
		Object[] b = null;
		assertTrue("01", ApiUtil.equals(a, b));
	}

	/*
	 * Test equals method with arrays
	 */
	@Test
	public void test_equals_12()
	{
		String[] a = new String[] { "John" };
		String[] b = new String[] { "John" };
		assertTrue("01", ApiUtil.equals(a, b));
	}

	/*
	 * Test equals method with arrays
	 */
	@Test
	public void test_equals_13()
	{
		String[] a = new String[] { "John" };
		String[] b = new String[] { "John", "Smith" };
		assertFalse("01", ApiUtil.equals(a, b));
	}

	/*
	 * Test equals method with default value
	 */
	@Test
	public void test_equals_20()
	{
		assertTrue("01", ApiUtil.equals("John", null, "John"));
	}

	/*
	 * Test equals method with default value
	 */
	@Test
	public void test_equals_21()
	{
		assertTrue("01", ApiUtil.equals(null, "John", "John"));
	}

	/*
	 * Test equals method with default value
	 */
	@Test
	public void test_equals_22()
	{
		assertTrue("01", ApiUtil.equals(null, null, "John"));
	}

	/*
	 * Test equals method with default value
	 */
	@Test
	public void test_equals_23()
	{
		assertFalse("01", ApiUtil.equals(null, "Jim", "John"));
	}

	/*
	 * Test equals method with default value
	 */
	@Test
	public void test_equals_24()
	{
		assertTrue("01", ApiUtil.equals("Jim", "Jim", "John"));
	}

	/*
	 * Tests hashCode method with List arguments
	 */
	@Test
	public void test_hashCode_00()
	{
		List<Object> a = new ArrayList<Object>();
		List<Object> b = null;
		assertEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Tests hashCode method with List arguments
	 */
	@Test
	public void test_hashCode_01()
	{
		List<Object> a = null;
		List<Object> b = null;
		assertEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Tests hashCode method with List arguments
	 */
	@Test
	public void test_hashCode_02()
	{
		List<String> a = Arrays.asList("John");
		List<String> b = Arrays.asList("John");
		assertEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Tests hashCode method with List arguments
	 */
	@Test
	public void test_hashCode_03()
	{
		List<String> a = Arrays.asList("John");
		List<String> b = Arrays.asList("John", "Smith");
		assertNotEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Test hashCode method with arrays
	 */
	@Test
	public void test_hashCode_10()
	{
		Object[] a = new Object[0];
		Object[] b = null;
		assertEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Test hashCode method with arrays
	 */
	@Test
	public void test_hashCode_11()
	{
		Object[] a = null;
		Object[] b = null;
		assertEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Test hashCode method with arrays
	 */
	@Test
	public void test_hashCode_12()
	{
		String[] a = new String[] { "John" };
		String[] b = new String[] { "John" };
		assertEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Test hashCode method with arrays
	 */
	@Test
	public void test_hashCode_13()
	{
		String[] a = new String[] { "John" };
		String[] b = new String[] { "John", "Smith" };
		assertNotEquals("01", ApiUtil.hashCode(a), ApiUtil.hashCode(b));
	}

	/*
	 * Test hashCode method with default value
	 */
	@Test
	public void test_hashCode_20()
	{
		assertEquals("01", ApiUtil.hashCode(null, "John"), "John".hashCode());
	}

	/*
	 * Test hashCode method with default value
	 */
	@Test
	public void test_hashCode_21()
	{
		assertEquals("01", ApiUtil.hashCode("John", "John"), "John".hashCode());
	}

	/*
	 * Test hashCode method with default value
	 */
	@Test
	public void test_hashCode_22()
	{
		assertEquals("01", ApiUtil.hashCode("Jim", "John"), "Jim".hashCode());
	}

}
