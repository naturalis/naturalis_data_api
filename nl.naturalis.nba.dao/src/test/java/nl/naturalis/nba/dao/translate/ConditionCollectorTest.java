package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.LogicalOperator.AND;
import static nl.naturalis.nba.api.LogicalOperator.OR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.junit.Test;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.DocumentType;

public class ConditionCollectorTest {

  @Test
  public void test_01() {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
    QueryCondition condition = new QueryCondition("unitID", NOT_EQUALS, null);
    QueryCondition condition01 = new QueryCondition("gatheringEvent.gatheringPersons.fullName", NOT_EQUALS, null);
    QueryCondition condition02 = new QueryCondition("identifications.scientificName.genusOrMonomial", EQUALS, "Passer");
    QueryCondition condition03 = new QueryCondition("license", EQUALS, "CC0");
    QueryCondition condition04 = new QueryCondition("identifications.scientificName.specificEpithet", EQUALS, "domesticus");
    QueryCondition condition04A = new QueryCondition("gatheringEvent.country", EQUALS, "Spain");
    QueryCondition condition05 = new QueryCondition("associatedMultiMediaUris.variant", EQUALS, "MEDIUM_QUALITY");
    QueryCondition condition06 = new QueryCondition("gatheringEvent.country", EQUALS, "Portugal");
    QueryCondition condition07 = new QueryCondition("associatedMultiMediaUris.format", EQUALS, "image/jpeg");
    
    condition.or(condition01).or(condition02).or(condition03).or(condition04.or(condition04A)).or(condition05).or(condition06).or(condition07);
    LogicalOperator operator = OR;
    
    ConditionCollector collector = new ConditionCollector(condition, mappingInfo);
    LinkedHashMap<String, ArrayList<QueryCondition>> conditionsMap = collector.createConditionsMap(operator);
    
    assertTrue("01", conditionsMap.containsKey(null));
    assertTrue("02", conditionsMap.containsKey("gatheringEvent.gatheringPersons"));
    assertTrue("03", conditionsMap.containsKey("identifications"));
    assertTrue("04", conditionsMap.containsKey("associatedMultiMediaUris"));
    assertTrue("05", conditionsMap.get("gatheringEvent.gatheringPersons").contains(condition01));
    assertTrue("06", conditionsMap.get("identifications").contains(condition02));
    assertTrue("07", conditionsMap.get(null).contains(condition03));
    assertFalse("08", conditionsMap.get(null).contains(condition04A));
    assertTrue("09", conditionsMap.get("associatedMultiMediaUris").contains(condition07));
    assertTrue("10", conditionsMap.get("associatedMultiMediaUris").size() == 2);    
  }
  
  @Test
  public void test_02() {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
    QueryCondition condition = new QueryCondition("unitID", NOT_EQUALS, null);
    QueryCondition condition01 = new QueryCondition("license", EQUALS, "CC0");
    QueryCondition condition02 = new QueryCondition("associatedMultiMediaUris.variant", EQUALS, "MEDIUM_QUALITY");
    QueryCondition condition03 = new QueryCondition("gatheringEvent.gatheringPersons.fullName", NOT_EQUALS, null);
    QueryCondition condition04 = new QueryCondition("identifications.scientificName.specificEpithet", EQUALS, "Badger");

    condition.and(condition01).and(condition02).and(condition03).and(condition04);
    LogicalOperator operator = AND;
    
    ConditionCollector collector = new ConditionCollector(condition, mappingInfo);
    LinkedHashMap<String, ArrayList<QueryCondition>> conditionsMap = collector.createConditionsMap(operator);
    assertTrue("01", conditionsMap.containsKey(null));
    assertTrue("02", conditionsMap.containsKey("associatedMultiMediaUris"));
    assertTrue("03", conditionsMap.containsKey("gatheringEvent.gatheringPersons"));
    assertTrue("04", conditionsMap.containsKey("identifications"));
    assertTrue("05", conditionsMap.get(null).size() == 2);
    assertTrue("05", conditionsMap.get("associatedMultiMediaUris").size() == 1);
    assertTrue("05", conditionsMap.get("gatheringEvent.gatheringPersons").size() == 1);
    assertTrue("05", conditionsMap.get("identifications").size() == 1);
  }

}
