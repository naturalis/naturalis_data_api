package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.MedialibMimeTypeCache;

import static nl.naturalis.nda.elasticsearch.load.MedialibMimeTypeCache.MEDIALIB_URL_START;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class that retrieves mime types for all media specified in the locally stored
 * OAI XML files. These files are the saved output from the CRS OAI interface
 * and can be created by the {@link CrsDownloader} or (on the fly) by the
 * {@link CrsMultiMediaImporter}. The {@code CrsMimeTypeRetriever} is itself not
 * part of the import procedure. Rather it should be called once to prebuild the
 * mime type cache, so that the import procedure itself does not waste time
 * waiting for the medialib.
 * 
 * @author Ayco Holleman
 *
 */
public class CrsMimeTypeRetriever {

	public static void main(String[] args)
	{
		CrsMimeTypeRetriever retriever = new CrsMimeTypeRetriever();
		retriever.retrieveMimeTypes();
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsMimeTypeRetriever.class);

	private final MedialibMimeTypeCache crsCache;


	public CrsMimeTypeRetriever()
	{
		crsCache = MedialibMimeTypeCache.getCRSInstance();
	}


	public void retrieveMimeTypes()
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builderFactory.setNamespaceAware(false);
		int processed = 0;
		try {
			builder = builderFactory.newDocumentBuilder();
			Iterator<File> xmlFiles = CrsMultiMediaImporter.getLocalFileIterator();
			while (xmlFiles.hasNext()) {
				File xmlFile = xmlFiles.next();
				Document doc = builder.parse(xmlFile);
				doc.normalize();
				NodeList records = doc.getElementsByTagName("record");
				int numRecords = records.getLength();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Processing file %s (%s records)", xmlFile.getAbsolutePath(), numRecords));
				}
				for (int i = 0; i < numRecords; ++i) {
					if (++processed % 50000 == 0 && logger.isInfoEnabled()) {
						logger.info("Records processed: " + processed);
						logger.info("Medialib request: " + crsCache.getMedialibRequests());
						logger.info("Request failures: " + crsCache.getRequestFailures());
						logger.info("Cache size: " + crsCache.getSize());
					}
					Element record = (Element) records.item(i);
					if (CrsMultiMediaImporter.isDeletedRecord(record)) {
						continue;
					}
					Element dc = DOMUtil.getDescendant(record, "oai_dc:dc");
					List<Element> mediaFiles = DOMUtil.getDescendants(dc, "frmDigitalebestanden");
					if (mediaFiles == null) {
						continue;
					}
					for (Element mediaFile : mediaFiles) {
						String url = DOMUtil.getDescendantValue(mediaFile, "abcd:fileuri");
						if (url == null) {
							continue;
						}
						url = url.trim();
						if (!url.startsWith(MEDIALIB_URL_START)) {
							continue;
						}
						String unitID = url.substring(MEDIALIB_URL_START.length() + 1);
						int x = unitID.indexOf('/');
						if (x != -1) {
							unitID = unitID.substring(0, x);
						}
						crsCache.getMimeType(unitID);
					}
				}
			}
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				logger.info("Records processed: " + processed);
				logger.info("Medialib request: " + crsCache.getMedialibRequests());
				logger.info("Request failures: " + crsCache.getRequestFailures());
				logger.info("Cache size: " + crsCache.getSize());
				crsCache.close();
			}
			catch (IOException e) {
				logger.error("Error saving mime type cache to file system", e);
			}
		}
	}

}
