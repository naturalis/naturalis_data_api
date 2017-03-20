package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.crs.CrsImportUtil.callSpecimenService;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Class that manages the import of CRS specimens, sourced through "live" calls
 * to the CRS OAI service.
 * 
 * @author Ayco Holleman
 * 
 * @see CrsSpecimenImportOffline
 *
 */
public class CrsSpecimenImport {

	public static void main(String[] args)
	{
		try {
			CrsSpecimenImport importer = new CrsSpecimenImport();
			importer.importSpecimens();
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(CrsSpecimenImport.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	private ETLStatistics stats;
	private CrsSpecimenTransformer transformer;
	private CrsSpecimenLoader loader;

	public CrsSpecimenImport()
	{
		suppressErrors = ConfigObject.isEnabled("crs.suppress-errors");
		String key = LoadConstants.SYSPROP_LOADER_QUEUE_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Import specimens through repetitive calls to the CRS OAI service.
	 */
	public void importSpecimens()
	{
		long start = System.currentTimeMillis();
		ESUtil.truncate(SPECIMEN, CRS);
		stats = new ETLStatistics();
		transformer = new CrsSpecimenTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		loader = new CrsSpecimenLoader(esBulkRequestSize, stats);
		ThemeCache.getInstance().resetMatchCounters();
		try {
			String resumptionToken = null;
			do {
				byte[] response = callSpecimenService(resumptionToken);
				resumptionToken = processResponse(response);
			} while (resumptionToken != null);
		}
		finally {
			IOUtil.close(loader);
		}
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger);
		ETLUtil.logDuration(logger, getClass(), start);
	}

	private String processResponse(byte[] bytes)
	{
		CrsExtractor extractor = new CrsExtractor(bytes, stats);
		for (XMLRecordInfo extracted : extractor) {
			List<Specimen> transformed = transformer.transform(extracted);
			loader.queue(transformed);
			if (stats.recordsProcessed % 50000 == 0) {
				logger.info("Records processed: " + stats.recordsProcessed);
			}
		}
		return extractor.getResumptionToken();
	}

}
