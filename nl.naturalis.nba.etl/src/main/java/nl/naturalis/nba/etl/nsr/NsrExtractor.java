package nl.naturalis.nba.etl.nsr;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.utils.xml.DOMUtil;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The extractor component in the NSR ETL cycle. Reads the NSR XML source files
 * and produces an {@link Iterator} over {@link XMLRecordInfo} instances.
 * 
 * @author Ayco Holleman
 *
 */
class NsrExtractor implements Iterable<XMLRecordInfo> {

	private static final Logger logger;
	private static final Iterator<XMLRecordInfo> zeroRecordsIterator;

	static {
		logger = ETLRegistry.getInstance().getLogger(NsrExtractor.class);
		zeroRecordsIterator = new Iterator<XMLRecordInfo>() {

			public XMLRecordInfo next()
			{
				return null;
			}

			public boolean hasNext()
			{
				return false;
			}

			public void remove()
			{
			}
		};
	}

	final ETLStatistics stats;
	final List<Element> elems;

	NsrExtractor(File f, ETLStatistics stats)
	{
		this.stats = stats;
		try {
			logger.info("Parsing XML");
			DocumentBuilder docBuilder = getDocumentBuilder();
			Document doc = docBuilder.parse(f);
			//			Document doc = LineNumberXMLParser.readXML(new FileInputStream(f));
			logger.info("Queueing records");
			Element taxa = DOMUtil.getChild(doc.getDocumentElement());
			elems = taxa == null ? null : DOMUtil.getChildren(taxa);
			logger.info("Extractor ready");
		}
		catch (SAXException | IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	@Override
	public Iterator<XMLRecordInfo> iterator()
	{
		if (elems == null) {
			return zeroRecordsIterator;
		}
		return new Iterator<XMLRecordInfo>() {

			private int i;

			public boolean hasNext()
			{
				return i < elems.size();
			}

			public XMLRecordInfo next()
			{
				return new XMLRecordInfo(elems.get(i++));
			}

			public void remove()
			{

			}
		};
	}

	/**
	 * Get the statistics object used by this extractor.
	 * 
	 * @return
	 */
	public ETLStatistics getStatistics()
	{
		return stats;
	}

	private static DocumentBuilder getDocumentBuilder()
	{
		DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
		bf.setNamespaceAware(true);
		try {
			return bf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new ETLRuntimeException(e);
		}
	}
}
