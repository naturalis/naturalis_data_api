package nl.naturalis.nba.etl;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.ESUtil;
import nl.naturalis.nba.etl.brahms.BrahmsImportAll;
import nl.naturalis.nba.etl.col.CoLImportAll;
import nl.naturalis.nba.etl.crs.CrsImportAll;
import nl.naturalis.nba.etl.ndff.NdffSpecimenImporter;
import nl.naturalis.nba.etl.nsr.NsrImporter;

/**
 * The central class of the import library. Start here. Allows you to bootstrap
 * the NBA index (i.e. create an empty NBA index) and to import all datasources
 * one by one. In other words this class lets you do a full import.
 * 
 * @author Ayco Holleman
 * 
 */
public class NBAImportAll {

	public static void main(String[] args)
	{
		try {
			if (args.length == 0 || Arrays.asList(args).contains("bootstrap")) {
				bootstrap();
			}
			if (args.length == 0 || Arrays.asList(args).contains("import")) {
				NBAImportAll nbaImportAll = new NBAImportAll();
				nbaImportAll.importAll();
			}
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		finally {
			// ...
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(NBAImportAll.class);

	/**
	 * Runs all individual import programs in the following order: NSR, Brahms,
	 * CRS, CoL.
	 */
	public void importAll()
	{

		long start = System.currentTimeMillis();

		try {

			logger.info("[>--- Starting NDFF import ---<]");
			NdffSpecimenImporter ndffImporter = new NdffSpecimenImporter();
			ndffImporter.importSpecimens();

			logger.info("[>--- Starting NSR import ---<]");
			NsrImporter nsrImporter = new NsrImporter();
			nsrImporter.importAll();

			logger.info("[>--- Starting Brahms import ---<]");
			BrahmsImportAll brahmsImportAll = new BrahmsImportAll();
			brahmsImportAll.importAll();

			logger.info("[>--- Starting CRS import ---<]");
			CrsImportAll crsImportAll = new CrsImportAll();
			crsImportAll.importAll();

			logger.info("[>--- Starting CoL import ---<]");
			CoLImportAll colImportAll = new CoLImportAll();
			colImportAll.importAll();

		}
		catch (Throwable t) {
			logger.error("NBA Import failed!");
		}
		finally {
			LoadUtil.logDuration(logger, getClass(), start);
			ESClientManager.getInstance().closeClient();
		}

		int i = MimeTypeCacheFactory.getInstance().getCache().getMisses();
		if (i != 0) {
			String fmt = "%s mime type cache lookup failures";
			logger.warn(String.format(fmt, String.valueOf(i)));
			logger.warn("The mime type cache is out-of-date!");
		}
	}

	/**
	 * Creates the NBA indices from scratch. Will delete any pre-existing
	 * indices used by the NBA and then re-create them. All data will be lost.
	 * WATCH OUT!
	 */
	public static void bootstrap()
	{
		ESUtil.deleteAllIndices();
		ESUtil.createAllIndices();
//		index.delete();
//		Registry registry = Registry.getInstance();
//		File settingsFile = registry.getFile("es-settings.json");
//		String settings = FileUtil.getContents(settingsFile);
//		logger.debug("Creating index using\n" + settings);
//		index.create(settings);
//		MappingFactory mappingFactory = new MappingFactory();
//		MappingSerializer serializer = MappingSerializer.getInstance();
//		if (logger.isDebugEnabled()) {
//			serializer.setPretty(true);
//		}
//
//		Mapping mapping = mappingFactory.getMapping(ESSpecimen.class);
//		String json = serializer.serialize(mapping);
//		if (logger.isDebugEnabled()) {
//			logger.debug("*********************************************");
//			logger.debug("Mapping for type Specimen:\n" + json);
//			logger.debug("*********************************************");
//		}
//		index.addType(LUCENE_TYPE_SPECIMEN, json);
//
//		mapping = mappingFactory.getMapping(ESMultiMediaObject.class);
//		json = serializer.serialize(mapping);
//		if (logger.isDebugEnabled()) {
//			logger.debug("*********************************************");
//			logger.debug("Mapping for type MultiMediaObject:\n" + json);
//			logger.debug("*********************************************");
//		}
//		index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, json);
//
//		mapping = mappingFactory.getMapping(ESTaxon.class);
//		json = serializer.serialize(mapping);
//		if (logger.isDebugEnabled()) {
//			logger.debug("*********************************************");
//			logger.debug("Mapping for type Taxon:\n" + json);
//			logger.debug("*********************************************");
//		}
//		index.addType(LUCENE_TYPE_TAXON, json);
	}
}
