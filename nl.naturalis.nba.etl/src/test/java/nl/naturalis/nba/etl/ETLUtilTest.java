
package nl.naturalis.nba.etl;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

@SuppressWarnings("static-method")
public class ETLUtilTest {

  @Test
  public void testGetSpecimenPurl() {
    String purl = ETLUtil.getSpecimenPurl("L  0219");
    assertEquals("01", "http://data.biodiversitydata.nl/naturalis/specimen/L%20%200219", purl);
  }

}
