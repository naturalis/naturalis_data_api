package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for Brahms multimedia objects.
 */
public class BrahmsMultiMediaJsonNDWriter extends JsonNDWriter<MultiMediaObject> {

  public BrahmsMultiMediaJsonNDWriter(ETLStatistics stats) {
    super(MULTI_MEDIA_OBJECT, "Brahms", stats);
  }

}
