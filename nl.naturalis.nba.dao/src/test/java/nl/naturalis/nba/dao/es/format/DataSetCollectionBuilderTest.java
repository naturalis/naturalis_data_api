package nl.naturalis.nba.dao.es.format;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;
//
//import nl.naturalis.nba.dao.es.format.config.DataSetsXmlConfig;
//import nl.naturalis.nba.dao.es.format.config.SourceXmlConfig;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

public class DataSetCollectionBuilderTest {

	@Test
	public void testBuild_01() throws DataSetConfigurationException, JAXBException
	{
		String config = "DataSetCollectionBuilderTest_testBuild01.xml";
		DataSetBuilder builder = new DataSetBuilder(config, true);
		DataSetCollection dsc = builder.build(new CsvFieldFactory());
//		DataSetsXmlConfig dsxc = new DataSetsXmlConfig();
//		SourceXmlConfig sxc = new SourceXmlConfig();
//		sxc.setValue("test");
//		dsxc.setSource(sxc);
//		JAXBContext ctx = JAXBContext.newInstance(DataSetsXmlConfig.class);
//		Marshaller m = ctx.createMarshaller();
//		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		m.marshal(dsxc, System.out);
		
	}

}
