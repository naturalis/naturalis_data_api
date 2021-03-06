package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonID;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.DocumentObjectWriter;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Imports taxa from the taxa.txt file ...
 *
 */
public class CoLTaxonFullImporter extends CoLImporter {

  private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLTaxonFullImporter.class);
  
  private Connection connection;
  
  public CoLTaxonFullImporter(Connection connection) {
    super();
    this.connection = connection;
  }

  public static void main(String[] args) throws Exception {
    try {
      CoLTaxonImporter importer = new CoLTaxonImporter();
      String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
      importer.importCsv(dwcaDir + "/taxa.txt");
    } catch (Throwable t) {
      logger.error("CoLTaxonImporter terminated unexpectedly!", t);
      System.exit(1);
    } finally {
      ESClientManager.getInstance().closeClient();
    }
  }


  /**
   * Writes CoL taxa to a file in JsonND format
   * 
   * @param path
   * @throws SQLException 
   */
  public void importCsv(String path) throws SQLException {
   
    long start = System.currentTimeMillis();
    ETLStatistics stats = null;
    CSVExtractor<CoLTaxonCsvField> extractor = null;
    CoLTaxonFullTransformer transformer = null;
    DocumentObjectWriter<Taxon> loader = null;
   
    try {
      File f = new File(path);
      if (!f.exists())
        throw new ETLRuntimeException("No such file: " + path);
      stats = new ETLStatistics();
      extractor = createExtractor(stats, f);
      extractor.setDelimiter('\t');
      extractor.setQuote('\u0000'); // CoL export doesn't use quotes!
      transformer = new CoLTaxonFullTransformer(stats, connection);
      transformer.setSuppressErrors(suppressErrors);
      
      loader = new ColTaxonJsonNDWriter(f.getName(), stats);
      logger.info("ETL Output: Writing the documents to the file system");
      loader.suppressErrors(suppressErrors);
      logger.info("Processing file {}", f.getAbsolutePath());

      int batchSize = 1000;
      ArrayList<String> taxonIds = new ArrayList<>(batchSize);
      List<CSVRecordInfo<CoLTaxonCsvField>> csvRecords = new ArrayList<>(batchSize);
      
      for (CSVRecordInfo<CoLTaxonCsvField> rec : extractor) {
        if (rec == null)
          continue;
        csvRecords.add(rec);
        taxonIds.add(rec.get(taxonID));
        if (csvRecords.size() == batchSize) {
          transformer.createLookupTable(taxonIds);
          for (CSVRecordInfo<CoLTaxonCsvField> record : csvRecords) {
            List<Taxon> taxa = transformer.transform(record);
            if (stats.recordsProcessed != 0 && stats.recordsProcessed % 50000 == 0) {
              logger.info("Records processed: {}", stats.recordsProcessed);
              logger.info("Documents indexed: {}", stats.documentsIndexed);
            }
            loader.write(taxa);
          }
          taxonIds.clear();
          csvRecords.clear();
        }
      }
      if (csvRecords.size() > 0) {
        transformer.createLookupTable(taxonIds);
        for (CSVRecordInfo<CoLTaxonCsvField> record : csvRecords) {
          List<Taxon> taxa = transformer.transform(record);
          loader.write(taxa);
        }
        taxonIds.clear();
        csvRecords.clear();
        logger.info("Records processed: {}", stats.recordsProcessed);
        logger.info("Documents indexed: {}", stats.documentsIndexed);
      }
    } 
    finally {
      IOUtil.close(loader);
    }
    stats.logStatistics(logger); // NOTE: skipped records are synonyms or higher taxa
    logDuration(logger, getClass(), start);
  }

  private CSVExtractor<CoLTaxonCsvField> createExtractor(ETLStatistics stats, File f) {
    CSVExtractor<CoLTaxonCsvField> extractor;
    extractor = new CSVExtractor<>(f, CoLTaxonCsvField.class, stats);
    extractor.setSkipHeader(true);
    extractor.setDelimiter('\t');
    extractor.setSuppressErrors(suppressErrors);
    return extractor;
  }
}
