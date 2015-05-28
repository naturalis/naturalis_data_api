package nl.naturalis.nda.elasticsearch.load.crs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrsImportUtil {

	private static final Logger logger = LoggerFactory.getLogger(CrsImportUtil.class);


	/**
	 * Make sure the XML we are about the process does not start with whitespace
	 * in order to avoid error "Content is not allowed in prolog" when parsing
	 * the XML.
	 * 
	 * @param xml
	 * @return
	 */
	public static String cleanupXml(String xml)
	{
		xml = xml.trim();
		if (!xml.startsWith("<?xml")) {
			if (xml.indexOf("<?xml") == -1) {
				throw new RuntimeException("Unexpected response from OAI service: " + xml);
			}
			xml = xml.substring(xml.indexOf("<?xml"));
		}
		return xml;
	}


	private CrsImportUtil()
	{
	}

}
