package nl.naturalis.nda.elasticsearch.load.nsr;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.load.crs.CrsHarvester;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NsrHarvester {

	public static void main(String[] args)
	{
		URL url = NsrHarvester.class.getResource("/nsr/nsr_test_export.xml");
		System.out.println("URL: " + url);
	}


	private static final String NDA_TYPE = "nsr_taxon";


	private static final Logger logger = LoggerFactory.getLogger(NsrHarvester.class);

	private final ConfigObject config;
	private final DocumentBuilder builder;
	private int batch;
	private int recordsProcessed;
	private int badRecords;

	private Index index;

	public NsrHarvester()
	{
		URL url = CrsHarvester.class.getResource("/config/crs/CrsHarvester.properties");
		config = new ConfigObject(url);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
	}

}
