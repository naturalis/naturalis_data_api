package nl.naturalis.nba.etl.normalize;

import nl.naturalis.nba.api.model.TaxonRelationType;

/**
 * Normalizes different names for the various taxon relation types.
 *
 */
public class TaxonRelationTypeNormalizer extends ClasspathMappingFileNormalizer<TaxonRelationType> {

  private static final String RESOURCE = "/normalize/taxon-relation-type.csv";
  
  private static TaxonRelationTypeNormalizer instance;
  
  public static TaxonRelationTypeNormalizer getInstance() 
  {
    if (instance == null) {
      instance = new TaxonRelationTypeNormalizer();
    }
    return instance;
  }
  
  private TaxonRelationTypeNormalizer()
  {
    super(TaxonRelationType.class, RESOURCE);
  }

}
