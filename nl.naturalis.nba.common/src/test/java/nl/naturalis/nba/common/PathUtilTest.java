package nl.naturalis.nba.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.mock.TestPerson;

public class PathUtilTest {

	@Test
	public void testValidate_01() throws InvalidPathException
	{
		// Happy flow, no exception shouild be thrown
		Path path = new Path("pets.0.name");
		PathUtil.validate(path, MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_02() throws InvalidPathException
	{
		// Missing array index
		Path path = new Path("pets.colors.0");
		PathUtil.validate(path, MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_03() throws InvalidPathException
	{
		// Illegal array index
		Path path = new Path("pets.0.name.0");
		PathUtil.validate(path, MappingFactory.getMapping(TestPerson.class));
	}

	@Test
	public void testValidate_04() throws InvalidPathException
	{
		// Happy flow, no exception shouild be thrown
		Path path = new Path("pets.2.colors.3");
		PathUtil.validate(path, MappingFactory.getMapping(TestPerson.class));
	}

	@Test
	public void testIsPrimitive_01() throws InvalidPathException
	{
		Path path = new Path("pets.0.name");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isPrimitive(path, mapping));
	}

	@Test
	public void testIsPrimitive_02() throws InvalidPathException
	{
		Path path = new Path("pets.name");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isPrimitive(path, mapping));
	}

	@Test
	public void testIsPrimitive_03() throws InvalidPathException
	{
		Path path = new Path("luckyNumbers");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isPrimitive(path, mapping));
	}

	@Test
	public void testIsPrimitive_04() throws InvalidPathException
	{
		Path path = new Path("hobbies");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isPrimitive(path, mapping));
	}

	@Test
	public void testIsPrimitive_05() throws InvalidPathException
	{
		Path path = new Path("smoker");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isPrimitive(path, mapping));
	}

	@Test
	public void testIsPrimitive_06() throws InvalidPathException
	{
		Path path = new Path("addressBook");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertFalse("01", PathUtil.isPrimitive(path, mapping));
	}

	@Test
	public void testIsArray_01() throws InvalidPathException
	{
		Path path = new Path("pets.0.name");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertFalse("01", PathUtil.isArray(path, mapping));
	}

	@Test
	public void testIsArray_02() throws InvalidPathException
	{
		Path path = new Path("pets.name");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertFalse("01", PathUtil.isArray(path, mapping));
	}

	@Test
	public void testIsArray_03() throws InvalidPathException
	{
		Path path = new Path("luckyNumbers");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isArray(path, mapping));
	}

	@Test
	public void testIsArray_04() throws InvalidPathException
	{
		Path path = new Path("hobbies");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isArray(path, mapping));
	}

	@Test
	public void testIsArray_05() throws InvalidPathException
	{
		Path path = new Path("smoker");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertFalse("01", PathUtil.isArray(path, mapping));
	}

	@Test
	public void testIsArray_06() throws InvalidPathException
	{
		Path path = new Path("addressBook");
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		assertTrue("01", PathUtil.isArray(path, mapping));
	}

}
