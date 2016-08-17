package nl.naturalis.nba.dao.es.dwca;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.domainobject.util.FileUtil;
import org.junit.Test;

import nl.naturalis.nba.dao.es.format.FieldConfigurator;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.IDataSetFieldFactory;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;
import nl.naturalis.nba.dao.es.format.dwca.MetaXmlGenerator;
import nl.naturalis.nba.dao.es.format.dwca.OccurrenceMetaXmlGenerator;

public class MetaXmlGeneratorTest {

	@Test
	public void testGenerateMetaXml_01() throws UnsupportedEncodingException
	{
		IDataSetFieldFactory fieldFactory = new CsvFieldFactory();
		FieldConfigurator fc = new FieldConfigurator(SPECIMEN, fieldFactory);
		String cfg = "MetaXmlGeneratorTest_testGenerateMetaXml_01_fields.config";
		InputStream is = getClass().getResourceAsStream(cfg);
		IDataSetField[] fields = fc.getFields(is, "dummy");
		MetaXmlGenerator generator = new OccurrenceMetaXmlGenerator(fields);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		generator.generateMetaXml(baos);
		String actual = new String(baos.toString("UTF-8"));
		// System.out.println(actual);
		String expected = getContents("MetaXmlGeneratorTest_testGenerateMetaXml_01.txt");
		assertEquals("01", expected.trim(), actual.trim());
	}

	/*
	 * Test with id field in different slot.
	 */
	@Test
	public void testGenerateMetaXml_02() throws UnsupportedEncodingException
	{
		IDataSetFieldFactory fieldFactory = new CsvFieldFactory();
		FieldConfigurator fc = new FieldConfigurator(SPECIMEN, fieldFactory);
		String cfg = "MetaXmlGeneratorTest_testGenerateMetaXml_02_fields.config";
		InputStream is = getClass().getResourceAsStream(cfg);
		IDataSetField[] fields = fc.getFields(is, "dummy");
		MetaXmlGenerator generator = new OccurrenceMetaXmlGenerator(fields);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		generator.generateMetaXml(baos);
		String actual = new String(baos.toString("UTF-8"));
		// System.out.println(actual);
		String expected = getContents("MetaXmlGeneratorTest_testGenerateMetaXml_02.txt");
		assertEquals("01", expected.trim(), actual.trim());
	}

	private String getContents(String file)
	{
		return FileUtil.getContents(getClass().getResourceAsStream(file));
	}
}
