package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.title;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.creator;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.date;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.description;
import static nl.naturalis.nba.etl.col.CoLEntityType.REFERENCE_DATA;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.common.es.ESDateInput;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * The CoLReferenceLoader loads literature references 
 * into a temporary H2 Database
 * 
 * @author Tom Gilissen
 *
 */
public class CoLReferenceLoader {
  
  private static final Logger logger = getLogger(CoLReferenceLoader.class);
  
  private Connection connection;
  private int batchSize;
  
  public CoLReferenceLoader(Connection connection) throws SQLException
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
   * Processes the reference.txt file
   * 
   * @param path
   * @throws SQLException 
   */
  public void importCsv(String path) throws SQLException
  {
    File f = new File(path);
    if (!f.exists()) {
      throw new ETLRuntimeException("No such file: " + path);
    }
    long start = System.currentTimeMillis();
    ETLStatistics stats = new ETLStatistics();
    
    CSVExtractor<CoLReferenceCsvField> extractor = createExtractor(stats, f);
    ArrayList<CSVRecordInfo<CoLReferenceCsvField>> csvRecords = new ArrayList<>(batchSize);
    
    int processed = 0;
    int skipped = 0;
    logger.info("Processing file {}", f.getAbsolutePath());
    logger.info("Batch size: {}", batchSize);
    
    for (CSVRecordInfo<CoLReferenceCsvField> record : extractor) {
      if (++processed % 100000 == 0) {
        logger.info("Records processed: {}", processed);
      }
      if (record == null || record.get(taxonID) == null) {
        // Garbage
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
    addIndex();
    logger.info("Records processed:        {}", processed);
    logger.info("Records skipped:          {}", skipped);
    logger.info("References created: {}", countRecordsCreated());
    logDuration(logger, getClass(), start);
  }

  private static CSVExtractor<CoLReferenceCsvField> createExtractor(ETLStatistics stats, File f)
  {
    CSVExtractor<CoLReferenceCsvField> extractor;
    extractor = new CSVExtractor<>(f, CoLReferenceCsvField.class, stats);
    extractor.setSkipHeader(true);
    extractor.setDelimiter('\t');
    extractor.setQuote('\u0000');
    return extractor;
  }
  
  private void saveRecords(ArrayList<CSVRecordInfo<CoLReferenceCsvField>> records) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(String.format("INSERT INTO %s (taxonId, document) VALUES(?, ?)", REFERENCE_DATA))) {          
      for (CSVRecordInfo<CoLReferenceCsvField> record : records) {
        Reference reference = createReference(record);
        String taxonId = record.get(taxonID);
        String document = JsonUtil.toJson(reference).replaceAll("'", "''");
        ps.setString(1,  taxonId);
        ps.setString(2,  document);
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }
  
  private static Reference createReference(CSVRecordInfo<CoLReferenceCsvField> record)
  {
    Reference reference = new Reference();
    reference.setTitleCitation(record.get(title));
    reference.setCitationDetail(record.get(description));
    String s;
    if ((s = record.get(date)) != null) {
      OffsetDateTime odt = new ESDateInput(s).parseAsYear();
      if (odt == null) {
        logger.warn("Invalid date: {}", s);
      }
      else {
        reference.setPublicationDate(odt);
      }
    }
    if ((s = record.get(creator)) != null) {
      reference.setAuthor(new Person(s));
    }
    return reference;
  }
  
  private void createTable() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(String.format("CREATE TABLE %s (id INT, taxonId VARCHAR(50) NOT NULL, document LONGTEXT);", REFERENCE_DATA));
    } 
  }
  
  private void addIndex() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(String.format("CREATE INDEX referenceTaxonIdIndex ON %s (taxonId);", REFERENCE_DATA));
    }
  }
  
  private long countRecordsCreated() throws SQLException {
    long n = 0L;
    try (Statement stmt = connection.createStatement()){          
      connection.setAutoCommit(false);
      try (ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) FROM %s;", REFERENCE_DATA))) {
        rs.next();
        n = rs.getLong(1);        
      }
    } 
    return n;
  }
  
}