package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.DaoTestUtil.jsonEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;

@SuppressWarnings("static-method")
public class NestedConditionsTest {

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

    QueryCondition condition01 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");

    QueryCondition condition02 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition03 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "domesticus");

    QueryCondition condition04 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");

    QueryCondition condition05 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");

    condition01.and(condition02).and(condition03);
    condition04.and(condition02).and(condition05);
    condition01.or(condition04);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);

    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
//    System.out.println(translator.translate());

  }

  @Test
  public void test_07() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");

    QueryCondition condition02a = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition02b = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition03 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "domesticus");

    QueryCondition condition04 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");

    QueryCondition condition05 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");

    condition01.and(condition02a.and(condition03));
    condition04.and(condition02b.and(condition05));
    condition01.or(condition04);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);

    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    //System.out.println(translator.translate());

  }

}
