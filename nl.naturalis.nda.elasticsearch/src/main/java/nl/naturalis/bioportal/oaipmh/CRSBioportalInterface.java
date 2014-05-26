package nl.naturalis.bioportal.oaipmh;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.domainobject.util.FileUtil;
import org.domainobject.util.debug.BeanPrinter;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2_0.oai_dc.OaiDcType;

public class CRSBioportalInterface {
	
	static final String JAXB_PACKAGES = "org.openarchives.oai._2:org.openarchives.oai._2_0.oai_dc:org.purl.dc.elements._1:org.tdwg.schemas.abcd._2";

	public static void main(String[] args) throws JAXBException, IOException
	{
		
		
		BeanPrinter bp = new BeanPrinter();
		//bp.ignorePackage("javax.xml").ignorePackage("javax.xml.namespace");
		//bp.ignoreType(Class.class).ignoreType(QName.class);
		bp.showSuper(false);
		JAXBContext ctx = JAXBContext.newInstance(JAXB_PACKAGES);
		Unmarshaller u = ctx.createUnmarshaller();
		FileReader fr = new FileReader("C:/tmp/crs-oai.xml");
		@SuppressWarnings("unchecked")
		JAXBElement<OAIPMHtype> e = (JAXBElement<OAIPMHtype>) u.unmarshal(fr);
		OAIPMHtype root = e.getValue();
		List<RecordType> records = root.getListRecords().getRecord();
		for(RecordType record : records) {
			MetadataType metadata = record.getMetadata();
			JAXBElement<OaiDcType> x = (JAXBElement<OaiDcType>) metadata.getAny();
			bp.dump(x.getValue());
			break;
		}
	}

}
