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
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.AssociatedTaxon;
import nl.naturalis.nba.api.model.BioStratigraphy;
import nl.naturalis.nba.api.model.ChronoStratigraphy;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.LithoStratigraphy;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.NamedArea;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonRelationType;
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

      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID", "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input", extracted);
      Object returned = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "doTransform");
      list = (List<Specimen>) returned;
      Specimen sp = list.get(0);

      List<String> previousSourceIds = new ArrayList<>();
      previousSourceIds.add("AAA");
      previousSourceIds.add("OOO");
      
      Specimen expectedSpecimen = new Specimen();
      expectedSpecimen.setSourceID("CRS");
      expectedSpecimen.setId("RMNH.MAM.TT.5@CRS");
      expectedSpecimen.setUnitGUID("http://data.biodiversitydata.nl/naturalis/specimen/RMNH.MAM.TT.5");
      expectedSpecimen.setOwner("Naturalis Biodiversity Center");
      expectedSpecimen.setTitle("RMNH.ART");
      expectedSpecimen.setKindOfUnit("WholeOrganism");
      expectedSpecimen.setCollectionType("PreservedSpecimen");
      expectedSpecimen.setNumberOfSpecimen(1);
      expectedSpecimen.setPreviousSourceID(previousSourceIds);
      
      assertNotNull("01",sp);
      assertEquals("02",expectedSpecimen.getSourceID(), sp.getSourceID());
      assertEquals("03",expectedSpecimen.getId(), sp.getId());
      assertEquals("04",expectedSpecimen.getUnitGUID(), sp.getUnitGUID());
      assertEquals("05",expectedSpecimen.getOwner(), sp.getOwner());
      assertEquals("06",expectedSpecimen.getTitle(), sp.getTitle());
      assertEquals("07",expectedSpecimen.getKindOfUnit(), sp.getKindOfUnit());
      assertEquals("08",expectedSpecimen.getCollectionType(), sp.getCollectionType());
      assertEquals("09",expectedSpecimen.getNumberOfSpecimen(), sp.getNumberOfSpecimen());
      assertEquals("10",expectedSpecimen.getPreviousSourceID(), sp.getPreviousSourceID());
      
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
      Object obj = CommonReflectionUtil.callMethod(elems, List.class, crsSpecimenTransformer, "hasTestGenus");
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

    assertTrue(identification.isPreferred());

    assertEquals("01",expectedSid.getRockType(), identification.getRockType());
    assertEquals("02",expectedSid.getAssociatedFossilAssemblage(),
        identification.getAssociatedFossilAssemblage());
    assertEquals("03",expectedSid.getRockMineralUsage(), identification.getRockMineralUsage());
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

    assertEquals("01",expectedGa.getCountry(), event.getCountry());
    assertEquals("02",expectedGa.getProvinceState(), event.getProvinceState());
    assertEquals("03",expectedGa.getLocality(), event.getLocality());
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

    assertEquals("01",expectedSn.getFullScientificName(), scientificName.getFullScientificName());
    assertEquals("02",expectedSn.getGenusOrMonomial(), scientificName.getGenusOrMonomial());
    assertEquals("03",expectedSn.getSpecificEpithet(), scientificName.getSpecificEpithet());
    assertEquals("04",expectedSn.getScientificNameGroup(), scientificName.getScientificNameGroup());

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

    assertEquals("01",2, listSize);
    assertEquals("02",expectedMono.getName(), classification.get(0).getName());
    assertEquals("03",expectedMono.getRank(), classification.get(0).getRank());
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

    assertEquals("01",1, listSize);
    assertEquals("02",expectedCsg.getYoungRegionalSubstage(),
        actualList.get(0).getYoungRegionalSubstage());
    assertEquals("03",expectedCsg.getYoungRegionalStage(), actualList.get(0).getYoungRegionalStage());
    assertEquals("04",expectedCsg.getYoungChronoName(), actualList.get(0).getYoungChronoName());

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

    assertEquals("01",2, listSize);
    assertEquals("02",expectedBs.getYoungBioName(), actualList.get(0).getYoungBioName());
    assertEquals("03",expectedBs.getYoungFossilZone(), actualList.get(0).getYoungFossilZone());
    assertEquals("04",expectedBs.getOldBioName(), actualList.get(0).getOldBioName());

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

    assertEquals("01",2, listSize);
    assertEquals("02",expectedLs.getQualifier(), actualList.get(1).getQualifier());
    assertEquals("03",expectedLs.getBed(), actualList.get(1).getBed());
    assertEquals("04",expectedLs.getInformalName(), actualList.get(1).getInformalName());

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
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID", "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input", extracted);

      Object returned = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "getPhaseOrStage");
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
        Object returned = CommonReflectionUtil.callMethod(element, Element.class, crsSpecimenTransformer, "getTypeStatus");
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
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#val(Element element, String s)}.
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

      Object obj = ReflectionUtil.call(crsSpecimenTransformer, "val", new Class[] {Element.class, String.class}, new Object[] {sexElement, "abcd:Sex"});
      val = (String) obj;
    }
    assertNotNull(val);
    assertEquals("Male", val);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getAreaClass()}.
   * to verify whether it returns the expected {@link AreaClass} object
   * 
   * @throws Exception
   */
  @Test
  public void testGetAreaClass() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    List<AreaClass> areaClasses = null;
    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID", "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input", extracted);

      Element element = extracted.getRecord();
      List<Element> elements = DOMUtil.getDescendants(element, "ncrsNamedAreas");
      areaClasses = new ArrayList<>();
      
      for (Element e : elements) {
        Object returned = CommonReflectionUtil.callMethod(e, Element.class, crsSpecimenTransformer, "getAreaClass");
        if (returned != null) {
          areaClasses.add((AreaClass) returned);
        }
      }
    }
    
    assertEquals("01", 2, areaClasses.size());

    String[] expectedAreaClasses = {"county", "continent"};
    int n = 2;
    for (AreaClass areaClass: areaClasses) {
      assertTrue("0" + n++, Arrays.asList(expectedAreaClasses).contains(areaClass.toString()));
    }
    
  }

  
  /**
   * Test method for getNamedAreas()
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getNamedAreas()}.
   * 
   * @throws Exception
   * 
   */
  @Test
  public void testGetNamedAreas() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();    
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);
    
    List<NamedArea> namedAreas = new ArrayList<>();
    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID", "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input", extracted);
      Object obj = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "getNamedAreas");
      namedAreas = (List<NamedArea>) obj;      
    }
    
    NamedArea expectedNamedArea_01 = new NamedArea(AreaClass.parse("CONTINENT"), "Africa");
    NamedArea expectedNamedArea_02 = new NamedArea(AreaClass.parse("county"), "Atewa Range FR");
    
    assertEquals("01", 2, namedAreas.size());
    assertTrue("02", namedAreas.contains(expectedNamedArea_01));
    assertTrue("02", namedAreas.contains(expectedNamedArea_02));
  }
  
  /**
   * Test method for {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getRelationType()}.
   * to verify whether it returns the expected {@link TaxonRelationType} object
   * 
   * @throws Exception
   */
  @Test
  public void testGetTaxonRelationType() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    List<TaxonRelationType> taxonRelationTypes = null;
    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID", "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input", extracted);

      Element element = extracted.getRecord();
      List<Element> elements = DOMUtil.getDescendants(element, "ncrsSynecology");
      taxonRelationTypes = new ArrayList<>();
      
      for (Element e : elements) {
        Object returned = CommonReflectionUtil.callMethod(e, Element.class, crsSpecimenTransformer, "getTaxonRelationType");
        if (returned != null) {
          taxonRelationTypes.add((TaxonRelationType) returned);
        }
      }
    }
    
    assertEquals("01", 3, taxonRelationTypes.size());

    String[] expectedRelationTypes = {"in relation with", "has host", "has parasite"};
    int n = 2;
    for (TaxonRelationType relationType : taxonRelationTypes) {
      assertTrue("0" + n++, Arrays.asList(expectedRelationTypes).contains(relationType.toString()));
    }
    
  }
  
  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getAssociatedTaxa()}.
   * to verify whether it returns the expected result
   * 
   * @throws Exception
   * 
   */
  @Test
  public void testGetAssociatedTaxa() throws Exception {

    ETLStatistics etlStatistics = new ETLStatistics();
    List<AssociatedTaxon> associatedTaxa = null;
    int listSize = 0;
    CrsSpecimenTransformer crsSpecimenTransformer = new CrsSpecimenTransformer(etlStatistics);
    CrsExtractor extractor = new CrsExtractor(specimenFile, etlStatistics);

    for (XMLRecordInfo extracted : extractor) {
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "objectID", "RMNH.MAM.TT.5");
      CommonReflectionUtil.setField(AbstractTransformer.class, crsSpecimenTransformer, "input", extracted);

      Object returned = CommonReflectionUtil.callMethod(null, null, crsSpecimenTransformer, "getAssociatedTaxa");
      associatedTaxa = (List<AssociatedTaxon>) returned;
      listSize = associatedTaxa.size();
    }
    
    AssociatedTaxon expectedAssociatedTaxon = new AssociatedTaxon( "Achillea millefolium", TaxonRelationType.parse("in relation with") );
    assertEquals( "01", 2, listSize);
    assertTrue("02", associatedTaxa.contains(expectedAssociatedTaxon));
  }
  
}