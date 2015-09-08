package nl.naturalis.nda.elasticsearch.load;

import java.util.Arrays;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportAll;
import nl.naturalis.nda.elasticsearch.load.col.CoLImportAll;
import nl.naturalis.nda.elasticsearch.load.crs.CrsImportAll;
import nl.naturalis.nda.elasticsearch.load.nsr.NsrImportAll;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;

/**
 * Utility class for creating/managing/updating the NBA document store. Provides
 * a method for bootstrapping the NBA index (i.e. re-create it from scratch) and
 * for doing a full import.
 * 
 * @author ayco_holleman
 * 
 */
public class NBAImportAll {

	public static void main(String[] args)
	{
		IndexNative index = null;
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

	private final IndexNative index;

	public NBAImportAll(IndexNative index)
	{
		this.index = index;
	}

	/**
	 * Kicks of all individual import programs in the following order: NSR,
	 * Brahms, CRS, CoL.
	 */
	public void importAll()
	{

		long start = System.currentTimeMillis();

		try {

			logger.info("[>--- Starting NSR import ---<]");
			try {
				NsrImportAll nsrImportAll = new NsrImportAll(index);
				nsrImportAll.importAllPerType();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("NSR import Failed!");
			}

			logger.info("[>--- Starting Brahms import ---<]");
			try {
				BrahmsImportAll brahmsImportAll = new BrahmsImportAll();
				brahmsImportAll.importPerFile();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("Brahms import Failed!");
			}

			logger.info("[>--- Starting CRS import ---<]");
			try {
				CrsImportAll crsImportAll = new CrsImportAll(index);
				crsImportAll.importAll();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("CRS specimen import Failed!");
			}

			logger.info("[>--- Starting CoL import ---<]");
			try {
				CoLImportAll colImportAll = new CoLImportAll(index);
				colImportAll.importAll();
			}
			catch (Throwable t) {
				logger.error(t.getMessage(), t);
				logger.error("CoL import Failed!");
			}

		}
		
		finally {
			LoadUtil.logDuration(logger, getClass(), start);
		}

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
	}
}
