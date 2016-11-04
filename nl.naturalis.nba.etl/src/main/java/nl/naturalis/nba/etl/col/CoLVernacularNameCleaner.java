package nl.naturalis.nba.etl.col;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;

/**
 * Utility class that erases all vernacular names from taxon documents
 * referenced by CSV records in vernacular.txt. Can be called before starting
 * the {@link CoLVernacularNameImporter} to make sure you start with a clean
 * slate. Note though that kicking off the {@link CoLTaxonImporter} provides the
 * ultimate clean slate, because it starts by removing all taxon documents.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLVernacularNameCleaner {

	public static void main(String[] args)
	{
		CoLVernacularNameCleaner remover = new CoLVernacularNameCleaner();
		String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
		remover.cleanup(dwcaDir + "/vernacular.txt");
	}

	static final Logger logger = ETLRegistry.getInstance().getLogger(CoLVernacularNameCleaner.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public CoLVernacularNameCleaner()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Processes the vernacular.txt file and for each CSV record, extracts the ID
	 * of the referenced taxon, using it to remove all literature references
	 * from the corresponding Lucene document.
	 * 
	 * @param path
	 */
	public void cleanup(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLVernacularNameCsvField> extractor = null;
		CoLTaxonLoader loader = null;
		CoLVernacularNameTransformer transformer = null;
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
				List<Taxon> taxa = transformer.clean(rec);
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
