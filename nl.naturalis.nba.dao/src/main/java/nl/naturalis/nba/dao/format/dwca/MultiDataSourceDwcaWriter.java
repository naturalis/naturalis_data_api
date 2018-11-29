package nl.naturalis.nba.dao.format.dwca;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.DaoUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.dao.format.csv.CsvRecordWriter;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.DirtyScroller;
import nl.naturalis.nba.dao.util.es.ESUtil;
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
  private static TimeValue TIME_OUT = new TimeValue(10000);

  private DwcaConfig cfg;
  private OutputStream out;

  MultiDataSourceDwcaWriter(DwcaConfig dwcaConfig, OutputStream out) {
    this.cfg = dwcaConfig;
    this.out = out;
  }

  @Override
  public void writeDwcaForQuery(QuerySpec querySpec) throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException {
    // It's not possible to create a DarwinCore archive for a user-defined query with more than
    // one data source.
  }

  @Override
  public void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException {
    long start = System.currentTimeMillis();
    logger.info("Generating DarwinCore archive for multiple data sets");

    DwcaPreparator dwcaPreparator = new DwcaPreparator(cfg);
    dwcaPreparator.prepare();

    RandomEntryZipOutputStream rezos = createRandomEntryZipOutputStream();

    try {
      writeCsvFilesForDataSet(rezos);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    // Finally, add eml and meta xml files ....
    try {
      ZipOutputStream zos = rezos.mergeEntries();
      logger.info("Writing meta.xml");
      zos.putNextEntry(new ZipEntry("meta.xml"));
      zos.write(dwcaPreparator.getMetaXml());
      logger.info("Writing eml.xml ({})", cfg.getEmlFile());
      zos.putNextEntry(new ZipEntry("eml.xml"));
      zos.write(dwcaPreparator.getEml());
      zos.finish();
      String took = DaoUtil.getDuration(start);
      logger.info("DarwinCore archive generated (took {})", took);
    } catch (Throwable t) {
      String msg = "Error writing archive for dataset " + cfg.getDataSetName();
      logger.error(msg, t);
    }
    // fmt = "Finished writing DarwinCore archive for data set \"{}\"";
    // logger.info(fmt, cfg.getDataSetName());
    logger.info("Finished writing DarwinCore archive for multiple data sets");
  }

//  private void writeCsvFilesForQuery(QuerySpec querySpec) throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException, IOException {
//    for (Entity entity : cfg.getDataSet().getEntities()) {
//      String fileName = cfg.getCsvFileName(entity);
//      logger.info("Adding CSV file for entity {}", entity.getName());
//      // out.putNextEntry(new ZipEntry(fileName));
//      DocumentType<?> dt = DocumentType.forName(entity.getName());
//      writeCsvFile(entity, executeQuery(querySpec, dt));
//    }
//  }

  private void writeCsvFilesForDataSet(RandomEntryZipOutputStream rezos)
      throws DataSetConfigurationException, DataSetWriteException, IOException {

    logger.info(">>> Number of csv files to create: " + cfg.getDataSet().getEntities().length);

    for (Entity entity : cfg.getDataSet().getEntities()) {
      String fileName = cfg.getCsvFileName(entity);
      logger.info(">>> Creating csv file for entity {} ({})", entity.getName(), entity.getDataSource().getDocumentType());

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
      logger.info("Writing csv file for " + entity.getName());
      try {
        handler.printHeaders();
        scroller.scroll(handler);
        handler.logStatistics();
        rezos.flush();
      } catch (Throwable t) {
        logger.error("ERROR", t);
      }

      // out.putNextEntry(new ZipEntry(fileName));
      out.flush();
          
//      SearchResponse response;
//      try {
//        response = executeQuery(query, dt);
//      } catch (InvalidQueryException e) {
//        /*
//         * Not the user's fault but the application maintainer's, because we got the QuerySpec from
//         * the config file, so we convert the InvalidQueryException to a
//         * DataSetConfigurationException
//         */
//        String fmt = "Invalid query specification for entity %s:\n%s";
//        String queryString = JsonUtil.toPrettyJson(query);
//        String msg = String.format(fmt, entity, queryString);
//        throw new DataSetConfigurationException(msg);
//      }
//      logger.info(">>>> We could be writing " + response.getHits().getTotalHits() + " docs");
//      out.flush();
//      writeCsvFile(entity, response);
      
    }
  }

  private void writeCsvFile(Entity entity, SearchResponse response) throws DataSetWriteException, IOException {

    logger.info("We should be writing the csv file for " + entity.getName());

    Path path = entity.getDataSource().getPath();

    logger.info(">>>> path: " + path.toString());

    DocumentFlattener flattener = new DocumentFlattener(path);
    IField[] fields = entity.getFields();
    CsvRecordWriter csvPrinter = new CsvRecordWriter(fields, out);
    csvPrinter.printBOM();
    csvPrinter.printHeader();
    int processed = 0;
    while (true) {
      for (SearchHit hit : response.getHits().getHits()) {
        List<EntityObject> eos = flattener.flatten(hit.getSource());
        ENTITY_OBJECT_LOOP: for (EntityObject eo : eos) {
          for (IEntityFilter filter : entity.getFilters()) {
            if (!filter.accept(eo)) {
              continue ENTITY_OBJECT_LOOP;
            }
          }
          csvPrinter.printRecord(eo);
        }
        if (++processed % 10000 == 0) {
          csvPrinter.flush();
        }
        if (logger.isDebugEnabled() && processed % 100000 == 0) {
          logger.debug("Documents processed: " + processed);
        }
      }
      String scrollId = response.getScrollId();
      Client client = ESClientManager.getInstance().getClient();
      SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
      response = ssrb.setScroll(TIME_OUT).execute().actionGet();
      if (response.getHits().getHits().length == 0) {
        break;
      }
    }
    out.flush();
  }

  private static SearchResponse executeQuery(QuerySpec spec, DocumentType<?> dt) throws InvalidQueryException {
    QuerySpecTranslator qst = new QuerySpecTranslator(spec, dt);
    SearchRequestBuilder request = qst.translate();
    request.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
    request.setScroll(TIME_OUT);
    request.setSize(1000);
    return ESUtil.executeSearchRequest(request);
  }

  private RandomEntryZipOutputStream createRandomEntryZipOutputStream()
      throws DataSetConfigurationException, DataSetWriteException {
    Entity[] entities = cfg.getDataSet().getEntities();
    Entity firstEntity = entities[0];
    String fileName = cfg.getCsvFileName(firstEntity);
    logger.info("First fileName for RandomEntryZipOutputStream: " + fileName);
    RandomEntryZipOutputStream rezos = null;
    try {
      rezos = new RandomEntryZipOutputStream(out, fileName);
    } catch (IOException exc) {
      IOUtil.close(rezos);
      throw new DataSetWriteException(exc);
    }
    HashSet<String> fileNames = new HashSet<>();
    for (Entity e : entities) {
      /*
       * NB Multiple entities may get written to the same zip entry (e.g. taxa and synonyms are both
       * written to taxa.txt). Thus we must make sure to create only unique zip entries.
       */
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
