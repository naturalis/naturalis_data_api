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
import static nl.naturalis.nba.etl.col.CoLEntityType.SYNONYM_NAMES;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

  public CoLSynonymLoader(Connection connection) throws SQLException
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
    addIndex();
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
  
  private void saveRecords(ArrayList<CSVRecordInfo<CoLTaxonCsvField>> records) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(String.format("INSERT INTO %s (id, taxonId, document) VALUES(?, ?, ?)", SYNONYM_NAMES))) {
      for (CSVRecordInfo<CoLTaxonCsvField> record : records) {
        ScientificName synonym = createSynonym(record);
        String id = record.get(taxonID);
        ps.setString(1, id);
        String taxonId = record.get(acceptedNameUsageID);
        ps.setString(2, taxonId);
        String document = JsonUtil.toJson(synonym).replaceAll("'", "''");
        ps.setString(3, document);
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }
  
  private static ScientificName createSynonym(CSVRecordInfo<CoLTaxonCsvField> record) {
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

  private void createTable() throws SQLException {
    try (Statement stmt = connection.createStatement();) {
      stmt.execute(String.format("CREATE TABLE %s (id VARCHAR(50) NOT NULL, taxonId VARCHAR(50) NOT NULL, document LONGTEXT);", SYNONYM_NAMES));
    }
  }
  
  private void addIndex() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(String.format("CREATE INDEX synonymTaxonIdIndex ON %s (taxonId);", SYNONYM_NAMES));
    }
  }

  private long countRecordsCreated() throws SQLException {
    long n = 0L;
    try (Statement stmt  = connection.createStatement()) {
      try (ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) FROM %s;", SYNONYM_NAMES))) {
        rs.next();
        n = rs.getLong(1);
      }
    } 
    return n;
  }

}
