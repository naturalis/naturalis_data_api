package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.naturalis.nba.api.Path;

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
		assertEquals("01", 4, path.countElements());
		path = path.getPurePath();
		assertEquals("02", 3, path.countElements());
		assertEquals("03", "identifications.defaultClassification.kingdom", path.toString());
	}

	@Test
	public void testAppend_01()
	{
		Path path = new Path("identifications.0");
		path = path.append("defaultClassification.kingdom");
		assertEquals("01", 4, path.countElements());
		assertEquals("02", "identifications", path.getElement(0));
		assertEquals("03", "0", path.getElement(1));
		assertEquals("04", "defaultClassification", path.getElement(2));
		assertEquals("05", "kingdom", path.getElement(3));
	}

	@Test
	public void testShift_01()
	{
		Path path0 = new Path("identifications.0.defaultClassification.kingdom");
		Path path1 = new Path("0.defaultClassification.kingdom");
		assertEquals("01", path1, path0.shift());
		path0 = path1;
		path1 = new Path("defaultClassification.kingdom");
		assertEquals("02", path1, path0.shift());
		path0 = path1;
		path1 = new Path("kingdom");
	}

}
