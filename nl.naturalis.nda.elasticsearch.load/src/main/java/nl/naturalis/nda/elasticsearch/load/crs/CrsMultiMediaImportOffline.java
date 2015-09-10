package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.NBAImportAll;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

/**
 * Class that manages the import of CRS multimedia. Data is sourced from files
 * on the local file system, presumable put there by the {@link CrsHarvester}.
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
		finally {
			Registry.getInstance().closeESClient();
		}
	}

	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(CrsMultiMediaImportOffline.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	private ETLStatistics stats;
	private CrsMultiMediaTransformer transformer;
	private CrsMultiMediaLoader loader;

	public CrsMultiMediaImportOffline()
	{
		suppressErrors = ConfigObject.isEnabled("crs.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
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
		LoadUtil.truncate(NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT, SourceSystem.CRS);
		stats = new ETLStatistics();
		transformer = new CrsMultiMediaTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		loader = new CrsMultiMediaLoader(stats, esBulkRequestSize);
		ThemeCache.getInstance().resetMatchCounters();
		try {
			for (File f : xmlFiles)
				importFile(f);
		}
		finally {
			IOUtil.close(loader);
		}
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger);
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void importFile(File f)
	{
		logger.info("Processing file " + f.getName());
		CrsExtractor extractor = new CrsExtractor(f, stats);
		for (XMLRecordInfo extracted : extractor) {
			List<ESMultiMediaObject> transformed = transformer.transform(extracted);
			loader.load(transformed);
			if (stats.recordsProcessed % 50000 == 0) {
				logger.info("Records processed: " + stats.recordsProcessed);
			}
		}
	}

	private static File[] getXmlFiles()
	{
		ConfigObject config = Registry.getInstance().getConfig();
		String path = config.required("crs.data_dir");
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
