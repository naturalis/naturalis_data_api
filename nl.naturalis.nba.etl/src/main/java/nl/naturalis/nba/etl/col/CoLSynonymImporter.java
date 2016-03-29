package nl.naturalis.nba.etl.col;

import java.io.File;
import java.util.List;

import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

/**
 * Enriches CoL taxon documents with synonyms sourced from the taxa&#46;txt
 * file in a CoL DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLSynonymImporter {

	public static void main(String[] args) throws Exception
	{
		CoLSynonymImporter importer = new CoLSynonymImporter();
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		importer.importCsv(dwcaDir + "/taxa.txt");
	}

	private static final Logger logger = Registry.getInstance().getLogger(CoLSynonymImporter.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public CoLSynonymImporter()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Extracts and imports synonyms from the taxa&#46;txt file.
	 * 
	 * @param path
	 */
	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLTaxonCsvField> extractor = null;
		CoLSynonymTransformer transformer = null;
		CoLTaxonLoader loader = null;
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
		logger.info("(NB skipped records are accepted names)");
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
