package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * The CoLVernacularNameLoader loads vernacular names into a temporary H2 Database
 * 
 * @author Tom Gilissen
 *
 */
public class CoLVernacularNameLoader {

  private static final Logger logger = getLogger(CoLVernacularNameLoader.class);

  private Connection connection;
  private int batchSize;

  public CoLVernacularNameLoader(Connection connection) {
    this.connection = connection;
    this.batchSize = 1000;
    createTable();
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  /**
   * Processes the vernacular.txt file
   * 
   * @param path
   */
  public void importCsv(String path) {
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
    addIndex();
    logger.info("Records processed:        {}", processed);
    logger.info("Records skipped:          {}", skipped);
    logger.info("Vernacular names created: {}", countRecordsCreated());
    logDuration(logger, getClass(), start);
  }

  private static CSVExtractor<CoLVernacularNameCsvField> createExtractor(ETLStatistics stats, File f) {
    CSVExtractor<CoLVernacularNameCsvField> extractor;
    extractor = new CSVExtractor<>(f, CoLVernacularNameCsvField.class, stats);
    extractor.setSkipHeader(true);
    extractor.setDelimiter('\t');
    extractor.setQuote('\u0000');
    return extractor;
  }

  private void saveRecords(ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records) {
    try (PreparedStatement ps = connection.prepareStatement("INSERT INTO VERNACULARNAMES(taxonId, document) VALUES(?, ?)")) {
      for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
        VernacularName vernacularName = createVernacularName(record);
        String taxonId = record.get(taxonID);
        String document = JsonUtil.toJson(vernacularName).replaceAll("'", "''");
        ps.setString(1, taxonId);
        ps.setString(2, document);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static VernacularName createVernacularName(CSVRecordInfo<CoLVernacularNameCsvField> record) {
    VernacularName vn = new VernacularName();
    vn.setName(record.get(vernacularName));
    vn.setLanguage(record.get(language));
    return vn;
  }

  private void createTable() {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("CREATE TABLE VERNACULARNAMES (taxonId varchar(50) not null, document LONGTEXT)");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void addIndex() {
    logger.info("Creating index");
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("CREATE INDEX verTaxonIdInd ON VERNACULARNAMES (taxonId);");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    logger.info("Creating index: done");
  }
  
  private long countRecordsCreated() {
    long n = 0L;
    try (Statement stmt = connection.createStatement()) {
      try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM VERNACULARNAMES")) {
        rs.next();
        n = rs.getLong(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return n;
  }

}
