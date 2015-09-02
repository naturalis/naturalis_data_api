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
 * Utility class that set the "references" field in taxon documents to null. Can
 * be called before starting the {@link CoLReferenceImporter} to make sure you
 * start with a clean slate. Note though that kicking off the
 * {@link CoLTaxonImporter} provides the ultimate clean slate, because it starts
 * by removing all taxon documents.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLReferenceRemover {

	public static void main(String[] args) throws Exception
	{
		CoLReferenceRemover remover = new CoLReferenceRemover();
		String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
		remover.removeReferences(dwcaDir + "/reference.txt");
	}

	static final Logger logger = Registry.getInstance().getLogger(CoLReferenceRemover.class);

	private final boolean suppressErrors;

	public CoLReferenceRemover()
	{
		suppressErrors = ConfigObject.isEnabled("col.suppress-errors");
	}

	/**
	 * Processes the reference.txt file and for each CSV record, extracts the ID
	 * of the referenced taxon, and uses the ID to remove all literature
	 * references from the corresponding Lucene document.
	 * 
	 * @param path
	 */
	public void removeReferences(String path)
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

			CoLReferenceTransformer transformer = new CoLReferenceTransformer(stats, loader);
			transformer.setSuppressErrors(suppressErrors);

			logger.info("Processing file " + f.getAbsolutePath());
			for (CSVRecordInfo rec : extractor) {
				if (rec == null)
					continue;
				List<ESTaxon> taxa = transformer.removeReferences(rec);
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
