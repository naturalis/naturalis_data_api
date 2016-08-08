package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.es.DocumentType.TAXON;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.util.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;

/**
 * Imports taxa from the taxa.txt file. This is the only import program for the
 * CoL that actually creates documents. The other programs only enrich existing
 * documents (e.g. with synonym data, vernacular names and literature
 * references).
 * 
 * @author Ayco Holleman
 *
 */
public class CoLTaxonImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			CoLTaxonImporter importer = new CoLTaxonImporter();
			String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
			importer.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			ESUtil.refreshIndex(TAXON);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(CoLTaxonImporter.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;
	private final String colYear;

	public CoLTaxonImporter()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "5000");
		esBulkRequestSize = Integer.parseInt(val);
		colYear = DaoRegistry.getInstance().getConfiguration().required("col.year");
	}

	/**
	 * Imports CoL taxa into ElasticSearch.
	 * 
	 * @param path
	 */
	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLTaxonCsvField> extractor = null;
		CoLTaxonTransformer transformer = null;
		CoLTaxonLoader loader = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			LoadUtil.truncate(TAXON, SourceSystem.COL);
			stats = new ETLStatistics();
			extractor = createExtractor(stats, f);
			transformer = new CoLTaxonTransformer(stats);
			transformer.setColYear(colYear);
			transformer.setSuppressErrors(suppressErrors);
			loader = new CoLTaxonLoader(stats, esBulkRequestSize);
			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo<CoLTaxonCsvField> rec : extractor) {
				if (rec == null) {
					continue;
				}
				List<ESTaxon> taxa = transformer.transform(rec);
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
		logger.info("(NB skipped records are synonyms or higher taxa)");
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
