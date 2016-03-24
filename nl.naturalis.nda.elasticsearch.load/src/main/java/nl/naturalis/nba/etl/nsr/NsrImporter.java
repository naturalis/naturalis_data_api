package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_TAXON;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.backupXmlFile;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.backupXmlFiles;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.getXmlFiles;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.removeBackupExtension;

import java.io.File;
import java.util.List;

import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

/**
 * Driver class for the import of NSR taxa and multimedia. Also allows you to
 * back up NSR source files indepedently of the import procedure.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrImporter {

	public static void main(String[] args)
	{
		if (args.length == 0)
			new NsrImporter().importAll();
		else if (args[0].equalsIgnoreCase("taxa"))
			new NsrImporter().importTaxa();
		else if (args[0].equalsIgnoreCase("multimedia"))
			new NsrImporter().importMultiMedia();
		else if (args[0].equalsIgnoreCase("backup"))
			new NsrImporter().backup();
		else if (args[0].equalsIgnoreCase("reset"))
			new NsrImporter().reset();
		else
			logger.error("Invalid argument: " + args[0]);
	}

	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(NsrImporter.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public NsrImporter()
	{
		suppressErrors = ConfigObject.isEnabled("nsr.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Imports both taxa and multimedia, processing each source file just once,
	 * and backing it up once done.
	 */
	public void importAll()
	{
		long start = System.currentTimeMillis();
		File[] xmlFiles = getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}
		LoadUtil.truncate(LUCENE_TYPE_TAXON, NSR);
		LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, NSR);
		ETLStatistics taxonStats = new ETLStatistics();
		ETLStatistics mediaStats = new ETLStatistics();
		mediaStats.setOneToMany(true);
		//mediaStats.setUseObjectsAccepted(true);
		NsrTaxonTransformer tTransformer = new NsrTaxonTransformer(taxonStats);
		tTransformer.setSuppressErrors(suppressErrors);
		NsrMultiMediaTransformer mTransformer = new NsrMultiMediaTransformer(mediaStats);
		mTransformer.setSuppressErrors(suppressErrors);
		NsrTaxonLoader taxonLoader = null;
		NsrMultiMediaLoader mediaLoader = null;
		try {
			taxonLoader = new NsrTaxonLoader(esBulkRequestSize, taxonStats);
			mediaLoader = new NsrMultiMediaLoader(esBulkRequestSize, mediaStats);
			for (File f : xmlFiles) {
				logger.info("Processing file " + f.getAbsolutePath());
				int i = 0;
				for (XMLRecordInfo extracted : new NsrExtractor(f, taxonStats)) {
					List<ESTaxon> taxa = tTransformer.transform(extracted);
					taxonLoader.load(taxa);
					mTransformer.setTaxon(taxa == null ? null : taxa.get(0));
					List<ESMultiMediaObject> multimedia = mTransformer.transform(extracted);
					mediaLoader.load(multimedia);
					if (++i % 5000 == 0)
						logger.info("Records processed: " + i);
				}
				backupXmlFile(f);
			}
		}
		finally {
			IOUtil.close(taxonLoader, mediaLoader);
		}
		taxonStats.logStatistics(logger, "Taxa");
		mediaStats.badInput = taxonStats.badInput;
		mediaStats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	/**
	 * Extracts and imports just the taxa from the source files. Does not make
	 * backups.
	 */
	public void importTaxa()
	{
		long start = System.currentTimeMillis();
		File[] xmlFiles = getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}
		LoadUtil.truncate(LUCENE_TYPE_TAXON, NSR);
		ETLStatistics stats = new ETLStatistics();
		NsrTaxonTransformer transformer = new NsrTaxonTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		NsrTaxonLoader loader = null;
		try {
			loader = new NsrTaxonLoader(esBulkRequestSize, stats);
			for (File f : xmlFiles) {
				logger.info("Processing file " + f.getAbsolutePath());
				int i = 0;
				for (XMLRecordInfo extracted : new NsrExtractor(f, stats)) {
					List<ESTaxon> transformed = transformer.transform(extracted);
					loader.load(transformed);
					if (++i % 5000 == 0)
						logger.info("Records processed: " + i);
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		stats.logStatistics(logger, "Taxa");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	/**
	 * Extracts and imports just the multimedia from the source files. Does not
	 * make backups.
	 */
	public void importMultiMedia()
	{
		long start = System.currentTimeMillis();
		File[] xmlFiles = getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}
		LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, NSR);
		ETLStatistics stats = new ETLStatistics();
		stats.setOneToMany(true);
		NsrMultiMediaTransformer transformer = new NsrMultiMediaTransformer(stats);
		transformer.setSuppressErrors(suppressErrors);
		/*
		 * For multimedia we will re-use our taxon transformer class to extract
		 * taxon-related data from the XML records, so we don't have to
		 * duplicate that functionality in the multimedia transformer. However,
		 * we are not interested in the statistics maintained by the taxon
		 * transformer (only whether it was able to produce an ESTaxon object or
		 * not). Therefore we instantiate the taxon transformer with a trash
		 * statistics object.
		 */
		NsrTaxonTransformer ntt = new NsrTaxonTransformer(new ETLStatistics());
		ntt.setSuppressErrors(suppressErrors);
		NsrMultiMediaLoader loader = null;
		try {
			loader = new NsrMultiMediaLoader(esBulkRequestSize, stats);
			for (File f : xmlFiles) {
				logger.info("Processing file " + f.getAbsolutePath());
				for (XMLRecordInfo extracted : new NsrExtractor(f, stats)) {
					List<ESTaxon> taxa = ntt.transform(extracted);
					transformer.setTaxon(taxa == null ? null : taxa.get(0));
					List<ESMultiMediaObject> multimedia = transformer.transform(extracted);
					loader.load(multimedia);
					if (stats.recordsProcessed % 5000 == 0)
						logger.info("Records processed: " + stats.recordsProcessed);
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		stats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	/**
	 * Backs up the XML files in the NSR data directory by appending a
	 * "&#46;imported" extension to the file name.
	 */
	public void backup()
	{
		backupXmlFiles();
	}

	/**
	 * Removes the "&#46;imported" file name extension from the files in the
	 * NSR data directory. Nice for repitive testing. Not meant for production
	 * purposes.
	 */
	public void reset()
	{
		removeBackupExtension();
	}

}
