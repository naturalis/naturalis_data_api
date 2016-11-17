package nl.naturalis.nba.utils.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML parser that stores the line number of an element in its userData map.
 * 
 * Based on https://eyalsch.wordpress.com/2010/11/30/xml-dom-2/
 * 
 * @author Ayco Holleman
 *
 */
public class LineNumberXMLParser {

	public static String LINE_NUMBER_KEY_NAME = "lineNumber";

	public static Document readXML(InputStream is) throws IOException, SAXException
	{
		final Document doc;
		final SAXParser parser;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException("Failed to create SAX parser / DOM builder.", e);
		}
		Stack<Element> elements = new Stack<>();
		StringBuilder textBuffer = new StringBuilder(2048);
		MyDefaultHandler handler = new MyDefaultHandler(doc, elements, textBuffer);
		parser.parse(is, handler);
		return doc;
	}
}

class MyDefaultHandler extends DefaultHandler {

	private Locator locator;

	private final Document doc;
	private final Stack<Element> elements;
	private final StringBuilder textBuffer;

	MyDefaultHandler(Document doc, Stack<Element> elements, StringBuilder textBuffer)
	{
		this.doc = doc;
		this.elements = elements;
		this.textBuffer = textBuffer;
	}

	@Override
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs)
			throws SAXException
	{
		addTextIfNeeded();
		Element e = doc.createElement(qName);
		for (int i = 0; i < attrs.getLength(); i++) {
			e.setAttribute(attrs.getQName(i), attrs.getValue(i));
		}
		String line = String.valueOf(locator.getLineNumber());
		e.setUserData(LineNumberXMLParser.LINE_NUMBER_KEY_NAME, line, null);
		elements.push(e);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	{
		addTextIfNeeded();
		Element closedEl = elements.pop();
		if (elements.isEmpty()) { // Is this the root element?
			doc.appendChild(closedEl);
		}
		else {
			Element parentEl = elements.peek();
			parentEl.appendChild(closedEl);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException
	{
		if (length < 1000) {
			textBuffer.append(ch, start, length);
		}
	}

	// Outputs text accumulated under the current node
	private void addTextIfNeeded()
	{
		if (textBuffer.length() > 0) {
			String s = textBuffer.toString();
			Element el = elements.peek();
			Node textNode = doc.createTextNode(s);
			el.appendChild(textNode);
			textBuffer.delete(0, textBuffer.length());
		}
	}

}