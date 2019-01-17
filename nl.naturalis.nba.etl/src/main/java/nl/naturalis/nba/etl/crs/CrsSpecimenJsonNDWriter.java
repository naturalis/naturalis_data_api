package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for CRS specimen objects.
 */
public class CrsSpecimenJsonNDWriter extends JsonNDWriter<Specimen> {
  
  public CrsSpecimenJsonNDWriter(ETLStatistics stats) 
  {
    super(SPECIMEN, "CRS", stats);
  }

}
