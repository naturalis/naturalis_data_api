package nl.naturalis.nba.client;

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
 * @author Tom Gilissen
 *
 */
public class MultiMediaObjectClientTest {

  private String baseUrl = "http://localhost:8080/v3";
  private MultiMediaObjectClient client;

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
    client = session.getMultiMediaObjectClient();
  }

  @After
  public void tearDown() throws Exception {}

  /*
   * Tests of the aggregation services
   */
  @Test
  public void test_count() throws InvalidQueryException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystem.code", "=", "BRAHMS"));
    String output = JsonUtil.toPrettyJson(client.count(qs));
    Long result = Long.parseLong(output);
    Assert.assertTrue(result >= 0);
  }
  
  @Test
  public void test_countDistinctValues() throws InvalidQueryException {
    String field = "collectionType";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystem.code", "=", "CRS"));
    String output = JsonUtil.toPrettyJson(client.countDistinctValues(field, qs));
    Long result = Long.parseLong(output);
    Assert.assertTrue(result >= 0);
  }

  @Test
  public void test_countDistinctValuesPerGroup() throws InvalidQueryException {
    String field = "sourceSystem.code";
    String group = "collectionType";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("unitID", "!=", null));
    System.out.println(JsonUtil.toPrettyJson(client.countDistinctValuesPerGroup(group, field, qs)));
  }
  
  @Test
  public void test_getDistinctValues() throws InvalidQueryException {
    String field = "sourceSystem.code";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("unitID", "!=", null));
    System.out.println(JsonUtil.toPrettyJson(client.getDistinctValues(field, qs)));
  }

  @Test
  public void test_getDistinctValuesPerGroup() throws InvalidQueryException {
    String field = "sourceSystem.code";
    String group = "collectionType";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystem.code", "=", "CRS"));
    System.out.println(JsonUtil.toPrettyJson(client.getDistinctValuesPerGroup(group, field, qs)));
  }

  

}
