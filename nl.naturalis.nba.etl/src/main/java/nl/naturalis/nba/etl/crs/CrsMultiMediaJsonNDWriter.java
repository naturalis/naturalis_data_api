package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for CRS multimedia objects.
 */
public class CrsMultiMediaJsonNDWriter extends JsonNDWriter<MultiMediaObject> {

  public CrsMultiMediaJsonNDWriter(ETLStatistics stats) {
    super(MULTI_MEDIA_OBJECT, "CRS", stats);
  }

}
