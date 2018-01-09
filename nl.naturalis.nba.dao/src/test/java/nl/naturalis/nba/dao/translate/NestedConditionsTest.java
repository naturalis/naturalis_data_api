package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoTestUtil;
import nl.naturalis.nba.dao.DocumentType;
import static nl.naturalis.nba.dao.DaoTestUtil.*;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.*;

@SuppressWarnings("static-method")
public class NestedConditionsTest {

  @Test
  public void test_01() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(new Path("sourceSystem.code"), EQUALS, "CRS");

    QueryCondition condition02 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");

    QueryCondition condition03 = new QueryCondition(new Path("sex"), EQUALS, "male");

    QueryCondition condition04 = new QueryCondition(new Path("gatheringEvent.gatheringPersons.fullName"), EQUALS, "Mavromonstakis, G.A.");
    
    QueryCondition condition05 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition06 = new QueryCondition(new Path("collectionType"), EQUALS, "Aves");

    QueryCondition condition07 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");

    condition01.and(condition02).and(condition03).and(condition04).and(condition05)
        .and(condition06).and(condition07);

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

    QueryCondition condition01 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");
    QueryCondition condition02 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");
    
    QueryCondition condition03 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");
    
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

    QueryCondition condition01 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Larus");
    QueryCondition condition02 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");
    condition01.or(condition02);
    QueryCondition condition03 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");
    condition01.and(condition03);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);

    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    System.out.println(JsonUtil.toPrettyJson(query));
    System.out.println(translator.translate());
  }

  @Test
  public void test_04() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition02 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "domesticus");

    QueryCondition condition03 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition04 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");

    condition01.and(condition02);
    condition03.and(condition04);
    condition01.or(condition03);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);

    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    System.out.println(JsonUtil.toPrettyJson(query));
    System.out.println(translator.translate());

  }

  @Test
  public void test_05() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 = new QueryCondition(
        new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Passer");

    QueryCondition condition02 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition03 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "domesticus");

    QueryCondition condition05 = new QueryCondition(
        new Path("identifications.scientificName.specificEpithet"), EQUALS, "domesticus");

    QueryCondition condition06 = new QueryCondition(
        new Path("identifications.scientificName.infraspecificEpithet"), EQUALS, "biblicus");

    condition02.and(condition03);
    condition05.and(condition06);
    condition02.or(condition05);

    condition01.and(condition02);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);

    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    System.out.println(JsonUtil.toPrettyJson(query));
    System.out.println(translator.translate());

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
    System.out.println(JsonUtil.toPrettyJson(query));
    System.out.println(translator.translate());

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
    System.out.println(JsonUtil.toPrettyJson(query));
    System.out.println(translator.translate());

  }

}
