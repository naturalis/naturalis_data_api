package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.domainobject.util.debug.BeanPrinter;
import org.domainobject.util.http.SimpleHttpGet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class AbstractSpecimenImporter {

	private static final ConfigObject config = Registry.getInstance().getConfig();
	private static final Logger logger = Registry.getInstance().getLogger(AbstractSpecimenImporter.class);
	private static final int INDEXED_NOTIFIER_INTERVAL = 10000;

	private final DocumentBuilder builder;

	private final int bulkRequestSize;

	/*
	 * Whether or not to look for a file containing the last resumption token
	 * and use that for this first OAI call, or just start from scratch.
	 * However, it seems like resumption tokens become invalid if there is too
	 * much time between OAI calls (e.g. when resuming the next day after an
	 * aborted/failed attempt to harvest). Needs to be figured out first.
	 */
	private final boolean forceRestart = true;

	private int processed;
	private int bad;
	private int indexed;
	private int indexedTreshold = INDEXED_NOTIFIER_INTERVAL;


	public AbstractSpecimenImporter() throws Exception
	{
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
	}


	public void importSpecimens()
	{
		processed = 0;
		bad = 0;
		indexed = 0;
		try {

			ThemeCache.getInstance().resetMatchCounters();
			beforeFirst();
			if (config.isTrue("crs.use_local")) {
				importLocal();
			}
			else {
				importRemote();
			}
			afterLast();
			ThemeCache.getInstance().logMatchInfo();

			logger.info("Records processed: " + processed);
			logger.info("Bad records: " + bad);
			logger.info("Documents indexed: " + indexed);
			logger.info(getClass().getSimpleName() + " finished");
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}
	}


	public void checkSpecimen(String unitID) throws IOException, SAXException
	{
		logger.info(String.format("Searching for specimen with UnitID \"%s\"", unitID));
		Iterator<File> localFileIterator = getLocalFileIterator();
		while (localFileIterator.hasNext()) {
			File f = localFileIterator.next();
			logger.info("Searching file " + f.getCanonicalPath());
			if (checkFile(FileUtil.getContents(f), unitID)) {
				break;
			}
		}
	}


	/**
	 * Will be called just before processing the very first record (either
	 * coming back from the first OAI request or within within the first locally
	 * stored OAI XML file).
	 */
	protected void beforeFirst()
	{

	}


	/**
	 * Will be called just after the last record has been processed
	 */
	protected void afterLast()
	{

	}


	protected abstract void saveSpecimens(List<ESSpecimen> specimens, List<String> ids);


	protected abstract void deleteSpecimen(String databaseId);


	private void importRemote() throws IOException
	{
		int batch = 0;
		String resumptionToken;
		File resTokenFile = getResumptionTokenFile();
		logger.info(String.format("Looking for resumption token file: %s", resTokenFile.getCanonicalPath()));
		if (!resTokenFile.exists()) {
			logger.info("Resumption token file not found. Will start from scratch");
			resumptionToken = null;
			batch = 0;
		}
		else {
			if (forceRestart) {
				resTokenFile.delete();
				logger.info("Resumption token file found but ignored and deleted (forceRestart=true). Will start from scratch");
				resumptionToken = null;
				batch = 0;
			}
			else {
				String[] elements = FileUtil.getContents(resTokenFile).split(",");
				batch = Integer.parseInt(elements[0]);
				resumptionToken = elements[1];
				logger.info(String.format("Will resume with resumption token %s (batch %s)", resumptionToken, batch));
			}
		}

		do {
			logger.info("Processing batch " + batch);
			byte[] response = callOaiService(resumptionToken);
			++batch;
			resumptionToken = index(response);
		} while (resumptionToken != null);

		logger.info("Deleting resumption token file");
		if (resTokenFile.exists()) {
			resTokenFile.delete();
		}
	}


	private void importLocal() throws IOException
	{
		Iterator<File> localFileIterator = getLocalFileIterator();
		File f;
		while (localFileIterator.hasNext()) {
			f = localFileIterator.next();
			logger.info("Processing file " + f.getCanonicalPath());
			index(Files.readAllBytes(f.toPath()));
		}
	}

	private static final SimpleDateFormat oaiDateFormatter = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");


	static byte[] callOaiService(String resumptionToken)
	{
		String url;
		if (resumptionToken == null) {
			url = config.required("crs.specimens.url.initial");
			int maxAge = config.required("crs.max_age", int.class);
			if (maxAge != 0) {
				DateTime now = new DateTime();
				DateTime wayback = now.minusHours(maxAge);
				url += "&from=" + oaiDateFormatter.format(wayback.toDate());
			}
		}
		else {
			url = String.format(config.required("crs.specimens.url.resume"), resumptionToken);
		}
		logger.info("Calling service: " + url);
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
	}


	static byte[] callOaiService(Date fromDate, Date untilDate)
	{
		String url = config.required("crs.specimens.url.initial");
		if (fromDate != null) {
			url += "&from=" + oaiDateFormatter.format(fromDate);
		}
		if (untilDate != null) {
			url += "&until=" + oaiDateFormatter.format(untilDate);
		}
		logger.info("Calling service: " + url);
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
	}


	private boolean checkFile(String xml, String unitID) throws SAXException, IOException
	{
		Document doc = builder.parse(StringUtil.toInputStream(xml));
		doc.normalize();
		NodeList records = doc.getElementsByTagName("record");
		int numRecords = records.getLength();
		boolean found = false;
		for (int i = 0; i < numRecords; ++i) {
			Element record = (Element) records.item(i);
			if (isDeletedRecord(record)) {
				continue;
			}
			String id = CrsSpecimenTransfer.val(record, "abcd:UnitID");
			if (!id.equals(unitID)) {
				continue;
			}
			found = true;
			ESSpecimen specimen = null;
			try {
				specimen = CrsSpecimenTransfer.transfer(record);
			}
			catch (Throwable t) {
				logger.error(String.format("An error occurred for specimen with UnitID \"%s\": %s", unitID, t.getMessage()));
			}
			StringWriter sw = new StringWriter(1024);
			PrintWriter pw = new PrintWriter(sw);
			BeanPrinter bp = new BeanPrinter(pw);
			bp.dump(specimen);
			logger.info(sw.toString());
			break;
		}
		return found;
	}


	private String index(byte[] xml)
	{
		try {
			Document doc;
			logger.debug("Parsing XML");
			doc = builder.parse(new ByteArrayInputStream(xml));
			doc.normalize();
			NodeList records = doc.getElementsByTagName("record");
			int numRecords = records.getLength();
			logger.debug("Number of records in XML output: " + numRecords);
			List<ESSpecimen> specimens = new ArrayList<>(bulkRequestSize);
			List<String> ids = new ArrayList<>(bulkRequestSize);
			for (int i = 0; i < numRecords; ++i) {
				++processed;
				try {
					Element record = (Element) records.item(i);
					String id = DOMUtil.getDescendantValue(record, "identifier");
					if (isDeletedRecord(record)) {
						// With full harvest we ignore records with status DELETED
						if (config.getInt("crs.max_age") != 0) {
							//index.deleteDocument(LUCENE_TYPE_SPECIMEN, id);
							deleteSpecimen(id);
						}
					}
					else {
						ESSpecimen specimen = CrsSpecimenTransfer.transfer(record);
						if (specimen != null) {
							specimens.add(specimen);
							ids.add(id);
							if (specimens.size() >= bulkRequestSize) {
								try {
									saveSpecimens(specimens, ids);
									indexed += specimens.size();
								}
								finally {
									specimens.clear();
									ids.clear();
								}
							}
						}
					}
				}
				catch (Throwable t) {
					++bad;
					logger.error(t.getMessage(), t);
				}
				if (processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
				if (indexed >= indexedTreshold) {
					logger.info("Documents indexed: " + indexed);
					indexedTreshold += INDEXED_NOTIFIER_INTERVAL;
				}
			}
			if (!specimens.isEmpty()) {
				saveSpecimens(specimens, ids);
				indexed += specimens.size();
			}
			return DOMUtil.getDescendantValue(doc, "resumptionToken");
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
			return null;
		}
	}


	static Iterator<File> getLocalFileIterator()
	{
		logger.debug("Retrieving file list");
		String path = config.required("crs.data_dir");
		File[] files = new File(path).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				if (!name.startsWith("specimens.")) {
					return false;
				}
				if (!name.endsWith(".oai.xml")) {
					return false;
				}
				return true;
			}
		});
		logger.debug("Sorting file list");
		Arrays.sort(files);
		return Arrays.asList(files).iterator();
	}


	private static boolean isDeletedRecord(Element record)
	{
		if (!DOMUtil.getChild(record, "header").hasAttribute("status")) {
			return false;
		}
		return DOMUtil.getChild(record, "header").getAttribute("status").equals("deleted");
	}


	private static File getResumptionTokenFile()
	{
		return new File(System.getProperty("java.io.tmpdir") + "/crs-specimens.resumption-token");
	}

}
