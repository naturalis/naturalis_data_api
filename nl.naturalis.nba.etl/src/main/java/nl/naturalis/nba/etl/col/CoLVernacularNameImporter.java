package nl.naturalis.nba.etl.col;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Enriches CoL taxa with vernacular name information.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLVernacularNameImporter {

	public static void main(String[] args) throws Exception
	{
		CoLVernacularNameImporter importer = new CoLVernacularNameImporter();
		String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
		importer.importCsv(dwcaDir + "/vernacular.txt");
	}

	static final Logger logger = ETLRegistry.getInstance()
			.getLogger(CoLVernacularNameImporter.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public CoLVernacularNameImporter()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Processes the reference.txt file to enrich CoL taxa with vernacular
	 * names.
	 * 
	 * @param path
	 */
	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLVernacularNameCsvField> extractor = null;
		CoLVernacularNameTransformer transformer = null;
		CoLTaxonLoader loader = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			stats = new ETLStatistics();
			stats.setNested(true);
			extractor = createExtractor(stats, f);
			loader = new CoLTaxonLoader(stats, esBulkRequestSize);
			transformer = new CoLVernacularNameTransformer(stats, loader);
			transformer.setSuppressErrors(suppressErrors);
			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo<CoLVernacularNameCsvField> rec : extractor) {
				if (rec == null)
					continue;
				List<Taxon> taxa = transformer.transform(rec);
				loader.load(taxa);
				if (rec.getLineNumber() % 50000 == 0)
					logger.info("Records processed: " + rec.getLineNumber());
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		finally {
			IOUtil.close(loader);
		}
		stats.logStatistics(logger);
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private CSVExtractor<CoLVernacularNameCsvField> createExtractor(ETLStatistics stats, File f)
	{
		CSVExtractor<CoLVernacularNameCsvField> extractor;
		extractor = new CSVExtractor<>(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
