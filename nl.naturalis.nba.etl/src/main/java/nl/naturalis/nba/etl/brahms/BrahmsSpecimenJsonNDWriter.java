package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for Brahms specimens.
 */
class BrahmsSpecimenJsonNDWriter extends JsonNDWriter<Specimen> {

  public BrahmsSpecimenJsonNDWriter(String sourceFile, ETLStatistics stats)
  {
    super(SPECIMEN, "Brahms", sourceFile, stats);
  }

}
