package nl.naturalis.nba.dao.es.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.test.TestPerson;

@SuppressWarnings("static-method")
public class MappingInfoTest {

	private static MappingInfo specimenInfo;
	private static MappingInfo personInfo;

	@BeforeClass
	public static void setup()
	{
		specimenInfo = new MappingInfo(DocumentType.SPECIMEN.getMapping());
		personInfo = new MappingInfo(MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_01()
	{
		specimenInfo.getField("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_02()
	{
		specimenInfo.getField("bla.bla");
	}

	@Test
	public void testGetField_04()
	{
		ESField f = specimenInfo.getField("gatheringEvent");
		assertNotNull("01", f);
		assertTrue("02", f instanceof Document);
	}

	@Test
	public void testGetField_05()
	{
		ESField f = specimenInfo.getField("unitID");
		assertNotNull("01", f);
		assertTrue("02", f instanceof AnalyzableField);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_06()
	{
		specimenInfo.getField("unitID.analyzed");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_01()
	{
		specimenInfo.getType("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_02()
	{
		specimenInfo.getType("bla.bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_03()
	{
		specimenInfo.getType("gatheringEvent.bla");
	}

	@Test
	public void testGetType_04()
	{
		ESDataType type = specimenInfo.getType("gatheringEvent");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_05()
	{
		ESDataType type = specimenInfo.getType("sourceSystem");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_06()
	{
		ESDataType type = specimenInfo.getType("unitID");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test
	public void testGetType_07()
	{
		ESDataType type = specimenInfo.getType("numberOfSpecimen");
		assertEquals("01", ESDataType.INTEGER, type);
	}

	@Test
	public void testGetType_08()
	{
		ESDataType type = specimenInfo.getType("objectPublic");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_09()
	{
		ESDataType type = specimenInfo.getType("identifications");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_10()
	{
		ESDataType type = specimenInfo.getType("identifications.preferred");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_11()
	{
		ESDataType type = specimenInfo.getType("identifications.preferred");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_12()
	{
		ESDataType type = specimenInfo.getType("identifications.defaultClassification");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_13()
	{
		ESDataType type = specimenInfo.getType("identifications.defaultClassification.genus");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test
	public void testGetType_14()
	{
		ESDataType type = specimenInfo.getType("identifications.systemClassification");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_15()
	{
		ESDataType type = specimenInfo.getType("identifications.systemClassification.name");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_01()
	{
		List<Document> ancestors = specimenInfo.getAncestors("bla");
		assertEquals("01", 2, ancestors.size());
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_02()
	{
		List<Document> ancestors = specimenInfo.getAncestors("identifications.bla");
		assertEquals("01", 2, ancestors.size());
	}

	@Test
	public void testGetAncestors_03()
	{
		String path = "identifications.systemClassification.name";
		List<Document> ancestors = specimenInfo.getAncestors(path);
		assertEquals("01", 2, ancestors.size());
	}

	@Test
	public void testGetNestedPath_01()
	{
		String path = "identifications.systemClassification.name";
		String nested = specimenInfo.getNestedPath(path);
		assertEquals("01", "identifications.systemClassification", nested);
	}

	@Test
	public void testGetNestedPath_02()
	{
		String path = "identifications.defaultClassification.genus";
		String nested = specimenInfo.getNestedPath(path);
		assertEquals("01", "identifications", nested);
	}

	@Test
	public void testGetNestedPath_03()
	{
		String path = "unitID";
		String nested = specimenInfo.getNestedPath(path);
		assertNull("01", nested);
	}

	@Test
	public void testIsMultiValued_01()
	{
		assertFalse("01", personInfo.isMultiValued("firstName"));
	}

	@Test
	public void testIsMultiValued_02()
	{
		assertTrue("01", personInfo.isMultiValued("luckyNumbers"));
	}

	@Test
	public void testIsMultiValued_03()
	{
		assertFalse("01", personInfo.isMultiValued("address.street"));
	}

	@Test
	public void testIsMultiValued_04()
	{
		assertTrue("01", personInfo.isMultiValued("addressBook.street"));
	}
}
