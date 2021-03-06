package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_ETL_OUTPUT;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_SUPPRESS_ERRORS;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.backupJsonFile;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.backupJsonFiles;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.getJsonFiles;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.removeBackupExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.DocumentObjectWriter;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Driver class for the import of NSR taxa and multimedia. Also allows you to
 * back up NSR source files indepedently of the import procedure.
 *
 * @author Ayco Holleman
 */
public class NsrImporter {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                new NsrImporter().importAll();
            } else if (args[0].equalsIgnoreCase("taxa")) {
                new NsrImporter().importTaxa();
            } else if (args[0].equalsIgnoreCase("multimedia")) {
                new NsrImporter().importMultiMedia();
            } else if (args[0].equalsIgnoreCase("backup")) {
                new NsrImporter().backup();
            } else if (args[0].equalsIgnoreCase("reset")) {
                new NsrImporter().reset();
            }
        } catch (Throwable t) {
            logger.error("NsrImport terminated unexpectedly!", t);
            System.exit(1);
        } finally {
            if (shouldUpdateES && (args.length == 0 || args[0].equalsIgnoreCase("taxa"))) {
                ESUtil.refreshIndex(TAXON);
            }
            if (shouldUpdateES && (args.length == 0 || args[0].equalsIgnoreCase("multimedia"))) {
                ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
            }
            ESClientManager.getInstance().closeClient();
        }
    }

    private static final Logger logger = getLogger(NsrImporter.class);
    private static final boolean shouldUpdateES = DaoRegistry.getInstance().getConfiguration().get(SYSPROP_ETL_OUTPUT, "es").equals("file") ? false : true;

    private final int loaderQueueSize;
    private final boolean suppressErrors;
    private final boolean toFile;

    public NsrImporter() {
        suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
        String val = System.getProperty(SYSPROP_LOADER_QUEUE_SIZE, "1000");
        loaderQueueSize = Integer.parseInt(val);
        toFile = DaoRegistry.getInstance().getConfiguration().get("etl.output", "file").equals("file");
    }

    /**
     * Imports both taxa and multimedia, processing each source file just once,
     * and backing it up once done.
     */
    public void importAll() throws IOException {
        long start = System.currentTimeMillis();
        File[] files = getJsonFiles();
        Arrays.sort(files);
        if (files.length == 0) {
            logger.info("No source files to process");
            return;
        }

        if (shouldUpdateES) {
            ETLUtil.truncate(TAXON, NSR);
            ETLUtil.truncate(MULTI_MEDIA_OBJECT, NSR);
        }
        ETLStatistics taxonStats = new ETLStatistics();
        ETLStatistics mediaStats = new ETLStatistics();
        mediaStats.setOneToMany(true);
        NsrTaxonTransformer taxonTransformer = new NsrTaxonTransformer(taxonStats);
        taxonTransformer.setSuppressErrors(suppressErrors);
        NsrMultiMediaTransformer multimediaTransformer = new NsrMultiMediaTransformer(mediaStats);
        multimediaTransformer.setSuppressErrors(suppressErrors);
        DocumentObjectWriter<Taxon> taxonLoader = null;
        DocumentObjectWriter<MultiMediaObject> mediaLoader = null;
        try {
            for (File f : files) {
                if (toFile) {
                    logger.info("ETL Output: Writing the documents to the file system");
                    taxonLoader = new NsrTaxonJsonNDWriter(f.getName(), taxonStats);
                    mediaLoader = new NsrMultiMediaJsonNDWriter(f.getName(), mediaStats);
                } else {
                    logger.info("ETL Output: loading documents into the document store");
                    taxonLoader = new NsrTaxonLoader(loaderQueueSize, taxonStats);
                    mediaLoader = new NsrMultiMediaLoader(loaderQueueSize, mediaStats);
                }
                logger.info("Processing file {}", f.getAbsolutePath());

                LineNumberReader lnr;
                try {
                    FileReader fr = new FileReader(f);
                    lnr = new LineNumberReader(fr, 4096);
                    String json ;
                    while ((json = lnr.readLine()) != null) {
                        List<Taxon> taxa = taxonTransformer.transform(json);
                        taxonLoader.write(taxa);
                        multimediaTransformer.setTaxon(taxa == null ? null : taxa.get(0));
                        List<MultiMediaObject> multimedia = multimediaTransformer.transform(json);
                        mediaLoader.write(multimedia);
                    }
                }
                catch (FileNotFoundException e) {
                    logger.error("Missing file {}. File has been skipped from importing.", f.getAbsolutePath());
                }

                // Summary after file has finished
                if (taxonStats.recordsProcessed != 0) {
                    logger.info("Records processed: {}", taxonStats.recordsProcessed);
                    logger.info("Taxon documents indexed: {}", taxonStats.documentsIndexed);
                    logger.info("Multimedia documents indexed: {}", mediaStats.documentsIndexed);
                } else {
                    logger.info("No record was processed");
                }
                if (toFile) {
                    try {
                        taxonLoader.close();
                        mediaLoader.close();
                    } catch (IOException e) {
                        logger.warn("Failed to close file. There may have been documents lost.");
                    }
                } else {
                    taxonLoader.flush();
                    mediaLoader.flush();
                }
                backupJsonFile(f);
            }
            // Summery after entire import has finished
            if (taxonStats.recordsProcessed != 0) {
                logger.info("NSR Import complete");
                logger.info("Records processed: {}", taxonStats.recordsProcessed);
                logger.info("Taxon documents indexed: {}", taxonStats.documentsIndexed);
                logger.info("Multimedia documents indexed: {}", mediaStats.documentsIndexed);
            } else {
                logger.info("No record was processed");
            }
        } finally {
            IOUtil.close(taxonLoader, mediaLoader);
        }
        taxonStats.logStatistics(logger, "Taxa");
        mediaStats.badInput = taxonStats.badInput;
        mediaStats.logStatistics(logger, "Multimedia");
        ETLUtil.logDuration(logger, getClass(), start);
    }

    /**
     * Extracts and imports just the taxa from the source files. Does not make
     * backups.
     */
    public void importTaxa() throws IOException {
        long start = System.currentTimeMillis();
        File[] files = getJsonFiles();
        Arrays.sort(files);
        if (files.length == 0) {
            logger.info("No source files to process");
            return;
        }
        if (shouldUpdateES) {
            ETLUtil.truncate(TAXON, NSR);
        }
        ETLStatistics stats = new ETLStatistics();
        NsrTaxonTransformer transformer = new NsrTaxonTransformer(stats);
        transformer.setSuppressErrors(suppressErrors);
        DocumentObjectWriter<Taxon> loader = null;
        if (toFile) {
            logger.info("ETL Output: Writing the documents to the file system");
        } else {
            logger.info("ETL Output: loading documents into the document store");
        }
        try {
            for (File f : files) {
                logger.info("Processing file {}", f.getAbsolutePath());
                if (toFile) {
                    loader = new NsrTaxonJsonNDWriter(f.getName(), stats);
                } else {
                    loader = new NsrTaxonLoader(loaderQueueSize, stats);
                }
                LineNumberReader lnr;
                try {
                    FileReader fr = new FileReader(f);
                    lnr = new LineNumberReader(fr, 4096);
                    String json ;
                    while ((json = lnr.readLine()) != null) {
                        List<Taxon> taxa = transformer.transform(json);
                        loader.write(taxa);
                    }
                }
                catch (FileNotFoundException e) {
                    logger.error("Missing file {}. File has been skipped from importing.", f.getAbsolutePath());
                }
                if (toFile) {
                    try {
                        loader.close();
                    } catch (IOException e) {
                        logger.warn("Failed to close file. There may have been documents lost.");
                    }
                } else {
                    loader.flush();
                }
            }
        } finally {
            IOUtil.close(loader);
        }
        stats.logStatistics(logger, "Taxa");
        ETLUtil.logDuration(logger, getClass(), start);
    }

    /**
     * Extracts and imports just the multimedia from the source files. Does not
     * make backups.
     */
    public void importMultiMedia() throws IOException {
        long start = System.currentTimeMillis();
        File[] files = getJsonFiles();
        if (files.length == 0) {
            logger.info("No source files to process");
            return;
        }
        if (shouldUpdateES) {
            ETLUtil.truncate(MULTI_MEDIA_OBJECT, NSR);
        }
        ETLStatistics stats = new ETLStatistics();
        stats.setOneToMany(true);
        NsrMultiMediaTransformer transformer = new NsrMultiMediaTransformer(stats);
        transformer.setSuppressErrors(suppressErrors);
        /*
         * For multimedia we will re-use our taxon transformer class to extract
         * taxon-related data from the XML records, so we don't have to
         * duplicate that functionality in the multimedia transformer. However,
         * we are not interested in the statistics maintained by the taxon
         * transformer (only whether it was able to produce an Taxon object or
         * not). Therefore we instantiate the taxon transformer with a trash
         * statistics object.
         */
        NsrTaxonTransformer ntt = new NsrTaxonTransformer(new ETLStatistics());
        ntt.setSuppressErrors(suppressErrors);
        DocumentObjectWriter<MultiMediaObject> loader = null;
        if (toFile) {
            logger.info("ETL Output: Writing the documents to the file system");
        } else {
            logger.info("ETL Output: loading documents into the document store");
        }
        try {
            for (File f : files) {
                if (toFile) {
                    loader = new NsrMultiMediaJsonNDWriter(f.getName(), stats);
                } else {
                    loader = new NsrMultiMediaLoader(loaderQueueSize, stats);
                }
                logger.info("Processing file {}", f.getAbsolutePath());

                LineNumberReader lnr;
                try {
                    FileReader fr = new FileReader(f);
                    lnr = new LineNumberReader(fr, 4096);
                    String json ;
                    while ((json = lnr.readLine()) != null) {
                        List<Taxon> taxa = ntt.transform(json);
                        transformer.setTaxon(taxa == null ? null : taxa.get(0));
                        List<MultiMediaObject> multimedia = transformer.transform(json);
                        loader.write(multimedia);
                    }
                }
                catch (FileNotFoundException e) {
                    logger.error("Missing file {}. File has been skipped from importing.", f.getAbsolutePath());
                }

                if (toFile) {
                    try {
                        loader.close();
                    } catch (IOException e) {
                        logger.warn("Failed to close file. There may have been documents lost.");
                    }
                } else {
                    loader.flush();
                }
            }
        } finally {
            IOUtil.close(loader);
        }
        stats.logStatistics(logger, "Multimedia");
        logDuration(logger, getClass(), start);
    }

    /**
     * Backs up the XML files in the NSR data directory by appending a
     * "&#46;imported" extension to the file name.
     */
    public void backup() {
        backupJsonFiles();
    }

    /**
     * Removes the "&#46;imported" file name extension from the files in the NSR
     * data directory. Nice for repitive testing. Not meant for production
     * purposes.
     */
    public void reset() {
        removeBackupExtension();
    }

}
