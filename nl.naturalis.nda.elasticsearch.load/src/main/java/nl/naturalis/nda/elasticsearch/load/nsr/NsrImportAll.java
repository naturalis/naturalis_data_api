package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Class that imports both taxa and multimedia from NSR.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrImportAll {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = LoadUtil.getNbaIndexManager();
			NsrImportAll importer = new NsrImportAll(index);
			importer.importAllPerType();
		}
		catch (Throwable t) {
			logger.error("NSR import failed!");
			logger.error(t.getMessage(), t);
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	public static final String ID_PREFIX = "NSR-";
	public static final String SYSPROP_BACKUP = "nl.naturalis.nda.elasticsearch.load.nsr.backup";
	public static final String SYSPROP_BATCHSIZE = "nl.naturalis.nda.elasticsearch.load.nsr.batchsize";

	private static final Logger logger = LoggerFactory.getLogger(NsrImportAll.class);

	private final IndexNative index;
	private final boolean backup;


	public NsrImportAll(IndexNative index)
	{
		this.index = index;
		String prop = System.getProperty(SYSPROP_BACKUP, "1");
		backup = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	/**
	 * First import all taxa from all files, then all multimedia from all files.
	 * 
	 * @throws Exception
	 */
	public void importAllPerType() throws Exception
	{
		if (NsrImportUtil.getXmlFiles().length == 0) {
			logger.info("No XML files to process");
			return;
		}
		NsrTaxonImporter taxonImporter = new NsrTaxonImporter(index);
		taxonImporter.importXmlFiles();
		NsrMultiMediaImporter mediaImporter = new NsrMultiMediaImporter(index);
		mediaImporter.importXmlFiles();
		if (backup) {
			NsrImportUtil.backupXmlFiles();
		}
		logger.info(getClass().getSimpleName() + " finished");
	}


	/**
	 * For each XML file, first import all taxa contained in it, then all
	 * multimedia.
	 * 
	 * @throws Exception
	 */
	public void importAllPerFile() throws Exception
	{
		if (NsrImportUtil.getXmlFiles().length == 0) {
			logger.info("No XML files to process");
			return;
		}
		NsrTaxonImporter taxonImporter = new NsrTaxonImporter(index);
		NsrMultiMediaImporter multimediaImporter = new NsrMultiMediaImporter(index);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		for (File xmlFile : NsrImportUtil.getXmlFiles()) {
			logger.info("Processing file " + xmlFile.getCanonicalPath());
			Document document = builder.parse(xmlFile);
			taxonImporter.importXmlFile(document);
			multimediaImporter.importXmlFile(document);
		}
		if (backup) {
			NsrImportUtil.backupXmlFiles();
		}
		logger.info(getClass().getSimpleName() + " finished");
	}

}
