package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
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
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.AggregationSpecimensMock;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_Aggregations {

  private static final Logger logger = DaoRegistry.getInstance().getLogger(SpecimenDaoTest_Aggregations.class);

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

  
  @BeforeClass
  public static void before() {
    logger.info("Starting tests");
    deleteIndex(DocumentType.SPECIMEN);
    createIndex(DocumentType.SPECIMEN);
    /*
     * Insert 10 test specimens.
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
    DaoTestUtil.saveSpecimens(specimen01, specimen02, specimen03, specimen04, specimen05, specimen06, specimen07, specimen08, specimen09, specimen10);
  }
  
  @AfterClass
  public static void after() {
    logger.info("Ending tests");
    deleteIndex(DocumentType.SPECIMEN);
  }
  
  /*
   * Test count with and without QuerySpec
   */
  @Test
  public void testCount() throws InvalidQueryException
  {
    SpecimenDao dao = new SpecimenDao();
    assertEquals("01", 10L, dao.count(null));
    
    QuerySpec querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition("sourceSystem.code", EQUALS, "BRAHMS");
    querySpec.addCondition(condition);
    assertEquals("02", 5L, dao.count(querySpec));
  }
  
  /*
   * Test countDistinctValues
   */
  @Test
  public void testCountDistinctValues() throws InvalidQueryException
  {
    SpecimenDao dao = new SpecimenDao();
    QuerySpec querySpec;
    String field;
    
    // 1. QuerySpec == null && nestedPath == null
    querySpec = null;
    field = "collectionType";
    assertEquals("01", 4L, dao.countDistinctValues(field, querySpec));
    
    // 2. QuerySpec == null && nestedPath != null
    querySpec = null;
    field = "identifications.defaultClassification.className";
    assertEquals("02", 4L, dao.countDistinctValues(field, querySpec));

    // 3. QuerySpec =! null && nestedPath == null
    querySpec = new QuerySpec();
    QueryCondition condition = new QueryCondition("sourceSystem.code", EQUALS, "CRS");
    querySpec.addCondition(condition);
    field = "collectionType";
    assertEquals("03", 3L, dao.countDistinctValues(field, querySpec));
    
    // 4. QuerySpec =! null && nestedPath != null
    querySpec = new QuerySpec();
    condition = new QueryCondition("sourceSystem.code", EQUALS, "CRS");
    querySpec.addCondition(condition);
    field = "identifications.defaultClassification.className";
    assertEquals("04", 3L, dao.countDistinctValues(field, querySpec));
  }
  
  /*
   * Test countDistinctValuesPerGroup
   */
  @Test
  public void testCountDistinctValuesPerGroup() throws JsonParseException, JsonMappingException, IOException, InvalidQueryException
  {
    SpecimenDao dao = new SpecimenDao();
    QuerySpec querySpec;
    String field;
    String group;
    List<Map<String, Object>> actual;
    String jsonFile;

    // 1. QuerySpec == null && nestedPath == null && nestedGroup == null    
    querySpec = null;
    field = "collectionType";
    group = "sourceSystem.code";
    actual = dao.countDistinctValuesPerGroup(field, group, querySpec);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__01.json";
    assertTrue("01", jsonEquals(this.getClass(), actual.toString(), jsonFile));
    
    // 2. QuerySpec == null && nestedPath =! null && nestedGroup == null    
    querySpec = null;
    field = "identifications.defaultClassification.className";
    group = "sourceSystem.code";
    actual = dao.countDistinctValuesPerGroup(field, group, querySpec);
    System.out.println(actual);
    jsonFile = "CountDistinctValuesPerGroupTest__testResult__02.json";
    assertTrue("02", jsonEquals(this.getClass(), actual.toString(), jsonFile));
  }
  
  public static boolean jsonEquals(Class<?> unitTestClass, String actual, String jsonFile) throws JsonParseException, JsonMappingException, IOException
  {
    InputStream input = unitTestClass.getResourceAsStream(jsonFile);
    ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String, Object>> expected = objectMapper.readValue(input, new TypeReference<List<Map<String, Object>>>(){});
    return actual.equals(expected.toString());
  }
  
  /*
   * Test getDistinctValues with simple field an no QuerySpec
   */
//  @Test
//  public void testGetDistinctValues_01() throws InvalidQueryException
//  {
//    SpecimenDao dao = new SpecimenDao();
//    
//    Map<String, Long> result = dao.getDistinctValues("recordBasis", null);
//    assertEquals("01", 3, result.size());
//    Iterator<Map.Entry<String, Long>> entries = result.entrySet().iterator();
//    Map.Entry<String, Long> entry = entries.next();
//    assertEquals("02", "Preserved specimen", entry.getKey());
//    assertEquals("03", new Long(2), entry.getValue());
//    entry = entries.next();
//    assertEquals("04", "Herbarium sheet", entry.getKey());
//    assertEquals("05", new Long(1), entry.getValue());
//    entry = entries.next();
//    assertEquals("06", "FossileSpecimen", entry.getKey());
//    assertEquals("07", new Long(1), entry.getValue());
//  }

  /*
   * Test getDistinctValues with nested field an no QuerySpec
   */
//  @Test
//  public void testGetDistinctValues_02() throws InvalidQueryException
//  {
//    SpecimenDao dao = new SpecimenDao();
//    String field = "identifications.defaultClassification.genus";
//    Map<String, Long> result = dao.getDistinctValues(field, null);
//    // Genus Larus occurs twice
//    assertEquals("01", 4, result.size());
//  }

//  @Test
//  public void testGetDistinctValuesPerGroup() throws InvalidQueryException
//  {
//    SpecimenDao dao = new SpecimenDao();
//    String field = "collectionType";
//    String group = "sourceSystem.code";
//    List<Map<String, Object>> result = dao.getDistinctValuesPerGroup(field, group, null);
//    logger.info(result);
//    // assertEquals("01", 4, result.size());
//  }

  

}
