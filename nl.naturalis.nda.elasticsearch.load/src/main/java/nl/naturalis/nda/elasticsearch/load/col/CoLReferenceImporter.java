package nl.naturalis.nda.elasticsearch.load.col;

import java.io.File;
import java.util.List;

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

/**
 * Enriches Taxon documents with literature references sourced from the
 * reference.txt file in the DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLReferenceImporter {

	public static void main(String[] args) throws Exception
	{
		CoLReferenceImporter importer = new CoLReferenceImporter();
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		importer.importCsv(dwcaDir + "/reference.txt");
	}

	static final Logger logger = Registry.getInstance().getLogger(CoLSynonymImporter.class);

	private final boolean suppressErrors;

	public CoLReferenceImporter()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
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
		CoLTaxonLoader loader = null;
		try {

			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);

			stats = new ETLStatistics();
			stats.setObjectsAcceptedNotObjectsIndexed(true);

			CSVExtractor extractor = new CSVExtractor(f, stats);
			extractor.setSkipHeader(true);
			extractor.setDelimiter('\t');
			extractor.setSuppressErrors(suppressErrors);

			loader = new CoLTaxonLoader(stats);

			CoLReferenceTransformer transformer = new CoLReferenceTransformer(stats);
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
		LoadUtil.logDuration(logger, getClass(), start);

	}
}
