package nl.naturalis.nba.utils.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import nl.naturalis.nba.utils.StringUtil;

/**
 * W3C DOM utilities.
 * 
 * @author Ayco Holleman
 * @created Jul 23, 2015
 *
 */
public class DOMUtil {

	public static DocumentBuilder docBuilder;

	public static Element getDocumentElement(String xml) throws SAXParseException
	{
		return getDocumentElement(xml, false, false);
	}

	public static Element getDocumentElement(String xml, boolean namespaceAware, boolean validating)
			throws SAXParseException
	{
		return getDocument(xml, namespaceAware, validating).getDocumentElement();
	}

	public static Document getDocument(String xml) throws SAXParseException
	{
		return getDocument(xml, false, false);
	}

	public static Document getDocument(String xml, boolean namespaceAware, boolean validating)
			throws SAXParseException
	{
		DocumentBuilder docBuilder = getDocumentBuilder(namespaceAware, validating);
		try {
			return docBuilder.parse(StringUtil.toInputStream(xml, "UTF-8"));
		}
		catch (SAXParseException e) {
			throw e;
		}
		catch (IOException | SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public static Document getDocument(File xml) throws SAXParseException
	{
		return getDocument(xml, false, false);
	}

	public static Document getDocument(File xml, boolean namespaceAware, boolean validating)
			throws SAXParseException
	{
		DocumentBuilder docBuilder = getDocumentBuilder(namespaceAware, validating);
		try {
			return docBuilder.parse(xml);
		}
		catch (SAXParseException e) {
			throw e;
		}
		catch (IOException | SAXException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns all child elements of the specified element. This method returns
	 * {@code null} if the specified element has no children of type
	 * {@code Element}.
	 * 
	 * @param parent
	 * @return
	 */
	public static List<Element> getChildren(Element parent)
	{
		NodeList nl = parent.getChildNodes();
		if (nl.getLength() == 0)
			return null;
		List<Element> elements = new ArrayList<>((int) Math.ceil(nl.getLength() / 2));
		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i) instanceof Element) {
				elements.add((Element) nl.item(i));
			}
		}
		return elements;
	}

	/**
	 * Returns all child elements of the specified element which have the
	 * specified tag name. This method returns {@code null} if the specified
	 * element has no children of type {@code Element}.
	 * 
	 * @param parent
	 * @return
	 */
	public static List<Element> getChildren(Element parent, String tagName)
	{
		NodeList nl = parent.getChildNodes();
		if (nl.getLength() == 0)
			return null;
		List<Element> elements = new ArrayList<>((int) Math.ceil(nl.getLength() / 2));
		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i) instanceof Element) {
				Element e = (Element) nl.item(i);
				if (e.getTagName().endsWith(tagName)) {
					elements.add(e);
				}
			}
		}
		return elements;
	}

	/**
	 * Returns the first child element of the specified element.
	 * 
	 * @param element
	 * @return
	 */
	public static Element getChild(Element parent)
	{
		List<Element> children = getChildren(parent);
		return children.size() == 0 ? null : children.get(0);
	}

	/**
	 * Returns the first child element of the specified node which has the
	 * specified tag name.
	 * 
	 * @param parent
	 * @param tagName
	 * @return
	 */
	public static Element getChild(Element parent, String tagName)
	{
		for (Element e : getChildren(parent)) {
			if (e.getTagName().equals(tagName)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Get the value (i&#46;e&#46; text content) of the first child of the
	 * specified element which has the specified tag name. This method returns
	 * {@code null} if the specified element has no child element with the
	 * specified tag name.
	 * 
	 * @param parent
	 * @param tagName
	 * @return
	 */
	public static String getValue(Element parent, String tagName)
	{
		Element e = getChild(parent, tagName);
		return e == null ? null : e.getTextContent();
	}

	/**
	 * Get the value (i&#46;e&#46; text content) of the first child of the
	 * specified element which has the specified tag name. The value is assumed
	 * to be, and converted to an integer. This method returns 0 (zero) if the
	 * specified element has no child element with the specified tag name.
	 * 
	 * @param parent
	 * @param tagName
	 * @return
	 */
	public static int getIntValue(Element parent, String tagName)
	{
		Element e = getChild(parent, tagName);
		return e == null ? 0 : Integer.parseInt(e.getTextContent());
	}

	public static List<Element> getDescendants(Element ancestor, String tagName)
	{
		NodeList nl = ancestor.getElementsByTagName(tagName);
		if (nl.getLength() == 0)
			return null;
		List<Element> descendants = new ArrayList<>(nl.getLength());
		for (int i = 0; i < nl.getLength(); ++i) {
			descendants.add((Element) nl.item(i));
		}
		return descendants;
	}

	/**
	 * Returns the first element descending from {@code ancestor} that has the
	 * specified {@code tagName}.
	 * 
	 * @param ancestor
	 * @param tagName
	 * @return
	 */
	public static Element getDescendant(Element ancestor, String tagName)
	{
		NodeList nl = ancestor.getElementsByTagName(tagName);
		if (nl.getLength() == 0) {
			return null;
		}
		return (Element) nl.item(0);
	}

	public static Element getDescendant(Element ancestor, String tagName, String namespaceUri)
	{
		NodeList nl = ancestor.getElementsByTagNameNS(namespaceUri, tagName);
		if (nl.getLength() == 0) {
			return null;
		}
		return (Element) nl.item(0);
	}

	public static String getDescendantValue(Document doc, String tagName)
	{
		return getDescendantValue(doc.getDocumentElement(), tagName);
	}

	public static String getDescendantValue(Element ancestor, String tagName)
	{
		Element e = getDescendant(ancestor, tagName);
		return e == null ? null : e.getTextContent();
	}

	public static String getDescendantValue(Element ancestor, String tagName, String namespaceUri)
	{
		Element e = getDescendant(ancestor, tagName, namespaceUri);
		return e == null ? null : e.getTextContent();
	}

	public static int getDescendantIntValue(Element ancestor, String tagName)
	{
		Element e = getDescendant(ancestor, tagName);
		return e == null ? 0 : Integer.parseInt(e.getTextContent());
	}

	public static int getDescendantIntValue(Element ancestor, String tagName, String namespaceUri)
	{
		Element e = getDescendant(ancestor, tagName, namespaceUri);
		return e == null ? 0 : Integer.parseInt(e.getTextContent());
	}

	public static Double getDescendantDoubleValue(Element ancestor, String tagName)
	{
		Element e = getDescendant(ancestor, tagName);
		if (e == null) {
			return null;
		}
		String s = e.getTextContent().trim();
		return s.length() == 0 ? null : Double.valueOf(s);
	}

	public static Double getDescendantDoubleValue(Element ancestor, String tagName,
			String namespaceUri)
	{
		Element e = getDescendant(ancestor, tagName, namespaceUri);
		if (e == null) {
			return null;
		}
		String s = e.getTextContent().trim();
		return s.length() == 0 ? null : Double.valueOf(s);
	}

	private static DocumentBuilder getDocumentBuilder(boolean namespaceAware, boolean validating)
	{
		DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
		bf.setNamespaceAware(namespaceAware);
		bf.setValidating(validating);
		try {
			return bf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
