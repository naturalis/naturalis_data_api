package nl.naturalis.nda.elasticsearch.load;

import org.w3c.dom.Element;

/**
 * A simple bean representing an XML record, i.e. something that comes out of an
 * XML extractor and goes into a {@link XMLTransformer}.
 * 
 * @author Ayco Holleman
 *
 */
public class XMLRecordInfo {

	private Element element;

	public XMLRecordInfo(Element element)
	{
		this.element = element;
	}

	/**
	 * Returns the XML element containing the data to be transformed.
	 * 
	 * @return
	 */
	public Element getElement()
	{
		return element;
	}

}
