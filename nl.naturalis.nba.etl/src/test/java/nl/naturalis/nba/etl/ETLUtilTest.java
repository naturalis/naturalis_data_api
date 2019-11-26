package nl.naturalis.nba.etl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;

import org.joda.time.DateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.naturalis.nba.etl.col.CoLTaxonImporter;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for ETLUtil.java
 */
public class ETLUtilTest {
  
  private static final Logger logger = ETLRegistry.getInstance().getLogger(ETLUtilTest.class);
  
  @Test
  public void test() {}

  @Before
  public void setUp() throws Exception {

    logger.info("Begin setUp");
    // First import a test data row into the ES store .
    CoLTaxonImporter cti = new CoLTaxonImporter();
    String path = AllTests.class.getResource("taxa.txt").getPath();
    cti.importCsv(path);
    logger.info("csv has been loaded!");
    System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getSpecimenPurl(java.lang.String)}.
   * 
   * Test to verify getSpecimenPurl returns an expected PURL
   */
  @Test
  public void testGetSpecimenPurl() {

    String purl = ETLUtil.getSpecimenPurl("L  0219");

    assertNotNull(purl);
    assertEquals("01", "https://data.biodiversitydata.nl/naturalis/specimen/L%20%200219", purl);
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getRootCause(java.lang.Throwable)}.
   * 
   * Test the getRootCause() which returns the root cause of the specified throwable
   */
  @Test
  public void testGetRootCause() {

    IOException ioException = new IOException("This is a an IO Exception...");
    Throwable actual = ETLUtil.getRootCause(ioException);
    String expected = "This is a an IO Exception...";

    assertNotNull(actual);
    assertEquals("01", expected, actual.getMessage());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.ETLUtil#logDuration(org.apache.logging.log4j.Logger, java.lang.Class, long)}.
   * 
   * Test to verify if void static method logDuration is called.
   * 
   * @throws Exception
   * 
   */
  @Ignore
  @Test
  public void testLogDuration() throws Exception {

    // TODO: unfinished unit test
    // NOTE: Mockito cannot test static methods
    
//    Logger logger = ETLUtil.getLogger(Loader.class);
//    long timeStamp = 12585L;

//    PowerMockito.mockStatic(ETLUtil.class);
//   
//    ETLUtil.logDuration(logger, logger.getClass(), timeStamp);
//    PowerMockito.verifyStatic(ETLUtil.class, times(1));
//    ETLUtil.logDuration(logger, logger.getClass(), timeStamp);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getDuration(long)}.
   * 
   * Test to see if the gerDuration() returns a string in time format 00:00:00
   */
  @Test
  public void testGetDuration() {

    String actual = ETLUtil.getDuration(DateTime.now().getMillis());
    Pattern patternFormat = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
    Pattern validFormat = Pattern.compile("[0-2]|[0-9]:[0-5]|[0-9]:[0-5]|[0-9]");
    Matcher matcherFormat = patternFormat.matcher(actual);
    Matcher matcherValidFormat = validFormat.matcher(actual);

    assertTrue("01", matcherFormat.find());
    assertTrue("02",matcherValidFormat.find());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getDuration(long, long)}.
   * 
   * Test to verify getDuration method returns a correct time duration format 00:00:00 .
   */
  @Test
  public void testGetDurationLong() {

    String actual = ETLUtil.getDuration(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1),
        DateTime.now().getMillis());
    Pattern patternFormat = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
    Pattern ifValidFormat = Pattern.compile("[0-2]|[0-9]:[0-5]|[0-9]:[0-5]|[0-9]");
    Matcher matcherFormat = patternFormat.matcher(actual);
    Matcher matcherValidFormat = ifValidFormat.matcher(actual);

    assertTrue("01",matcherFormat.find());
    assertTrue("02",matcherValidFormat.find());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getLogger(java.lang.Class)}.
   * 
   * Test for getLogger method.
   */
  @Test
  public void testGetLogger() {

    String expected = "nl.naturalis.nba.etl.Loader";
    Logger actual = ETLUtil.getLogger(Loader.class);

    assertNotNull("01",actual);
    assertEquals("02",actual.getName(), expected);
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.ETLUtil#truncate(nl.naturalis.nba.dao.DocumentType)}.
   */
  @Test
  public void testTruncateDocumentTypeOfT() {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getTestGenera()}.
   * 
   * @throws Exception
   * 
   * Test to verify the array of genera that is returned by getTestGeneral
   */
  @Test
  public void testGetTestGenera() throws Exception {
    String[] actual = ETLUtil.getTestGenera();
    String[] expected = {"malus", "parus", "larus", "bombus", "rhododendron", "felix", "tulipa", "rosa", "canis", "passer", "trientalis"};

    assertNotNull("01",expected);
    assertArrayEquals("02",expected, actual);
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getPurlBuilder()}.
   * 
   * @throws Exception
   * 
   *         Test to verify private getUrlBuilder method returns the correct url
   */
  @Test
  public void testGetPurlBuilder() throws Exception {

    String expectedUrl = "https://data.biodiversitydata.nl";
    URIBuilder actualBuilder = (URIBuilder) ReflectionUtil.callStatic(ETLUtil.class, "getPurlBuilder", new Object[] {});

    assertNotNull("01",actualBuilder);
    assertEquals("02",expectedUrl, actualBuilder.toString());

  }

}
