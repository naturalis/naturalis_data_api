package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.SortOrder.ASC;
import static nl.naturalis.nba.api.SortOrder.DESC;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.AggregationSpecimensMock;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_Aggregations {

  private static final Logger logger =
      DaoRegistry.getInstance().getLogger(SpecimenDaoTest_Aggregations.class);

  static Specimen specimen01;
  static Specimen specimen02;
  static Specimen specimen03;
  static Specimen specimen04;
  static Specimen specimen05;
  static Specimen specimen06;
  static Specimen specimen07;
  static Specimen specimen08;
  static Specimen specimen09;
  static Specimen specimen10;
  static Specimen specimen11;
  static Specimen specimen12;

  @BeforeClass
  public static void before() {
    logger.info("Starting tests");
    deleteIndex(DocumentType.SPECIMEN);
    createIndex(DocumentType.SPECIMEN);
    /*
     * Insert 12 test specimens.
     */
    specimen01 = AggregationSpecimensMock.specimen01();
    specimen02 = AggregationSpecimensMock.specimen02();
    specimen03 = AggregationSpecimensMock.specimen03();
    specimen04 = AggregationSpecimensMock.specimen04();
    specimen05 = AggregationSpecimensMock.specimen05();
    specimen06 = AggregationSpecimensMock.specimen06();
    specimen07 = AggregationSpecimensMock.specimen07();
    specimen08 = AggregationSpecimensMock.specimen08();
    specimen09 = AggregationSpecimensMock.specimen09();
    specimen10 = AggregationSpecimensMock.specimen10();
    specimen11 = AggregationSpecimensMock.specimen11();
    specimen12 = AggregationSpecimensMock.specimen12();
    DaoTestUtil.saveSpecimens(specimen01, specimen02, specimen03, specimen04, specimen05,
        specimen06, specimen07, specimen08, specimen09, specimen10, specimen11, specimen12);
  }

  @AfterClass
  public static void after() {
    logger.info("Test finished. Removing test specimens");
    deleteIndex(DocumentType.SPECIMEN);
  }

  /*
   * Test count
   */
  @Test
  public void testCount() throws InvalidQueryException {
    // 1. Count without QuerySpec
    SpecimenDao dao = new SpecimenDao();
    assertEquals("01", 12L, dao.count(null));

    // 2. Count with QuerySpec
    QuerySpec querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition("sourceSystem.code", EQUALS, "BRAHMS");
    querySpec.addCondition(condition);
    assertEquals("02", 6L, dao.count(querySpec));
  }

  /*
   * Test countDistinctValues
   */
  @Test
  public void testCountDistinctValues() throws InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    QuerySpec querySpec;
    String field;

    // 1. QuerySpec == null && simple field
    querySpec = null;
    field = "collectionType";
    assertEquals("01", 4L, dao.countDistinctValues(field, querySpec));

    // 2. QuerySpec == null && nested field
    querySpec = null;
    field = "identifications.defaultClassification.className";
    assertEquals("02", 5L, dao.countDistinctValues(field, querySpec));

    // 3. QuerySpec =! null && simple field
    querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition("sourceSystem.code", EQUALS, "CRS");
    querySpec.addCondition(condition);
    field = "collectionType";
    assertEquals("03", 3L, dao.countDistinctValues(field, querySpec));

    // 4. QuerySpec =! null && nested field
    querySpec = new QuerySpec();
    condition = new QueryCondition("sourceSystem.code", EQUALS, "CRS");
    querySpec.addCondition(condition);
    field = "identifications.defaultClassification.className";
    assertEquals("04", 4L, dao.countDistinctValues(field, querySpec));
  }

  /*
   * Test countDistinctValuesPerGroup
   */
  @Test
  public void testCountDistinctValuesPerGroup()
      throws JsonParseException, JsonMappingException, IOException, InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    QuerySpec querySpec;
    String field;
    String group;
    List<Map<String, Object>> actual;
    String jsonFile;

    // 1. QuerySpec == null && simple field && simple group
    querySpec = null;
    field = "collectionType";
    group = "sourceSystem.code";
    actual = dao.countDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__01.json";
    assertTrue("01", jsonListEquals(this.getClass(), actual.toString(), jsonFile));

    // 2. QuerySpec == null && nested field && simple group
    querySpec = null;
    field = "identifications.defaultClassification.className";
    group = "sourceSystem.code";
    actual = dao.countDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__02.json";
    assertTrue("02", jsonListEquals(this.getClass(), actual.toString(), jsonFile));

    // 3. QuerySpec == null && simple field && nested group
    querySpec = null;
    querySpec = new QuerySpec();
    List<SortField> sortFields = new ArrayList<>();
    sortFields.add(new SortField("identifications.defaultClassification.className"));
    querySpec.setSortFields(sortFields);
    field = "sourceSystem.code";
    group = "identifications.defaultClassification.className";
    actual = dao.countDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__03.json";
    assertTrue("03", jsonListEquals(this.getClass(), actual.toString(), jsonFile));

    // 4. QuerySpec == null && nested field && nested group
    querySpec = null;
    field = "identifications.scientificName.genusOrMonomial";
    group = "identifications.defaultClassification.className";
    actual = dao.countDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__04.json";
    assertTrue("04", jsonListEquals(this.getClass(), actual.toString(), jsonFile));

    // 5. QuerySpec != null
    querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition("sourceSystem.code", EQUALS, "CRS");
    querySpec.addCondition(condition);
    field = "sourceSystem.code";
    group = "identifications.defaultClassification.className";
    actual = dao.countDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__05.json";
    assertTrue("05", jsonListEquals(this.getClass(), actual.toString(), jsonFile));

    // 6. QuerySpec != null && sort = desc
    SortField sortField = new SortField(group, ASC);
    List<SortField> sortFlds = new ArrayList<>();
    sortFlds.add(sortField);
    querySpec.setSortFields(sortFlds);
    actual = dao.countDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__06.json";
    assertTrue("06", jsonListEquals(this.getClass(), actual.toString(), jsonFile));
  }

  /*
   * Test getDistinctValues with simple field and without QuerySpec
   */
  @Test
  public void testGetDistinctValues_01() throws InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    Map<String, Long> result = dao.getDistinctValues("recordBasis", null);

    assertEquals("01", 5, result.size());
    Iterator<Map.Entry<String, Long>> entries = result.entrySet().iterator();
    Map.Entry<String, Long> entry = entries.next();
    assertEquals("02", "PreservedSpecimen", entry.getKey());
    assertEquals("03", new Long(5), entry.getValue());
    entry = entries.next();
    assertEquals("04", "OtherSpecimen", entry.getKey());
    assertEquals("05", new Long(2), entry.getValue());
    entry = entries.next();
    assertEquals("06", "Wood sample", entry.getKey());
    assertEquals("07", new Long(2), entry.getValue());
    entry = entries.next();
    assertEquals("08", "Herbarium sheet", entry.getKey());
    assertEquals("09", new Long(2), entry.getValue());
    entry = entries.next();
    assertEquals("10", "FossilSpecimen", entry.getKey());
    assertEquals("11", new Long(1), entry.getValue());
  }

  /*
   * Test getDistinctValues with nested field and without QuerySpec
   */
  @Test
  public void testGetDistinctValues_02() throws InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    String field = "identifications.defaultClassification.family";
    Map<String, Long> result = dao.getDistinctValues(field, null);
    assertEquals("01", 5, result.size());
    Iterator<Map.Entry<String, Long>> entries = result.entrySet().iterator();
    Map.Entry<String, Long> entry = entries.next();
    assertEquals("02", "Compositae", entry.getKey());
    assertEquals("03", new Long(6), entry.getValue());
    entry = entries.next();
    assertEquals("04", "Apidae", entry.getKey());
    assertEquals("05", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("05", "Ussuritidae", entry.getKey());
    assertEquals("06", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("07", "Mytilidae", entry.getKey());
    assertEquals("08", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("09", "Naticidae", entry.getKey());
    assertEquals("10", new Long(1), entry.getValue());
  }

  /*
   * Test getDistinctValues with nested field and a QuerySpec
   */
  @Test
  public void testGetDistinctValues_03() throws InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    String field = "identifications.defaultClassification.family";
    QuerySpec querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition(field, NOT_EQUALS, null);
    querySpec.addCondition(condition);
    Map<String, Long> result = dao.getDistinctValues(field, querySpec);

    assertEquals("01", 5, result.size());
    Iterator<Map.Entry<String, Long>> entries = result.entrySet().iterator();
    Map.Entry<String, Long> entry = entries.next();
    assertEquals("02", "Compositae", entry.getKey());
    assertEquals("03", new Long(6), entry.getValue());
    entry = entries.next();
    assertEquals("04", "Apidae", entry.getKey());
    assertEquals("05", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("05", "Ussuritidae", entry.getKey());
    assertEquals("06", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("07", "Mytilidae", entry.getKey());
    assertEquals("08", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("09", "Naticidae", entry.getKey());
    assertEquals("10", new Long(1), entry.getValue());
  }

  /*
   * Test getDistinctValues with nested field, a QuerySpec and a Sort field
   */
  @Test
  public void testGetDistinctValues_04() throws InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    String field = "identifications.defaultClassification.family";
    QuerySpec querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition(field, NOT_EQUALS, null);
    querySpec.addCondition(condition);
    SortField sortField = new SortField(field, DESC);
    List<SortField> sortFields = new ArrayList<>();
    sortFields.add(sortField);
    querySpec.setSortFields(sortFields);
    Map<String, Long> result = dao.getDistinctValues(field, querySpec);

    assertEquals("01", 5, result.size());
    Iterator<Map.Entry<String, Long>> entries = result.entrySet().iterator();
    Map.Entry<String, Long> entry = entries.next();
    assertEquals("02", "Ussuritidae", entry.getKey());
    assertEquals("03", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("04", "Naticidae", entry.getKey());
    assertEquals("05", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("06", "Mytilidae", entry.getKey());
    assertEquals("07", new Long(1), entry.getValue());
    entry = entries.next();
    assertEquals("08", "Compositae", entry.getKey());
    assertEquals("09", new Long(6), entry.getValue());
    entry = entries.next();
    assertEquals("10", "Apidae", entry.getKey());
    assertEquals("11", new Long(1), entry.getValue());
  }

  /*
   * Test getDistinctValuesPerGroup
   */
  @Test
  public void testGetDistinctValuesPerGroup()
      throws JsonParseException, JsonMappingException, IOException, InvalidQueryException {
    SpecimenDao dao = new SpecimenDao();
    String field;
    String group;
    QuerySpec querySpec;
    List<Map<String, Object>> result;
    String jsonFile;

    // 1. Simple field and simple group
    querySpec = null;
    field = "collectionType";
    group = "sourceSystem.code";
    result = dao.getDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__07.json";
    assertTrue("01", jsonListEquals(this.getClass(), result.toString(), jsonFile));

    // 2. Nested field and simple group
    querySpec = null;
    field = "identifications.defaultClassification.className";
    group = "sourceSystem.code";
    result = dao.getDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__08.json";
    assertTrue("02", jsonListEquals(this.getClass(), result.toString(), jsonFile));

    // 3. Simple field and nested group
    querySpec = null;
    field = "sourceSystem.code";
    group = "identifications.defaultClassification.className";
    result = dao.getDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__09.json";
    assertTrue("01", jsonListEquals(this.getClass(), result.toString(), jsonFile));

    // 4. Nested field and nested group
    querySpec = null;
    field = "identifications.scientificName.genusOrMonomial";
    group = "identifications.defaultClassification.className";
    result = dao.getDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__10.json";
    assertTrue("01", jsonListEquals(this.getClass(), result.toString(), jsonFile));

    // 5. QuerySpec != null && sorting
    field = "collectionType";
    group = "sourceSystem.code";
    SortField sortField1 = new SortField(field, ASC);
    SortField sortField2 = new SortField(group, DESC);
    List<SortField> sortFields = new ArrayList<>();
    querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition(field, NOT_EQUALS, null);
    querySpec.addCondition(condition);
    sortFields.add(sortField1);
    sortFields.add(sortField2);
    querySpec.setSortFields(sortFields);
    result = dao.getDistinctValuesPerGroup(group, field, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__11.json";
    assertTrue("05", jsonListEquals(this.getClass(), result.toString(), jsonFile));
  }

  public static boolean jsonListEquals(Class<?> unitTestClass, String actual, String jsonFile)
      throws JsonParseException, JsonMappingException, IOException {
    InputStream input = unitTestClass.getResourceAsStream(jsonFile);
    ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String, Object>> expected = objectMapper.readValue(input, new TypeReference<List<Map<String, Object>>>() {});
    return actual.equals(expected.toString());
  }

}
