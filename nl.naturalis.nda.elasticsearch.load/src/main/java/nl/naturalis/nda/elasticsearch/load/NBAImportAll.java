package nl.naturalis.nda.elasticsearch.load;

import java.util.Arrays;

import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportAll;
import nl.naturalis.nda.elasticsearch.load.col.CoLImportAll;
import nl.naturalis.nda.elasticsearch.load.crs.CrsImportAll;
import nl.naturalis.nda.elasticsearch.load.nsr.NsrImporter;

import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;

/**
 * The "main" class of the import library. Start here. Allows you to bootstrap
 * the NBA index (i.e. create an empty NBA index) and to import all datasources
 * one by one. In other words this class lets you do a full import.
 * 
 * @author Ayco Holleman
 * 
 */
public class NBAImportAll {

	public static void main(String[] args)
	{
		IndexManagerNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			NBAImportAll indexManager = new NBAImportAll(index);
			if (args.length == 0 || Arrays.asList(args).contains("bootstrap")) {
				indexManager.bootstrap();
			}
			if (args.length == 0 || Arrays.asList(args).contains("import")) {
				indexManager.importAll();
			}
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	public static final String LUCENE_TYPE_TAXON = "Taxon";
	public static final String LUCENE_TYPE_SPECIMEN = "Specimen";
	public static final String LUCENE_TYPE_MULTIMEDIA_OBJECT = "MultiMediaObject";

	private static final Logger logger = Registry.getInstance().getLogger(NBAImportAll.class);

	private final IndexManagerNative index;

	public NBAImportAll(IndexManagerNative index)
	{
		this.index = index;
	}

	/**
	 * Runs all individual import programs in the following order: NSR, Brahms,
	 * CRS, CoL.
	 */
	public void importAll()
	{

		long start = System.currentTimeMillis();

		try {

			logger.info("[>--- Starting NSR import ---<]");
			try {
				NsrImporter nsrImporter = new NsrImporter();
				nsrImporter.importAll();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("NSR import Failed!");
			}

			logger.info("[>--- Starting Brahms import ---<]");
			try {
				BrahmsImportAll brahmsImportAll = new BrahmsImportAll();
				brahmsImportAll.importAll();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("Brahms import Failed!");
			}

			logger.info("[>--- Starting CRS import ---<]");
			try {
				CrsImportAll crsImportAll = new CrsImportAll();
				crsImportAll.importAll();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("CRS specimen import Failed!");
			}

			logger.info("[>--- Starting CoL import ---<]");
			try {
				CoLImportAll colImportAll = new CoLImportAll();
				colImportAll.importAll();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("CoL import Failed!");
			}

		}

		finally {
			LoadUtil.logDuration(logger, getClass(), start);
			Registry.getInstance().closeESClient();
		}

	}

	/**
	 * Creates the NDA schema from scratch. Will delete the entire index
	 * (mappings and documents) and then re-create it. !!! WATCH OUT !!!
	 */
	public void bootstrap()
	{
		index.delete();
		Registry reg = Registry.getInstance();
		String settings = FileUtil.getContents(reg.getFile("es-settings.json"));
		logger.info("Creating index using settings: " + settings);
		index.create(settings);
		String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
		index.addType(LUCENE_TYPE_TAXON, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
		index.addType(LUCENE_TYPE_SPECIMEN, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
		index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
	}
}
