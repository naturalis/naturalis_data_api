package nl.naturalis.nba.etl.normalize;

import nl.naturalis.nba.api.model.AreaClass;

/**
 * Normalizes different area class names to the normalized version. 
 * E.g. "island group" to "islandGroup"
 *
 */
public class AreaClassNormalizer extends ClasspathMappingFileNormalizer<AreaClass> {
  
  private static final String RESOURCE = "/normalize/area-class.csv";
  
  private static AreaClassNormalizer instance;
  
  public static AreaClassNormalizer getInstance()
  {
    if (instance == null) {
      instance = new AreaClassNormalizer();
    }
    return instance;
  }
  
  private AreaClassNormalizer()
  {
    super(AreaClass.class, RESOURCE);
  }

}
