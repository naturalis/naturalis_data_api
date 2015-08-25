package nl.naturalis.nda.elasticsearch.load.col;

import java.io.File;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

public class CoLTaxonSynonymEnricher {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			CoLTaxonSynonymEnricher enricher = new CoLTaxonSynonymEnricher();
			String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
			enricher.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = Registry.getInstance().getLogger(CoLTaxonSynonymEnricher.class);

	private final boolean suppressErrors;
	
	public CoLTaxonSynonymEnricher()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
	}


	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CoLTaxonLoader loader = null;
		try {

			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);

			stats = new ETLStatistics();

			CSVExtractor extractor = new CSVExtractor(f, stats);
			extractor.setSkipHeader(true);
			extractor.setDelimiter('\t');
			extractor.setSuppressErrors(suppressErrors);

			loader = new CoLTaxonLoader(stats);

			CoLSynonymTransformer transformer = new CoLSynonymTransformer(stats);
			transformer.setSuppressErrors(suppressErrors);
			transformer.setLoader(loader);

			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo rec : extractor) {
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



}
