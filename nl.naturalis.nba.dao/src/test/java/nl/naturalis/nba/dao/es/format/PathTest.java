package nl.naturalis.nba.dao.es.format;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.DocumentType;

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
	public void testGetPathElements()
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		String[] elements = new String[] { "identifications", "0", "defaultClassification",
				"kingdom" };
		assertTrue("01", Arrays.deepEquals(path.getElements(), elements));
	}

	@Test
	public void testGetPurePathElements_01()
	{
		Path path = new Path("identifications.defaultClassification.kingdom");
		String[] elements = new String[] { "identifications", "defaultClassification", "kingdom" };
		assertTrue("01", Arrays.deepEquals(path.getPureElements(), elements));
	}

	@Test
	public void testGetPurePathElements_02()
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		String[] elements = new String[] { "identifications", "defaultClassification", "kingdom" };
		assertTrue("01", Arrays.deepEquals(path.getPureElements(), elements));
	}

	@Test
	public void testValidate_01() throws EntityConfigurationException
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		path.validate(DocumentType.SPECIMEN);
		// Should NOT get a EntityConfigurationException
	}

	@Test(expected = EntityConfigurationException.class)
	public void testValidate_02() throws EntityConfigurationException
	{
		Path path = new Path("identifications.defaultClassification.kingdom");
		path.validate(DocumentType.SPECIMEN);
	}

	@Test(expected = EntityConfigurationException.class)
	public void testValidate_03() throws EntityConfigurationException
	{
		Path path = new Path("identifications.defaultClassification.0.kingdom");
		path.validate(DocumentType.SPECIMEN);
	}

	@Test(expected = EntityConfigurationException.class)
	public void testValidate_04() throws EntityConfigurationException
	{
		Path path = new Path("identifications.0.defaultClassification.kingdom");
		path.validate(DocumentType.TAXON);
	}
}
