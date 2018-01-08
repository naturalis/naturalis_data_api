package nl.naturalis.nba.etl.crs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;
/**
 * Test class for CrsImportUtil.java
 */
@SuppressWarnings({"static-method", "static-access", "cast", "deprecation"})
public class CrsImportUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    String logFile = "log4j2.xml";
    URL logFileUrl = AllTests.class.getResource(logFile);
    String logFilePath = logFileUrl.getFile().toString();
    String dirPath = logFilePath.substring(0, logFilePath.lastIndexOf("/"));
    System.setProperty("nba.v2.conf.dir", dirPath);
    System.setProperty("brahms.data.dir", dirPath);
    System.setProperty("log4j.configurationFile", logFilePath);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsImportUtil#callSpecimenService(java.util.Date, java.util.Date)}.
   * 
   * Test method to verify the callSpecimenService(Date, Date)
   */

  @Test
  public void testCallSpecimenServiceDate() {

    CrsImportUtil crsImportUtil = ReflectionUtil.newInstance(CrsImportUtil.class, new Object[] {});

    Date fromDate = new Date(2017, 11, 25);
    Date untilDate = new Date(2016, 11, 27);

    byte[] result = crsImportUtil.callSpecimenService(fromDate, untilDate);
    assertNotNull(result);
    assertTrue(result instanceof byte[]);
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsImportUtil#callSpecimenService(java.lang.String)}.
   */
  @Test
  public void testCallSpecimenServiceString() {

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsImportUtil#callMultimediaService(java.util.Date, java.util.Date)}.
   */
  @Test
  public void testCallMultimediaServiceDateDate() {

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsImportUtil#callMultimediaService(java.lang.String)}.
   */
  @Test
  public void testCallMultimediaServiceString() {

  }

}
