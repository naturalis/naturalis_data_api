package nl.naturalis.nba.etl.col;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.net.URL;
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

    String logFile = "log4j2.xml";
    URL logFileUrl = AllTests.class.getResource(logFile);
    String logFilePath = logFileUrl.getFile().toString();
    dirPath = logFilePath.substring(0, logFilePath.lastIndexOf("/"));
    System.setProperty("nba.v2.conf.dir", dirPath);
    System.setProperty("brahms.data.dir", dirPath);
    System.setProperty("log4j.configurationFile", logFilePath);
    System.setProperty("nl.naturalis.nba.etl.testGenera",
        "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");

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

    File file = new File(dirPath + "/taxa.txt");
    ETLStatistics etlStatistics = new ETLStatistics();
    CoLTaxonImporter coLTaxonImporter = new CoLTaxonImporter();
    Object returned = ReflectionUtil.call(coLTaxonImporter, "createExtractor",
        new Class[] {ETLStatistics.class, File.class}, new Object[] {etlStatistics, file});
    CSVExtractor<CoLTaxonCsvField> actual = (CSVExtractor<CoLTaxonCsvField>) returned;

    char expectedDelimiter = '\t';
    String expectedCharterSet = "UTF-8";

    assertNotNull(actual);
    assertEquals(expectedDelimiter, actual.getDelimiter());
    assertEquals(expectedCharterSet, actual.getCharset().toString());

  }

}
