package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.ETLRegistry;

public class CoLWriteToJsonND {
  
  private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLWriteToJsonND.class);
  
  private static final String DB_DRIVER = "org.h2.Driver";
  private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String DB_USER = "";
  private static final String DB_PASSWORD = "";
  private static Connection connection;
  
//  private final ETLStatistics stats;

    public CoLWriteToJsonND() {
      connection = getDBConnection();
    }
  
    public static void main(String[] args)
    {
      CoLWriteToJsonND importer = new CoLWriteToJsonND();
      importer.importAll();
      
    }

    public void importAll() {
      long start = System.currentTimeMillis();
      String colDataDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
      int batchSize = 1000;
      
      // 1. Load synonyms and store them in the H2 database
      CoLSynonymLoader synonymLoader = new CoLSynonymLoader(connection);
      synonymLoader.importCsv(colDataDir + "/taxa.txt");
      logger.info("Loading of synonyms has finished");
      logger.info("{} synonyms have been created", countSynonyms());
      
      // 2. Load vernacular names and store them in the H2 database
      CoLVernacularNameLoader vernacularNameLoader = new CoLVernacularNameLoader(connection);
      vernacularNameLoader.setBatchSize(batchSize);
      vernacularNameLoader.importCsv(colDataDir + "/vernacular.txt");

        // 3. References
      CoLReferenceLoader referenceLoader = new CoLReferenceLoader(connection);
      referenceLoader.setBatchSize(batchSize);
      referenceLoader.importCsv(colDataDir + "/reference.txt");
      
      // And finally, create the taxon documents using the data collected earlier
      CoLTaxonImporterToJson cti = new CoLTaxonImporterToJson(connection);
      cti.importCsv(colDataDir + "/taxa.txt");
      
      logDuration(logger, getClass(), start);

      
    }
    
    private static Connection getDBConnection() {
      Connection dbConnection = null;
      try {
        Class.forName(DB_DRIVER);
      } catch (ClassNotFoundException e) {
        System.out.println(e.getMessage());
      }
      try {
        dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        return dbConnection;
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
      return dbConnection;
    }
    
    private long countSynonyms() {
      long n = 0L;
      Statement stmt = null;
      try {          
        connection.setAutoCommit(false);
        stmt = connection.createStatement();
        stmt.execute("SELECT COUNT(taxonId) FROM SYNONYMS");
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
