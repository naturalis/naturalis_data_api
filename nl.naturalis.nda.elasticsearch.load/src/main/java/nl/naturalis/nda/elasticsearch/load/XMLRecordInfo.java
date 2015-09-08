package nl.naturalis.nda.elasticsearch.load;

import org.w3c.dom.Element;

public class XMLRecordInfo {

	private Element element;

	public XMLRecordInfo(Element element)
	{
		this.element = element;
	}

	public Element getElement()
	{
		return element;
	}

}
