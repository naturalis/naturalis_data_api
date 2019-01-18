package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.utils.FileUtil;

public abstract class JsonNDWriter<T extends IDocumentObject> implements DocumentObjectWriter<T> {

  private static final Logger logger = getLogger(JsonNDWriter.class);

  private String sourceFile;
  private File file;
  private FileOutputStream fos = null;
  private BufferedOutputStream bos = null;
  private ObjectWriter writer;
  private final ETLStatistics stats;
 
  private String sourceSystem;
  private DocumentType<T> documentType;
 
  private boolean suppressErrors;

  private static final byte[] NEW_LINE = "\n".getBytes();

  /**
   * Creates a JsonND (newline delimited JSON) writer for the specified document 
   * type. The writer will write all documents to an export file.
   * 
   * @param documentType
   * @param sourceSystem
   * @param sourceFile
   * @param stats
   */
  public JsonNDWriter(DocumentType<T> dt, String sourceSystem, String sourceFile, ETLStatistics stats) {
    this.documentType = dt;
    this.sourceSystem = sourceSystem.toLowerCase();
    this.sourceFile = sourceFile;
    this.stats = stats;
    createExportFile();
    openFile();
    createWriter();
    logger.info("Writing documents to: " + file.getAbsolutePath());
  }


  private void openFile() {
    try {
      fos = new FileOutputStream(file, true);
      bos = new BufferedOutputStream(fos, 4096);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void closeFile() {
    try {
      if (bos != null)
        bos.close();
      if (fos != null)
        fos.close();
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
  }

  private void createWriter() {
    ObjectMapper mapper = ObjectMapperLocator.getInstance().getObjectMapper(documentType.getJavaType());
    this.writer = mapper.writer();
  }

  
  @Override
  public final void write(Collection<T> objects) {

    if (objects == null || objects.size() == 0) {
      return;
    }

    for (T object : objects) {
      if (object != null) {
        try {
          // writer.writeValue(bos, object); // Closes the outputstream, therefore:
          bos.write(writer.writeValueAsBytes(object));
          bos.write(NEW_LINE);
          stats.documentsIndexed++;
        } catch (IOException e) {
          if (!suppressErrors)
            logger.warn(e.getMessage());
        }
      }
    }
    
  }

  @Override
  public void close() throws IOException {
    closeFile();
  }

  /**
   * Determines whether to suppress ERROR and WARN messages while still letting through INFO messages.
   * This is sometimes helpful if you expect large amounts of well-known errors and warnings that just
   * clog up your log file.
   * 
   * @param suppressErrors
   */
  public void suppressErrors(boolean suppressErrors) {
    this.suppressErrors = suppressErrors;
  }

  private void createExportFile() {
    File dir = DaoRegistry.getInstance().getConfiguration().getDirectory("nba.etl.install.dir");
    File exportDir = FileUtil.newFile(dir, "export" + "/" + documentType.getName().toLowerCase());
    if (!exportDir.isDirectory()) {
      exportDir.mkdirs();
    }
    StringBuilder name = new StringBuilder(100);
    name.append(sourceSystem).append(".");
    name.append(documentType.getName().toLowerCase());
    name.append(".export.");
    name.append(sourceFile).append(".");
    Supplier<String> exportDateTime = () -> LocalDateTime.now().toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
    name.append(exportDateTime.get());
    name.append(".ndjson");
    file = FileUtil.newFile(exportDir, name.toString());      
  }

}
