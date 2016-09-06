package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.MappingFactory;

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
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		path.validate(MappingFactory.getMapping(Specimen.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_02() throws InvalidPathException 
	{
		// Missing array indix
		Path path = new Path("identifications.defaultClassification.kingdom");
		path.validate(MappingFactory.getMapping(Specimen.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_03() throws InvalidPathException
	{
		// Illegal array index
		Path path = new Path("identifications.defaultClassification.0.kingdom");
		path.validate(MappingFactory.getMapping(Specimen.class));
	}

	@Test(expected = InvalidPathException.class)
	public void testValidate_04() throws InvalidPathException
	{
		// Happy flow, no exception shouild be thrown
		Path path = new Path("identifications.0.systemClassification.1.rank");
		path.validate(MappingFactory.getMapping(Specimen.class));
	}
}
