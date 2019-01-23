package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.JsonNDWriter;

/**
 * The JsonNDWriter component in the ETL cycle for NSR taxon documents.
 */
public class NsrTaxonJsonNDWriter extends JsonNDWriter<Taxon> {

  public NsrTaxonJsonNDWriter(String sourceFile, ETLStatistics stats) {
    super(TAXON, "Nsr", sourceFile, stats);    
  }

}
