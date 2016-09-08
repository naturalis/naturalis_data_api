package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.test.TestPerson;

@SuppressWarnings("static-method")
public class PathTest {

	@Test
	public void testGetPathString()
	{
		String[] elements = new String[] { "identifications", "0", "defaultClassification",
				"kingdom" };
		Path path = new Path(elements);
		assertEquals("01", "identifications.0.defaultClassification.kingdom", path.getPathString());
	}

	@Test
	public void testGetPurePath()
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		assertEquals("01", "identifications.defaultClassification.kingdom",
				path.getPurePath().toString());
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
