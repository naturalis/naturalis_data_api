package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.genericName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.infraspecificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificNameAuthorship;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.specificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonomicStatus;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.TransformUtil;
import nl.naturalis.nba.etl.normalize.TaxonomicStatusNormalizer;
import nl.naturalis.nba.etl.normalize.UnmappedValueException;

/**
 * 
 * The CoLSynonymLoader loads synonym names into a temporary 
 * H2 Database
 * 
 * @author Tom Gilissen
 *
 */
public class CoLSynonymLoader {

  private static final Logger logger = getLogger(CoLSynonymBatchImporter.class);
  private static final TaxonomicStatusNormalizer statusNormalizer = TaxonomicStatusNormalizer.getInstance();
  
  private Connection connection;
  private int batchSize;

  public CoLSynonymLoader(Connection connection)
  {
    this.connection = connection;
    this.batchSize = 1000;
    createTable();
  }

  public int getBatchSize()
  {
    return batchSize;
  }
  
  public void setBatchSize(int batchSize)
  {
    this.batchSize = batchSize;
  }
  
  /**
   * Processes the taxa.txt file to retrieve and store synonyms
   * 
   * @param path
   */
  public void importCsv(String path)
  {
    File f = new File(path);
    if (!f.exists()) {
      throw new ETLRuntimeException("No such file: " + path);
    }
    long start = System.currentTimeMillis();
    ETLStatistics stats = new ETLStatistics();
    
    CSVExtractor<CoLTaxonCsvField> extractor = createExtractor(stats, f);    
    ArrayList<CSVRecordInfo<CoLTaxonCsvField>> csvRecords;
    csvRecords = new ArrayList<>(batchSize);

    int processed = 0;
    int skipped = 0;
    logger.info("Processing file {}", f.getAbsolutePath());
    logger.info("Batch size: {}", batchSize);
    
    for (CSVRecordInfo<CoLTaxonCsvField> record : extractor) {
      if (++processed % 100000 == 0) {
        logger.info("Records processed: {}", processed);
      }
      if (record == null) {
        // Garbage
        skipped++;
        continue;
      }
      if (record.get(acceptedNameUsageID) == null) {
        // This is an accepted name, not a synonym
        skipped++;
        continue;
      }
      csvRecords.add(record);
      if (csvRecords.size() == batchSize) {
        saveRecords(csvRecords);
        csvRecords.clear();
      }      
    }
    if (!csvRecords.isEmpty()) {
      saveRecords(csvRecords);
      csvRecords.clear();
    }
    logger.info("Records processed: {}", processed);
    logger.info("Records skipped:   {}", skipped);
    logger.info("Synonyms created:  {}", countRecordsCreated());
    logDuration(logger, getClass(), start);
  }

  private static CSVExtractor<CoLTaxonCsvField> createExtractor(ETLStatistics stats, File f)
  {
    CSVExtractor<CoLTaxonCsvField> extractor;
    extractor = new CSVExtractor<>(f, CoLTaxonCsvField.class, stats);
    extractor.setSkipHeader(true);
    extractor.setDelimiter('\t');
    extractor.setQuote('\u0000');
    return extractor;
  }
  
  private void saveRecords(ArrayList<CSVRecordInfo<CoLTaxonCsvField>> records) {
    Statement stmt = null;
    try {          
      connection.setAutoCommit(false);
      stmt = connection.createStatement();
      for (CSVRecordInfo<CoLTaxonCsvField> record : records) {
        ScientificName synonym = createSynonym(record);
        String taxonId = record.get(taxonID);
        String acceptedNameUsageId = record.get(acceptedNameUsageID);
        String document = JsonUtil.toJson(synonym).replaceAll("'", "''");
        stmt.execute(String.format("INSERT INTO SYNONYMS(taxonId, acceptedNameUsageId, document) VALUES('%s', '%s', '%s')", taxonId, acceptedNameUsageId, document));
      }
      stmt.close();
      connection.commit();
    } catch (SQLException e) {
      System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private static ScientificName createSynonym(CSVRecordInfo<CoLTaxonCsvField> record)
  {
    ScientificName sn = new ScientificName();
    sn.setFullScientificName(record.get(scientificName));
    sn.setGenusOrMonomial(record.get(genericName));
    sn.setSpecificEpithet(record.get(specificEpithet));
    sn.setInfraspecificEpithet(record.get(infraspecificEpithet));
    sn.setAuthorshipVerbatim(record.get(scientificNameAuthorship));
    TaxonomicStatus status = null;
    try {
      status = statusNormalizer.map(record.get(taxonomicStatus));
    }
    catch (UnmappedValueException e) {
      String id = record.get(taxonID);
      logger.warn("{} | {}", id, e.getMessage());
    }
    sn.setTaxonomicStatus(status);
    TransformUtil.setScientificNameGroup(sn);
    return sn;
  }

  private void createTable() {
    Statement stmt = null;
    try {
      connection.setAutoCommit(false);      
      stmt = connection.createStatement();
      stmt.execute("CREATE TABLE SYNONYMS (taxonId varchar(50) not null primary key, acceptedNameUsageId varchar(50) not null, document LONGTEXT)");
      stmt.close();
      connection.commit();

    } catch (SQLException e) {
      System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  private long countRecordsCreated() {
    long n = 0L;
    Statement stmt = null;
    try {          
      connection.setAutoCommit(false);
      stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT COUNT(taxonId) FROM SYNONYMS");
      rs.next();
      n = rs.getLong(1);
      stmt.close();
      connection.commit();
    } catch (SQLException e) {
        System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
        e.printStackTrace();
    } 
    return n;
  }

}
