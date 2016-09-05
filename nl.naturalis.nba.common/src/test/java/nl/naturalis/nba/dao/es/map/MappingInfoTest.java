package nl.naturalis.nba.dao.es.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.AnalyzableField;
import nl.naturalis.nba.common.es.map.Document;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.es.test.TestPerson;

@SuppressWarnings("static-method")
public class MappingInfoTest {

	private static MappingInfo specimenInfo;
	private static MappingInfo personInfo;

	@BeforeClass
	public static void setup()
	{
		specimenInfo = new MappingInfo(MappingFactory.getMapping(Specimen.class));
		personInfo = new MappingInfo(MappingFactory.getMapping(TestPerson.class));
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_01() throws NoSuchFieldException
	{
		specimenInfo.getField("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_02() throws NoSuchFieldException
	{
		specimenInfo.getField("bla.bla");
	}

	@Test
	public void testGetField_04() throws NoSuchFieldException
	{
		ESField f = specimenInfo.getField("gatheringEvent");
		assertNotNull("01", f);
		assertTrue("02", f instanceof Document);
	}

	@Test
	public void testGetField_05() throws NoSuchFieldException
	{
		ESField f = specimenInfo.getField("unitID");
		assertNotNull("01", f);
		assertTrue("02", f instanceof AnalyzableField);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetField_06() throws NoSuchFieldException
	{
		specimenInfo.getField("unitID.analyzed");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_01() throws NoSuchFieldException
	{
		specimenInfo.getType("bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_02() throws NoSuchFieldException
	{
		specimenInfo.getType("bla.bla");
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetType_03() throws NoSuchFieldException
	{
		specimenInfo.getType("gatheringEvent.bla");
	}

	@Test
	public void testGetType_04() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("gatheringEvent");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_05() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("sourceSystem");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_06() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("unitID");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test
	public void testGetType_07() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("numberOfSpecimen");
		assertEquals("01", ESDataType.INTEGER, type);
	}

	@Test
	public void testGetType_08() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("objectPublic");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_09() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_10() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications.preferred");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_11() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications.preferred");
		assertEquals("01", ESDataType.BOOLEAN, type);
	}

	@Test
	public void testGetType_12() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications.defaultClassification");
		assertEquals("01", ESDataType.OBJECT, type);
	}

	@Test
	public void testGetType_13() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications.defaultClassification.genus");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test
	public void testGetType_14() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications.systemClassification");
		assertEquals("01", ESDataType.NESTED, type);
	}

	@Test
	public void testGetType_15() throws NoSuchFieldException
	{
		ESDataType type = specimenInfo.getType("identifications.systemClassification.name");
		assertEquals("01", ESDataType.STRING, type);
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_01() throws NoSuchFieldException
	{
		List<Document> ancestors = specimenInfo.getAncestors("bla");
		assertEquals("01", 2, ancestors.size());
	}

	@Test(expected = NoSuchFieldException.class)
	public void testGetAncestors_02() throws NoSuchFieldException
	{
		List<Document> ancestors = specimenInfo.getAncestors("identifications.bla");
		assertEquals("01", 2, ancestors.size());
	}

	@Test
	public void testGetAncestors_03() throws NoSuchFieldException
	{
		String path = "identifications.systemClassification.name";
		List<Document> ancestors = specimenInfo.getAncestors(path);
		assertEquals("01", 2, ancestors.size());
	}

	@Test
	public void testGetNestedPath_01() throws NoSuchFieldException
	{
		String path = "identifications.systemClassification.name";
		String nested = specimenInfo.getNestedPath(path);
		assertEquals("01", "identifications.systemClassification", nested);
	}

	@Test
	public void testGetNestedPath_02() throws NoSuchFieldException
	{
		String path = "identifications.defaultClassification.genus";
		String nested = specimenInfo.getNestedPath(path);
		assertEquals("01", "identifications", nested);
	}

	@Test
	public void testGetNestedPath_03() throws NoSuchFieldException
	{
		String path = "unitID";
		String nested = specimenInfo.getNestedPath(path);
		assertNull("01", nested);
	}

	@Test
	public void testIsMultiValued_01() throws NoSuchFieldException
	{
		assertFalse("01", personInfo.isMultiValued("firstName"));
	}

	@Test
	public void testIsMultiValued_02() throws NoSuchFieldException
	{
		assertTrue("01", personInfo.isMultiValued("luckyNumbers"));
	}

	@Test
	public void testIsMultiValued_03() throws NoSuchFieldException
	{
		assertFalse("01", personInfo.isMultiValued("address.street"));
	}

	@Test
	public void testIsMultiValued_04() throws NoSuchFieldException
	{
		assertTrue("01", personInfo.isMultiValued("addressBook.street"));
	}
}
