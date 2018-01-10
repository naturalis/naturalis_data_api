package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.utils.xml.DOMUtil.getChild;
import static nl.naturalis.nba.utils.xml.DOMUtil.getChildren;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.net.URL;
import java.util.List;
import org.junit.Before;
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

    String logFile = "log4j2.xml";
    URL logFileUrl = AllTests.class.getResource(logFile);
    String logFilePath = logFileUrl.getFile().toString();
    String dirPath = logFilePath.substring(0, logFilePath.lastIndexOf("/"));
    System.setProperty("nba.v2.conf.dir", dirPath);
    System.setProperty("brahms.data.dir", dirPath);
    System.setProperty("log4j.configurationFile", logFilePath);
    System.setProperty("nl.naturalis.nba.etl.testGenera",
        "malus,aedes,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
    nsrFileURL = AllTests.class.getResource("nsr-export--2017-12-30_0533--06.xml");
    nsrFile = new File(nsrFileURL.getFile());

  }

  /**
   * @throws java.lang.Exception
   */
  // @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.nsr.NsrImportUtil#val(org.w3c.dom.Element, java.lang.String)}.
   * 
   * Test to verify if the val method returns the correct Element tag value
   * 
   * @throws Exception
   */
  @Test
  public void testVal() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String actual = null;
    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
          extracted);

      Element namesElem = getChild(extracted.getRecord(), "names");

      List<Element> nameElems = getChildren(namesElem);

      actual = NsrImportUtil.val(nameElems.get(0), "fullname");

    }
    String expectedNameValue = "Scandinavische zilvermeeuw";
    assertNotNull(actual);
    assertEquals(expectedNameValue, actual);

  }

}
