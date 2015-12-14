package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.domain.SourceSystem.NSR;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.backupXmlFile;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.backupXmlFiles;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.getXmlFiles;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.removeBackupExtension;

import java.io.File;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

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
		ETLStatistics tStats = new ETLStatistics();
		ETLStatistics mStats = new ETLStatistics();
		mStats.setOneToMany(true);
		try {
			LoadUtil.truncate(LUCENE_TYPE_TAXON, SourceSystem.NSR);
			LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, SourceSystem.NSR);
			for (File f : xmlFiles) {
				processFile(f, tStats, mStats);
				backupXmlFile(f);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		tStats.logStatistics(logger, "Taxa");
		mStats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics sStats, ETLStatistics mStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		ETLStatistics myTaxonStats = new ETLStatistics();
		ETLStatistics myMultimediaStats = new ETLStatistics();
		myMultimediaStats.setOneToMany(true);
		ETLStatistics extractionStats = new ETLStatistics();
		//NsrExtractor extractor = null;
		//NsrTaxonTransformer specimenTransformer = null;
		//NsrMultiMediaTransformer multimediaTransformer = null;
		NsrTaxonLoader specimenLoader = null;
		NsrMultiMediaLoader multimediaLoader = null;
		try {
			//NsrExtractor extractor = new NsrExtractor(f, extractionStats);
			NsrTaxonTransformer specimenTransformer = new NsrTaxonTransformer(myTaxonStats);
			NsrMultiMediaTransformer multimediaTransformer = new NsrMultiMediaTransformer(myMultimediaStats);
			specimenLoader = new NsrTaxonLoader(esBulkRequestSize, myTaxonStats);
			multimediaLoader = new NsrMultiMediaLoader(esBulkRequestSize, myMultimediaStats);
			for (XMLRecordInfo rec : new NsrExtractor(f, extractionStats)) {
				List<ESTaxon> taxa = specimenTransformer.transform(rec);
				specimenLoader.load(taxa);
				// Ayco
				multimediaTransformer.setTaxon(taxa.get(0));
				multimediaLoader.load(multimediaTransformer.transform(rec));
			}
		}
		finally {
			IOUtil.close(specimenLoader, multimediaLoader);
		}
		myTaxonStats.add(extractionStats);
		myMultimediaStats.add(extractionStats);
		myTaxonStats.logStatistics(logger, "Specimens");
		myMultimediaStats.logStatistics(logger, "Multimedia");
		sStats.add(myTaxonStats);
		mStats.add(myMultimediaStats);
		logger.info("Importing " + f.getName() + " took " + LoadUtil.getDuration(start));
		logger.info(" ");
		logger.info(" ");
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
	 * Removes the "&#46;imported" file name extension from the files in the NSR
	 * data directory. Nice for repitive testing. Not meant for production
	 * purposes.
	 */
	public void reset()
	{
		removeBackupExtension();
	}

}
