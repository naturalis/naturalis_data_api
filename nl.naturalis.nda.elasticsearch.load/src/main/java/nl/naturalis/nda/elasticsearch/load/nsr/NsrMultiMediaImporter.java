package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexManager;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.InvalidDataException;
import nl.naturalis.nda.elasticsearch.load.MalformedDataException;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.SkippableDataException;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class that imports multimedia from NSR.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrMultiMediaImporter {

	public static void main(String[] args) throws Exception
	{
		IndexManagerNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			NsrMultiMediaImporter importer = new NsrMultiMediaImporter(index);
			importer.importXmlFiles();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = Registry.getInstance().getLogger(NsrMultiMediaImporter.class);

	private final IndexManager index;

	private final int bulkRequestSize;
	private final boolean backup;

	private int totalNumTaxa = 0;
	private int totalNumImages = 0;
	private int totalNumBadTaxa = 0;


	public NsrMultiMediaImporter(IndexManager index)
	{
		this.index = index;
		String prop = System.getProperty(NsrImportAll.SYSPROP_BATCHSIZE, "1000");
		bulkRequestSize = Integer.parseInt(prop);
		prop = System.getProperty(NsrImportAll.SYSPROP_BACKUP, "0");
		backup = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importXmlFiles() throws Exception
	{
		File[] xmlFiles = NsrImportUtil.getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}
		logger.info("Deleting old NSR multimedia documents from document store");
		index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.NSR.getCode());
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		for (File xmlFile : xmlFiles) {
			logger.info("Processing file " + xmlFile.getCanonicalPath());
			Document document = builder.parse(xmlFile);
			importXmlFile(document);
		}
		if (backup) {
			NsrImportUtil.backupXmlFiles();
		}
		logger.info("Total number of taxa processed: " + totalNumTaxa);
		logger.info("Total number of bad taxa: " + totalNumBadTaxa);
		logger.info("Total number of imported images: " + totalNumImages);
		logger.info(getClass().getSimpleName() + " finished");
	}


	public void importXmlFile(Document doc)
	{
		int numTaxa = 0;
		int numImages = 0;
		int numBadTaxa = 0;

		Element taxaElement = DOMUtil.getChild(doc.getDocumentElement());
		List<Element> taxonElements = DOMUtil.getChildren(taxaElement);

		List<ESMultiMediaObject> batch = new ArrayList<ESMultiMediaObject>(bulkRequestSize);

		for (Element taxonElement : taxonElements) {
			++totalNumTaxa;
			if (++numTaxa % 1000 == 0) {
				logger.info("Records processed: " + numTaxa);
			}
			try {
				List<ESMultiMediaObject> mmos = NsrMultiMediaTransfer.getImages(taxonElement);
				if (mmos != null) {
					batch.addAll(NsrMultiMediaTransfer.getImages(taxonElement));
				}
			}
			catch (SkippableDataException e) {
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Skipping record %s (\"%s\"): %s", (numTaxa + 1), name, e.getMessage());
				logger.debug(msg);
			}
			catch (MalformedDataException | InvalidDataException e) {
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Invalid or malformed data in record %s (\"%s\"): %s", (numTaxa + 1), name, e.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", e);
			}
			catch (Throwable t) {
				++numBadTaxa;
				++totalNumBadTaxa;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Error in taxon \"%s\": %s", name, t.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", t);
			}
			if (batch.size() >= bulkRequestSize) {
				index.saveObjects(LUCENE_TYPE_MULTIMEDIA_OBJECT, batch);
				numImages += batch.size();
				totalNumImages += batch.size();
				batch.clear();
			}
		}
		if (!batch.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_MULTIMEDIA_OBJECT, batch);
			numImages += batch.size();
			totalNumImages += batch.size();
		}

		logger.info("Number of taxa processed: " + numTaxa);
		logger.info("Number of bad taxa: " + numBadTaxa);
		logger.info("Number of imported images: " + numImages);
	}

}
