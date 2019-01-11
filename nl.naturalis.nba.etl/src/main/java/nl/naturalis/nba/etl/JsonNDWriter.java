package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.utils.FileUtil;

public abstract class JsonNDWriter<T extends IDocumentObject> implements DocumentObjectWriter<T> {

  private static final Logger logger = getLogger(JsonNDWriter.class);
  
  private final ETLStatistics stats;
  private final ArrayList<T> objs;
  
  private String sourceSystem;
  private String documentType;
  
  private int threshold;
  private boolean suppressErrors;
  
  private static final byte[] NEW_LINE = "\n".getBytes();
  
  private File file;
  
  
  /**
   * Creates ...
   *  
   * @param documentType
   * @param sourceSystem
   * @param queueSize
   * @param stats
   */
  public JsonNDWriter(DocumentType<T> dt, String sourceSystem, int queueSize, ETLStatistics stats){
    this.documentType = dt.getName().toLowerCase();
    this.sourceSystem = sourceSystem.toLowerCase();
    this.threshold = queueSize;
    this.stats = stats;
    int sz = queueSize == 0 ? 256 : queueSize + 16;
    objs = new ArrayList<>(sz);
    createExportFile();
    logger.info("Writing documents to: " + file.getAbsolutePath());
  }
  
  @Override
  public final void write(Collection<T> objects) 
  {  
    if (objects == null || objects.size() == 0) {
      return;
    }
    objs.addAll(objects);
    if (threshold != 0 && threshold < objs.size()) {
      flush();
    }
  }

  @Override
  public void flush() {
    if (!objs.isEmpty()) {
        try 
        {
          saveToFile(objs);          
        } catch (IOException e) 
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        finally {
          // TODO
        }
      objs.clear();
    }
  }

  private void saveToFile(ArrayList<T> objs) throws IOException {
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    try {
      fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos, 4096);
      for (T obj : objs) 
      {
        byte[] json = JsonUtil.serialize(obj);
        bos.write(json);
        bos.write(NEW_LINE);
        stats.documentsIndexed++;            
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      bos.close();
    }
  }
  
  @Override
  public void close() throws IOException 
  {
    flush();
  }

  
  /**
   * Determines whether to suppress ERROR and WARN messages while still
   * letting through INFO messages. This is sometimes helpful if you expect
   * large amounts of well-known errors and warnings that just clog up your
   * log file.
   * 
   * @param suppressErrors
   */
  public void suppressErrors(boolean suppressErrors)
  {
    this.suppressErrors = suppressErrors;
  }

  
  private void createExportFile() {
    File dir = DaoRegistry.getInstance().getConfiguration().getDirectory("nba.etl.install.dir");
    File exportDir = FileUtil.newFile(dir, "export" + "/" + documentType);
    if (!exportDir.isDirectory()) {
      exportDir.mkdirs();
    }
    StringBuilder name = new StringBuilder(100);
    name.append(sourceSystem);
    name.append(".");
    name.append(documentType);
    name.append(".export.");
    name.append(System.currentTimeMillis());
    name.append(".ndjson");
    file = FileUtil.newFile(exportDir, name.toString());
  }

}
