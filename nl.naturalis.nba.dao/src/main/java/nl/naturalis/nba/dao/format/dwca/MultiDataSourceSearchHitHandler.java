package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.dao.format.csv.CsvRecordWriter;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.dao.util.es.SearchHitHandler;

class MultiDataSourceSearchHitHandler implements SearchHitHandler {

  private static final Logger logger = getLogger(MultiDataSourceSearchHitHandler.class);

  private RandomEntryZipOutputStream zip;
  private final Entity entity;
  private final String fileName;
  private final CsvRecordWriter printer;
  private final DocumentFlattener flattener;

  private int processed = 0;
  private int written = 0;
  private int filtered = 0;

  MultiDataSourceSearchHitHandler(Entity entity, String fileName, RandomEntryZipOutputStream rezos) {
    this.zip = rezos;
    this.entity = entity;
    this.fileName = fileName;
    this.printer = getPrinter(rezos);
    Path path = entity.getDataSource().getPath();
    flattener = new DocumentFlattener(path);
  }

  @Override
  public boolean handle(SearchHit hit) throws NbaException {
    DocumentType<?> dt = entity.getDataSource().getDocumentType();
    try {
      Object document = ESUtil.toDocumentObject(hit, dt);
      List<EntityObject> eos = flattener.flatten(document);
      LOOP: for (EntityObject eo : eos) {
        for (IEntityFilter filter : entity.getFilters()) {
          if (!filter.accept(eo)) {
            filtered += 1;
            continue LOOP;
          }
        }
        zip.setActiveEntry(fileName);
        printer.printRecord(eo);
        written += 1;
      }
      if (++processed % 10000 == 0) {
        printer.flush();
      }
    } 
    catch (IOException e) {
      throw new DaoException(e);
    }
    if (processed % 10000 == 0) logger.info("Documents processed: " + processed);
    return true;
  }

  void printHeaders() throws IOException {
    zip.setActiveEntry(fileName);
    printer.printBOM();
    printer.printHeader();
  }

  private CsvRecordWriter getPrinter(OutputStream out) {
    IField[] fields = entity.getFields();
    return new CsvRecordWriter(fields, out);
  }

  void logStatistics()
  {
    logger.info("Documents processed: {}", processed);
    logger.info("Records written for entity {}  : {}", entity.getName(), written);
    logger.info("Records rejected for entity {} : {}", entity.getName(), filtered);
  }

}
