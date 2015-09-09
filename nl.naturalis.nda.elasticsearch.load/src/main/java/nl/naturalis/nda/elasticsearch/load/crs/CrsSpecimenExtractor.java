package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CrsSpecimenExtractor implements Iterable<XMLRecordInfo> {

	private static DocumentBuilder docBuilder;

	private final ETLStatistics stats;
	private final Document doc;

	/**
	 * Create a new CRS specimen extractor for the specified XML file. The
	 * {@link ETLStatistics} argument is not currently used. That is, its
	 * {@link ETLStatistics#badInput} counter is not updated, because extraction
	 * failures are already detected when the file is parsed into a DOM tree.
	 * Once we're past that stage nothing can go wrong when handing out
	 * individual records.
	 * 
	 * @param f
	 * @param stats
	 */
	public CrsSpecimenExtractor(File f, ETLStatistics stats)
	{
		this.stats = stats;
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
		try {
			doc = docBuilder.parse(f);
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

	public ETLStatistics getStatistics()
	{
		return stats;
	}

}
