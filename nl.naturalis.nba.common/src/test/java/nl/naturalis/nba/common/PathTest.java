package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.test.TestPerson;

@SuppressWarnings("static-method")
public class PathTest {

	@Test
	public void testGetPath()
	{
		String[] elements = new String[] { "identifications", "0", "defaultClassification",
				"kingdom" };
		Path path = new Path(elements);
		assertEquals("01", "identifications.0.defaultClassification.kingdom", path.getPath());
	}

	@Test
	public void testGetPurePath()
	{
		String[] elements = new String[] { "identifications", "0", "defaultClassification",
				"kingdom" };
		Path path = new Path(elements);
		assertEquals("01", "identifications.defaultClassification.kingdom", path.getPurePath());
	}

	@Test
	public void testGetElements()
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		String[] elements = new String[] { "identifications", "0", "defaultClassification",
				"kingdom" };
		assertTrue("01", Arrays.deepEquals(path.getElements(), elements));
	}

	@Test
	public void testGetPureElements_01()
	{
		Path path = new Path("identifications.defaultClassification.kingdom");
		String[] elements = new String[] { "identifications", "defaultClassification", "kingdom" };
		assertTrue("01", Arrays.deepEquals(path.getPureElements(), elements));
	}

	@Test
	public void testGetPureElements_02()
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		String[] elements = new String[] { "identifications", "defaultClassification", "kingdom" };
		assertTrue("01", Arrays.deepEquals(path.getPureElements(), elements));
	}

	@Test
	public void testValidate_01() throws InvalidPathException
	{
		// Happy flow, no exception shouild be thrown
		Path path = new Path("pets.0.name");
		path.validate(MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_02() throws InvalidPathException
	{
		// Missing array index
		Path path = new Path("pets.colors.0");
		path.validate(MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_03() throws InvalidPathException
	{
		// Illegal array index
		Path path = new Path("pets.0.name.0");
		path.validate(MappingFactory.getMapping(TestPerson.class));
	}

	public void testValidate_04() throws InvalidPathException
	{
		// Happy flow, no exception shouild be thrown
		Path path = new Path("pets.2.colors.3");
		path.validate(MappingFactory.getMapping(TestPerson.class));
	}
}
