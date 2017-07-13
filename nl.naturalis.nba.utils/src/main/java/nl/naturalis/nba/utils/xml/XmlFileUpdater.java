package nl.naturalis.nba.utils.xml;

import static nl.naturalis.nba.utils.DOMUtil.getDescendant;
import static nl.naturalis.nba.utils.DOMUtil.getDocument;

import java.io.File;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

public class XmlFileUpdater {

	private File file;
	private boolean nsAware = false;
	private boolean validating = false;

	private Document doc;

	public XmlFileUpdater(File file)
	{
		if (!file.isFile()) {
			throw new IllegalArgumentException("No such file: " + file.getPath());
		}
		this.file = file;
	}

	public boolean isNamespaceAware()
	{
		return nsAware;
	}

	public void setNamespaceAware(boolean nsAware)
	{
		this.nsAware = nsAware;
	}

	public boolean isValidating()
	{
		return validating;
	}

	public void setValidating(boolean validating)
	{
		this.validating = validating;
	}

	/**
	 * Parses the XML file into a DOM structure. You must call this method
	 * before any of the updateXXX methods.
	 * 
	 * @throws SAXParseException
	 */
	public void readFile() throws SAXParseException
	{
		doc = getDocument(file, nsAware, validating);
	}

	/**
	 * Updates the value of the first element encountered having the specified
	 * name.
	 * 
	 * @param name
	 * @param value
	 */
	public void updateFirstElement(String name, String value)
	{
		Element e = getDescendant(getDocumentElement(), name);
		if (e == null) {
			throw new IllegalArgumentException("No such element: " + name);
		}
		e.setTextContent(value);
	}

	// TODO: Make namespace-aware variant with extra namespaceURI parameter

	// TODO: More cool update methods, but currently we only need this one

	/**
	 * Saves the changes to the original XML file.
	 */
	public void save()
	{
		save(file);
	}

	/**
	 * Saves the changes to the specified file.
	 */
	public void save(File file)
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		}
		catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Wrties the modified XML to the specified {@link OutputStream}.
	 * 
	 * @param out
	 */
	public void save(OutputStream out)
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		}
		catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	private Element getDocumentElement()
	{
		if (doc == null) {
			throw new IllegalStateException("File has not be read yet. Call readFile first");
		}
		return doc.getDocumentElement();
	}

}
