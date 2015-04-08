package nl.naturalis.nda.elasticsearch.load;

import java.util.Arrays;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsMultiMediaImporter;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter;
import nl.naturalis.nda.elasticsearch.load.col.CoLImportAll;
import nl.naturalis.nda.elasticsearch.load.crs.CrsMultiMediaImporter;
import nl.naturalis.nda.elasticsearch.load.crs.CrsSpecimenImporter;
import nl.naturalis.nda.elasticsearch.load.nsr.NsrImportAll;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for creating/managing/updating the ElasticSearch index for the
 * NDA.
 * 
 * @author ayco_holleman
 * 
 */
public class NDAIndexManager {

	public static void main(String[] args)
	{
		String indexName = LoadUtil.getConfig().required("elasticsearch.index.name");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), indexName);
		NDAIndexManager indexManager = new NDAIndexManager(index);
		if (args.length == 0 || Arrays.asList(args).contains("bootstrap")) {
			indexManager.bootstrap();
		}
		if (args.length == 0 || Arrays.asList(args).contains("import")) {
			indexManager.importAll();
		}
	}

	public static final String LUCENE_TYPE_TAXON = "Taxon";
	public static final String LUCENE_TYPE_SPECIMEN = "Specimen";
	public static final String LUCENE_TYPE_MULTIMEDIA_OBJECT = "MultiMediaObject";

	private static final Logger logger = LoggerFactory.getLogger(NDAIndexManager.class);

	private final IndexNative index;


	public NDAIndexManager(IndexNative index)
	{
		this.index = index;
	}


	public void importAll()
	{

		logger.info("[>--- Starting NSR import ---<]");
		try {
			NsrImportAll nsrImportAll = new NsrImportAll(index);
			nsrImportAll.importXmlFiles();
		}
		catch (Throwable t) {
			logger.error("NSR import Failed!");
			logger.error(t.getMessage(), t);
		}

		logger.info("[>--- Starting Brahms specimen import ---<]");
		try {
			BrahmsSpecimensImporter brahmsSpecimenImporter = new BrahmsSpecimensImporter(index);
			brahmsSpecimenImporter.importCsvFiles();
		}
		catch (Throwable t) {
			logger.error("Brahms specimen import Failed!");
			logger.error(t.getMessage(), t);
		}

		logger.info("[>--- Starting Brahms multimedia import ---<]");
		try {
			BrahmsMultiMediaImporter brahmsMultiMediaImporter = new BrahmsMultiMediaImporter(index);
			brahmsMultiMediaImporter.importCsvFiles();
		}
		catch (Throwable t) {
			logger.error("Brahms multimedia import Failed!");
			logger.error(t.getMessage(), t);
		}

		logger.info("[>--- Starting CRS specimen import ---<]");
		try {
			CrsSpecimenImporter crsSpecimenImporter = new CrsSpecimenImporter(index);
			crsSpecimenImporter.importSpecimens();
		}
		catch (Throwable t) {
			logger.error("CRS specimen import Failed!");
			logger.error(t.getMessage(), t);
		}

		logger.info("[>--- Starting CRS multimedia import ---<]");
		try {
			CrsMultiMediaImporter crsMultiMediaImporter = new CrsMultiMediaImporter(index);
			crsMultiMediaImporter.importMultiMedia();
		}
		catch (Throwable t) {
			logger.error("CRS multimedia import Failed!");
			logger.error(t.getMessage(), t);
		}

		logger.info("[>--- Starting CoL import ---<]");
		try {
			CoLImportAll colImportAll = new CoLImportAll(index);
			colImportAll.importAll();
		}
		catch (Throwable t) {
			logger.error("CoL import Failed!");
			logger.error(t.getMessage(), t);
		}

		logger.info("Ready");

	}


	/**
	 * Creates the NDA schema from scratch. Will delete the entire index
	 * (mappings and documents) and then re-create it. !!! WATCH OUT !!!
	 */
	public void bootstrap()
	{
		index.delete();
		String settings = StringUtil.getResourceAsString("/es-settings.json");
		logger.info("Creating index using settings: " + settings);
		index.create(settings);
		String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
		index.addType(LUCENE_TYPE_TAXON, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
		index.addType(LUCENE_TYPE_SPECIMEN, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
		index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		//logger.info(index.describe());
	}
}
