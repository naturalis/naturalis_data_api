package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.ESUtil;
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
 * Enriches Taxon documents with literature references sourced from the
 * reference.txt file in a CoL DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLReferenceImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			CoLReferenceImporter importer = new CoLReferenceImporter();
			String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
			importer.importCsv(dwcaDir + "/reference.txt");
		}
		finally {
			ESUtil.refreshIndex(TAXON);
			ESClientManager.getInstance().closeClient();

		}
	}

	static final Logger logger = ETLRegistry.getInstance().getLogger(CoLReferenceImporter.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public CoLReferenceImporter()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Processes the reference.txt file
	 * 
	 * @param path
	 */
	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLReferenceCsvField> extractor = null;
		CoLReferenceTransformer transformer = null;
		CoLTaxonLoader loader = null;
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
