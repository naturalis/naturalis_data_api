package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
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

public class CrsFindInSource {

	public static void main(String[] args) throws Exception
	{

		ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
		boolean caseSensitive = getBooleanOption("--case-sensitive", argList, true);
		boolean exactMatch = getBooleanOption("--exact-match", argList, true);
		boolean noDots = getBooleanOption("--no-dots", argList, false);
		int maxRecords = getIntOption("--max-records", argList, 1);

		if (argList.size() != 3) {
			usage();
			return;
		}

		String type = argList.get(0);
		String xmlElement = argList.get(1);
		String value = argList.get(2);

		if (type != null && !(type.equals("specimens") || type.equals("multimedia"))) {
			usage();
			return;
		}

		CrsFindInSource crsFindInSource = new CrsFindInSource(type, xmlElement, value);

		crsFindInSource.maxRecords = maxRecords;
		crsFindInSource.caseSensitive = caseSensitive;
		crsFindInSource.exactMatch = exactMatch;
		crsFindInSource.noDots = noDots;

		crsFindInSource.findOaiRecords();

	}

	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

	private final DocumentBuilder builder;
	private final Transformer transformer;

	private final String type;
	private final String xmlElement;
	private final String value;

	private int maxRecords = 1;
	private boolean caseSensitive = true;
	private boolean exactMatch = true;
	private boolean noDots = false;


	public CrsFindInSource(String type, String xmlElement, String value) throws ParserConfigurationException, TransformerConfigurationException
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
		System.out.print("Searching " + LoadUtil.getConfig().required("crs.local_dir") + " ");
		Iterator<File> iterator = getFileIterator(type);
		int matches = 0;
		String valueUpperCase = value.toUpperCase();
		LEVEL0:
		while (iterator.hasNext()) {
			File f = iterator.next();
			if (!noDots) {
				System.out.print('.');
			}
			Document doc = builder.parse(f);
			List<Element> recordElements = DOMUtil.getDescendants(doc.getDocumentElement(), "record");
			if(recordElements == null) {
				continue;
			}
			for (int i=0;i<recordElements.size(); ++i) {
				Element recordElement = recordElements.get(i);
				List<Element> elems = DOMUtil.getDescendants(recordElement, xmlElement);
				if (elems != null) {
					for (Element e : elems) {
						boolean match = false;
						if (!exactMatch) {
							if (caseSensitive) {
								if (e.getTextContent().indexOf(value) != -1) {
									match = true;
								}
							}
							else {
								if (e.getTextContent().toUpperCase().indexOf(valueUpperCase) != -1) {
									match = true;
								}
							}
						}
						else if (caseSensitive) {
							if (e.getTextContent().trim().equalsIgnoreCase(value)) {
								match = true;
							}
						}
						else if (e.getTextContent().trim().toUpperCase().equals(valueUpperCase)) {
							match = true;
						}
						if (match) {
							System.out.println();
							System.out.println();
							System.out.println("********** [ " + f.getAbsolutePath() + " ] [ Record " + i + " ] **********");
							recordElement.setAttribute("xmlns:xsi", XSI_NAMESPACE);
							DOMSource source = new DOMSource(recordElement);
							StreamResult result = new StreamResult(System.out);
							transformer.transform(source, result);
							System.out.println();
							if (maxRecords > 0 && ++matches == maxRecords) {
								break LEVEL0;
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


	private static boolean getBooleanOption(String optionName, ArrayList<String> args, boolean dfault) throws Exception
	{
		for (String arg : args) {
			if (arg.startsWith(optionName)) {
				args.remove(arg);
				args.trimToSize();
				if (arg.length() == optionName.length()) {
					return dfault;
				}
				int i = arg.indexOf('=');
				if (i == -1) {
					throw new Exception(String.format("Option %1$s must be specified as %1$s (%2$s) or %1$s=true|false", optionName, dfault));
				}
				if (i == arg.length() - 1) {
					return dfault;
				}
				return Boolean.parseBoolean(arg.substring(i + 1));
			}
		}
		return dfault;
	}


	private static int getIntOption(String optionName, ArrayList<String> args, int dfault) throws Exception
	{
		for (String arg : args) {
			if (arg.startsWith(optionName)) {
				args.remove(arg);
				args.trimToSize();
				if (arg.length() == optionName.length()) {
					return dfault;
				}
				int i = arg.indexOf('=');
				if (i == -1) {
					throw new Exception(String.format("Option %1$s must be specified as %1$s=<integer>", optionName, dfault));
				}
				if (i == arg.length() - 1) {
					return dfault;
				}
				return Integer.parseInt(arg.substring(i + 1));
			}
		}
		return dfault;
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
		System.out.println("USAGE: " + shellScript
				+ " specimens|multimedia <xml-element> <value> [--case-sensitive[=true|false]] [--maxRecords=<integer>] [--exact-match[=true|false]] [--no-dots]");
		System.out.println();
		System.out.println("OPTIONS: ");
		System.out.println("--case-sensitive      Whether or not to do a case sensitive search. Default true.");
		System.out.println("--max-records         Maximum number of records to find. Default 1. Zero (0) means: find all.");
		System.out.println("--exact-match         Whether or not the value argument must be matched exactly. Default true.");
		System.out.println("--no-dots             Suppress printing dots while searching. Default false.");
		System.out.println();
		System.out.println("Example 1: find specimen record with UnitID \"RMNH.MAM.45522.A\":");
		System.out.println("           " + shellScript + " specimens abcd:UnitID RMNH.MAM.45522.A");
		System.out.println("Example 2: find ALL specimen records with RecordBasis \"Preserved Specimen\":");
		System.out.println("           " + shellScript + " specimens abcd:RecordBasis \"Preserved Specimen\" --max-records=0");
		System.out.println("Example 3: find at most 5 multimedia records with associatedSpecimenReference \"RGM.1101811\":");
		System.out.println("           " + shellScript + " multimedia abcd:associatedSpecimenReference RGM.1101811 --max-records=5");
		System.out.println("Example 4: find, ignoring case, a multimedia record with CollectionType \"mineralogy and petrology\":");
		System.out.println("           " + shellScript + " multimedia abcd:CollectionType \"mineralogy and petrology\" --case-sensitive");
		System.out.println("Example 5: find all specimen records with \"MAM\" in their UnitID:");
		System.out.println("           " + shellScript + " multimedia abcd:UnitID MAM --exact-match=false");
		System.out.println();
	}
}
