package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("static-method")
public class PathTest {

	@Test
	public void testToString()
	{
		String[] elements = new String[] { "identifications", "0", "defaultClassification",
				"kingdom" };
		Path path = new Path(elements);
		assertEquals("01", "identifications.0.defaultClassification.kingdom", path.toString());
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
	
	@Test
	public void testReplace_01()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		Path path1 = new Path("identifications.defaultClassification.phylum");
		assertEquals("01", path1, path0.replace(2, "phylum"));
		Path path2 = new Path("test.defaultClassification.phylum");
		assertEquals("02", path2, path1.replace(0, "test"));
	}
	
	@Test
	public void testCompareTo_01()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		Path path1 = new Path("identifications.defaultClassification.kingdom");
		Path path2 = new Path("identifications.defaultClassification.phylum");
		assertTrue("01", path0.compareTo(path1) == 0);
		assertFalse("02", path0.compareTo(path2) == 0);
	}
	
	@Test
	public void testElement_01()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		assertEquals("01", new Path("identifications"), path0.element(0));
		assertEquals("02", new Path("defaultClassification"), path0.element(1));
		assertEquals("03", new Path("kingdom"), path0.element(2));
	}
	
	@Test
	public void testPath_01()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		Path path1 = new Path("identifications.defaultClassification.kingdom");
		assertFalse("01", (path0 == path1));
		assertEquals("02", path1, path0);
	}
	
	@Test
	public void testPath_02()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		Path path1 = new Path(path0);
		assertFalse("01", (path0 == path1));
		assertEquals("02", path1, path0);
	}

	
	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquals_01()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		assertTrue("01", path0.equals(path0));
		Path path1 = new Path("identifications.defaultClassification.kingdom");
		assertTrue("02", path0.equals(path1));
		String str = "identifications.defaultClassification.kingdom";
		assertFalse("03", path0.equals(str));
	}
	
	@Test
	public void testHashCode()
	{
		Path path0 = new Path("identifications.defaultClassification.kingdom");
		assertEquals("01", 106656136, path0.hashCode());
	}
	
	@Test
	public void testPath()
	{
		Path path0 = new Path();
		Path path1 = new Path("");
		assertFalse("01", path0.equals(path1));
	}
	

}
