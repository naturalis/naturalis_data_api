package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for NSR multimedia documents.
 */
public class NsrMultiMediaJsonNDWriter extends JsonNDWriter<MultiMediaObject> {

  public NsrMultiMediaJsonNDWriter(String sourceFile, ETLStatistics stats) {
    super(MULTI_MEDIA_OBJECT, "Nsr", sourceFile, stats);
  }

}
