package nl.naturalis.nba.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

/**
 * @author Hannes Hettling
 * @author Tom Gilissen
 *
 */
public class GeoAreaClientTest {

  private String baseUrl = "http://localhost:8080/v2";
  private GeoAreaClient client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    ClientConfig config = new ClientConfig();
    config.setBaseUrl(baseUrl);
    config.setPreferGET(true);
    NbaSession session = new NbaSession(config);
    client = session.getGeoAreaClient();
  }

  @After
  public void tearDown() throws Exception {}

  /*
   * Test with non-existent locality.
   */
  @Test
  public void test_getGeoJsonForLocality01() {
    assertNull(client.getGeoJsonForLocality("FOO"));
  }

  /*
   * Test with non-existent locality.
   */
  @Test
  public void test_getGeoJsonForLocality02() {
    assertNotNull(client.getGeoJsonForLocality("Amsterdam"));
  }

  /*
   * Tests of the aggregation services
   */
  @Test
  public void test_count() throws InvalidQueryException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("areaType", "=", "Country"));
    String output = JsonUtil.toPrettyJson(client.count(qs));
    Long result = Long.parseLong(output);
    Assert.assertTrue(result >= 0);
  }
  
  @Test
  public void test_countDistinctValues() throws InvalidQueryException {
    String field = "locality";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("areaType", "=", "Country"));
    String output = JsonUtil.toPrettyJson(client.countDistinctValues(field, qs));
    Long result = Long.parseLong(output);
    Assert.assertTrue(result >= 0);
  }

  @Test
  public void test_countDistinctValuesPerGroup() throws InvalidQueryException {
    String field = "areaType";
    String group = "locality";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("areaType", "!=", "Nature"));
    System.out.println(JsonUtil.toPrettyJson(client.countDistinctValuesPerGroup(field, group, qs)));
  }
  
  @Test
  public void test_getDistinctValues() throws InvalidQueryException {
    String field = "locality";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("areaType", "=", "Country"));
    System.out.println(JsonUtil.toPrettyJson(client.getDistinctValues(field, qs)));
  }

  @Test
  public void test_getDistinctValuesPerGroup() throws InvalidQueryException {
    String field = "areaType";
    String group = "locality";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("areaType", "!=", "Nature"));
    System.out.println(JsonUtil.toPrettyJson(client.getDistinctValuesPerGroup(field, group, qs)));
  }

}
