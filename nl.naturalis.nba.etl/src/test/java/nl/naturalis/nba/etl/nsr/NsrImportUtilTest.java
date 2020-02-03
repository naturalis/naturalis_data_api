package nl.naturalis.nba.etl.nsr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * Test class for NsrImportUtil.java
 *
 */
public class NsrImportUtilTest {

  URL nsrFileURL;
  File nsrFile;

  /**
   * @throws java.lang.Exception exception
   */
  @Before
  public void setUp() throws Exception {
    nsrFileURL = AllTests.class.getResource("nsr-export--2020-01-30_1359--05.jsonl");
    nsrFile = new File(nsrFileURL.getFile());
  }

  @After
  public void tearDown() {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.nsr.NsrImportUtil#val(java.lang.String)}.
   * 
   * Test to verify if the val method returns the correct Element tag value
   * 
   * @throws Exception exception
   */
  @Test
  public void testVal() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String actual = null;

    LineNumberReader lnr;
    FileReader fr = new FileReader(nsrFile);
    lnr = new LineNumberReader(fr, 4096);
    String json;

    while ((json = lnr.readLine()) != null) {
      NsrTaxonTransformer taxonTransformer = new NsrTaxonTransformer(etlStatistics);
      List<Taxon> taxa = taxonTransformer.transform(json);
      Taxon taxon = taxa.get(0);
      actual = NsrImportUtil.val(taxon.getVernacularNames().get(0).getName());
    }
    String expectedNameValue = "Scandinavische zilvermeeuw";
    assertNotNull("01", actual);
    assertEquals("02", expectedNameValue, actual);
  }

}
