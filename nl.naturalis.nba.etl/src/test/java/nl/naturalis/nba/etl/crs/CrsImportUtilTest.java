package nl.naturalis.nba.etl.crs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;
/**
 * Test class for CrsImportUtil.java
 */
@SuppressWarnings({"static-access", "deprecation"})
@Ignore
public class CrsImportUtilTest {

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
    assertNotNull("01",result);
    assertTrue("02",result instanceof byte[]);
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
