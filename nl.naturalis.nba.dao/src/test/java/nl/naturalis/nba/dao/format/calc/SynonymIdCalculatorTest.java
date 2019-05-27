package nl.naturalis.nba.dao.format.calc;

import static org.junit.Assert.assertEquals;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.EntityObject;

public class SynonymIdCalculatorTest {

  @Before
  public void init() throws URISyntaxException
  {
  }

  @Test
  public void testCalculateValue() throws CalculationException
  {
    String nsr_id = "11E0A704FE3";
    String nsr_parent_id = "E0CCED34D8E";

    Taxon taxon = new Taxon();
    SourceSystem sourceSystem = SourceSystem.NSR;
    taxon.setSourceSystem(sourceSystem);
    taxon.setSourceSystemId(nsr_id);
    taxon.setId(nsr_id + "@" + SourceSystem.NSR.getCode());
    taxon.setSourceSystemParentId(nsr_parent_id);
    taxon.setTaxonRank("species");

    List<ScientificName> synonyms = new ArrayList<>();

    ScientificName synonym01 = new ScientificName();
    synonym01.setFullScientificName("Sagra purpurea (Horn)");
    synonym01.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
    synonyms.add(synonym01);

    ScientificName synonym02 = new ScientificName();
    synonym02.setFullScientificName("Sagra purpurea foo");
    // No taxonomic status. Shouldn't happen, but could emerge
    synonyms.add(synonym02);
    
    taxon.setSynonyms(synonyms);

    DocumentFlattener df = new DocumentFlattener(new Path("synonyms"), 1);
    List<EntityObject> entities = df.flatten(taxon);
    SynonymIdCalculator calculator = new SynonymIdCalculator();
    
    EntityObject entity = entities.get(0);
    //long expected = 0xFFFFFE18DB5D1287L;
    long expected = 0xFFFFFE18DB5D1292L;
    assertEquals("01", Long.toHexString(expected).toUpperCase(), calculator.calculateValue(entity));
    
    entity = entities.get(1);
    expected = 0xFFFFFFF0F17D1C0BL;
    assertEquals("02", Long.toHexString(expected).toUpperCase(), calculator.calculateValue(entity));    
    
  }

}
