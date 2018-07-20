package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;

public class JsonImporter {

  private int writeBatchSize = 10;
  private static final Logger logger = getLogger(JsonImporter.class);
  private DocumentType<?> docType;
  private String dir;

  public static void main(String[] args) {
    
    String documentType = args[0];
    JsonImporter importer = new JsonImporter();
    importer.setDocType(DocumentType.forName(documentType));

    try {
      File[] files = getJsonFiles(documentType);
      importer.importJsonFiles(files);
    } catch (Throwable t) {
      logger.error("JsonImporter terminated unexpectly!", t);
      System.exit(1);      
    }
  }

  public void importJsonFiles() throws IOException, BulkIndexException {
    importJsonFiles(getJsonFiles(dir));
  }

  public void importJsonFiles(File[] jsonFiles) throws IOException, BulkIndexException {
    for (File f : jsonFiles) {
      System.out.println("Processing file: " + f.getName());
      this.importSomeFile(f);
    }
  }
  
  public static File[] getJsonFiles(String documentType) {
    System.out.println("getJsonFiles: " + documentType);
    String dir = "";
    switch(documentType) {
      case("Specimen") : dir = "specimen"; break;
      case("MultiMediaObject") : dir = "multimedia"; break;
    }
    File[] files = getDataDir(dir).listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".json");
      }
    });
    
    return files;
  }
  
  public void importJsonFile(String path) throws IOException, BulkIndexException {
    File file;
    if (path.startsWith("/")) {
      file = new File(path);
    } else {
      file = FileUtil.newFile(getDataDir(dir), path);
    }
    if (!file.isFile()) {
      logger.error("No such file: " + file.getAbsolutePath());
      throw new ETLRuntimeException("No such file: " + file.getAbsolutePath());
    }
    importJsonFiles(new File[] {file});
  }

  private static File getDataDir(String dir) {
    return FileUtil.newFile(DaoRegistry.getInstance().getConfiguration().getDirectory("json.data.dir"), dir);
  }

//  private void importFile(File file) throws IOException, BulkIndexException {
//    boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);
//
//    BulkIndexer indexer = new BulkIndexer(dt);
//    List<IDocumentObject> batch = new ArrayList<>(writeBatchSize);
//
//    LineNumberReader lnr = null;
//    int processed = 0;
//    try {
//      FileReader fr = new FileReader(file);
//      lnr = new LineNumberReader(fr, 4096);
//      String line;
//      while ((line = lnr.readLine()) != null) {
//        IDocumentObject specimen = JsonUtil.deserialize(line, dt.getJavaType());
//        batch.add(specimen);
//        if (batch.size() == writeBatchSize) {
//          if (!dryRun) {
//            indexer.index(batch);
//          }
//          batch.clear();
//        }
//        if (++processed % 100000 == 0) {
//          logger.info("Specimen documents imported: {}", processed);
//        }
//        if (batch != null) {
//          indexer.index(batch);
//          logger.info("Specimen documents imported: {}", processed);
//        }
//      }
//    } finally {
//      IOUtil.close(lnr);
//    }
//  }


  
  
  private void importSomeFile(File file) throws IOException, BulkIndexException {
    
    boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);
    
    BulkIndexer indexer = new BulkIndexer<>(docType);
    List<IDocumentObject> batch = new ArrayList<>(writeBatchSize);

    LineNumberReader lnr = null;
    int processed = 0;
    
    try {
      FileReader fr = new FileReader(file);
      lnr = new LineNumberReader(fr, 4096);
      String line;
      while ((line = lnr.readLine()) != null) {
        
        IDocumentObject documentObject = JsonUtil.deserialize(line, docType.getJavaType());
        batch.add(documentObject);
        if (batch.size() == writeBatchSize) {
          if (!dryRun) {
            indexer.index(batch);
            logger.info(dir + " documents imported: {}", processed);
          }
          batch.clear();
        }
      }
      if (!batch.isEmpty()) {
        indexer.index(batch);
        logger.info(dir + " documents imported: {}", processed);
      }
    } finally {
      IOUtil.close(lnr);
    }
  }


  
  @SuppressWarnings("unused")
  private void importSpecimenFile(File file) throws IOException, BulkIndexException {
    boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    BulkIndexer<Specimen> indexer = new BulkIndexer<>(dt);
    List<Specimen> batch = new ArrayList<>(writeBatchSize);

    LineNumberReader lnr = null;
    int processed = 0;
    try {
      FileReader fr = new FileReader(file);
      lnr = new LineNumberReader(fr, 4096);
      String line;
      while ((line = lnr.readLine()) != null) {
        Specimen specimen = JsonUtil.deserialize(line, Specimen.class);
        batch.add(specimen);
        if (batch.size() == writeBatchSize) {
          if (!dryRun) {
            indexer.index(batch);
          }
          batch.clear();
        }
        if (++processed % 100000 == 0) {
          logger.info("Specimen documents imported: {}", processed);
        }
        if (batch != null) {
          indexer.index(batch);
          logger.info("Specimen documents imported: {}", processed);
        }
      }
    } finally {
      IOUtil.close(lnr);
    }
  }

  @SuppressWarnings("unused")
  private void importMultimediaFile(File file) throws IOException, BulkIndexException {
    boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

    DocumentType<MultiMediaObject> dt = DocumentType.MULTI_MEDIA_OBJECT;
    BulkIndexer<MultiMediaObject> indexer = new BulkIndexer<>(dt);
    List<MultiMediaObject> batch = new ArrayList<>(writeBatchSize);

    LineNumberReader lnr = null;
    int processed = 0;
    try {
      FileReader fr = new FileReader(file);
      lnr = new LineNumberReader(fr, 4096);
      String line;
      while ((line = lnr.readLine()) != null) {
        MultiMediaObject multimediaObject = JsonUtil.deserialize(line, MultiMediaObject.class);
        batch.add(multimediaObject);
        if (batch.size() == writeBatchSize) {
          if (!dryRun) {
            indexer.index(batch);
          }
          batch.clear();
        }
        if (++processed % 100000 == 0) {
          logger.info("Multimedia documents imported: {}", processed);
        }
        if (batch != null) {
          indexer.index(batch);
          logger.info("Multimedia documents imported: {}", processed);
        }
      }
    } finally {
      IOUtil.close(lnr);
    }
  }

  public DocumentType<?> getDocType() {
    return docType;
  }

  public void setDocType(DocumentType<?> docType) {
    this.docType = docType;
  }

  
  
  
}
