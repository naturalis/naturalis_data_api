package nl.naturalis.nba.etl.crs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for CrsImportUtil.java
 */
public class CrsImportUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}

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
  @SuppressWarnings("static-access")
  @Test
  public void testCallSpecimenServiceDate() {

    CrsImportUtil crsImportUtil = ReflectionUtil.newInstance(CrsImportUtil.class, new Object[] {});

    Date fromDate = java.sql.Date.valueOf("2017-11-25");
    Date untilDate = java.sql.Date.valueOf("2016-11-27");

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
    
    // TODO: implement unit test
    
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsImportUtil#callMultimediaService(java.util.Date, java.util.Date)}.
   */
  @Test
  public void testCallMultimediaServiceDateDate() {
    
    // TODO: implement unit test
    
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsImportUtil#callMultimediaService(java.lang.String)}.
   */
  @Test
  public void testCallMultimediaServiceString() {

    // TODO: implement unit test
    
  }

}
