package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.col.CoLImportUtil.createExtractor;

import java.io.File;
import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

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
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		remover.cleanup(dwcaDir + "/vernacular.txt");
	}

	static final Logger logger = Registry.getInstance().getLogger(CoLVernacularNameCleaner.class);

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
		CSVExtractor extractor = null;
		CoLTaxonLoader loader = null;
		CoLVernacularNameTransformer transformer = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			stats = new ETLStatistics();
			stats.setNested(true);
			extractor = createExtractor(stats, f, suppressErrors);
			loader = new CoLTaxonLoader(stats, esBulkRequestSize);
			transformer = new CoLVernacularNameTransformer(stats, loader);
			transformer.setSuppressErrors(suppressErrors);
			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo rec : extractor) {
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

}
