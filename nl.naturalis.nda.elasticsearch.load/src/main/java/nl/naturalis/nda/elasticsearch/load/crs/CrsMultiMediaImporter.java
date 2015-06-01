package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.MedialibMimeTypeCache;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.domainobject.util.http.SimpleHttpGet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ETL class using CRS's OAIPMH service to extract the data, w3c DOM to parse
 * the data, and ElasticSearch's native client to save the data.
 * 
 * @author ayco_holleman
 * 
 */
public class CrsMultiMediaImporter {

	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		// Make sure thematic search is configured properly
		ThematicSearchConfig.getInstance();
		IndexNative index = null;
		try {
			index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			CrsMultiMediaImporter importer = new CrsMultiMediaImporter(index);
			importer.importMultiMedia();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsMultiMediaImporter.class);
	private static final String ID_PREFIX = "CRS-";

	private final DocumentBuilder builder;

	private final Index index;
	private final int bulkRequestSize;
	private final int maxRecords;
	private final boolean forceRestart;

	int recordsProcessed;
	int multimediaProcessed;

	int recordsRejected;
	int multimediaRejected;

	int recordsSkipped;
	int recordsInvestigated;
	int multimediaSkipped;

	int multimediaIndexed;


	public CrsMultiMediaImporter(Index index) throws Exception
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);

		prop = System.getProperty("maxRecords", "0");
		maxRecords = Integer.parseInt(prop);

		prop = System.getProperty("forceRestart", "true");
		forceRestart = Boolean.parseBoolean(prop);

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
	}


	public void importMultiMedia()
	{
		recordsProcessed = 0;
		recordsInvestigated = 0;
		multimediaProcessed = 0;
		recordsRejected = 0;
		multimediaRejected = 0;
		recordsSkipped = 0;

		multimediaIndexed = 0;

		try {

			ThematicSearchConfig.getInstance().resetMatchCounters();

			/*
			 * Make sure we can create the mime type cache, otherwise we get
			 * opaque class initialization errors in CrsMultiMediaTransfer
			 */
			MedialibMimeTypeCache.getInstance();

			index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.CRS.getCode());

			if (LoadUtil.getConfig().isTrue("crs.use_local")) {
				processLocal();
			}
			else {
				processRemote();
			}

			ThematicSearchConfig.getInstance().logMatchInfo();

			logger.info(" ");
			logger.info("Multimedia indexed            : " + String.format("%7d", multimediaIndexed));
			logger.info("Multimedia skipped            : " + String.format("%7d", multimediaSkipped));
			logger.info("Malformed/rejected multimedia : " + String.format("%7d", multimediaRejected));
			logger.info("--------------------------------------- +");
			logger.info("Multimedia processed          : " + String.format("%7d", multimediaProcessed));

			logger.info(" ");
			logger.info("Records skipped               : " + String.format("%7d", recordsSkipped));
			logger.info("Records investigated          : " + String.format("%7d", recordsInvestigated));
			logger.info("Malformed/rejected records    : " + String.format("%7d", recordsRejected));
			logger.info("--------------------------------------- +");
			logger.info("Records processed             : " + String.format("%7d", recordsProcessed));
			logger.info(" ");

		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}
	}


	private void processRemote() throws IOException
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
			String xml = callOaiService(resumptionToken);
			++batch;
			resumptionToken = index(xml);
		} while (resumptionToken != null);

		logger.info("Deleting resumption token file");
		if (resTokenFile.exists()) {
			resTokenFile.delete();
		}
	}


	private void processLocal() throws IOException
	{
		Iterator<File> localFileIterator = getLocalFileIterator();
		File f;
		while (localFileIterator.hasNext()) {
			f = localFileIterator.next();
			logger.info("Processing file " + f.getCanonicalPath());
			index(FileUtil.getContents(f));
		}
	}


	private String index(String xml)
	{
		Document doc;
		logger.debug("Parsing XML");
		try {
			doc = builder.parse(StringUtil.asInputStream(xml));
		}
		catch (SAXException | IOException e) {
			throw ExceptionUtil.smash(e);
		}
		doc.normalize();
		NodeList records = doc.getElementsByTagName("record");
		int numRecords = records.getLength();
		logger.debug("Number of records in XML output: " + numRecords);

		List<ESMultiMediaObject> mediaObjects = new ArrayList<ESMultiMediaObject>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);

		for (int i = 0; i < numRecords; ++i) {
			++recordsProcessed;
			Element record = (Element) records.item(i);
			if (isDeletedRecord(record)) {
				++recordsSkipped;
				logger.debug("Skipped record with status \"deleted\"");
				// TODO delete media from ES index
			}
			else {
				List<ESMultiMediaObject> extractedMedia = null;
				try {
					extractedMedia = CrsMultiMediaTransfer.transfer(record, this);
				}
				catch (Throwable t) {
					++recordsRejected;
					logger.error("Error while processing record " + i + ": " + t.getMessage());
					logger.trace("Stack trace: ", t);
					continue;
				}
				if (extractedMedia != null) {
					List<String> extractedIds = new ArrayList<String>(extractedMedia.size());
					for (ESMultiMediaObject mo : extractedMedia) {
						extractedIds.add(ID_PREFIX + mo.getSourceSystemId());
					}
					mediaObjects.addAll(extractedMedia);
					ids.addAll(extractedIds);
					if (mediaObjects.size() >= bulkRequestSize) {
						try {
							index.saveObjects(LUCENE_TYPE_MULTIMEDIA_OBJECT, mediaObjects, ids);
							multimediaIndexed += mediaObjects.size();
						}
						finally {
							mediaObjects.clear();
							ids.clear();
						}
					}
				}
			}
			if (maxRecords > 0 && recordsProcessed >= maxRecords) {
				break;
			}
			if (recordsProcessed % 50000 == 0) {
				logger.info("Records processed: " + recordsProcessed);
				logger.info("Documents indexed: " + multimediaIndexed);
			}
		}
		if (!mediaObjects.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_MULTIMEDIA_OBJECT, mediaObjects, ids);
			multimediaIndexed += mediaObjects.size();
		}
		if (maxRecords > 0 && recordsProcessed >= maxRecords) {
			return null;
		}
		return DOMUtil.getDescendantValue(doc, "resumptionToken");
	}

	private static final SimpleDateFormat oaiDateFormatter = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");


	static String callOaiService(String resumptionToken)
	{
		String url;
		if (resumptionToken == null) {
			url = LoadUtil.getConfig().required("crs.multimedia.url.initial");
			int maxAge = LoadUtil.getConfig().required("crs.max_age", int.class);
			if (maxAge != 0) {
				DateTime now = new DateTime();
				DateTime wayback = now.minusHours(maxAge);
				url += "&from=" + oaiDateFormatter.format(wayback.toDate());
			}
		}
		else {
			url = String.format(LoadUtil.getConfig().required("crs.multimedia.url.resume"), resumptionToken);
		}
		logger.info("Calling service: " + url);
		byte[] response = new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
		return new String(response, Charset.forName("UTF-8"));
	}


	static String callOaiService(Date fromDate, Date untilDate)
	{
		String url = LoadUtil.getConfig().required("crs.multimedia.url.initial");
		if (fromDate != null) {
			url += "&from=" + oaiDateFormatter.format(fromDate);
		}
		if (untilDate != null) {
			url += "&until=" + oaiDateFormatter.format(untilDate);
		}
		logger.info("Calling service: " + url);
		byte[] response = new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
		return new String(response, Charset.forName("UTF-8"));
	}


	static Iterator<File> getLocalFileIterator()
	{
		logger.debug("Retrieving file list");
		String path = LoadUtil.getConfig().required("crs.local_dir");
		File[] files = new File(path).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				if (!name.startsWith("multimedia.")) {
					return false;
				}
				if (!name.endsWith(".oai.xml")) {
					return false;
				}
				return true;
			}
		});
		logger.debug("Sorting file list");
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
		return Arrays.asList(files).iterator();
	}


	static boolean isDeletedRecord(Element record)
	{
		if (!DOMUtil.getChild(record, "header").hasAttribute("status")) {
			return false;
		}
		return DOMUtil.getChild(record, "header").getAttribute("status").equals("deleted");
	}

	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyyMMddHHmmss");


	static String getLocalPath(String resToken)
	{
		String testDir = LoadUtil.getConfig().required("crs.local_dir");
		return String.format("%s/multimedia.%s.oai.xml", testDir, DF.format(new Date()));
	}


	private static File getResumptionTokenFile()
	{
		return new File(System.getProperty("java.io.tmpdir") + "/crs-multimedia.resumption-token");
	}

}
