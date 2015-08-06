package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.MimeTypeCache;
import nl.naturalis.nda.elasticsearch.load.MimeTypeCacheFactory;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.IOUtil;
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

	private final DocumentBuilder builder;

	private final Index index;
	private final int bulkRequestSize;
	private final int maxRecords;
	private final boolean forceRestart;

	private final CrsMultiMediaTransfer transfer;

	int recordsSkipped;
	int recordsProcessed;
	/*
	 * Records that are not skipped. recordsSkipped + recordsInvestigated =
	 * recordsProcessed
	 */
	int recordsInvestigated;
	int recordsRejected;

	int multimediaSkipped;
	int multimediaProcessed;
	int multimediaRejected;
	int multimediaIndexed;

	int nonMedialibUrls;


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

		transfer = new CrsMultiMediaTransfer(this);
	}


	public void importMultiMedia()
	{

		long start = System.currentTimeMillis();

		recordsProcessed = 0;
		recordsSkipped = 0;
		recordsInvestigated = 0;
		recordsRejected = 0;

		multimediaProcessed = 0;
		multimediaSkipped = 0;
		multimediaRejected = 0;
		multimediaIndexed = 0;

		nonMedialibUrls = 0;

		MimeTypeCache mtc = null;

		try {

			ThemeCache.getInstance().resetMatchCounters();

			mtc = MimeTypeCacheFactory.getInstance().getCache();
			mtc.resetCounters();

			index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.CRS.getCode());

			if (LoadUtil.getConfig().isTrue("crs.use_local")) {
				processLocal();
			}
			else {
				processRemote();
			}

			ThemeCache.getInstance().logMatchInfo();

			logger.info(" ");
			logger.info("Mime type cache hits          : " + String.format("%7d", mtc.getCacheHits()));
			logger.info("Mime type cache misses        : " + String.format("%7d", mtc.getMedialibRequests()));
			logger.info("Non-medialib URLs             : " + String.format("%7d", nonMedialibUrls));

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

			logger.info("Total duration: " + LoadUtil.getDuration(start));

		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}
		finally {
			IOUtil.close(mtc);
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
			byte[] response = callOaiService(resumptionToken);
			++batch;
			resumptionToken = index(response);
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
			logger.info("Processing file " + f.getAbsolutePath());
			index(Files.readAllBytes(f.toPath()));
		}
	}


	private String index(byte[] xml)
	{
		Document doc;
		logger.debug("Parsing XML");
		try {
			doc = builder.parse(new ByteArrayInputStream(xml));
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
				// TODO delete media from ES index
				++recordsSkipped;
				if (logger.isDebugEnabled()) {
					logger.debug("Skipped record with status \"deleted\"");
				}
			}
			else {
				List<ESMultiMediaObject> extractedMedia = null;
				try {
					extractedMedia = transfer.transfer(record);
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
						extractedIds.add(CrsImportAll.ID_PREFIX + mo.getSourceSystemId());
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


	static byte[] callOaiService(String resumptionToken)
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
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
	}


	static byte[] callOaiService(Date fromDate, Date untilDate)
	{
		String url = LoadUtil.getConfig().required("crs.multimedia.url.initial");
		if (fromDate != null) {
			url += "&from=" + oaiDateFormatter.format(fromDate);
		}
		if (untilDate != null) {
			url += "&until=" + oaiDateFormatter.format(untilDate);
		}
		logger.info("Calling service: " + url);
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
	}


	static Iterator<File> getLocalFileIterator()
	{
		logger.debug("Retrieving file list");
		File path = LoadUtil.getConfig().getDirectory("crs.local_dir");
		File[] files = path.listFiles(new FilenameFilter() {
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
		Arrays.sort(files);
		return Arrays.asList(files).iterator();
	}


	static boolean isDeletedRecord(Element record)
	{
		if (!DOMUtil.getChild(record, "header").hasAttribute("status")) {
			return false;
		}
		return DOMUtil.getChild(record, "header").getAttribute("status").equals("deleted");
	}


	private static File getResumptionTokenFile()
	{
		return new File(System.getProperty("java.io.tmpdir") + "/crs-multimedia.resumption-token");
	}

}
