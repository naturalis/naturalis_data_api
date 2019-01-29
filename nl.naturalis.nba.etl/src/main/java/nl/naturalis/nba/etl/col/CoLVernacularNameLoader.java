package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * Load vernacular names into H2 Database
 * 
 * @author Tom Gilissen
 *
 */
public class CoLVernacularNameLoader {
  
  private static final Logger logger = getLogger(CoLVernacularNameLoader.class);
  
  private Connection connection;
  private int batchSize = 1000;
  
  public CoLVernacularNameLoader(Connection connection)
  {
    this.connection = connection;
    createTable();
  }

  /**
   * Processes the vernacular.txt file
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

    CSVExtractor<CoLVernacularNameCsvField> extractor = createExtractor(stats, f);
    ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> csvRecords;
    csvRecords = new ArrayList<>(batchSize);
    
    int processed = 0;
    int skipped = 0;
    logger.info("Processing file {}", f.getAbsolutePath());
    logger.info("Batch size: {}", batchSize);
    
    for (CSVRecordInfo<CoLVernacularNameCsvField> rec : extractor) {
      if (++processed % 100000 == 0) {
        logger.info("Records processed: {}", processed);
      }
      if (rec == null || rec.get(taxonID) == null) {
        // Garbage
        skipped++;
        continue;
      }
      csvRecords.add(rec);
      if (csvRecords.size() == batchSize) {
        saveRecords(csvRecords);
        csvRecords.clear();
        }
      }
    if (!csvRecords.isEmpty()) {
      saveRecords(csvRecords);
      csvRecords.clear();
    }
    logger.info("Records processed:        {}", processed);
    logger.info("Records skipped:          {}", skipped);
    logger.info("Vernacular names created: {}", countVernacularNames());
    logDuration(logger, getClass(), start);
  }

  public int getBatchSize()
  {
    return batchSize;
  }

  public void setBatchSize(int batchSize)
  {
    this.batchSize = batchSize;
  }

  private static CSVExtractor<CoLVernacularNameCsvField> createExtractor(
      ETLStatistics stats, File f)
  {
    CSVExtractor<CoLVernacularNameCsvField> extractor;
    extractor = new CSVExtractor<>(f, CoLVernacularNameCsvField.class, stats);
    extractor.setSkipHeader(true);
    extractor.setDelimiter('\t');
    extractor.setQuote('\u0000');
    return extractor;
  }
  
  private static VernacularName createVernacularName(CSVRecordInfo<CoLVernacularNameCsvField> record)
  {
    VernacularName vn = new VernacularName();
    vn.setName(record.get(vernacularName));
    vn.setLanguage(record.get(language));
    return vn;
  }
  
  private void createTable() {
    Statement stmt = null;
    try {
      connection.setAutoCommit(false);      
      stmt = connection.createStatement();
      stmt.execute("CREATE TABLE VERNACULARNAMES (id int primary key auto_increment, taxonId varchar(50) not null, document LONGTEXT)");
      stmt.close();
      connection.commit();

    } catch (SQLException e) {
      System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  private void saveRecords(ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records) {
    Statement stmt = null;
    try {          
      connection.setAutoCommit(false);
      stmt = connection.createStatement();
      for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
        VernacularName vernacularName = createVernacularName(record);
        String taxonId = record.get(taxonID);
        String document = JsonUtil.toJson(vernacularName).replaceAll("'", "''");
        stmt.execute(String.format("INSERT INTO VERNACULARNAMES(taxonId, document) VALUES('%s', '%s')", taxonId, document));
      }
      stmt.close();
      connection.commit();
    } catch (SQLException e) {
        System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  private long countVernacularNames() {
    long n = 0L;
    Statement stmt = null;
    try {          
      connection.setAutoCommit(false);
      stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT COUNT(taxonId) FROM VERNACULARNAMES");
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
