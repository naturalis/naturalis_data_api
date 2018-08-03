package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.utils.xml.DOMUtil.getDescendants;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for NsrMultiMediaTransformer.java
 *
 */
@SuppressWarnings({"unchecked", "static-method"})
public class NsrMultiMediaTransformerTest {

  URL nsrFileURL;
  File nsrFile;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    nsrFileURL = AllTests.class.getResource("nsr-export--2017-12-30_0533--06.xml");
    nsrFile = new File(nsrFileURL.getFile());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformer#doTransform()}.
   * 
   * Test to verify if the doTransform method returns an expected {List<MultiMediaObject> }
   * 
   * @throws Exception
   */
  @Test
  public void testDoTransform() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<Taxon> list = null;
    List<MultiMediaObject> actual = null;

    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
    NsrMultiMediaTransformer mediaTransformer = new NsrMultiMediaTransformer(etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
          extracted);
      Object taxonObject =
          CommonReflectionUtil.callMethod(null, null, nsrTaxonTransformer, "doTransform");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "input",
          extracted);
      list = (List<Taxon>) taxonObject;

      mediaTransformer.setTaxon(list.get(0));
      Object returned =
          CommonReflectionUtil.callMethod(null, null, mediaTransformer, "doTransform");

      actual = (List<MultiMediaObject>) returned;

    }
    String expectedAssociatedTaxRef = "D3KF0JNQ0UA@NSR";
    String expectedCreator = "Arnold Wijker";
    String expectedCollectionType = null;
    String expectedCaption = "Adult winter";
    String expectedDescription = "Adult winter";
    String expectedId = "D3KF0JNQ0UA_1011552535@NSR";
    String expectedLicenseType = "Copyright";
    String expectedLicense = "CC BY-NC-ND";
    String expectedOwner = "Naturalis Biodiversity Center";
    String expectedSourceId = "LNG NSR";
    String expectedSourceSystemId = "D3KF0JNQ0UA_1011552535";
    String expectedUnitId = "D3KF0JNQ0UA_1011552535";

    assertEquals("01",expectedAssociatedTaxRef, actual.get(0).getAssociatedTaxonReference());
    assertEquals("02",expectedCreator, actual.get(0).getCreator());
    assertEquals("03",expectedCollectionType, actual.get(0).getCollectionType());
    assertEquals("04",expectedCaption, actual.get(0).getCaption());
    assertEquals("05",expectedDescription, actual.get(0).getDescription());
    assertEquals("06",expectedId, actual.get(0).getId());
    assertEquals("07",expectedLicenseType, actual.get(0).getLicenseType().toString());
    assertEquals("08",expectedLicense, actual.get(0).getLicense().toString());
    assertEquals("09",expectedOwner, actual.get(0).getOwner());
    assertEquals("10",expectedSourceId, actual.get(0).getSourceID());
    assertEquals("12",expectedSourceSystemId, actual.get(0).getSourceSystemId());
    assertEquals("13",expectedUnitId, actual.get(0).getUnitID());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformer#transformOne(Element element)}.
   * 
   * Test to verify if the transformOne method returns an expected {@MultiMediaObject} object
   * 
   * @throws Exception
   */
  @Test
  public void testTransformOne() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<Taxon> list = null;
    MultiMediaObject actual = null;

    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
    NsrMultiMediaTransformer mediaTransformer = new NsrMultiMediaTransformer(etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
          extracted);
      Object taxonObject =
          CommonReflectionUtil.callMethod(null, null, nsrTaxonTransformer, "doTransform");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "input",
          extracted);
      list = (List<Taxon>) taxonObject;

      mediaTransformer.setTaxon(list.get(0));

      List<Element> imageElems = getDescendants(extracted.getRecord(), "image");
      Object returned = ReflectionUtil.call(mediaTransformer, "transformOne",
          new Class[] {Element.class}, new Object[] {imageElems.get(0)});

      actual = (MultiMediaObject) returned;

    }
    String expectedAssociatedTaxRef = "D3KF0JNQ0UA@NSR";
    String expectedCreator = "Arnold Wijker";
    String expectedCollectionType = null;
    String expectedCaption = "Adult winter";
    String expectedDescription = "Adult winter";
    String expectedId = "D3KF0JNQ0UA_1011552535@NSR";
    String expectedLicenseType = "Copyright";
    String expectedLicense = "CC BY-NC-ND";
    String expectedOwner = "Naturalis Biodiversity Center";
    String expectedSourceId = "LNG NSR";
    String expectedSourceSystemId = "D3KF0JNQ0UA_1011552535";
    String expectedUnitId = "D3KF0JNQ0UA_1011552535";

    assertEquals("01",expectedAssociatedTaxRef, actual.getAssociatedTaxonReference());
    assertEquals("01",expectedCreator, actual.getCreator());
    assertEquals("02",expectedCollectionType, actual.getCollectionType());
    assertEquals("03",expectedCaption, actual.getCaption());
    assertEquals("04",expectedDescription, actual.getDescription());
    assertEquals("05",expectedId, actual.getId());
    assertEquals("06",expectedLicenseType, actual.getLicenseType().toString());
    assertEquals("07",expectedLicense, actual.getLicense().toString());
    assertEquals("08",expectedOwner, actual.getOwner());
    assertEquals("09",expectedSourceId, actual.getSourceID());
    assertEquals("10",expectedSourceSystemId, actual.getSourceSystemId());
    assertEquals("11",expectedUnitId, actual.getUnitID());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformer#parseDateTaken(String date)}.
   * 
   * Test to verify if the parseDateTaken method returns an expected {@OffsetDateTime} object
   * 
   * @throws Exception
   */

  @Test
  public void testParseDateTaken() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    NsrMultiMediaTransformer mediaTransformer = new NsrMultiMediaTransformer(etlStatistics);

    Object returned = ReflectionUtil.call(mediaTransformer, "parseDateTaken",
        new Class[] {String.class}, new Object[] {"10 February 2013"});
    OffsetDateTime date = (OffsetDateTime) returned;

    String expectedDateString = "2013-02-10T00:00Z";
    assertNotNull(date);
    assertEquals(expectedDateString, date.toString());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformer#newMediaObject()}.
   * 
   * Test to verify if the newMediaObject method returns an expected {@MultiMediaObject} object
   * 
   * @throws Exception
   */
  @Test
  public void testNewMediaObject() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<Taxon> list = null;
    MultiMediaObject actual = null;

    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
    NsrMultiMediaTransformer mediaTransformer = new NsrMultiMediaTransformer(etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
          extracted);
      Object taxonObject =
          CommonReflectionUtil.callMethod(null, null, nsrTaxonTransformer, "doTransform");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "input",
          extracted);
      list = (List<Taxon>) taxonObject;

      mediaTransformer.setTaxon(list.get(0));

      Object returned =
          CommonReflectionUtil.callMethod(null, null, mediaTransformer, "newMediaObject");

      actual = (MultiMediaObject) returned;

    }
    String expectedAssociatedTaxRef = "D3KF0JNQ0UA@NSR";
    String expectedCollectionType = null;
    String expectedOwner = "Naturalis Biodiversity Center";
    String expectedSourceId = "LNG NSR";

    assertEquals("01",expectedAssociatedTaxRef, actual.getAssociatedTaxonReference());
    assertEquals("02",expectedCollectionType, actual.getCollectionType());
    assertEquals("03",expectedOwner, actual.getOwner());
    assertEquals("04",expectedSourceId, actual.getSourceID());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformer#getIdentification()}.
   * 
   * Test to verify if the getIdentification method returns an expected
   * {@MultiMediaContentIdentification} object
   * 
   * @throws Exception
   */
  @Test
  public void testGetIdentification() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<Taxon> list = null;
    MultiMediaContentIdentification actual = null;

    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
    NsrMultiMediaTransformer mediaTransformer = new NsrMultiMediaTransformer(etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
          extracted);
      Object taxonObject =
          CommonReflectionUtil.callMethod(null, null, nsrTaxonTransformer, "doTransform");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "input",
          extracted);
      list = (List<Taxon>) taxonObject;

      mediaTransformer.setTaxon(list.get(0));

      Object returned =
          CommonReflectionUtil.callMethod(null, null, mediaTransformer, "getIdentification");

      actual = (MultiMediaContentIdentification) returned;

    }

    String expectedTaxonRank = "subspecies";
    String expectedFullScientificName = "Larus argentatus argentatus Pontoppidan, 1763";
    String expectedScientificNameGroup = "larus argentatus argentatus";
    String expectedGenusOrMonomial = "Larus";

    assertNotNull("01",actual);
    assertEquals("02",expectedTaxonRank, actual.getTaxonRank());
    assertEquals("03",expectedFullScientificName, actual.getScientificName().getFullScientificName());
    assertEquals("04",expectedScientificNameGroup, actual.getScientificName().getScientificNameGroup());
    assertEquals("05",expectedGenusOrMonomial, actual.getScientificName().getGenusOrMonomial());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformer#getUri(Element element)}.
   * 
   * Test to verify if the getUri method returns an expected {@URI} object
   * 
   * @throws Exception
   */
  @Test
  public void testGetUri() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<Taxon> list = null;
    URI actual = null;

    NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

    NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
    NsrMultiMediaTransformer mediaTransformer = new NsrMultiMediaTransformer(etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input",
          extracted);
      Object taxonObject =
          CommonReflectionUtil.callMethod(null, null, nsrTaxonTransformer, "doTransform");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "objectID",
          "D3KF0JNQ0UA");
      CommonReflectionUtil.setField(AbstractTransformer.class, mediaTransformer, "input",
          extracted);
      list = (List<Taxon>) taxonObject;

      mediaTransformer.setTaxon(list.get(0));

      List<Element> imageElems = getDescendants(extracted.getRecord(), "image");
      Object returned = ReflectionUtil.call(mediaTransformer, "getUri", new Class[] {Element.class},
          new Object[] {imageElems.get(0)});

      actual = (URI) returned;

    }
    String expectedURI =
        "http://images.naturalis.nl/original/104527_zilvermeeuw-20130210-egmond_aan_zee-001arnold_wijker.jpg";

    assertNotNull("01",actual);
    assertEquals("02",expectedURI, actual.toString());
  }

}