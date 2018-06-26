package nl.naturalis.nba.etl;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
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
import nl.naturalis.nba.api.model.NbaTraceableObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;

public class JsonImporter {

  private int writeBatchSize = 1000;
  private static final Logger logger = getLogger(JsonImporter.class);
  private DocumentType<?> dt;

  private <T extends IDocumentObject> JsonImporter(DocumentType<T> dt) {
    this.dt = dt;
  }

  public static void main(String[] args) {
    DocumentType<?> dt = DocumentType.forName(args[0]);
    JsonImporter importer = new JsonImporter(dt);
    try {
      if (args.length == 1 || args[1].trim().length() == 0) {
        importer.importJsonFiles();
      } else {
        importer.importJsonFile(args[1]);
      }
    } catch (Throwable t) {
      logger.error("JsonImporter terminated unexpectly!", t);
      System.exit(1);
    }
  }

  public void importJsonFile(String path) throws IOException, BulkIndexException {
    File file;
    if (path.startsWith("/")) {
      file = new File(path);
    } else {
      file = FileUtil.newFile(getDataDir(), path);
    }
    if (!file.isFile()) {
      logger.error("No such file: " + file.getAbsolutePath());
      throw new ETLRuntimeException("No such file: " + file.getAbsolutePath());
    }
    importJsonFiles(new File[] {file});
  }

  public void importJsonFiles() throws IOException, BulkIndexException {
    importJsonFiles(getJsonFiles());
  }

  public void importJsonFiles(File[] jsonFiles) throws IOException, BulkIndexException {
    for (File f : jsonFiles) {
      System.out.println("Processing file: " + f.getName());
      importFile(f);
    }
  }

  private static File getDataDir() {
    return DaoRegistry.getInstance().getConfiguration().getDirectory("json.data.dir");
  }

  public static File[] getJsonFiles() {
    File[] files = getDataDir().listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".json");
      }
    });
    return files;
  }

  private <T> void importFile(File file) throws IOException, BulkIndexException {
    boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

    BulkIndexer indexer = new BulkIndexer<>(dt);
    List<IDocumentObject> batch = new ArrayList<>(writeBatchSize);

    LineNumberReader lnr = null;
    int processed = 0;
    try {
      FileReader fr = new FileReader(file);
      lnr = new LineNumberReader(fr, 4096);
      String line;
      while ((line = lnr.readLine()) != null) {
        IDocumentObject specimen = JsonUtil.deserialize(line, dt.getJavaType());
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

}
