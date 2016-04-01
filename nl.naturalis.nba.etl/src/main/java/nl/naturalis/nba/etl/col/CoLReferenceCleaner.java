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
 * Utility class that sets the {@code references} field in taxon documents to null.
 * Can be called before starting the {@link CoLReferenceImporter} to make sure
 * you start with a clean slate. Note though that kicking off the
 * {@link CoLTaxonImporter} provides the ultimate clean slate, because it starts
 * by removing all taxon documents.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLReferenceCleaner {

	public static void main(String[] args)
	{
		CoLReferenceCleaner remover = new CoLReferenceCleaner();
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		remover.cleanup(dwcaDir + "/reference.txt");
	}

	static final Logger logger = Registry.getInstance().getLogger(CoLReferenceCleaner.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public CoLReferenceCleaner()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Processes the reference.txt file and for each CSV record, extracts the ID
	 * of the referenced taxon, using it to remove all literature references
	 * from the corresponding Lucene document.
	 * 
	 * @param path
	 */
	public void cleanup(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLReferenceCsvField> extractor = null;
		CoLTaxonLoader loader = null;
		CoLReferenceTransformer transformer = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			stats = new ETLStatistics();
			stats.setNested(true);
			extractor = createExtractor(stats, f);
			loader = new CoLTaxonLoader(stats, esBulkRequestSize);
			transformer = new CoLReferenceTransformer(stats, loader);
			transformer.setSuppressErrors(suppressErrors);
			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo<CoLReferenceCsvField> rec : extractor) {
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


	private CSVExtractor<CoLReferenceCsvField> createExtractor(ETLStatistics stats, File f)
	{
		CSVExtractor<CoLReferenceCsvField> extractor;
		extractor = new CSVExtractor<>(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}