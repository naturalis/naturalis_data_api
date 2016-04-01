package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.etl.crs.CrsImportUtil.callSpecimenService;

import java.util.List;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.NBAImportAll;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.XMLRecordInfo;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

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
			Registry.getInstance().closeESClient();
		}
	}

	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(CrsSpecimenImport.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	private ETLStatistics stats;
	private CrsSpecimenTransformer transformer;
	private CrsSpecimenLoader loader;

	public CrsSpecimenImport()
	{
		suppressErrors = ConfigObject.isEnabled("crs.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Import specimens through repetitive calls to the CRS OAI service.
	 */
	public void importSpecimens()
	{
		long start = System.currentTimeMillis();
		LoadUtil.truncate(NBAImportAll.LUCENE_TYPE_SPECIMEN, SourceSystem.CRS);
		stats = new ETLStatistics();
		transformer = new CrsSpecimenTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		loader = new CrsSpecimenLoader(stats, esBulkRequestSize);
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
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private String processResponse(byte[] bytes)
	{
		CrsExtractor extractor = new CrsExtractor(bytes, stats);
		for (XMLRecordInfo extracted : extractor) {
			List<ESSpecimen> transformed = transformer.transform(extracted);
			loader.load(transformed);
			if (stats.recordsProcessed % 50000 == 0) {
				logger.info("Records processed: " + stats.recordsProcessed);
			}
		}
		return extractor.getResumptionToken();
	}

}