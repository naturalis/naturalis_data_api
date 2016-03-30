package nl.naturalis.nba.etl.col;

import java.io.File;
import java.util.List;

import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.Registry;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

/**
 * Utility class that set the {@code synonyms} field in taxon documents to null. Can
 * be called before starting the {@link CoLSynonymImporter} to make sure you
 * start with a clean slate. Note though that kicking off the
 * {@link CoLTaxonImporter} provides the ultimate clean slate, because it starts
 * by removing all taxon documents.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLSynonymCleaner {

	public static void main(String[] args)
	{
		CoLSynonymCleaner cleaner = new CoLSynonymCleaner();
		ConfigObject config = Registry.getInstance().getConfig();
		String dwcaDir = config.required("col.csv_dir");
		cleaner.cleanup(dwcaDir + "/taxa.txt");
	}

	static final Logger logger = Registry.getInstance().getLogger(CoLSynonymCleaner.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public CoLSynonymCleaner()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Processes the reference.txt file and for each CSV record, extracts the ID
	 * of the referenced taxon, using it to remove all synonyms from the
	 * corresponding Lucene document.
	 * 
	 * @param path
	 */
	public void cleanup(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLTaxonCsvField> extractor = null;
		CoLTaxonLoader loader = null;
		CoLSynonymTransformer transformer = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			stats = new ETLStatistics();
			stats.setNested(true);
			extractor = createExtractor(stats, f);
			loader = new CoLTaxonLoader(stats, esBulkRequestSize);
			transformer = new CoLSynonymTransformer(stats);
			transformer.setSuppressErrors(suppressErrors);
			transformer.setLoader(loader);
			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo<CoLTaxonCsvField> rec : extractor) {
				if (rec == null)
					continue;
				List<ESTaxon> taxa = transformer.clean(rec);
				loader.load(taxa);
				if (rec.getLineNumber() % 50000 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
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


	private CSVExtractor<CoLTaxonCsvField> createExtractor(ETLStatistics stats, File f)
	{
		CSVExtractor<CoLTaxonCsvField> extractor;
		extractor = new CSVExtractor<>(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
