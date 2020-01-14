package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_ETL_OUTPUT;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_SUPPRESS_ERRORS;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_TRUNCATE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDataDir;
import java.io.File;
import java.nio.charset.Charset;

import nl.naturalis.nba.etl.*;
import org.apache.logging.log4j.Logger;
import com.univocity.parsers.common.TextParsingException;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Manages the import of Brahms specimens.
 * 
 * @author Ayco Holleman
 *
 */
public class BrahmsSpecimenImporter {

  public static void main(String[] args) throws Exception {
    try {
      BrahmsSpecimenImporter importer = new BrahmsSpecimenImporter();
      if (args.length == 0 || args[0].trim().length() == 0) {
        importer.importCsvFiles();
      } else {
        importer.importCsvFile(args[0]);
      }
    } catch (Throwable t) {
      logger.error("BrahmsSpecimenImporter terminated unexpectedly!", t);
      System.exit(1);
    } finally {
      ESUtil.refreshIndex(SPECIMEN);
      ESClientManager.getInstance().closeClient();
    }
  }

  private static final Logger logger = getLogger(BrahmsSpecimenImporter.class);

  private final int loaderQueueSize;
  private final boolean suppressErrors;
  private final boolean shouldUpdateES;

  public BrahmsSpecimenImporter() {
    suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
    String val = System.getProperty(SYSPROP_LOADER_QUEUE_SIZE, "1000");
    loaderQueueSize = Integer.parseInt(val);
    shouldUpdateES = DaoRegistry.getInstance().getConfiguration().get(SYSPROP_ETL_OUTPUT, "es").equals("file") ? false : true;
    MedialibIdsCache.getInstance();
  }

  public void importCsvFile(String path) {
    File file;
    if (path.startsWith("/")) {
      file = new File(path);
    } else {
      file = FileUtil.newFile(getDataDir(), path);
    }
    if (!file.isFile()) {
      throw new ETLRuntimeException("No such file: " + file.getAbsolutePath());
    }
    importCsvFiles(new File[] {file});
  }

  /**
   * Iterates over the CSV files in the brahms data directory and imports them.
   */
  public void importCsvFiles() {
    importCsvFiles(getCsvFiles());
  }

  public void importCsvFiles(File[] csvFiles) {
    long start = System.currentTimeMillis();
    if (csvFiles.length == 0) {
      logger.info("No CSV files to process");
      return;
    }
    SpecimenTypeStatusNormalizer.getInstance().resetStatistics();
    ThemeCache.getInstance().resetMatchCounters();
    ETLStatistics stats = new ETLStatistics();
    if (ConfigObject.isEnabled(SYSPROP_TRUNCATE, true) && !shouldUpdateES) {
      ETLUtil.truncate(SPECIMEN, BRAHMS);
    }
    for (File f : csvFiles) {
      processFile(f, stats);
    }
    SpecimenTypeStatusNormalizer.getInstance().logStatistics();
    ThemeCache.getInstance().logMatchInfo();
    stats.logStatistics(logger, "Specimens");
    logDuration(logger, getClass(), start);
  }

  private void processFile(File f, ETLStatistics globalStats) 
  {
    long start = System.currentTimeMillis();
    logger.info("Processing file {}", f.getAbsolutePath());
    ETLStatistics myStats = new ETLStatistics();
    CSVExtractor<BrahmsCsvField> extractor = null;
    BrahmsSpecimenTransformer transformer = null;
    DocumentObjectWriter<Specimen> loader = null;
    try {
      extractor = createExtractor(f, myStats);
      transformer = new BrahmsSpecimenTransformer(myStats);
      // Temporary (?) modification to allow for enrichment during the specimen import
      if (DaoRegistry.getInstance().getConfiguration().get("etl.enrich", "false").equals("true")) {
        transformer.setEnrich(true);
        logger.info("Taxonomic enrichment of Specimen documents: true");
      }
      if (DaoRegistry.getInstance().getConfiguration().get("etl.output", "file").equals("file")) {
        logger.info("ETL Output: Writing the specimen documents to the file system");
        loader = new BrahmsSpecimenJsonNDWriter(f.getName(), myStats);
      } else {
        logger.info("ETL Output: loading the specimen documents into the document store");
        loader = new BrahmsSpecimenLoader(loaderQueueSize, myStats);
      }
      for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
        if (rec == null) {
          continue;
        }
        loader.write(transformer.transform(rec));
        if (myStats.recordsProcessed != 0 && myStats.recordsProcessed % 50000 == 0) {
          logger.info("Records processed: {}", myStats.recordsProcessed);
          logger.info("Documents indexed: {}", myStats.documentsIndexed);
        }
      }
    } 
    catch (TextParsingException e) {
      logger.error("Parsing of csv file: {} failed!", f.getAbsolutePath());
      logger.error("Processing ended at line: {}", e.getLineIndex());
    } 
    catch (OutOfMemoryError e) {
      logger.error("Parsing of file: {} failed!", f.getAbsolutePath());
      logger.error("Cause: {}", e.getMessage());
    } 
    finally {
      loader.flush();
      IOUtil.close(loader);
    }
    myStats.logStatistics(logger, "Specimens");
    globalStats.add(myStats);
    logDuration(logger, getClass(), start);
    logger.info(" ");
    logger.info(" ");
  }

  private CSVExtractor<BrahmsCsvField> createExtractor(File f, ETLStatistics extractionStats) {
    CSVExtractor<BrahmsCsvField> extractor = new CSVExtractor<>(f, BrahmsCsvField.class, extractionStats);
    extractor.setSkipHeader(true);
    extractor.setDelimiter(',');
    extractor.setCharset(Charset.forName("Windows-1252"));
    extractor.setSuppressErrors(suppressErrors);
    return extractor;
  }
}
