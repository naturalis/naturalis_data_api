package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.License.CC0_10;
import static nl.naturalis.nba.utils.xml.DOMUtil.getDescendant;
import static nl.naturalis.nba.utils.xml.DOMUtil.getDescendants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Element;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.VernacularName;

import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;
import nl.naturalis.nba.utils.xml.DOMUtil;

/**
 * Test class for CrsSpecimenTransformer.java
 */
@SuppressWarnings("unchecked")
public class CrsMultiMediaTransformerTest {

  URL multimediaUrl;
  File multimediaFile;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,aedes,felix,tulipa,rosa,canis,passer,trientalis");
    multimediaUrl = AllTests.class.getResource("multimedia.00000000000000.013159.oai.xml");
    multimediaFile = new File(multimediaUrl.getFile());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#doTransform()}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the doTransform method returns the expected {@link MultiMediaObject} object
   */
  @Test
  public void testDoTransform() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<MultiMediaObject> transformed = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    String[] testGenera = new String[] {"malus", "parus", "larus", "bombus", "rhododendron", "felix", "tulipa", "rosa", "canis", "passer", "trientalis"};
    
    Class<CrsMultiMediaTransformer> testedClass = CrsMultiMediaTransformer.class;
    Field privateField = testedClass.getDeclaredField("testGenera");
    privateField.setAccessible(true);
    privateField.set(crsMultiMediaTransformer, testGenera);
    
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Object returned = CommonReflectionUtil.callMethod(null, null, crsMultiMediaTransformer, "doTransform");
      transformed = (List<MultiMediaObject>) returned;

      MultiMediaObject sp = transformed.get(0); // Just test the first one
      String id = "RMNH.INS.867435_1";
      
      int expectedNumberOfMmObjects = 2;
      assertEquals("01", expectedNumberOfMmObjects, transformed.size());
      
      // Test the 1st Mm object
      MultiMediaObject expectedMmo = new MultiMediaObject();
      expectedMmo.setId(id.concat("@CRS"));
      expectedMmo.setSourceInstitutionID("Naturalis Biodiversity Center");
      expectedMmo.setSourceID("CRS");
      expectedMmo.setOwner("Naturalis Biodiversity Center");
      expectedMmo.setUnitID(id);
      expectedMmo.setLicense(CC0_10);
      expectedMmo.setCollectionType("Diptera");
      expectedMmo.setTitle(id);
      expectedMmo.setAssociatedSpecimenReference("RMNH.INS.867435@CRS");
      expectedMmo.setMultiMediaPublic(true);
      
      List<ServiceAccessPoint> serviceAccessPoints = new ArrayList<>();
      URI expectedUri = URI.create("https://medialib.naturalis.nl/file/id/RMNH.INS.867435_1/format/large");
      String expectedFormat = "image/jpeg";
      String expectedVariant = "ac:GoodQuality";
      serviceAccessPoints.add(new ServiceAccessPoint(expectedUri, expectedFormat, expectedVariant));
      expectedMmo.setServiceAccessPoints(serviceAccessPoints);

      assertNotNull("02", sp);
      assertEquals("03", expectedMmo.getId(), sp.getId());
      assertEquals("04", expectedMmo.getSourceInstitutionID(), sp.getSourceInstitutionID());
      assertEquals("05", expectedMmo.getSourceID(), sp.getSourceID());
      assertEquals("06", expectedMmo.getOwner(), sp.getOwner());
      assertEquals("07", expectedMmo.getUnitID(), sp.getUnitID());
      assertEquals("08", expectedMmo.getLicense(), sp.getLicense());
      assertEquals("09", expectedMmo.getCollectionType(), sp.getCollectionType());
      assertEquals("10", expectedMmo.getTitle(), sp.getTitle());
      assertEquals("11", expectedMmo.getAssociatedSpecimenReference(), sp.getAssociatedSpecimenReference());
      assertEquals("12", expectedMmo.isMultiMediaPublic(), sp.isMultiMediaPublic());
      assertEquals("13", expectedMmo.getServiceAccessPoints().get(0).getAccessUri(), sp.getServiceAccessPoints().get(0).getAccessUri());
      assertEquals("14", expectedMmo.getServiceAccessPoints().get(0).getFormat(), sp.getServiceAccessPoints().get(0).getFormat());
      assertEquals("15", expectedMmo.getServiceAccessPoints().get(0).getVariant(), sp.getServiceAccessPoints().get(0).getVariant());
      
      // Test the ServiceAccessPoint of the 2nd Mm object
      expectedMmo = new MultiMediaObject();
      serviceAccessPoints.clear();
      expectedUri = URI.create("https://medialib.naturalis.nl/file/id/RMNH.INS.867435_2/format/large");
      expectedFormat = "image/jpeg";
      expectedVariant = "ac:GoodQuality";
      serviceAccessPoints.add(new ServiceAccessPoint(expectedUri, expectedFormat, expectedVariant));
      expectedMmo.setServiceAccessPoints(serviceAccessPoints);

      sp = transformed.get(1);
      assertEquals("16", expectedMmo.getServiceAccessPoints().get(0).getAccessUri(), sp.getServiceAccessPoints().get(0).getAccessUri());
      assertEquals("17", expectedMmo.getServiceAccessPoints().get(0).getFormat(), sp.getServiceAccessPoints().get(0).getFormat());
      assertEquals("18", expectedMmo.getServiceAccessPoints().get(0).getVariant(), sp.getServiceAccessPoints().get(0).getVariant());
    }
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#initialize(Element oaiDcElem,ArrayList<MultiMediaContentIdentification> identifications)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the initialize method returns the expected {@link MultiMediaObject} object
   */
  @Test
  public void testDoTransfor() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    MultiMediaObject mmo = null;
    
    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");
      ArrayList<MultiMediaContentIdentification> identifications;
      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getIdentifications", new Class[] {List.class}, new Object[] {ncsrDeterminationElems});
      identifications = (ArrayList<MultiMediaContentIdentification>) obj;
      Object mmoObj = ReflectionUtil.call(crsMultiMediaTransformer, "initialize", new Class[] {Element.class, ArrayList.class}, new Object[] {oaiDcElem, identifications});
      mmo = (MultiMediaObject) mmoObj;
    }

    MultiMediaObject expectedMmo = new MultiMediaObject();
    expectedMmo.setSourceInstitutionID("Naturalis Biodiversity Center");
    expectedMmo.setSourceID("CRS");
    expectedMmo.setOwner("Naturalis Biodiversity Center");
    expectedMmo.setLicense(CC0_10);
    expectedMmo.setCollectionType("Diptera");
    expectedMmo.setAssociatedSpecimenReference("RMNH.INS.867435@CRS");

    assertNotNull("01", mmo);
    assertEquals("02", expectedMmo.getSourceInstitutionID(), mmo.getSourceInstitutionID());
    assertEquals("03", expectedMmo.getSourceID(), mmo.getSourceID());
    assertEquals("04", expectedMmo.getOwner(), mmo.getOwner());
    assertEquals("05", expectedMmo.getLicense(), mmo.getLicense());
    assertEquals("06", expectedMmo.getCollectionType(), mmo.getCollectionType());
    assertEquals("07", expectedMmo.getAssociatedSpecimenReference(), mmo.getAssociatedSpecimenReference());
    assertEquals("08", expectedMmo.isMultiMediaPublic(), mmo.isMultiMediaPublic()); // If not explicitly set, Boolean multiMediaPublic = null;

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getIdentifications(List<Element> ncsrDeterminationElems)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the initialize method returns the expected expected list of {@ArrayList<MultiMediaContentIdentification>}
   */
  @Test
  public void testGetIdentifications() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    ArrayList<MultiMediaContentIdentification> ids = null;
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");
      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getIdentifications", new Class[] {List.class}, new Object[] {ncsrDeterminationElems});
      ids = (ArrayList<MultiMediaContentIdentification>) obj;
    }

    assertNotNull(ids);
    // Assert ScientificName Object values
    assertEquals("Aedes kabaenensis", ids.stream().map(i -> i.getScientificName().getFullScientificName()).findFirst().get());
    assertEquals("Aedes", ids.stream().map(i -> i.getScientificName().getGenusOrMonomial()).findFirst().get());
    assertEquals("kabaenensis", ids.stream().map(i -> i.getScientificName().getSpecificEpithet()).findFirst().get());

    // Assert DefaultClassification Object values
    assertEquals("01", "Aedes", ids.stream().map(i -> i.getDefaultClassification().getGenus()).findFirst().get());
    assertEquals("02", "kabaenensis", ids.stream().map(i -> i.getDefaultClassification().getSpecificEpithet()).findFirst().get());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getGatheringEvent(Element oaiDcElem)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getGatheringEvent method returns the expected expected {@GathringEvent} object
   */
  @Test
  public void testGetGatheringEvent() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    GatheringEvent ge = null;
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getGatheringEvent", new Class[] {Element.class}, new Object[] {oaiDcElem});
      ge = (GatheringEvent) obj;
    }

    GatheringEvent expectedGe = new GatheringEvent();
    expectedGe.setWorldRegion("Asia");
    expectedGe.setCountry("Indonesia");
    expectedGe.setProvinceState("Southeast Sulawesi");
    expectedGe.setSublocality("Kabaena, Pulau");

    assertNotNull("01", ge);
    assertEquals("02", expectedGe.getCountry(), ge.getCountry());
    assertEquals("03", expectedGe.getWorldRegion(), ge.getWorldRegion());
    assertEquals("04", expectedGe.getProvinceState(), ge.getProvinceState());
    assertEquals("05", expectedGe.getSublocality(), ge.getSublocality());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getScientificName(Element oaiDcElem)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getScientificName method returns the expected expected {@ScientificName} object
   */
  @Test
  public void testGetScientificName() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    ScientificName sn = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    outerloop: for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");

      for (Element element : ncsrDeterminationElems) {
        String fullScientificNameStr = DOMUtil.getDescendantValue(element, "dwc:scientificName");
        Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getScientificName", new Class[] {Element.class}, new Object[] {element});
        sn = (ScientificName) obj;
        if (sn != null && fullScientificNameStr.equals("Aedes kabaenensis"))
          break outerloop;
      }
      ScientificName expectedSn = new ScientificName();
      expectedSn.setFullScientificName("Aedes kabaenensis");
      expectedSn.setGenusOrMonomial("Aedes");
      expectedSn.setScientificNameGroup("aedes kabaenensis");

      assertNotNull(sn);
      assertEquals("01", expectedSn.getFullScientificName(), sn.getFullScientificName());
      assertEquals("02", expectedSn.getGenusOrMonomial(), sn.getGenusOrMonomial());
      assertEquals("03", expectedSn.getScientificNameGroup(), sn.getScientificNameGroup());

    }
  }
  
  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getScientificName(Element element, String collectionType)},
   * but aimed at testing the construction of the full scientific name.
   * 
   * @throws Exception
   * 
   * Test method to verify the getIdentification method returns an expected ScientificName object
   * 
   */
  @Test
  public void testGetScientificName_02() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    ScientificName scientificName = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    
    for (XMLRecordInfo extracted : extractor) {
      Element rootElement = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(rootElement, "ncrsDetermination");
      for (Element element : elems) {
        
        String fullScientificNameStr = DOMUtil.getDescendantValue(element, "dwc:scientificName");
        
        Object scientificNameObj = ReflectionUtil.call(crsMultiMediaTransformer, "getScientificName",
            new Class[] {Element.class}, new Object[] {element});
        scientificName = (ScientificName) scientificNameObj;

        if (scientificName != null && fullScientificNameStr.equals("fullscientificnametest01")) {
          ScientificName expectedSn = new ScientificName();
          expectedSn.setFullScientificName("Aaaaa (bbbbb) ccccc ddddd Abc1234");
          assertEquals("01", expectedSn.getFullScientificName(), scientificName.getFullScientificName());
        }

        if (scientificName != null && fullScientificNameStr.equals("fullscientificnametest02")) {
          ScientificName expectedSn = new ScientificName();
          expectedSn.setFullScientificName("Aaaaa (bbbbb) ccccc eeeee Abc1234");
          assertEquals("02", expectedSn.getFullScientificName(), scientificName.getFullScientificName());
        }

        if (scientificName != null && fullScientificNameStr.equals("fullscientificnametest03")) {
          ScientificName expectedSn = new ScientificName();
          expectedSn.setFullScientificName("fullscientificnametest03");
          assertEquals("03", expectedSn.getFullScientificName(), scientificName.getFullScientificName());
        }
        
        String test = DOMUtil.getDescendantValue(element, "abcd:IdentificationQualifier3");
        if (scientificName != null && test.equals("fullscientificnametest04")) {
          ScientificName expectedSn = new ScientificName();
          expectedSn.setFullScientificName("TAXONCOVERAGE");
          assertEquals("04", expectedSn.getFullScientificName(), scientificName.getFullScientificName());
        }
      }
    }
  }


  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getTitle()}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getTitle method returns the expected expected Title
   */
  @Test
  public void testGetTitle() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> frmDigitaleBestandenElems = getDescendants(oaiDcElem, "frmDigitalebestanden");

      Element frmDigitaleBestandenElem = frmDigitaleBestandenElems.get(0);

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getTitle", new Class[] {Element.class, String.class}, new Object[] {frmDigitaleBestandenElem, "RMNH.INS.867435"});
      String title = (String) obj;

      assertNotNull("01", obj);
      assertEquals("02", "RMNH.INS.867435", title);

    }

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getQualifiers(Element element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getQualifiers method returns the expected expected {List<@Qualifiers>} object
   */
  @Test
  public void testGetQualifiers() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<String> qualifiers = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");

      for (Element element : ncsrDeterminationElems) {
        Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getQualifiers", new Class[] {Element.class}, new Object[] {element});
        qualifiers = (List<String>) obj;

      }

    }
    String expectedQulifierName1 = qualifiers.get(0);
    String expectedQualifierName2 = qualifiers.get(1);

    assertNotNull("01", qualifiers);
    assertEquals("02", expectedQulifierName1, qualifiers.get(0));
    assertEquals("03", expectedQualifierName2, qualifiers.get(1));
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getVernacularNames(Element element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getVernacularNames method returns the expected expected {List<@VernacularName>} object
   */
  @Test
  public void testGetVernacularNames() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<VernacularName> vernacularNames = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");

      for (Element element : ncsrDeterminationElems) {
        Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getVernacularNames", new Class[] {Element.class}, new Object[] {element});
        vernacularNames = (List<VernacularName>) obj;
      }
    }
    String expectedVernecularName = "TestVernacularName";
    assertNotNull("01", vernacularNames);
    assertEquals("02", 1, vernacularNames.size());
    assertEquals("03", expectedVernecularName, vernacularNames.get(0).getName());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getPhaseOrStage(Element element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getPhaseOrStage method returns the expected expected String (Phase or Stage) object
   */
  @Test
  public void testGetPhaseOrStage() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String phaseOrStage = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncrsdatagroup = getDescendants(oaiDcElem, "ncrsdatagroup");

      for (Element element : ncrsdatagroup) {
        Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getPhaseOrStage", new Class[] {Element.class}, new Object[] {element});
        phaseOrStage = (String) obj;
      }
    }
    String expectedPhase = "embryo";
    assertNotNull("01", phaseOrStage);
    assertEquals("02", expectedPhase, phaseOrStage);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getTypeStatus(Element element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getTypeStatus method returns the expected expected {@SpecimenTypeStatus} object
   */
  @Test
  public void testGetTypeStatus() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    SpecimenTypeStatus specimenTypeStatus = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");

      for (Element element : ncsrDeterminationElems) {
        Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getTypeStatus", new Class[] {Element.class}, new Object[] {element});
        specimenTypeStatus = (SpecimenTypeStatus) obj;
      }
    }
    String expectedSpecimenTypeStatus = "ISOTYPE";

    assertNotNull("01", specimenTypeStatus);
    assertEquals("02", expectedSpecimenTypeStatus, specimenTypeStatus.name());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#getSex(Element element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the getTypeStatus method returns the expected expected {@SpecimenTypeStatus} object
   */
  @Test
  public void testGetSex() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String sex = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncrsdatagroup = getDescendants(oaiDcElem, "ncrsdatagroup");

      for (Element element : ncrsdatagroup) {
        Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "getSex", new Class[] {Element.class}, new Object[] {element});
        sex = (String) obj;
      }
    }
    String expectedSex = "male";

    assertNotNull("01", expectedSex);
    assertEquals("02", expectedSex, sex);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#hasTestGenus(List<@Element> element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the hasTestGenus method returns the expected expected boolean value
   */
  @Test
  public void testHasTestGenus_01() throws Exception {

    System.setProperty("nl.naturalis.nba.etl.testGenera", "aedes");
    ETLStatistics etlStatistics = new ETLStatistics();
    boolean testHasTestGenus = false;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");
      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "hasTestGenus", new Class[] {List.class}, new Object[] {ncsrDeterminationElems});
      testHasTestGenus = (boolean) obj;
      if (testHasTestGenus == true) break;
    }
    assertTrue(testHasTestGenus);
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#hasTestGenus(List<@Element> element)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the hasTestGenus method returns the expected expected boolean value
   */
  @Test
  public void testHasTestGenus_02() throws Exception {

    System.setProperty("nl.naturalis.nba.etl.testGenera", "test");
    ETLStatistics etlStatistics = new ETLStatistics();
    boolean testHasTestGenus = true;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem, "ncrsDetermination");

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "hasTestGenus", new Class[] {List.class}, new Object[] {ncsrDeterminationElems});
      testHasTestGenus = (boolean) obj;
      if (testHasTestGenus == true) break;
    }
    assertFalse(testHasTestGenus);
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#val(Element element, String s)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the val method returns the expected expected String value based on the value of a tag
   */
  @Test
  public void testVal() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String val = null;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncrsdatagroup = getDescendants(oaiDcElem, "ncrsdatagroup");

      Element frmDigitaleBestandenElem = ncrsdatagroup.get(0);

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "val", new Class[] {Element.class, String.class}, new Object[] {frmDigitaleBestandenElem, "dwc:sex"});
      val = (String) obj;
    }
    assertNotNull("01", val);
    assertEquals("02", "male", val);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#dval(Element element, String s)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the dval method returns the expected expected Double value based on the value of a tag
   */
  @Test
  public void testDval() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    Double dval = 0.0;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncrsGatheringSites = getDescendants(oaiDcElem, "ncrsGatheringSites");
      Element element = ncrsGatheringSites.get(0);

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "dval", new Class[] {Element.class, String.class}, new Object[] {element, "dwc:decimalLongitude"});
      dval = (Double) obj;

    }
    Double expectedDouble = 14.3405556;
    assertNotNull("01", dval);
    assertEquals("02", expectedDouble, dval);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#bval(Element element, String s)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the bval method returns the expected expected boolean value based on the value of a tag
   */
  @Test
  public void testBval() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    boolean bval = false;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> frmDigitaleBestandenElems = getDescendants(oaiDcElem, "frmDigitalebestanden");

      Element frmDigitaleBestandenElem = frmDigitaleBestandenElems.get(0);

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "bval", new Class[] {Element.class, String.class}, new Object[] {frmDigitaleBestandenElem, "abcd:MultiMediaPublic"});
      bval = (boolean) obj;

    }
    assertTrue(bval);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#skipRecord()}.
   * 
   * @throws Exception Test to verify if the skipRecord method returns the expected expected boolean value
   */

  @Test
  public void testSkipRecord() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    boolean bval = true;
    CrsMultiMediaTransformer crsMultiMediaTransformer = new CrsMultiMediaTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(multimediaFile, etlStatistics);
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "objectID", "RMNH.INS.867435");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsMultiMediaTransformer, "input", extracted);

      Object obj = ReflectionUtil.call(crsMultiMediaTransformer, "skipRecord", new Class[] {}, new Object[] {});
      bval = (boolean) obj;
    }
    assertFalse(bval);

  }

}
