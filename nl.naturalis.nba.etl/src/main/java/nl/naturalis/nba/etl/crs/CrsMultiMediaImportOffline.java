package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.DocumentObjectWriter;
import nl.naturalis.nba.etl.ETLConstants;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.etl.MimeTypeCacheFactory;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.etl.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nba.etl.normalize.SexNormalizer;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Class that manages the import of CRS multimedia, sourced from files on the
 * local file system. These files have most likely been put there by means of
 * the {@link CrsHarvester}.
 * 
 * @author Ayco Holleman
 *
 */
public class CrsMultiMediaImportOffline {

	public static void main(String[] args)
	{
		try {
			CrsMultiMediaImportOffline importer = new CrsMultiMediaImportOffline();
			importer.importMultimedia();
		}
		catch (Throwable t) {
			logger.error("CrsImportAll terminated unexpectedly!", t);
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(CrsMultiMediaImportOffline.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	private ETLStatistics stats;
	private CrsMultiMediaTransformer transformer;
	private DocumentObjectWriter<MultiMediaObject> loader;

	public CrsMultiMediaImportOffline()
	{
		suppressErrors = ConfigObject.isEnabled("suppressErrors");
		String key = ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Import multimedia from the data directory configured in
	 * nda-import.properties.
	 */
	public void importMultimedia()
	{
		long start = System.currentTimeMillis();
		File[] xmlFiles = getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.error("No multimedia oai.xml files found. Check nda-import.propties");
			return;
		}
		ETLUtil.truncate(MULTI_MEDIA_OBJECT, CRS);
		int cacheFailuresBegin = MimeTypeCacheFactory.getInstance().getCache().getMisses();
		stats = new ETLStatistics();
		stats.setOneToMany(true);
		transformer = new CrsMultiMediaTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);

		SexNormalizer.getInstance().resetStatistics();
		SpecimenTypeStatusNormalizer.getInstance().resetStatistics();
		PhaseOrStageNormalizer.getInstance().resetStatistics();
		ThemeCache.getInstance().resetMatchCounters();
		
		try {
			for (File f : xmlFiles)
				importFile(f);
		}
		finally {
		  logger.info("Finished importing {} files", xmlFiles.length);
		}
		SexNormalizer.getInstance().logStatistics();
		SpecimenTypeStatusNormalizer.getInstance().logStatistics();
		PhaseOrStageNormalizer.getInstance().logStatistics();
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger);
		ETLUtil.logDuration(logger, getClass(), start);
		int cacheFailuresEnd = MimeTypeCacheFactory.getInstance().getCache().getMisses();
		if (cacheFailuresBegin != cacheFailuresEnd) {
			int misses = cacheFailuresEnd - cacheFailuresBegin;
			String fmt = "%s mime type cache lookup failures for CRS multimedia";
			logger.warn(String.format(fmt, String.valueOf(misses)));
			logger.warn("THE MIME TYPE CACHE IS OUT-OF-DATE!");
		}
	}

	private void importFile(File f)
	{
		logger.info("Processing file {}", f.getName());
		if (DaoRegistry.getInstance().getConfiguration().get("etl.output", "file").equals("file")) {
      logger.info("ETL Output: Writing the multimedia documents to the file system");
      loader = new CrsMultiMediaJsonNDWriter(f.getName(), stats);
    }
    else {
      logger.info("ETL Output: loading the multimedia documents into the document store");
      loader = new CrsMultiMediaLoader(esBulkRequestSize, stats);
    }

		CrsExtractor extractor;
		try {
			extractor = new CrsExtractor(f, stats);
		}
		catch (SAXException e) {
			logger.error("Processing failed!");
			logger.error(e.getMessage());
			return;
		}
		for (XMLRecordInfo extracted : extractor) {
			List<MultiMediaObject> transformed = transformer.transform(extracted);
			loader.write(transformed);
			if (stats.recordsProcessed != 0 && stats.recordsProcessed % 50000 == 0) {
				logger.info("Records processed: {}", stats.recordsProcessed);
				logger.info("Documents indexed: {}", stats.documentsIndexed);
			}
		}
		IOUtil.close(loader);
	}

	private static File[] getXmlFiles()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String path = config.required("crs.data.dir");
		logger.info("Data directory for CRS multimedia import: " + path);
		File[] files = new File(path).listFiles(new FilenameFilter() {

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
		return files;
	}
}
