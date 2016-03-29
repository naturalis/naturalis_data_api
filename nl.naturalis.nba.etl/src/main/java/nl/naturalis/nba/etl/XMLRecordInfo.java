package nl.naturalis.nba.etl;

import org.w3c.dom.Element;

/**
 * A Java bean representing an XML record (something that comes out of an
 * XML extractor and goes into an {@link XMLTransformer}).
 * 
 * @author Ayco Holleman
 *
 */
public class XMLRecordInfo {

	private Element record;

	public XMLRecordInfo(Element element)
	{
		this.record = element;
	}

	/**
	 * Returns the XML element containing the data to be transformed.
	 * 
	 * @return
	 */
	public Element getRecord()
	{
		return record;
	}

}
