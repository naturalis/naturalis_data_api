package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for Geo Areas.
 */
public class GeoJsonNDWriter extends JsonNDWriter<GeoArea>{

  public GeoJsonNDWriter(String sourceFile, ETLStatistics stats) 
  {
    super(GEO_AREA, "Geo", sourceFile, stats);
  }

}
