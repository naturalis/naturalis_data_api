package nl.naturalis.nba.dao.es.format;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

public class FieldsParserTest {

	@Test
	public void testParse_01() throws EntityConfigurationException {
		EntityConfiguration conf = new EntityConfiguration();
		File file = new File("FieldsParserTest_testParse_01.entity.config");
		FieldsParser parser = new FieldsParser(file);
		parser.parse(conf, new CsvFieldFactory());
		IDataSetField[] fields = conf.getFields();
		assertEquals("01", 4, fields.length);
		assertEquals("02", "id", fields[0].getName());
		assertEquals("03", "scientificName", fields[1].getName());
		assertEquals("04", "nomenclaturalCode", fields[2].getName());
		assertEquals("05", "verbatimEventDate", fields[3].getName());
	}

//	@Test
//	public void testGetFields_01()
//	{
//		FieldConfigurator fc = getConfigurator();
//		String cfg = "EntityConfiguratorTest_testGetFields_01_fields.entity.config";
//		InputStream is = getClass().getResourceAsStream(cfg);
//		IDataSetField[] fields = fc.getFields(is, "dummy");
//		assertEquals("01", 4, fields.length);
//		assertEquals("02", "id", fields[0].getName());
//		assertEquals("03", "scientificName", fields[1].getName());
//		assertEquals("04", "nomenclaturalCode", fields[2].getName());
//		assertEquals("05", "verbatimEventDate", fields[3].getName());
//	}
//
//	@Test
//	public void testGetFields_02()
//	{
//		FieldConfigurator fc = getConfigurator();
//		String cfg = "EntityConfiguratorTest_testGetFields_02_fields.entity.config";
//		InputStream is = getClass().getResourceAsStream(cfg);
//		try {
//			fc.getFields(is, "dummy");
//			fail("Expected a no-such-field error");
//		}
//		catch (DaoException e) {
//			// System.out.println(e.getMessage());
//			String expected = "No such field: \"this.is.not.a.specimen.field\" (dummy, line 1)";
//			assertEquals("01", expected, e.getMessage());
//		}
//	}
//
//	@Test
//	public void testGetFields_03()
//	{
//		FieldConfigurator fc = getConfigurator();
//		String cfg = "EntityConfiguratorTest_testGetFields_03_fields.entity.config";
//		InputStream is = getClass().getResourceAsStream(cfg);
//		try {
//			fc.getFields(is, "dummy");
//			fail("Expected a NoSuchFieldException");
//		}
//		catch (DaoException e) {
//			// System.out.println(e.getMessage());
//			String expected = "Illegal array index (0) following single-valued field: unitID (dummy, line 1)";
//			assertEquals("01", expected, e.getMessage());
//		}
//	}
//
//	private static FieldConfigurator getConfigurator()
//	{
//		IDataSetFieldFactory fieldFactory = new CsvFieldFactory();
//		FieldConfigurator fc = new FieldConfigurator(SPECIMEN, fieldFactory);
//		return fc;
//	}

}
