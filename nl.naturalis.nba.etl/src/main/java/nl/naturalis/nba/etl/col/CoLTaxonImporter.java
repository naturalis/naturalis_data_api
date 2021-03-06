package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import java.io.File;
import java.util.List;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.DocumentObjectWriter;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Imports taxa from the taxa.txt file. This is the only import program for the CoL that actually
 * creates documents. The other programs only enrich existing documents (e.g. with synonym data,
 * vernacular names and literature references).
 * 
 * @author Ayco Holleman
 *
 */
public class CoLTaxonImporter extends CoLImporter {

  private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLTaxonImporter.class);

  public CoLTaxonImporter() {
    super();
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
   * Imports CoL taxa into ElasticSearch.
   * 
   * @param path
   */
  public void importCsv(String path) {
    long start = System.currentTimeMillis();
    ETLStatistics stats = null;
    CSVExtractor<CoLTaxonCsvField> extractor = null;
    CoLTaxonTransformer transformer = null;
    DocumentObjectWriter<Taxon> loader = null;

    if (truncate) ETLUtil.truncate(TAXON, SourceSystem.COL);
   
    try {
      File f = new File(path);
      if (!f.exists()) throw new ETLRuntimeException("No such file: " + path);
      stats = new ETLStatistics();
      extractor = createExtractor(stats, f);
      extractor.setDelimiter('\t');
      extractor.setQuote('\u0000'); // CoL export doesn't use quotes!
      transformer = new CoLTaxonTransformer(stats);
      transformer.setSuppressErrors(suppressErrors);
      loader = new CoLTaxonLoader(stats, loaderQueueSize);
      loader.suppressErrors(suppressErrors);
      logger.info("ETL Output: loading documents into the document store");
      logger.info("Processing file {}", f.getAbsolutePath());
      for (CSVRecordInfo<CoLTaxonCsvField> rec : extractor) {
        if (rec == null)
          continue;
        List<Taxon> taxa = transformer.transform(rec);
        loader.write(taxa);
        if (stats.recordsProcessed != 0 && stats.recordsProcessed % 50000 == 0) {
          logger.info("Records processed: {}", stats.recordsProcessed);
          logger.info("Documents indexed: {}", stats.documentsIndexed);
        }
      }
    } finally {
      if (loader != null) loader.flush();
      IOUtil.close(loader);
      if (!toFile) {
        ESUtil.refreshIndex(TAXON);        
      }
    }
    stats.logStatistics(logger);
    logger.info("(NB skipped records are synonyms or higher taxa)");
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
