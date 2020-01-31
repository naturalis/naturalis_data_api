package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.utils.xml.DOMUtil.getChild;
import static nl.naturalis.nba.utils.xml.DOMUtil.getChildren;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.nsr.model.NsrTaxon;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Element;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;

/**
 * Test class for NsrImportUtil.java
 *
 */
public class NsrImportUtilTest {

  URL nsrFileURL;
  File nsrFile;

  /**
   * @throws java.lang.Exception
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
   * @throws Exception
   */
  @Test
  public void testVal() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String actual = null;

    LineNumberReader lnr = null;
    FileReader fr = new FileReader(nsrFile);
    lnr = new LineNumberReader(fr, 4096);
    String json = null;

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
//  Unit test for XML source
//  @Test
//  public void testVal() throws Exception {
//
//    ETLStatistics etlStatistics = new ETLStatistics();
//    String actual = null;
//    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
//
//    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);
//
//    for (XMLRecordInfo extracted : extractor) {
//
//      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
//          "D3KF0JNQ0UA");
//      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
//          extracted);
//
//      Element namesElem = getChild(extracted.getRecord(), "names");
//
//      List<Element> nameElems = getChildren(namesElem);
//
//      //actual = NsrImportUtil.val(nameElems.get(0), "fullname");
//      actual = null;
//
//    }
//    String expectedNameValue = "Scandinavische zilvermeeuw";
//    assertNotNull("01",actual);
//    assertEquals("02",expectedNameValue, actual);
//  }

}
