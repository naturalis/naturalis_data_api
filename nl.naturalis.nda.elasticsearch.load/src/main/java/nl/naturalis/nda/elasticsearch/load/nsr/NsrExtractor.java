package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.domainobject.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class NsrExtractor implements Iterable<XMLRecordInfo> {

	private static final Iterator<XMLRecordInfo> zeroRecordsIterator;

	static {
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

	private static DocumentBuilder docBuilder;

	private final ETLStatistics stats;
	private final List<Element> elems;

	public NsrExtractor(File f, ETLStatistics stats)
	{
		this.stats = stats;
		try {
			Document doc = getDocumentBuilder().parse(f);
			Element taxa = DOMUtil.getChild(doc.getDocumentElement());
			elems = taxa == null ? null : DOMUtil.getChildren(taxa);
		}
		catch (SAXException | IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	@Override
	public Iterator<XMLRecordInfo> iterator()
	{
		if (elems == null)
			return zeroRecordsIterator;
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