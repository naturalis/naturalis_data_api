package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.xml.sax.SAXException;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.etl.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nba.etl.normalize.SexNormalizer;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;

/**
 * Class that manages the import of CRS specimens, sourced from files on the
 * local file system. These files have most likely been put there by means of
 * the {@link CrsHarvester}.
 * 
 * @author Ayco Holleman
 *
 */
public class CrsSpecimenImportOffline {

	public static void main(String[] args)
	{
		try {
			CrsSpecimenImportOffline importer = new CrsSpecimenImportOffline();
			importer.importSpecimens();
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(CrsSpecimenImportOffline.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	private ETLStatistics stats;
	private CrsSpecimenTransformer transformer;
	private CrsSpecimenLoader loader;

	public CrsSpecimenImportOffline()
	{
		suppressErrors = ConfigObject.isEnabled("crs.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Import specimens from the data directory configured in
	 * nda-import.properties.
	 */
	public void importSpecimens()
	{
		long start = System.currentTimeMillis();
		File[] xmlFiles = getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.error("No specimen oai.xml files found. Check nda-import.propties");
			return;
		}
		LoadUtil.truncate(SPECIMEN, CRS);
		stats = new ETLStatistics();
		transformer = new CrsSpecimenTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		loader = new CrsSpecimenLoader(stats, esBulkRequestSize);
		SexNormalizer.getInstance().resetStatistics();
		SpecimenTypeStatusNormalizer.getInstance().resetStatistics();
		PhaseOrStageNormalizer.getInstance().resetStatistics();
		ThemeCache.getInstance().resetMatchCounters();
		try {
			for (File f : xmlFiles)
				importFile(f);
		}
		finally {
			IOUtil.close(loader);
		}
		SexNormalizer.getInstance().logStatistics();
		SpecimenTypeStatusNormalizer.getInstance().logStatistics();
		PhaseOrStageNormalizer.getInstance().logStatistics();
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger);
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void importFile(File f)
	{
		logger.info("Processing file " + f.getName());
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
			List<Specimen> transformed = transformer.transform(extracted);
			loader.load(transformed);
			if (stats.recordsProcessed % 50000 == 0) {
				logger.info("Records processed: " + stats.recordsProcessed);
			}
		}
	}

	private static File[] getXmlFiles()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String path = config.required("crs.data.dir");
		logger.info("Data directory for CRS specimen import: " + path);
		File[] files = new File(path).listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name)
			{
				if (!name.startsWith("specimens.")) {
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
