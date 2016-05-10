package nl.naturalis.nba.elasticsearch.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.dao.es.map.Document;
import nl.naturalis.nba.dao.es.map.ESDataType;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;

public class MappingInspectorTest {

	private static MappingInspector inspector;

	@BeforeClass
	public static void setup()
	{
		inspector = MappingInspector.forType(ESSpecimen.class);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_01()
	{
		inspector.getField("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_02()
	{
		inspector.getField("bla.bla");
	}
	
	@Test
	public void testGetField_04()
	{
		ESField f = inspector.getField("gatheringEvent");
		assertNotNull("01", f);
	}


	@Test(expected = NoSuchFieldException.class)
	public void testGetType_01()
	{
		inspector.getType("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_02()
	{
		inspector.getType("bla.bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_03()
	{
		inspector.getType("gatheringEvent.bla");
	}

	@Test
	public void testGetType_04()
	{
		ESDataType type = inspector.getType("gatheringEvent");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_05()
	{
		ESDataType type = inspector.getType("sourceSystem");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_06()
	{
		ESDataType type = inspector.getType("unitID");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test
	public void testGetType_07()
	{
		ESDataType type = inspector.getType("numberOfSpecimen");
		assertEquals("01", ESDataType.INTEGER, type);
	}

	@Test
	public void testGetType_08()
	{
		ESDataType type = inspector.getType("objectPublic");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_09()
	{
		ESDataType type = inspector.getType("identifications");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_10()
	{
		ESDataType type = inspector.getType("identifications.preferred");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_11()
	{
		ESDataType type = inspector.getType("identifications.preferred");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_12()
	{
		ESDataType type = inspector.getType("identifications.defaultClassification");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_13()
	{
		ESDataType type = inspector.getType("identifications.defaultClassification.genus");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test
	public void testGetType_14()
	{
		ESDataType type = inspector.getType("identifications.systemClassification");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_15()
	{
		ESDataType type = inspector.getType("identifications.systemClassification.name");
		assertEquals("01", ESDataType.STRING, type);
	}
	
	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_01() {
		List<Document> ancestors = inspector.getAncestors("bla");
		assertEquals("01", 2, ancestors.size());
	}
	
	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_02() {
		List<Document> ancestors = inspector.getAncestors("identifications.bla");
		assertEquals("01", 2, ancestors.size());
	}
	
	@Test
	public void testGetAncestors_03() {
		List<Document> ancestors = inspector.getAncestors("identifications.systemClassification.name");
		assertEquals("01", 2, ancestors.size());
	}
}
