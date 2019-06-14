package nl.naturalis.nba.common.es.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.common.mock.TestPerson;

public class MappingInfoTest {

	private static MappingInfo<TestPerson> personInfo;

	@BeforeClass
	public static void setup()
	{
		personInfo = new MappingInfo<>(MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_01() throws NoSuchFieldException
	{
		personInfo.getField("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_02() throws NoSuchFieldException
	{
		personInfo.getField("bla.bla");
	}

	@Test
	public void testGetField_04() throws NoSuchFieldException
	{
		ESField f = personInfo.getField("pets");
		assertNotNull("01", f);
		assertTrue("02", f instanceof ComplexField);
	}

	@Test
	public void testGetField_05() throws NoSuchFieldException
	{
		ESField f = personInfo.getField("lastName");
		assertNotNull("01", f);
		assertTrue("02", f instanceof KeywordField);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_06() throws NoSuchFieldException
	{
		// Tests that you can get reference "multi-fields"
		personInfo.getField("lastName.analyzed");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_01() throws NoSuchFieldException
	{
		personInfo.getType("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_02() throws NoSuchFieldException
	{
		personInfo.getType("bla.bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_03() throws NoSuchFieldException
	{
		personInfo.getType("pets.bla");
	}

	@Test
	public void testGetType_04() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("pets");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_05() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("addressBook.country");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_06() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("hobbies");
		assertEquals("01", ESDataType.KEYWORD, type);
	}

	@Test
	public void testGetType_07() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("numChildren");
		assertEquals("01", ESDataType.INTEGER, type);
	}

	@Test
	public void testGetType_08() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("smoker");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_09() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("addressBook");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_10() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("height");
		assertEquals("01", ESDataType.FLOAT, type);
	}

	@Test
	public void testGetType_11() throws NoSuchFieldException
	{
		ESDataType type = personInfo.getType("birthDate");
		assertEquals("01", ESDataType.DATE, type);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_01() throws NoSuchFieldException
	{
		List<ComplexField> ancestors = personInfo.getAncestors("bla");
		assertEquals("01", 2, ancestors.size());
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_02() throws NoSuchFieldException
	{
		List<ComplexField> ancestors = personInfo.getAncestors("pets.bla");
		assertEquals("01", 2, ancestors.size());
	}

	@Test
	public void testGetAncestors_03() throws NoSuchFieldException
	{
		String path = "addressBook.country.name";
		List<ComplexField> ancestors = personInfo.getAncestors(path);
		assertEquals("01", 2, ancestors.size());
	}

	@Test
	public void testGetNestedPath_01() throws NoSuchFieldException
	{
		String path = "addressBook.country.name";
		String nested = personInfo.getNestedPath(path);
		assertEquals("01", "addressBook", nested);
	}

	@Test
	public void testIsOrDescendsFromArray_01() throws NoSuchFieldException
	{
		assertFalse("01", personInfo.isOrDescendsFromArray("firstName"));
	}

	@Test
	public void testIsOrDescendsFromArray_02() throws NoSuchFieldException
	{
		assertTrue("01", personInfo.isOrDescendsFromArray("luckyNumbers"));
	}

	@Test
	public void testIsOrDescendsFromArray_03() throws NoSuchFieldException
	{
		assertFalse("01", personInfo.isOrDescendsFromArray("address.street"));
	}

	@Test
	public void testIsOrDescendsFromArray_04() throws NoSuchFieldException
	{
		assertTrue("01", personInfo.isOrDescendsFromArray("addressBook.street"));
	}
}
