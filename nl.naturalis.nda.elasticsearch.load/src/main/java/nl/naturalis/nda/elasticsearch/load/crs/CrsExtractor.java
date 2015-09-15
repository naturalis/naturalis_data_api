package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.domainobject.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The extraction component for the CRS import. This class is used both for
 * specimen imports and for multimedia imports, and it is used both for
 * "offline" imports from the local file system and for "live" imports that call
 * the OAI service.
 * 
 * @author Ayco Holleman
 *
 */
class CrsExtractor implements Iterable<XMLRecordInfo> {

	private static DocumentBuilder docBuilder;

	private final ETLStatistics stats;
	private final Document doc;

	/**
	 * Create a new CRS specimen extractor for the specified XML file. Use this
	 * constructor when importing pre-harvested files from the local file
	 * system. The {@link ETLStatistics} parameter is only present for
	 * uniformity's sake (other extractors in this library are also instantiated
	 * with a statistics object). It is not currently used. That is, its
	 * {@link ETLStatistics#badInput} counter is not updated, because extraction
	 * failures are already detected when the file as a whole is parsed into a
	 * DOM tree. Once we're past that stage nothing can go wrong when handing
	 * out individual records.
	 * 
	 * @param f
	 * @param stats
	 */
	CrsExtractor(File f, ETLStatistics stats)
	{
		this.stats = stats;
		try {
			doc = getDocumentBuilder().parse(f);
		}
		catch (SAXException | IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	/**
	 * Create a new CRS specimen extractor for the specified byte array. Use
	 * this constructing when processing a "live" call to the CRS OAI service.
	 * 
	 * @param bytes
	 * @param stats
	 */
	CrsExtractor(byte[] bytes, ETLStatistics stats)
	{
		this.stats = stats;
		try {
			InputStream is = new ByteArrayInputStream(bytes);
			doc = getDocumentBuilder().parse(is);
			String root = doc.getDocumentElement().getTagName();
			if (!root.equals("OAI-PMH")) {
				// With timeouts we are sometimes redirected to the Naturalis
				// home page
				throw new ETLRuntimeException("Invalid OAI-PMH: <" + root + ">");
			}
			Element e = DOMUtil.getDescendant(doc.getDocumentElement(), "error");
			if (e != null) {
				String fmt = "OAI Error (code=\"%s\"): \"%s\"";
				String msg = String.format(fmt, e.getAttribute("code"), e.getTextContent());
				throw new ETLRuntimeException(msg);
			}
		}
		catch (SAXException | IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	@Override
	public Iterator<XMLRecordInfo> iterator()
	{
		return new Iterator<XMLRecordInfo>() {
			NodeList recs = doc.getElementsByTagName("record");
			int i = 0;
			public boolean hasNext()
			{
				return i < recs.getLength();
			}
			public XMLRecordInfo next()
			{
				return new XMLRecordInfo((Element) recs.item(i++));
			}
		};
	}

	/**
	 * Get the resumption token from the currently processed file or OAI
	 * response.
	 * 
	 * @return
	 */
	public String getResumptionToken()
	{
		return DOMUtil.getDescendantValue(doc, "resumptionToken");
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
		if (docBuilder == null) {
			DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
			bf.setNamespaceAware(true);
			try {
				docBuilder = bf.newDocumentBuilder();
			}
			catch (ParserConfigurationException e) {
				throw new ETLRuntimeException(e);
			}
		}
		return docBuilder;
	}

}
