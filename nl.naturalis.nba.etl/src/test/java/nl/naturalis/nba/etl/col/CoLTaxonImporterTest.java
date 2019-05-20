package nl.naturalis.nba.etl.col;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for CoLTaxonImporter
 *
 */
@SuppressWarnings("unchecked")
public class CoLTaxonImporterTest {

  String dirPath = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLTaxonImporter#createExtractor(ETLStatistics stats, File f)}.
   * 
   * Test to verify creteaExtractor method returns an expected {@ CSVExtractor} object
   */
  @Test
  public void testCreateExtractor() {

    String path = AllTests.class.getResource("taxa.txt").getPath();
    File file = new File(path);
    ETLStatistics etlStatistics = new ETLStatistics();
    CoLTaxonImporter coLTaxonImporter = new CoLTaxonImporter();
    Object returned = ReflectionUtil.call(coLTaxonImporter, "createExtractor",
        new Class[] {ETLStatistics.class, File.class}, new Object[] {etlStatistics, file});
    CSVExtractor<CoLTaxonCsvField> actual = (CSVExtractor<CoLTaxonCsvField>) returned;

    char expectedDelimiter = '\t';
    String expectedCharterSet = "UTF-8";

    assertNotNull("01",actual);
    assertEquals("02",expectedDelimiter, actual.getDelimiter());
    assertEquals("03",expectedCharterSet, actual.getCharset().toString());

  }

}
