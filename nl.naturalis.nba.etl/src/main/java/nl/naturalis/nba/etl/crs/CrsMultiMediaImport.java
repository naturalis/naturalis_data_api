package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.es.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.etl.crs.CrsImportUtil.callMultimediaService;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.XMLRecordInfo;

/**
 * Class that manages the import of CRS multimedia, sourced through "live" calls
 * to the CRS OAI service.
 * 
 * @author Ayco Holleman
 * 
 * @see CrsMultiMediaImportOffline
 *
 */
public class CrsMultiMediaImport {

	public static void main(String[] args)
	{
		try {
			CrsMultiMediaImport importer = new CrsMultiMediaImport();
			importer.importMultimedia();
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(CrsMultiMediaImport.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	private ETLStatistics stats;
	private CrsMultiMediaTransformer transformer;
	private CrsMultiMediaLoader loader;

	public CrsMultiMediaImport()
	{
		suppressErrors = ConfigObject.isEnabled("crs.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Import multimedia through repetitive calls to the CRS OAI service.
	 */
	public void importMultimedia()
	{
		long start = System.currentTimeMillis();
		LoadUtil.truncate(MULTI_MEDIA_OBJECT, CRS);
		stats = new ETLStatistics();
		stats.setOneToMany(true);
		transformer = new CrsMultiMediaTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		loader = new CrsMultiMediaLoader(stats, esBulkRequestSize);
		ThemeCache.getInstance().resetMatchCounters();
		try {
			String resumptionToken = null;
			do {
				byte[] response = callMultimediaService(resumptionToken);
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
			List<ESMultiMediaObject> transformed = transformer.transform(extracted);
			loader.load(transformed);
			if (stats.recordsProcessed % 50000 == 0) {
				logger.info("Records processed: " + stats.recordsProcessed);
			}
		}
		return extractor.getResumptionToken();
	}

}