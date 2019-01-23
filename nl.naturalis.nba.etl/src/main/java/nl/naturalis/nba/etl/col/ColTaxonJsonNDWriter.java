package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for Catalogue of Life
 * taxon documents.
 */
public class ColTaxonJsonNDWriter extends JsonNDWriter<Taxon> {

  public ColTaxonJsonNDWriter(String sourceFile, ETLStatistics stats) {
    super(TAXON, "Col", sourceFile, stats);
  }

}
