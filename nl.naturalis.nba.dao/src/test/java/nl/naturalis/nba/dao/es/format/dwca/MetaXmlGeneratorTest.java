package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;

import java.io.UnsupportedEncodingException;

import org.domainobject.util.FileUtil;
import org.junit.Test;

import nl.naturalis.nba.dao.es.format.DataSet;

@SuppressWarnings("static-method")
public class MetaXmlGeneratorTest {

//	@Test
//	public void testGenerateMetaXml_01() throws UnsupportedEncodingException
//	{
//		DwcaDataSetBuilder builder;
//		builder = new DwcaDataSetBuilder(SPECIMEN, "test-data-set-01");
//		DataSetConfiguration ds = builder.build();
//		SpecimenArchive archive = new SpecimenArchive().forDataSet(ds);
//		MetaXmlGenerator mxg = new MetaXmlGenerator(archive);
//		mxg.generateMetaXml(System.out);
//	}
//
//	private String getContents(String file)
//	{
//		return FileUtil.getContents(getClass().getResourceAsStream(file));
//	}
}