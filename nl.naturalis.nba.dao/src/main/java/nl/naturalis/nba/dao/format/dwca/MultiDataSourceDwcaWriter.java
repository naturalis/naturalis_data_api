package nl.naturalis.nba.dao.format.dwca;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.DaoUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.DirtyScroller;
import nl.naturalis.nba.dao.util.es.IScroller;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Manages the assembly and creation of DarwinCore archives. Use this class if you cannot generate
 * all CSV files from a single query (each CSV file requires a new query to be executed). With this
 * class a separate query is issued for each entity (i.e. each file generated as part of the DwC
 * archive). A {@code MultiDataSourceDwcaWriter} is used for dataset configuration files where each
 * &;lt;entity&gt; element has its own &;lt;data-source&gt; element. See also the XSD for dataset
 * configuration files in src/main/resources.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
class MultiDataSourceDwcaWriter implements IDwcaWriter {

  private static Logger logger = LogManager.getLogger(MultiDataSourceDwcaWriter.class);

  private DwcaConfig cfg;
  private OutputStream out;

  MultiDataSourceDwcaWriter(DwcaConfig dwcaConfig, OutputStream out) {
    this.cfg = dwcaConfig;
    this.out = out;
  }
  
  /** 
   * It's not possible -not at the moment at least- to create a DarwinCore archive for 
   * a user-defined query with more than one data source. The query the end-user provides does
   * not have to be a valid query for each of the datasets after all.
   */
  @Override
  public void writeDwcaForQuery(QuerySpec querySpec) throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException 
  {
    // Impossible ...
  }

  @Override
  public void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException {
    
    long start = System.currentTimeMillis();
    logger.info("Generating DarwinCore archive from multiple data sets");

    DwcaPreparator dwcaPreparator = new DwcaPreparator(cfg);
    dwcaPreparator.prepare();
    RandomEntryZipOutputStream rezos = createRandomEntryZipOutputStream();
    // Create csv files for each of the entities
    try {
      writeCsvFilesForDataSet(rezos);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    // ... and add eml and meta xml files
    try {
      ZipOutputStream zos = rezos.mergeEntries();
      logger.info("Writing meta.xml");
      zos.putNextEntry(new ZipEntry("meta.xml"));
      zos.write(dwcaPreparator.getMetaXml());
      logger.info("Writing eml.xml ({})", cfg.getEmlFile());
      zos.putNextEntry(new ZipEntry("eml.xml"));
      zos.write(dwcaPreparator.getEml());
      zos.finish();
    } catch (Throwable t) {
      String msg = "Error writing archive for dataset " + cfg.getDataSetName();
      logger.error(msg, t);
    }
    String took = DaoUtil.getDuration(start);
    logger.info("DarwinCore archive generated (took {})", took);
    logger.info("Finished writing DarwinCore archive for multiple data sets");
  }

  private void writeCsvFilesForDataSet(RandomEntryZipOutputStream rezos) throws DataSetConfigurationException, DataSetWriteException, IOException {

    HashSet<String> done = new HashSet<>(); // Keeps track of whether the header for an entity has been printed already
    
    for (Entity entity : cfg.getDataSet().getEntities()) {
      String fileName = cfg.getCsvFileName(entity);
      logger.info("Writing csv file for entity {} ({})", entity.getName(), entity.getDataSource().getDocumentType());

      QuerySpec query = entity.getDataSource().getQuerySpec();
      DocumentType<?> dt = entity.getDataSource().getDocumentType();

      IScroller scroller;
      try {
        // Hard coded to use DirtyScroller. This may need to be changed!!!
        scroller = new DirtyScroller(query, dt);
      } catch (InvalidQueryException e) {
        throw new DataSetConfigurationException(e);
      }
      
      MultiDataSourceSearchHitHandler handler = new MultiDataSourceSearchHitHandler(entity, fileName, rezos);
      try {
        if (!done.contains(fileName)) // Check if the header for this csv file has been printed already 
        {
          handler.printHeaders();
          done.add(fileName);
        }
        scroller.scroll(handler);
        handler.logStatistics();
        rezos.flush();
      } catch (Throwable t) {
        logger.error("ERROR", t);
        String msg = "Error while writing archive for entity " + entity.getName() + " of dataset " + cfg.getDataSetName();
        logger.error(msg, t);
        try {
          ZipOutputStream zos = rezos.mergeEntries();
          zos.putNextEntry(new ZipEntry("__ERROR__.txt"));
          t.printStackTrace(new PrintStream(zos));
          zos.finish();
        }
        catch (Throwable t2) {
          logger.error(t2);
        }
        return;
      }
      out.flush();
      }
  }

  private RandomEntryZipOutputStream createRandomEntryZipOutputStream() throws DataSetConfigurationException, DataSetWriteException {
    Entity[] entities = cfg.getDataSet().getEntities();
    Entity firstEntity = entities[0];
    String fileName = cfg.getCsvFileName(firstEntity);
    RandomEntryZipOutputStream rezos = null;
    try {
      rezos = new RandomEntryZipOutputStream(out, fileName);
    } 
    catch (IOException exc) {
      IOUtil.close(rezos);
      throw new DataSetWriteException(exc);
    }
    /*
     * NB Multiple entities may get written to the same zip entry (e.g. taxa and synonyms are both
     * written to taxa.txt). Thus we must make sure to create only unique zip entries.
     */
    HashSet<String> fileNames = new HashSet<>();
    for (Entity e : entities) {
      fileName = cfg.getCsvFileName(e);
      if (fileNames.contains(fileName)) {
        continue;
      }
      fileNames.add(fileName);
      if (e.getName().equals(firstEntity.getName())) {
        continue;
      }
      try {
        rezos.addEntry(fileName, 1024 * 1024);
      } catch (IOException exc) {
        IOUtil.close(rezos);
        throw new DataSetWriteException(exc);
      }
    }
    return rezos;
  }
}
