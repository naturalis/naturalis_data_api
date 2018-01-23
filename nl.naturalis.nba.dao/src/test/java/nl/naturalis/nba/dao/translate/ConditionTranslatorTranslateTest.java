package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.DaoTestUtil.jsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.SpecimenDao;


/*
 * Unit test for testing the translation of mulitple query conditions, nested or not, in need of 
 * a nested path or not, into a correct Elasticsearch query.
 */
public class ConditionTranslatorTranslateTest {

  @Test
  public void test_01() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("sourceSystem.code"), EQUALS, "CRS");
    QueryCondition condition02 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");
    QueryCondition condition03 = new QueryCondition(new Path("sex"), EQUALS, "male");
    QueryCondition condition04 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.fullName"), EQUALS, "Mavromonstakis, G.A.");
    QueryCondition condition05 = new QueryCondition(new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");
    QueryCondition condition06 = new QueryCondition(new Path("collectionType"), EQUALS, "Aves");
    QueryCondition condition07 = new QueryCondition(new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");

    condition01.and(condition02).and(condition03).and(condition04).
                and(condition05).and(condition06).and(condition07);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_01.json";
    String jsonString = translator.translate().toString();
    assertTrue("01", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_02() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");
    QueryCondition condition02 = new QueryCondition(new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");
    QueryCondition condition03 = new QueryCondition(new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");
    QueryCondition condition04 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.fullName"), EQUALS, "Mavromonstakis, G.A.");

    condition01.and(condition02.and(condition03.and(condition04)));

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_02.json";
    String jsonString = translator.translate().toString();
    assertTrue("02", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_03() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");
    QueryCondition condition02 = new QueryCondition(new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");
    QueryCondition condition03 = new QueryCondition(new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");
    
    QueryCondition condition04 = new QueryCondition(NOT, new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");
    QueryCondition condition05 = new QueryCondition(new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");
    QueryCondition condition06 = new QueryCondition(new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "domesticus");

    condition04.and(condition05).and(condition06);
    condition01.and(condition02).and(condition03).and(condition04);
    
    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_03.json";
    String jsonString = translator.translate().toString();
    assertTrue("03", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_04() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Conus");
    QueryCondition condition02 = new QueryCondition(new Path("unitID"), NOT_EQUALS, null);
    QueryCondition condition03 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.fullName"), EQUALS, "Hoenselaar, H.J.");
    QueryCondition condition04 = new QueryCondition(new Path("identifications.defaultClassification.phylum"), EQUALS, "Mollusca");
    QueryCondition condition05 = new QueryCondition(new Path("gatheringEvent.country"), EQUALS, "Spain");
    QueryCondition condition06 = new QueryCondition(new Path("gatheringEvent.country"), EQUALS, "Portugal");
    
    condition01.and(condition02.and(condition03.and(condition04.and(condition05.or(condition06)))));

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_04.json";
    String jsonString = translator.translate().toString();
    assertTrue("04", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_05() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), NOT_EQUALS, null);
    
    QueryCondition condition02 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.fullName"), NOT_EQUALS, null);
    
    QueryCondition condition03 = new QueryCondition(new Path("collectionType"), EQUALS, "Botany");
    QueryCondition condition04 = new QueryCondition(new Path("identifications.scientificName.fullScientificName"), NOT_EQUALS, "test");
    QueryCondition condition05 = new QueryCondition(new Path("identifications.defaultClassification.phylum"), NOT_EQUALS, "Aves");
    QueryCondition condition06 = new QueryCondition(new Path("gatheringEvent.country"), NOT_EQUALS, "Spain");

    QueryCondition condition07 = new QueryCondition(new Path("associatedMultiMediaUris.format"), EQUALS, "image/jpeg");

    QueryCondition condition08 = new QueryCondition(new Path("sourceInstitutionID"), EQUALS, "Naturalis Biodiversity Center");

    condition03.and(condition04).and(condition05).and(condition06);
    condition02.and(condition03).and(condition07);
    condition01.and(condition02).and(condition08);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_05.json";
    String jsonString = translator.translate().toString();
    assertTrue("05", jsonEquals(this.getClass(), jsonString, jsonFile));
  }
  

  @Test
  public void test_06() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    // Conditions without nested path
    QueryCondition condition10 = new QueryCondition(new Path("license"), EQUALS, "CC0");
    QueryCondition condition11 = new QueryCondition(new Path("licenseType"), EQUALS, "Copyright");
    QueryCondition condition12 = new QueryCondition(new Path("owner"), EQUALS, "Naturalis Biodiversity Center");
    
    QueryCondition condition20 = new QueryCondition(new Path("collectionType"), EQUALS, "Botany");
    QueryCondition condition21 = new QueryCondition(new Path("collectionType"), EQUALS, "Mollusca");
    QueryCondition condition22 = new QueryCondition(new Path("collectionType"), EQUALS, "Lepidoptera");
    
    QueryCondition condition30 = new QueryCondition(new Path("recordBasis"), EQUALS, "Herbarium sheet");
    QueryCondition condition31 = new QueryCondition(new Path("recordBasis"), EQUALS, "PreservedSpecimen");
    QueryCondition condition32 = new QueryCondition(new Path("recordBasis"), EQUALS, "FossilSpecimen");

    // Conditions with nested path
    QueryCondition condition40 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), NOT_EQUALS, "test");
    QueryCondition condition41 = new QueryCondition(new Path("identifications.scientificName.specificEpithet"), NOT_EQUALS, "test");
    QueryCondition condition42 = new QueryCondition(new Path("identifications.scientificName.infraspecificEpithet"), NOT_EQUALS, "test");

    QueryCondition condition50 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.fullName"), NOT_EQUALS, "test");
    QueryCondition condition51 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.agentText"), EQUALS, null);

    QueryCondition condition60 = new QueryCondition(new Path("associatedMultiMediaUris.format"), EQUALS, "image/jpeg");
    QueryCondition condition61 = new QueryCondition(new Path("associatedMultiMediaUris.variant"), EQUALS, "MEDIUM_QUALITY");

    
    QueryCondition conditionA = condition20
                                .or(condition21
                                    .or(condition22));
    
    QueryCondition conditionB = condition50
                                .and(condition51);
    
    QueryCondition conditionC = condition30
                                .and(condition31
                                    .and(condition40))
                                .or(condition41
                                    .and(condition42))
                                .or(condition50
                                    .and(condition60));
    
    QueryCondition conditionD = condition12.and(condition60.and(condition32.and(condition61)));
    
    QueryCondition condition;
    condition =      condition10
                 .or(condition40)
                 .or(conditionA)
                 .or(conditionB)
                 .or(conditionC)
                 .or(condition60)
                 .or(condition11)
                 .or(condition41)
                 .or(condition21)
                 .or(conditionD)
                 .or(condition31)
                 .or(condition61);
    
    
    QuerySpec query = new QuerySpec();
    query.addCondition(condition);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_06.json";
    String jsonString = translator.translate().toString();
    assertTrue("06", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_07() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Alethe");
    QueryCondition condition02 = new QueryCondition(new Path("identifications.scientificName.specificEpithet"), EQUALS, "castanea");
    QueryCondition condition03 = new QueryCondition(new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, null);

    QuerySpec query = new QuerySpec();
    query.addCondition( condition01.and(condition02.and(condition03)) );
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    String jsonFile = "NestedConditionsTest__testQuery_07.json";
    String jsonString = translator.translate().toString();
    assertTrue("01", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_08() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition = new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Alethe");

    QuerySpec query = new QuerySpec();
    query.addCondition(condition);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);

    String jsonFile = "NestedConditionsTest__testQuery_08.json";
    String jsonString = translator.translate().toString();
    assertTrue("01", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_09() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition("gatheringEvent.siteCoordinates.latitudeDecimal", "=", null);
    QueryCondition condition02 = new QueryCondition("gatheringEvent.siteCoordinates.longitudeDecimal", "=", null);

    condition01.and(condition02);
    
    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    System.out.println(translator.translate());

//    String jsonFile = "NestedConditionsTest__testQuery_09.json";
//    String jsonString = translator.translate().toString();
//    assertTrue("09", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_10() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition("gatheringEvent.siteCoordinates.latitudeDecimal", ">", 0);
    QueryCondition condition02 = new QueryCondition("gatheringEvent.siteCoordinates.longitudeDecimal", ">", 100);

    condition01.and(condition02);
    
    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    System.out.println(translator.translate());

//    String jsonFile = "NestedConditionsTest__testQuery_09.json";
//    String jsonString = translator.translate().toString();
//    assertTrue("09", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_11() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    String f1 = "identifications.scientificName.genusOrMonomial";
    String f2 = "identifications.scientificName.specificEpithet";

    QueryCondition condition01 = new QueryCondition(f1, NOT_EQUALS, null);
    QueryCondition condition02 = new QueryCondition(f2, NOT_EQUALS, null);
    
    QuerySpec query = new QuerySpec();
    query.addCondition( condition01.and(condition02) );
    query.sortBy(f1);
    query.sortBy(f2);
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
    System.out.println(translator.translate());
    
//    String jsonFile = "NestedConditionsTest__testQuery_11.json";
//    String jsonString = translator.translate().toString();
//    assertTrue("01", jsonEquals(this.getClass(), jsonString, jsonFile));
  }

  @Test
  public void test_12() throws InvalidQueryException {
    
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    String f1 = "identifications.scientificName.fullScientificName";
    String f2 = "identifications.scientificName.fullScientificName";

    QueryCondition condition01 = new QueryCondition(f1, EQUALS, "Larus f. fuscus");
    QueryCondition condition02 = new QueryCondition(f2, EQUALS, "Malus sylvestris");
    
    QuerySpec query = new QuerySpec();
    query.addCondition( condition01.or(condition02) );
    query.sortBy(f1, SortOrder.DESC);
    query.sortBy("unitID");
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    
//    System.out.println(translator.translate());
    
    String jsonFile = "NestedConditionsTest__testQuery_12.json";
    String jsonString = translator.translate().toString();
    assertTrue("01", jsonEquals(this.getClass(), jsonString, jsonFile));
  }
    
  
}
