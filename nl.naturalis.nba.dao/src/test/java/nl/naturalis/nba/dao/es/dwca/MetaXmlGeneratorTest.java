package nl.naturalis.nba.dao.es.dwca;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class MetaXmlGeneratorTest {

	@Test
	public void testGenerateMetaXml_01() throws UnsupportedEncodingException
	{
	}

	// @Test
	// public void testGenerateMetaXml_01() throws UnsupportedEncodingException
	// {
	// IDataSetField id = new ConstantField("id", "whatever");
	// IDataSetField basisOfRecord = new ConstantField("basisOfRecord",
	// "whatever");
	// IDataSetField catalogNumber = new ConstantField("catalogNumber",
	// "whatever");
	// IDataSetField[] columns = new IDataSetField[] { id, basisOfRecord,
	// catalogNumber };
	// MetaXmlGenerator generator = new OccurrenceMetaXmlGenerator(columns);
	// ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
	// generator.generateMetaXml(baos);
	// String actual = new String(baos.toString("UTF-8"));
	// // System.out.println(actual);
	// String expected =
	// getContents("MetaXmlGeneratorTest_testGenerateMetaXml_01.txt");
	// assertEquals("01", expected.trim(), actual.trim());
	// }
	//
	// @Test
	// public void testGenerateMetaXml_02() throws UnsupportedEncodingException
	// {
	// IDataSetField id = new ConstantField("id", "whatever");
	// IDataSetField basisOfRecord = new ConstantField("basisOfRecord",
	// "whatever");
	// IDataSetField catalogNumber = new ConstantField("catalogNumber",
	// "whatever");
	// IDataSetField[] columns = new IDataSetField[] { basisOfRecord, id,
	// catalogNumber };
	// MetaXmlGenerator generator = new OccurrenceMetaXmlGenerator(columns);
	// ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
	// generator.generateMetaXml(baos);
	// String actual = new String(baos.toString("UTF-8"));
	// System.out.println(actual);
	// String expected =
	// getContents("MetaXmlGeneratorTest_testGenerateMetaXml_02.txt");
	// assertEquals("01", expected.trim(), actual.trim());
	// }
	//
	// private String getContents(String file)
	// {
	// return FileUtil.getContents(getClass().getResourceAsStream(file));
	// }
}
