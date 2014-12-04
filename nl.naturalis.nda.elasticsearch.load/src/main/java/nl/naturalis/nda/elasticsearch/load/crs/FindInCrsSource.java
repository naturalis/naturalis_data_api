package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class FindInCrsSource {

	public static void main(String[] args) throws Exception
	{

		if (args.length != 3) {
			usage();
			return;
		}

		String type = args[0].toLowerCase();
		String xmlElement = args[1];
		String value = args[2];

		if (type != null && !(type.equals("specimens") || type.equals("multimedia"))) {
			usage();
			return;
		}

		FindInCrsSource findInCrsSource = new FindInCrsSource(type, xmlElement, value);

		int maxRecords = 1;
		String s = System.getProperty("maxRecords");
		if (s != null && s.length() != 0) {
			maxRecords = Integer.parseInt(s);
		}
		findInCrsSource.maxRecords = maxRecords;

		boolean caseInsensitive = false;
		s = System.getProperty("ci");
		if (s != null) {
			caseInsensitive = (s.length() == 0 || Boolean.parseBoolean(s));
		}
		findInCrsSource.caseInsensitive = caseInsensitive;

		boolean contains = false;
		s = System.getProperty("contains");
		if (s != null) {
			contains = (s.length() == 0 || Boolean.parseBoolean(s));
		}
		findInCrsSource.contains = contains;

		findInCrsSource.findOaiRecords();

	}

	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

	private final DocumentBuilder builder;
	private final Transformer transformer;

	private final String type;
	private final String xmlElement;
	private final String value;

	private int maxRecords = 1;
	private boolean caseInsensitive = false;
	private boolean contains = false;


	public FindInCrsSource(String type, String xmlElement, String value) throws ParserConfigurationException, TransformerConfigurationException
	{
		this.type = type;
		this.xmlElement = xmlElement;
		this.value = value;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		builderFactory.setValidating(false);
		builder = builderFactory.newDocumentBuilder();
		TransformerFactory transFactory = TransformerFactory.newInstance();
		transformer = transFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	}


	public void findOaiRecords() throws SAXException, IOException, TransformerException
	{
		
		Iterator<File> iterator = getFileIterator(type);
		int matches = 0;
		String valueUpperCase = value.toUpperCase();
		while (iterator.hasNext()) {
			File f = iterator.next();
			System.out.println("******************** [ Checking file " + f.getName() + " ] ********************");
			Document doc = builder.parse(f);
			List<Element> recordElements = DOMUtil.getDescendants(doc.getDocumentElement(), "record");
			for (Element recordElement : recordElements) {
				List<Element> elems = DOMUtil.getDescendants(recordElement, xmlElement);
				if (elems != null) {
					for (Element e : elems) {
						boolean match = false;
						if (contains) {
							if (caseInsensitive) {
								if (e.getTextContent().toUpperCase().indexOf(valueUpperCase) != -1) {
									match = true;
								}
							}
							else {
								if (e.getTextContent().indexOf(valueUpperCase) != -1) {
									match = true;
								}
							}
						}
						else if (caseInsensitive) {
							if (e.getTextContent().trim().equalsIgnoreCase(value)) {
								match = true;
							}
						}
						else if (e.getTextContent().trim().equals(value)) {
							match = true;
						}
						if (match) {
							System.out.println();
							recordElement.setAttribute("xmlns:xsi", XSI_NAMESPACE);
							DOMSource source = new DOMSource(recordElement);
							StreamResult result = new StreamResult(System.out);
							transformer.transform(source, result);
							System.out.println();
							if (maxRecords > 0 && ++matches == maxRecords) {
								System.out.println();
								System.out.println(String.format("Number of matches for value \"%s\": %s", value, matches));
								System.out.println();
								return;
							}
							continue;
						}
					}
				}
			}
		}
		System.out.println();
		System.out.println(String.format("Number of matches for value \"%s\": %s", value, matches));
		System.out.println();
	}


	private static Iterator<File> getFileIterator(final String type)
	{
		String path = LoadUtil.getConfig().required("crs.local_dir");
		if (type == null) {
			return Arrays.asList(new File(path).listFiles()).iterator();
		}
		return Arrays.asList(new File(path).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				if (!name.startsWith(type)) {
					return false;
				}
				if (!name.endsWith(".oai.xml")) {
					return false;
				}
				return true;
			}
		})).iterator();
	}


	private static void usage() throws Exception
	{
		String shellScript = System.getProperty("shellScript");
		if (shellScript == null) {
			throw new Exception("Missing system property: \"shellScript\"");
		}
		System.out.println("USAGE: " + shellScript + " specimens|multimedia <xml-element> <value> [-Dci] [-DmaxRecords=<integer>] [-Dcontains]");
		System.out.println("Example 1: find specimen record with unitID \"RMNH.MAM.45522.A\":");
		System.out.println("           " + shellScript + " specimens abcd:unitID RMNH.MAM.45522.A");
		System.out.println("Example 2: find all specimen records with RecordBasis \"Preserved Specimen\":");
		System.out.println("           " + shellScript + " specimens abcd:RecordBasis \"Preserved Specimen\" -DmaxRecords=0");
		System.out.println("Example 3: find at most 5 multimedia records with associatedSpecimenReference \"RGM.1101811\":");
		System.out.println("           " + shellScript + " multimedia abcd:associatedSpecimenReference RGM.1101811 -DmaxRecords=5");
		System.out.println("Example 4: find, ignoring case, a multimedia record with CollectionType \"mineralogy and petrology\":");
		System.out.println("           " + shellScript + " multimedia abcd:CollectionType \"mineralogy and petrology\" -Dci");
		System.out.println("Example 5: find all specimen records with \"MAM\" in their unitID:");
		System.out.println("           " + shellScript + " multimedia abcd:unitID MAM -Dcontains");
	}
}
