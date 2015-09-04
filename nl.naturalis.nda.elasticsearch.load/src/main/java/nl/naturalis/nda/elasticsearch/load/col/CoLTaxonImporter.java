package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;

import java.io.File;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
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

public class CoLTaxonImporter {

	public static void main(String[] args) throws Exception
	{
		CoLTaxonImporter importer = new CoLTaxonImporter();
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		importer.importCsv(dwcaDir + "/taxa.txt");
	}

	private static final Logger logger = Registry.getInstance().getLogger(CoLTaxonImporter.class);

	private final boolean suppressErrors;
	private final int esBulkRequestSize;
	private final String colYear;

	public CoLTaxonImporter()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
		colYear = Registry.getInstance().getConfig().required("col.year");
	}

	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor extractor = null;
		CoLTaxonTransformer transformer = null;
		CoLTaxonLoader loader = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			LoadUtil.truncate(LUCENE_TYPE_TAXON, SourceSystem.COL);
			stats = new ETLStatistics();
			extractor = new CSVExtractor(f, stats);
			extractor.setSkipHeader(true);
			extractor.setDelimiter('\t');
			extractor.setSuppressErrors(suppressErrors);
			transformer = new CoLTaxonTransformer(stats);
			transformer.setColYear(colYear);
			transformer.setSuppressErrors(suppressErrors);
			loader = new CoLTaxonLoader(stats, esBulkRequestSize);
			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo rec : extractor) {
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
		logger.info("(NB skipped records are synonyms)");
		LoadUtil.logDuration(logger, getClass(), start);

	}

}
