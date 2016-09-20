package nl.naturalis.nba.dao.es.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("static-method")
public class InValuesBuilderTest {

	@Test
	public void testWithNull()
	{
		InValuesBuilder ivb = new InValuesBuilder(null);
		assertTrue("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 0, ivb.getValues().size());
	}

	@Test
	public void testWithSingularValue()
	{
		InValuesBuilder ivb = new InValuesBuilder(100);
		assertFalse("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 1, ivb.getValues().size());
	}

	@Test
	public void testWithArrayNoNullValues()
	{
		Object value = new String[] { "1", "2", "3", "4" };
		InValuesBuilder ivb = new InValuesBuilder(value);
		assertFalse("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 4, ivb.getValues().size());
	}

	@Test
	public void testWithArrayWithNullValues()
	{
		Object value = new String[] { "1", "2", null, "3", null, "4" };
		InValuesBuilder ivb = new InValuesBuilder(value);
		assertTrue("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 4, ivb.getValues().size());
	}

	@Test
	public void testWithArrayOnlyNullValues()
	{
		Object value = new String[] { null, null };
		InValuesBuilder ivb = new InValuesBuilder(value);
		assertTrue("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 0, ivb.getValues().size());
	}

	@Test
	public void testWithCollectionNoNullValues()
	{
		Object value = Arrays.asList(1, 2, 3, 4);
		InValuesBuilder ivb = new InValuesBuilder(value);
		assertFalse("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 4, ivb.getValues().size());
	}

	@Test
	public void testWithCollectionWithNullValues()
	{
		ArrayList<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add(null);
		list.add("3");
		list.add(null);
		list.add("4");
		InValuesBuilder ivb = new InValuesBuilder(list);
		assertTrue("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 4, ivb.getValues().size());
	}

	@Test
	public void testWithCollectionOnlyNullValues()
	{
		ArrayList<String> list = new ArrayList<>();
		list.add(null);
		list.add(null);
		InValuesBuilder ivb = new InValuesBuilder(list);
		assertTrue("01", ivb.containsNull());
		assertNotNull("02", ivb.getValues());
		assertEquals("03", 0, ivb.getValues().size());
	}

}
