package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLConstants;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Enriches CoL taxa with vernacular name information.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLVernacularNameImporter extends CoLImporter {

	public static void main(String[] args)
	{
		CoLVernacularNameImporter importer = new CoLVernacularNameImporter();
		String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
		importer.importCsv(dwcaDir + "/vernacular.txt");
	}

	private static final Logger logger = getLogger(CoLVernacularNameImporter.class);

	public CoLVernacularNameImporter()
	{
		super();
	}

	/**
	 * Processes the reference.txt file to enrich CoL taxa with vernacular
	 * names.
	 * 
	 * @param path
	 */
	public void importCsv(String path)
	{
		if (ConfigObject.isEnabled(ETLConstants.SYSPROP_DRY_RUN)) {
			logger.info("Disabled in dry run: {}", getClass().getName());
			return;
		}
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
			loader = new CoLTaxonLoader(stats, loaderQueueSize);
			loader.enableQueueLookups(true);
			loader.suppressErrors(suppressErrors);
			transformer = new CoLVernacularNameTransformer(stats, loader);
			transformer.setSuppressErrors(suppressErrors);
			logger.info("Processing file {}", f.getAbsolutePath());
			for (CSVRecordInfo<CoLVernacularNameCsvField> rec : extractor) {
				if (rec == null)
					continue;
				List<Taxon> taxa = transformer.transform(rec);
				loader.queue(taxa);
				if (stats.recordsProcessed != 0 && stats.recordsProcessed % 50000 == 0) {
					logger.info("Records processed: {}", stats.recordsProcessed);
					logger.info("Documents indexed: {}", stats.documentsIndexed);
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		logger.info("Number of orphans: {}", transformer.getNumOrphans());
		stats.logStatistics(logger);
		logDuration(logger, getClass(), start);
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
