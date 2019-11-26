package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createMultiMediaLookupTableForSpecimens;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createTempFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DirtyDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

public class SpecimenMultimediaEnricher {

  public static void main(String[] args) {
    SpecimenMultimediaEnricher enricher = new SpecimenMultimediaEnricher();
    try {
      enricher.configureWithSystemProperties();
      enricher.enrich();
    } catch (Throwable t) {
      logger.error("Error while enriching specimens", t);
      System.exit(1);
    } finally {
      ESUtil.refreshIndex(SPECIMEN);
      ESClientManager.getInstance().closeClient();
    }
    System.exit(0);
  }

  private static final Logger logger = getLogger(SpecimenMultimediaEnricher.class);
  private static final byte[] NEW_LINE = "\n".getBytes();

  private int readBatchSize = 1000;
  private int writeBatchSize = 1000;

  private File tempFile;

  public void enrich() throws IOException, BulkIndexException {
    if (ConfigObject.isEnabled(SYSPROP_DRY_RUN)) {
      logger.info("Enrichment skipped dry run mode");
      return;
    }
    long start = System.currentTimeMillis();
    tempFile = createTempFile(getClass().getSimpleName());
    logger.info("Writing enriched specimens to " + tempFile.getAbsolutePath());
    saveToTempFile();
    logger.info("Reading enriched specimens from " + tempFile.getAbsolutePath());
    importTempFile();
    tempFile.delete();
    logDuration(logger, getClass(), start);
  }

  private void saveToTempFile() throws IOException {
    FileOutputStream fos = new FileOutputStream(tempFile);
    BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
    DocumentType<Specimen> dt = SPECIMEN;
    QueryCondition condition = new QueryCondition("sourceSystem.code", "=", "CRS");
    QuerySpec qs = new QuerySpec();
    qs.setConstantScore(true);
    qs.addCondition(condition);
    qs.setSize(readBatchSize);
    DirtyDocumentIterator<Specimen> extractor = new DirtyDocumentIterator<>(dt, qs);
    int batchNo = 0;
    int enriched = 0;
    List<Specimen> batch = extractor.nextBatch();
    try {
      while (batch != null) {
        List<Specimen> enrichedSpecimens = enrichSpecimens(batch);
        enriched += enrichedSpecimens.size();
        // Temporary file with JSON record per line
        for (Specimen specimen : enrichedSpecimens) {
          byte[] json = JsonUtil.serialize(specimen);
          bos.write(json);
          bos.write(NEW_LINE);
        }
        if (++batchNo % 100 == 0) {
          logger.info("Specimen documents processed: {}", (batchNo * readBatchSize));
          logger.info("Specimen documents enriched: {}", enriched);
        }
        batch = extractor.nextBatch();
      }
    } finally {
      bos.close();
      logger.info("Specimen documents read: {}", (batchNo * readBatchSize));
      logger.info("Specimen documents enriched: {}", enriched);
    }
  }

  private void importTempFile() throws IOException, BulkIndexException {
    boolean dryRun = ConfigObject.isEnabled(SYSPROP_DRY_RUN);
    BulkIndexer<Specimen> indexer = new BulkIndexer<>(SPECIMEN);
    List<Specimen> batch = new ArrayList<>(writeBatchSize);
    LineNumberReader lnr = null;
    int processed = 0;
    try {
      FileReader fr = new FileReader(tempFile);
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
          logger.info("Specimen documents updated: {}", processed);
        }
      }
    } finally {
      if (!dryRun && batch.size() > 0) {
        indexer.index(batch);
        logger.info("Specimen documents updated: {}", processed);
      }
      IOUtil.close(lnr);
    }
  }

  public void configureWithSystemProperties() {
    String prop = System.getProperty(SYS_PROP_ENRICH_READ_BATCH_SIZE, "1000");
    try {
      setReadBatchSize(Integer.parseInt(prop));
    } catch (NumberFormatException e) {
      throw new ETLRuntimeException("Invalid read batch size: " + prop);
    }
    prop = System.getProperty(SYS_PROP_ENRICH_WRITE_BATCH_SIZE, "1000");
    try {
      setWriteBatchSize(Integer.parseInt(prop));
    } catch (NumberFormatException e) {
      throw new ETLRuntimeException("Invalid write batch size: " + prop);
    }
  }

  public int getReadBatchSize() {
    return readBatchSize;
  }

  public void setReadBatchSize(int readBatchSize) {
    this.readBatchSize = readBatchSize;
  }

  public int getWriteBatchSize() {
    return writeBatchSize;
  }

  public void setWriteBatchSize(int writeBatchSize) {
    this.writeBatchSize = writeBatchSize;
  }

  private static List<Specimen> enrichSpecimens(List<Specimen> specimens) {
    if (logger.isDebugEnabled()) {
      logger.debug("Creating multimedia lookup table");
    }
    Map<String, List<ServiceAccessPoint>> multimediaLookupTable;
    multimediaLookupTable = createMultiMediaLookupTableForSpecimens(specimens);
    logger.info(">>> multimediaLookupTable size: {}", multimediaLookupTable.size());
    if (multimediaLookupTable.isEmpty()) {
      if (logger.isDebugEnabled()) {
        logger.debug("No multimedia found for current batch of specimens");
      }
      return Collections.emptyList();
    }
    List<Specimen> enriched = new ArrayList<>(specimens.size());
    for (Specimen specimen : specimens) {
      List<ServiceAccessPoint> uris = multimediaLookupTable.get(specimen.getId());
      if (uris != null) {
        specimen.setAssociatedMultiMediaUris(uris);
        enriched.add(specimen);
      }
    }
    return enriched;
  }

}
