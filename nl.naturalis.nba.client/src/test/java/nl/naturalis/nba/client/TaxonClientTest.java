/**
 * 
 */
package nl.naturalis.nba.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.debug.DevNullOutputStream;

/**
 * @author hettling
 *
 */
public class TaxonClientTest {

  private String baseUrl = "http://localhost:8080/v2";
  private TaxonClient client;

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
    client = session.getTaxonClient();
  }

  @After
  public void tearDown() throws Exception {}

  /*
   * Test with valid field & value.
   */
  @Test
  public void test_dwcaQuery01() throws InvalidQueryException {
    QuerySpec query = new QuerySpec();
    query.addCondition(new QueryCondition("acceptedName.genusOrMonomial", "=", "Parus"));
    client.dwcaQuery(query, new DevNullOutputStream());
  }

  /*
   * Test with non-existent field & value.
   */
  @Test(expected = InvalidQueryException.class)
  public void test_dwcaQuery02() throws InvalidQueryException {
    QuerySpec query = new QuerySpec();
    query.addCondition(new QueryCondition("FOO", "=", "BAR"));
    client.dwcaQuery(query, new DevNullOutputStream());
  }

  /*
   * Test with non-existent dataset.
   */
  @Test(expected = NoSuchDataSetException.class)
  public void test_dwcaGetDataSet01() throws NoSuchDataSetException {
    client.dwcaGetDataSet("FOO", new DevNullOutputStream());
  }

  /*
   * Test with non-existent dataset.
   */
  @Test
  public void test_groupByScientificName01() throws InvalidQueryException {
    GroupByScientificNameQuerySpec query = new GroupByScientificNameQuerySpec();
    query.addCondition(new QueryCondition("acceptedName.genusOrMonomial", "=", "Parus"));
    client.groupByScientificName(query);
  }

  /*
   * Tests of the aggregation services
   */
  @Test
  public void test_count() throws InvalidQueryException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystem.code", "=", "COL"));
    String output = JsonUtil.toPrettyJson(client.count(qs));
    Long result = Long.parseLong(output);
    Assert.assertTrue(result >= 0);
  }

  @Test
  public void test_countDistinctValues() throws InvalidQueryException {
    String field = "taxonRank";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystem.code", "=", "NSR"));
    String output = JsonUtil.toPrettyJson(client.countDistinctValues(field, qs));
    Long result = Long.parseLong(output);
    Assert.assertTrue(result >= 0);
  }

  @Test
  public void test_countDistinctValuesPerGroup() throws InvalidQueryException {
    String field = "sourceSystem.code";
    String group = "taxonRank";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystemId", "!=", null));
    System.out.println(JsonUtil.toPrettyJson(client.countDistinctValuesPerGroup(field, group, qs)));
  }

  @Test
  public void test_getDistinctValues() throws InvalidQueryException {
    String field = "sourceSystem.code";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystemId", "!=", null));
    System.out.println(JsonUtil.toPrettyJson(client.getDistinctValues(field, qs)));
  }

  @Test
  public void test_getDistinctValuesPerGroup() throws InvalidQueryException {
    String field = "sourceSystem.code";
    String group = "taxonRank";
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("sourceSystemId", "!=", null));
    System.out.println(JsonUtil.toPrettyJson(client.getDistinctValuesPerGroup(field, group, qs)));
  }

}
