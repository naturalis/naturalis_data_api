package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;
import nl.naturalis.nba.common.json.JsonDeserializationException;

/**
 * The JsonImporter class adds new documents from a file (or files) 
 * to the Document Store. The document in the file(s) have to be in 
 * the correct Json format of the given DocumentType.
 */
public class JsonImporter {

  private boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);
  private DocumentType<? extends IDocumentObject> docType;
  private final int esBulkRequestSize;
  private static final Logger logger = getLogger(JsonImporter.class);

  JsonImporter(DocumentType<? extends IDocumentObject> docType) {
    this.docType = docType;
    String key = ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
    String val = System.getProperty(key, "1000");
    esBulkRequestSize = Integer.parseInt(val);
  }
  
  public static void main(String[] args) {
    String documentType = args[0];
    JsonImporter importer = new JsonImporter( DocumentType.forName(documentType) );
    try {
      File[] files = getJsonFiles(documentType);
      Arrays.sort(files);
      importer.importJsonFiles(files);
    } catch (Throwable t) {
      logger.error("JsonImporter terminated unexpectly!", t);
      System.exit(1);      
    }
    importer.refresh();
  }

  public void importJsonFiles(File[] jsonFiles) throws IOException {
    for (File f : jsonFiles) {
      System.out.println("Processing file: " + f.getName());
      logger.info("Processing file: " + f.getName());
      try {
        importJsonFile(f, docType);        
      } catch (BulkIndexException e) {
        logger.warn("Import file contains errors: " + e.getMessage());
        logger.warn("Not all documents in " + f.getName() + " have been processed!");
      }
    }
  }
  
  private <T extends IDocumentObject> void importJsonFile(File file, DocumentType<T> docType) throws IOException, BulkIndexException {
    BulkIndexer<T> indexer = new BulkIndexer<>(docType);
    Collection<T> batch = new ArrayList<>(esBulkRequestSize);
    LineNumberReader lnr = null;
    int processed = 0;
    int skipped = 0;
    String msg = "";
    
    try {
      FileReader fr = new FileReader(file);
      lnr = new LineNumberReader(fr, 4096);
      String line;
      int lineNumber = 0;
      while ((line = lnr.readLine()) != null) {
        lineNumber++;
        try {
          T documentObject = JsonUtil.deserialize(line, docType.getJavaType());
          batch.add(documentObject);
          processed++;
        } catch (RuntimeException e) {
          skipped++;
          if (e instanceof JsonDeserializationException) {
            logger.error("The document at line {} is of invalid format. This document has been skipped!", lineNumber);
          } else {
            logger.error("An error occurred while processing the document at line {}. This document has been skipped!", lineNumber);
          }
          logger.error("Reason: \n" + e.getMessage());
        }
        if (batch.size() == esBulkRequestSize) {
          if (!dryRun) {
            indexer.index(batch);
            logger.info(docType.getName() + " documents imported: {}", processed);
          }
          batch.clear();
        }
      }
      if (!batch.isEmpty()) {
        indexer.index(batch);
      }
    } finally {
      IOUtil.close(lnr);
      logger.info(docType.getName() + " documents skipped: {}", skipped);
      logger.info(docType.getName() + " documents imported: {}", processed);
      if (skipped > 0) {
        msg = (skipped == 1) ? " document was skipped!" : " documents were skipped!";
        System.out.println("WARNING: the file has been processed but with errors! " + skipped + msg);
      }
      msg = (processed == 1) ? " document was imported." : " documents were imported.";
      System.out.println(processed + msg);
    }
  }
  
  private void refresh() {
    
    RestHighLevelClient client = ESClientManager.getInstance().getClient();
    String index = docType.getIndexInfo().getName();
    String msg = "Failed to refresh the index \"%s\": %s";
    
    try {
      RefreshRequest request = new RefreshRequest(index); 
      client.indices().refresh(request, RequestOptions.DEFAULT);
    } catch (ElasticsearchException e) {
      if (e.status() == RestStatus.NOT_FOUND) {
        logger.error(String.format(msg, index + " - Server was not found (404).", e.getMessage()));        
      } else {
        logger.error(String.format(msg, index + " - (" + e.status().ordinal() + ")" , e.getMessage()));
      }
    } catch (IOException e) {
      logger.error(String.format(msg, index, e.getMessage()));
      throw new DaoException(String.format(msg, index, e.getMessage()));
    }
  }
  
  public static File[] getJsonFiles(String documentType) {
    String path = "";
    switch(documentType) {
      case("Specimen") : path = "specimen"; break;
      case("MultiMediaObject") : path = "multimedia"; break;
      case("Taxon") : path = "taxa"; break;
    }
    File[] files = getDataDir(path).listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".json") || name.toLowerCase().endsWith(".ndjson");
      }
    });    
    return files;
  }

  private static File getDataDir(String path) {
    return FileUtil.newFile(DaoRegistry.getInstance().getConfiguration().getDirectory("json.data.dir"), path);
  }

}
