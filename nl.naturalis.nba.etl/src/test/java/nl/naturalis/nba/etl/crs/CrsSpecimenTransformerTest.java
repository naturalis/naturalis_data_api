/**
 * 
 */
package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.utils.xml.DOMUtil.getDescendant;
import static nl.naturalis.nba.utils.xml.DOMUtil.getDescendants;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import nl.naturalis.nba.api.model.BioStratigraphy;
import nl.naturalis.nba.api.model.ChronoStratigraphy;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.LithoStratigraphy;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
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
@SuppressWarnings({"unchecked"})
public class CrsSpecimenTransformerTest {

  URL specimenFileUrl;
  File specimenFile;

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
        "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
    specimenFileUrl = AllTests.class.getResource("specimens.20140701000000.000008.oai.xml");
    specimenFile = new File(specimenFileUrl.getFile());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#doTransform()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the doTransform method returns expected Specimen objected
   */
  @Test
  public void testDoTransform() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<Specimen> list = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);
      Object returned =
          CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "doTransform");
      list = (List<Specimen>) returned;
      // if (list != null) break;
      Specimen sp = list.get(0);

      Specimen expectedSpecimen = new Specimen();
      expectedSpecimen.setSourceID("CRS");
      expectedSpecimen.setId("RMNH.MAM.TT.5@CRS");
      expectedSpecimen.setUnitGUID("http://data.biodiversitydata.nl/naturalis/specimen/RMNH.MAM.TT.5");
      expectedSpecimen.setOwner("Naturalis Biodiversity Center");
      expectedSpecimen.setTitle("RMNH.ART");
      expectedSpecimen.setKindOfUnit("WholeOrganism");
      expectedSpecimen.setCollectionType("PreservedSpecimen");
      expectedSpecimen.setNumberOfSpecimen(1);

      assertEquals(expectedSpecimen.getSourceID(), sp.getSourceID());
      assertEquals(expectedSpecimen.getId(), sp.getId());
      assertEquals(expectedSpecimen.getUnitGUID(), sp.getUnitGUID());
      assertEquals(expectedSpecimen.getOwner(), sp.getOwner());
      assertEquals(expectedSpecimen.getTitle(), sp.getTitle());
      assertEquals(expectedSpecimen.getKindOfUnit(), sp.getKindOfUnit());
      assertEquals(expectedSpecimen.getCollectionType(), sp.getCollectionType());
      assertEquals(expectedSpecimen.getNumberOfSpecimen(), sp.getNumberOfSpecimen());

    }
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getIdentification(Element element, String collectionType)}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getIdentification method returns an expected
   *         {@link SpecimenIdentification} object
   * 
   */
  @Test
  public void testGetIdentification() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    SpecimenIdentification identification = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);
    outerloop: for (XMLRecordInfo extracted : extractor) {
      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      Object obj = CommonReflectionUtil.callMethod(elems, List.class, crsSpecimenTransformer,
          "hasTestGenus");
      boolean check = (boolean) obj;
      if (check) {
        for (Element element : elems) {
          Object identificationObj =
              ReflectionUtil.call(crsSpecimenTransformer, "getIdentification",
                  new Class[] {Element.class, String.class}, new Object[] {element, "Arts"});
          identification = (SpecimenIdentification) identificationObj;
          if (identification != null)
            break outerloop;
        }
      }
    }
    SpecimenIdentification expectedSid = new SpecimenIdentification();
    expectedSid.setRockType("Test_Rock");
    expectedSid.setAssociatedFossilAssemblage("Test_Assemblage");
    expectedSid.setRockMineralUsage("Test_Minerals");

    assertNotNull(identification);
    assertTrue(identification.isPreferred());

    assertEquals(expectedSid.getRockType(), identification.getRockType());
    assertEquals(expectedSid.getAssociatedFossilAssemblage(),
        identification.getAssociatedFossilAssemblage());
    assertEquals(expectedSid.getRockMineralUsage(), identification.getRockMineralUsage());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getGatheringEvent()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getGatheringEvent method returns an expected
   *         {@link GatheringEvent} object
   * 
   */
  @Test
  public void testGetGatheringEvent() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    GatheringEvent event = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned =
          CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "getGatheringEvent");
      event = (GatheringEvent) returned;
      if (event != null)
        break;
    }

    GatheringEvent expectedGa = new GatheringEvent();
    expectedGa.setCountry("Netherlands");
    expectedGa.setProvinceState("Noord Holland");
    expectedGa.setLocality("Amsterdam");

    assertNotNull(event);
    assertEquals(expectedGa.getCountry(), event.getCountry());
    assertEquals(expectedGa.getProvinceState(), event.getProvinceState());
    assertEquals(expectedGa.getLocality(), event.getLocality());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getScientificName(Element element, String collectionType)}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getIdentification method returns an expected ScientificName
   *         object
   * 
   */
  @Test
  public void testGetScientificName() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    ScientificName scientificName = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);
    outerloop: for (XMLRecordInfo extracted : extractor) {
      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      for (Element element : elems) {
        Object scientificNameObj = ReflectionUtil.call(crsSpecimenTransformer, "getScientificName",
            new Class[] {Element.class, String.class}, new Object[] {element, "Arts"});
        scientificName = (ScientificName) scientificNameObj;
        if (scientificName != null)
          break outerloop;
      }
    }
    ScientificName expectedSn = new ScientificName();
    expectedSn.setFullScientificName("Cleyera japonica");
    expectedSn.setGenusOrMonomial("Cleyera");
    expectedSn.setSpecificEpithet("japonica");
    expectedSn.setScientificNameGroup("cleyera japonica");

    assertNotNull(scientificName);
    assertEquals(expectedSn.getFullScientificName(), scientificName.getFullScientificName());
    assertEquals(expectedSn.getGenusOrMonomial(), scientificName.getGenusOrMonomial());
    assertEquals(expectedSn.getSpecificEpithet(), scientificName.getSpecificEpithet());
    assertEquals(expectedSn.getScientificNameGroup(), scientificName.getScientificNameGroup());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getSystemClassification(Element element, ScientificName scientificName)}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getSystemClassification method returns an expected
   *         List<{@link Monomial}> object
   * 
   */
  @Test
  public void testGetSystemClassification() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    int listSize = 0;
    List<Monomial> classification = null;
    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Cleyera japonica");
    scientificName.setGenusOrMonomial("Cleyera");
    scientificName.setSpecificEpithet("japonica");
    scientificName.setAuthor("Plabon");
    scientificName.setScientificNameGroup("cleyera japonica");
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);
    outerloop: for (XMLRecordInfo extracted : extractor) {
      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      for (Element element : elems) {
        Object scientificNameObj = ReflectionUtil.callStatic(crsSpecimenTransformer.getClass(),
            "getSystemClassification", new Class[] {Element.class, ScientificName.class},
            new Object[] {element, scientificName});
        classification = (List<Monomial>) scientificNameObj;
        listSize = classification.size();
        if (listSize > 0)
          break outerloop;
      }
    }

    Monomial expectedMono = new Monomial();
    expectedMono.setName("Cleyera");
    expectedMono.setRank("genus");

    assertNotNull(classification);
    assertEquals(2, listSize);
    assertEquals(expectedMono.getName(), classification.get(0).getName());
    assertEquals(expectedMono.getRank(), classification.get(0).getRank());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getChronoStratigraphyList()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getChronoStratigraphyList method returns an expected
   *         {@link ChronoStratigraphy} object
   * 
   */
  @Test
  public void testGetChronoStratigraphyList() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<ChronoStratigraphy> actualList = null;
    int listSize = 0;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer,
          "getChronoStratigraphyList");
      actualList = (List<ChronoStratigraphy>) returned;
      listSize = actualList.size();
      if (listSize > 0)
        break;
    }
    ChronoStratigraphy expectedCsg = new ChronoStratigraphy();
    expectedCsg.setYoungRegionalSubstage("Test Regional Sub-Stage");
    expectedCsg.setYoungRegionalStage("Test Regional Stage");
    expectedCsg.setYoungChronoName("CRETACEOUS");

    assertNotNull(actualList);
    assertEquals(1, listSize);
    assertEquals(expectedCsg.getYoungRegionalSubstage(),
        actualList.get(0).getYoungRegionalSubstage());
    assertEquals(expectedCsg.getYoungRegionalStage(), actualList.get(0).getYoungRegionalStage());
    assertEquals(expectedCsg.getYoungChronoName(), actualList.get(0).getYoungChronoName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getBioStratigraphyList()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getBioStratigraphyList method returns an expected
   *         {@link BioStratigraphy} object
   * 
   */
  @Test
  public void testGetBioStratigraphyList() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<BioStratigraphy> actualList = null;
    int listSize = 0;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer,
          "getBioStratigraphyList");
      actualList = (List<BioStratigraphy>) returned;
      listSize = actualList.size();
      if (listSize > 0)
        break;
    }
    BioStratigraphy expectedBs = new BioStratigraphy();
    expectedBs.setYoungBioName("Test Bio Name");
    expectedBs.setYoungFossilZone("Test Young Fossil Zone");
    expectedBs.setOldBioName("Hiatella arctica Acme Zone");

    assertNotNull(actualList);
    assertEquals(2, listSize);
    assertEquals(expectedBs.getYoungBioName(), actualList.get(0).getYoungBioName());
    assertEquals(expectedBs.getYoungFossilZone(), actualList.get(0).getYoungFossilZone());
    assertEquals(expectedBs.getOldBioName(), actualList.get(0).getOldBioName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getLithoStratigraphyList()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getLithoStratigraphyList method returns an expected
   *         {@link LithoStratigraphy} object
   * 
   */
  @Test
  public void testGetLithoStratigraphyList() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<LithoStratigraphy> actualList = null;
    int listSize = 0;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer,
          "getLithoStratigraphyList");
      actualList = (List<LithoStratigraphy>) returned;
      listSize = actualList.size();
      if (listSize > 0)
        break;
    }
    LithoStratigraphy expectedLs = new LithoStratigraphy();
    expectedLs.setQualifier("Test Qualifier");
    expectedLs.setBed("Test Bed");
    expectedLs.setInformalName("Test InfoName");

    assertNotNull(actualList);
    assertEquals(2, listSize);
    assertEquals(expectedLs.getQualifier(), actualList.get(1).getQualifier());
    assertEquals(expectedLs.getBed(), actualList.get(1).getBed());
    assertEquals(expectedLs.getInformalName(), actualList.get(1).getInformalName());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getPhaseOrStage()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getPhaseOrStage method returns an expected
   *         {@link PhaseOrStage} object
   * 
   */
  @Test
  public void testGetPhaseOrStage() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    PhaseOrStage orStage = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned =
          CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "getPhaseOrStage");
      orStage = (PhaseOrStage) returned;
      if (orStage != null)
        break;
    }

    String expectedStage = "embryo";

    assertEquals(expectedStage, orStage.toString());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getTypeStatus()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getTypeStatus method returns an expected
   *         {@link SpecimenTypeStatus} object
   * 
   */
  @Test
  public void testGetTypeStatus() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    SpecimenTypeStatus specimenTypeStatus = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      for (Element element : elems) {
        Object returned = CommonReflectionUtil.callMethod(element, Element.class,
            crsSpecimenTransformer, "getTypeStatus");
        specimenTypeStatus = (SpecimenTypeStatus) returned;
        if (specimenTypeStatus != null)
          break;
      }
    }

    String expectedSpecimenTypeStatus = "paralectotype";
    assertEquals(expectedSpecimenTypeStatus, specimenTypeStatus.toString());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getSex()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the getSex method returns an expected {@link Sex} object
   * 
   */
  @Test
  public void testGetSex() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    Sex sex = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned =
          CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "getSex");
      sex = (Sex) returned;
      if (sex != null)
        break;
    }

    String expectedStage = "male";

    assertEquals(expectedStage, sex.toString());

  }

  // 1980-11-22T00:00Z
  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#date(Element element, String tag)}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the date method returns an expected {@link OffsetDateTime} object
   * 
   */
  @Test
  public void testDate() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    OffsetDateTime offsetDateTime = null;
    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Cleyera japonica");
    scientificName.setGenusOrMonomial("Cleyera");
    scientificName.setSpecificEpithet("japonica");
    scientificName.setAuthor("Plabon");
    scientificName.setScientificNameGroup("cleyera japonica");
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);
    outerloop: for (XMLRecordInfo extracted : extractor) {
      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      for (Element element : elems) {
        Object dateObj = ReflectionUtil.call(crsSpecimenTransformer, "date",
            new Class[] {Element.class, String.class},
            new Object[] {element, "abcd:IdentificationDate"});
        offsetDateTime = (OffsetDateTime) dateObj;
        if (offsetDateTime != null)
          break outerloop;
      }
    }
    String expectedOffsetTime = "1980-11-22T00:00Z";

    assertEquals(expectedOffsetTime, offsetDateTime.toString());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#hasStatusDeleted()}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the hasStatusDeleted method returns an expected boolean object
   * 
   */
  @Test
  public void testHasStatusDeleted() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    boolean hasStatus = false;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);

      Object returned =
          CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "hasStatusDeleted");
      hasStatus = (boolean) returned;

    }
    assertFalse(hasStatus);

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#hasTestGenus(List<Element> list)}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the hasTestGenus method returns an expected boolean object
   * 
   */
  @Test
  public void testHasTestGenus_01() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    boolean hasTestGenus = false;
    List<Element> elementList = new ArrayList<>();
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);
      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      for (Element element : elems) {
        elementList.add(element);
      }
      assertNotNull(elementList);
      Object returned = CommonReflectionUtil.callMethod(elementList, List.class,
          crsSpecimenTransformer, "hasTestGenus");
      hasTestGenus = (boolean) returned;

    }
    assertTrue(hasTestGenus);
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#hasTestGenus(List<Element> list)}.
   * 
   * @throws Exception
   * 
   *         Test method to verify the hasTestGenus method returns an expected boolean object
   * 
   */
  @Test
  public void testHasTestGenus_02() throws Exception {
    System.setProperty("nl.naturalis.nba.etl.testGenera", "test");
    ETLStatistics etlStatistics = new ETLStatistics();
    boolean hasTestGenus = true;
    List<Element> elementList = new ArrayList<>();
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);
      Element elemet = extracted.getRecord();
      List<Element> elems = DOMUtil.getDescendants(elemet, "ncrsDetermination");
      for (Element element : elems) {
        elementList.add(element);
      }
      assertNotNull(elementList);
      Object returned = CommonReflectionUtil.callMethod(elementList, List.class,
          crsSpecimenTransformer, "hasTestGenus");
      hasTestGenus = (boolean) returned;

    }
    assertFalse(hasTestGenus);
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsMultiMediaTransformer#val(Element element, String s)}.
   * 
   * @throws Exception
   * 
   *         Test to verify if the val method returns the expected expected String value based on
   *         the value of a tag
   */
  @Test
  public void testVal() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    String val = null;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);
    for (XMLRecordInfo extracted : extractor) {

      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID",
          "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input",
          extracted);
      Element oaiDcElem = getDescendant(extracted.getRecord(), "oai_dc:dc");
      List<Element> ncrsdatagroup = getDescendants(oaiDcElem, "ncrsdatagroup");

      Element sexElement = ncrsdatagroup.get(0);

      Object obj = ReflectionUtil.call(crsSpecimenTransformer, "val",
          new Class[] {Element.class, String.class}, new Object[] {sexElement, "abcd:Sex"});
      val = (String) obj;
    }
    assertNotNull(val);
    assertEquals("Male", val);

  }

}
