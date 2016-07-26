package nl.naturalis.nba.dao.es.dwca;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.domainobject.util.FileUtil;
import org.junit.Test;

import nl.naturalis.nba.dao.es.csv.ConstantColumn;
import nl.naturalis.nba.dao.es.csv.IColumn;

public class MetaXmlGeneratorTest {

	@Test
	public void testGenerateMetaXml_01() throws UnsupportedEncodingException
	{
		IColumn id = new ConstantColumn("id", "whatever");
		IColumn basisOfRecord = new ConstantColumn("basisOfRecord", "whatever");
		IColumn catalogNumber = new ConstantColumn("catalogNumber", "whatever");
		IColumn[] columns = new IColumn[] { id, basisOfRecord, catalogNumber };
		MetaXmlGenerator generator = new OccurrenceMetaXmlGenerator(columns);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		generator.generateMetaXml(baos);
		String actual = new String(baos.toString("UTF-8"));
		// System.out.println(actual);
		String expected = getContents("MetaXmlGeneratorTest_testGenerateMetaXml_01.txt");
		assertEquals("01", expected.trim(), actual.trim());
	}

	@Test
	public void testGenerateMetaXml_02() throws UnsupportedEncodingException
	{
		IColumn id = new ConstantColumn("id", "whatever");
		IColumn basisOfRecord = new ConstantColumn("basisOfRecord", "whatever");
		IColumn catalogNumber = new ConstantColumn("catalogNumber", "whatever");
		IColumn[] columns = new IColumn[] { basisOfRecord, id, catalogNumber };
		MetaXmlGenerator generator = new OccurrenceMetaXmlGenerator(columns);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		generator.generateMetaXml(baos);
		String actual = new String(baos.toString("UTF-8"));
		System.out.println(actual);
		String expected = getContents("MetaXmlGeneratorTest_testGenerateMetaXml_02.txt");
		assertEquals("01", expected.trim(), actual.trim());
	}

	private String getContents(String file)
	{
		return FileUtil.getContents(getClass().getResourceAsStream(file));
	}
}
