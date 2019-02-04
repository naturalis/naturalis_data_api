package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;

/**
 * Manages the import of taxa, synonyms, vernacular names and literature references from
 * the Catalogue of Life.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
public class CoLImportAll {
  
  private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLImportAll.class);
  
  private static final String DB_DRIVER = "org.h2.Driver";
  private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String DB_USER = "";
  private static final String DB_PASSWORD = "";
  private static Connection connection;

  private int batchSize;
  
  public CoLImportAll() {}
  
  public int getBatchSize()
  {
    return batchSize;
  }
  
  public void setBatchSize(int batchSize)
  {
    this.batchSize = batchSize;
  }

  public static void main(String[] args) throws Exception
	{
		String prop = System.getProperty("batchSize", "1000");
		boolean toFile = DaoRegistry.getInstance().getConfiguration().get("etl.output", "file").equals("file");
		int batchSize = 0;
		try {
			batchSize = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid batch size: " + prop);
		}
		if (batchSize >= 1024) {
			// Elasticsearch ids query won't let you look up more than 1024 at once.
			throw new ETLRuntimeException("Batch size exceeds maximum of 1024");
		}
		try {
			CoLImportAll importer = new CoLImportAll();
			importer.setBatchSize(batchSize);
			importer.importAllToFile();
//			if (toFile) {
//			  importer.importAllToFile();
//			}
//			else {
//			  importer.importAll();			  
//			}
		}
		catch (Throwable t) {
			logger.error("CoLImportAll terminated unexpectedly!", t);
			System.exit(1);
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	/**
	 * Imports CoL taxa, synonyms, vernacular names and literature references.
	 * 
	 * @throws BulkIndexException
	 * 
	 */
	public void importAll() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
		CoLTaxonImporter cti = new CoLTaxonImporter();
		cti.importCsv(dwcaDir + "/taxa.txt");
		CoLSynonymBatchImporter csbi = new CoLSynonymBatchImporter();
		csbi.setBatchSize(batchSize);
		csbi.importCsv(dwcaDir + "/taxa.txt");
		CoLVernacularNameBatchImporter cvbi = new CoLVernacularNameBatchImporter();
		cvbi.setBatchSize(batchSize);
		cvbi.importCsv(dwcaDir + "/vernacular.txt");
		CoLReferenceBatchImporter crbi = new CoLReferenceBatchImporter();
		crbi.setBatchSize(batchSize);
		crbi.importCsv(dwcaDir + "/reference.txt");
		logDuration(logger, getClass(), start);
	}

  /**
   * Reads the CoL source file and creates CoL taxa documents, including
   * synonyms, vernacular names and literature references.
   * 
   * 
   */
  public void importAllToFile()
  {
    logger.info(">>>>>>>>>> IMPORT ALL TO FILE <<<<<<<<<<<<<");
    long start = System.currentTimeMillis();
    String colDataDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
    try (Connection connection = getDBConnection()) {

      // 1. Load synonym names into intermediate database
      CoLSynonymLoader synonymLoader = new CoLSynonymLoader(connection);
      synonymLoader.setBatchSize(batchSize);
      synonymLoader.importCsv(colDataDir + "/taxa.txt");
    
      // 2. Load vernacular names into intermediate database
      CoLVernacularNameLoader vernacularNameLoader = new CoLVernacularNameLoader(connection);
      vernacularNameLoader.setBatchSize(batchSize);
      vernacularNameLoader.importCsv(colDataDir + "/vernacular.txt");
    
      // 3. Load literature references into intermediate database
      CoLReferenceLoader referenceLoader = new CoLReferenceLoader(connection);
      referenceLoader.setBatchSize(batchSize);
      referenceLoader.importCsv(colDataDir + "/reference.txt");

      // Finally, create the taxon documents using the data from intermediate database
      CoLTaxonFullImporter cti = new CoLTaxonFullImporter(connection);
      cti.importCsv(colDataDir + "/taxa.txt");

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    
    logDuration(logger, getClass(), start);
  }
	
	public void importTaxa() {
	  logger.info("Creating taxa documents without enrichments");
    long start = System.currentTimeMillis();
    String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
    CoLTaxonImporter cti = new CoLTaxonImporter();
    cti.importCsv(dwcaDir + "/taxa.txt");
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

}