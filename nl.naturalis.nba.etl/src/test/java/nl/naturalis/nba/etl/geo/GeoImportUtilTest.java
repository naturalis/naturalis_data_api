/**
 * 
 */
package nl.naturalis.nba.etl.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * 
 * Test class for GeoImportUtil.java
 * 
 * @author Plabon
 *
 */
@SuppressWarnings({"static-method"})
public class GeoImportUtilTest {

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
   * Test method for {@link nl.naturalis.nba.etl.geo.GeoImportUtil#getCsvFiles()}.
   * 
   * Test to verify method getCsvFiles returns a array of .CSV files.
   */
  @Test
  public void testGetCsvFiles() {

    File[] actualFiles = GeoImportUtil.getCsvFiles();
    String file = Arrays.asList(actualFiles).stream().findFirst().get().getName();
    assertNotNull(file);
    assertEquals(".csv", (file.substring(file.indexOf("."))));

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.geo.GeoImportUtil#getDataDir()}.
   * 
   * Test to verify method getDataDir returns a File object
   */
  @Test
  public void testGetDataDir() {

    Object obj = ReflectionUtil.callStatic(GeoImportUtil.class, "getDataDir", new Class[] {},
        new Object[] {});
    File actual = (File) obj;

    String expcetedName = "nba-brondata-geo";

    assertEquals(expcetedName, actual.getName());

  }

}
