package nl.naturalis.nba.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Expert;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicRank;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.api.model.summary.SummaryGatheringEvent;
import nl.naturalis.nba.api.model.summary.SummaryGatheringSiteCoordinates;
import nl.naturalis.nba.api.model.summary.SummaryOrganization;
import nl.naturalis.nba.api.model.summary.SummaryPerson;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySourceSystem;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimenIdentification;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;
import nl.naturalis.nba.api.model.summary.SummaryVernacularName;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for SummaryObjectUtil.java
 *
 */
@SuppressWarnings({"unchecked"})
@Ignore
public class SummaryObjectUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copySpecimen(nl.naturalis.nba.api.model.Specimen, java.lang.String)}.
   * 
   * Test to verify copySpecimen methods returns an expected SummerySpecimen object.
   */
  @Test
  public void testCopySpecimen() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    Monomial monomial = new Monomial();
    monomial.setName("Plantae");
    monomial.setRank("kingdom");

    DefaultClassification defaultClassification = new DefaultClassification();
    defaultClassification.set(monomial);

    SpecimenTypeStatus typeStatus = SpecimenTypeStatus.ISOLECTOTYPE;

    TaxonomicEnrichment taxonomicEnrichments = new TaxonomicEnrichment();
    taxonomicEnrichments.setTaxonId("L.1911711@BRAHMS");

    List<TaxonomicEnrichment> enrichments = new ArrayList<>();
    enrichments.add(taxonomicEnrichments);

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);
    identification.setDefaultClassification(defaultClassification);
    identification.setTypeStatus(typeStatus);
    identification.setTaxonomicEnrichments(enrichments);

    SourceSystem sourceSystem = SourceSystem.getInstance("COL", "Species 2000 - Catalogue Of Life");
    Sex sex = Sex.MIXED;

    String phaseOrStage = "pupa";

    OffsetDateTime time = OffsetDateTime.parse("2011-12-03T10:15:30Z");

    Organization org = new Organization();
    org.setName("Naturalis");

    List<Organization> gatheringOrganizations = new ArrayList<>();
    gatheringOrganizations.add(org);

    Person person = new Person();
    person.setFullName("Plabon Kakoti");
    person.setOrganization(org);
    person.setAgentText("test");

    List<Person> gatheringPersons = new ArrayList<>();
    gatheringPersons.add(person);

    GatheringSiteCoordinates siteCoordinates = new GatheringSiteCoordinates();
    siteCoordinates.setLongitudeDecimal(5.016667);
    siteCoordinates.setLatitudeDecimal(51.433333);

    List<GatheringSiteCoordinates> coordinates = new ArrayList<>();
    coordinates.add(siteCoordinates);

    GatheringEvent gatheringEvent = new GatheringEvent();
    gatheringEvent.setDateTimeBegin(time);
    gatheringEvent.setGatheringOrganizations(gatheringOrganizations);
    gatheringEvent.setGatheringPersons(gatheringPersons);
    gatheringEvent.setLocality("Amsterdam");
    gatheringEvent.setSiteCoordinates(coordinates);

    Specimen specimen = new Specimen();
    specimen.addIndentification(identification);
    specimen.setId("L.1911711@BRAHMS");
    specimen.setCollectorsFieldNumber("Wiesbaur, SJ  s.n. ");
    specimen.setAssemblageID("");
    specimen.setCollectionType("Botany");
    specimen.setSex(sex);
    specimen.setPhaseOrStage(phaseOrStage);
    specimen.setSourceSystem(sourceSystem);
    specimen.setGatheringEvent(gatheringEvent);

    SummarySpecimen summarySpecimen = SummaryObjectUtil.copySpecimen(specimen, "bombus affinis");

    summarySpecimen.getCollectorsFieldNumber();
    summarySpecimen.getCollectionType();
    summarySpecimen.getPhaseOrStage();
    summarySpecimen.getGatheringEvent().getLocalityText();
    summarySpecimen.getMatchingIdentifications().get(0).getScientificName().getFullScientificName();

    assertNotNull(summarySpecimen);
    assertEquals("01", "L.1911711@BRAHMS", summarySpecimen.getId());
    assertEquals("02", "COL", summarySpecimen.getSourceSystem().getCode());
    assertEquals("03", "Wiesbaur, SJ  s.n. ", summarySpecimen.getCollectorsFieldNumber());
    assertEquals("04", "2011-12-03T10:15:30Z",
        summarySpecimen.getGatheringEvent().getDateTimeBegin().toString());
    assertEquals("05", "pupa", summarySpecimen.getPhaseOrStage().toString());
    assertEquals("06", "Botany", summarySpecimen.getCollectionType());
    assertEquals("07", "Bombus affinis Cresson, 1863", summarySpecimen.getMatchingIdentifications()
        .get(0).getScientificName().getFullScientificName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copyScientificName(nl.naturalis.nba.api.model.ScientificName)}.
   * 
   * Test to verify copyScientificName returns a an expected SummaryScientificName object
   */
  @Test
  public void testCopyScientificName() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    SummaryScientificName actual = SummaryObjectUtil.copyScientificName(scientificName);

    assertEquals("01", "Bombus affinis Cresson, 1863", actual.getFullScientificName());
    assertEquals("02", "accepted name", actual.getTaxonomicStatus().toString());
    assertEquals("03", "Larus", actual.getGenusOrMonomial());
    assertEquals("04", "affinis", actual.getSpecificEpithet());
    assertEquals("05", "Cresson, 1863", actual.getAuthorshipVerbatim());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copySourceSystem(nl.naturalis.nba.api.model.SourceSystem)}.
   * 
   * Test to verify copySourceSystem returns a an expected SummarySourceSystem object
   */
  @Test
  public void testCopySourceSystem() {

    SourceSystem sourceSystem = SourceSystem.getInstance("COL", "Species 2000 - Catalogue Of Life");

    SummarySourceSystem actual = SummaryObjectUtil.copySourceSystem(sourceSystem);

    assertNotNull("01", actual);
    assertEquals("02", "COL", actual.getCode());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copyGatheringEvent(nl.naturalis.nba.api.model.GatheringEvent)}.
   * 
   * Test to verify copyGatheringEvent returns a an expected SummaryGatheringEvent object
   */
  @Test
  public void testCopyGatheringEvent() {

    OffsetDateTime time = OffsetDateTime.parse("2011-12-03T10:15:30Z");

    Organization org = new Organization();
    org.setName("Naturalis");

    List<Organization> gatheringOrganizations = new ArrayList<>();
    gatheringOrganizations.add(org);

    Person person = new Person();
    person.setFullName("Plabon Kakoti");
    person.setOrganization(org);
    person.setAgentText("test");

    List<Person> gatheringPersons = new ArrayList<>();
    gatheringPersons.add(person);

    GatheringSiteCoordinates siteCoordinates = new GatheringSiteCoordinates();
    siteCoordinates.setLongitudeDecimal(5.016667);
    siteCoordinates.setLatitudeDecimal(51.433333);

    List<GatheringSiteCoordinates> coordinates = new ArrayList<>();
    coordinates.add(siteCoordinates);

    GatheringEvent gatheringEvent = new GatheringEvent();
    gatheringEvent.setDateTimeBegin(time);
    gatheringEvent.setGatheringOrganizations(gatheringOrganizations);
    gatheringEvent.setGatheringPersons(gatheringPersons);
    gatheringEvent.setLocality("Amsterdam");
    gatheringEvent.setSiteCoordinates(coordinates);

    Object obj = ReflectionUtil.callStatic(SummaryObjectUtil.class, "copyGatheringEvent",
        new Class[] {GatheringEvent.class}, new Object[] {gatheringEvent});
    SummaryGatheringEvent actual = (SummaryGatheringEvent) obj;

    assertNotNull("01", actual);
    assertEquals("02", "2011-12-03T10:15:30Z", actual.getDateTimeBegin().toString());
    assertEquals("03", "Plabon Kakoti", actual.getGatheringPersons().get(0).getFullName());
    assertEquals("04", "Naturalis", actual.getGatheringOrganizations().get(0).getName());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copySiteCoordinates(List<GatheringSiteCoordinates>
   * )}.
   * 
   * Test to verify copySiteCoordinates returns a an expected List<SummaryGatheringSiteCoordinates>
   * object
   */
  @Test
  public void testCopySiteCoordinates() {

    GatheringSiteCoordinates siteCoordinates = new GatheringSiteCoordinates();
    siteCoordinates.setLongitudeDecimal(5.016667);
    siteCoordinates.setLatitudeDecimal(51.433333);

    List<GatheringSiteCoordinates> coordinates = new ArrayList<>();
    coordinates.add(siteCoordinates);

    Object obj = ReflectionUtil.callStatic(SummaryObjectUtil.class, "copySiteCoordinates",
        new Class[] {List.class}, new Object[] {coordinates});
    List<SummaryGatheringSiteCoordinates> actual = (List<SummaryGatheringSiteCoordinates>) obj;

    Double expectedLatitude = 51.433333;
    Double expectedLongitude = 5.016667;

    assertNotNull(actual);
    assertEquals("01", expectedLatitude, actual.get(0).getGeoShape().getCoordinates().getLatitude(),
        .000001);
    assertEquals("02", expectedLongitude,
        actual.get(0).getGeoShape().getCoordinates().getLongitude(), .000001);
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copyIdentification(nl.naturalis.nba.api.model.SpecimenIdentification)}.
   * 
   * Test to verify copyIdentification returns a an expected SummarySpecimenIdentification object
   */
  @Test
  public void testCopyIdentification() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    Monomial monomial = new Monomial();
    monomial.setName("Plantae");
    monomial.setRank("kingdom");

    DefaultClassification defaultClassification = new DefaultClassification();
    defaultClassification.set(monomial);

    SpecimenTypeStatus typeStatus = SpecimenTypeStatus.ISOLECTOTYPE;

    TaxonomicEnrichment taxonomicEnrichments = new TaxonomicEnrichment();
    taxonomicEnrichments.setTaxonId("L.1911711@BRAHMS");

    List<TaxonomicEnrichment> enrichments = new ArrayList<>();
    enrichments.add(taxonomicEnrichments);

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);
    identification.setDefaultClassification(defaultClassification);
    identification.setTypeStatus(typeStatus);
    identification.setTaxonomicEnrichments(enrichments);

    Object obj = ReflectionUtil.callStatic(SummaryObjectUtil.class, "copyIdentification",
        new Class[] {SpecimenIdentification.class}, new Object[] {identification});
    SummarySpecimenIdentification actual = (SummarySpecimenIdentification) obj;

    assertNotNull("01", actual);
    assertEquals("02", "Bombus affinis Cresson, 1863",
        actual.getScientificName().getFullScientificName());
    assertEquals("03", "Larus", actual.getScientificName().getGenusOrMonomial());
    assertEquals("04", "Cresson, 1863", actual.getScientificName().getAuthorshipVerbatim());
    assertEquals("05", "affinis", actual.getScientificName().getSpecificEpithet());
    assertEquals("06", "accepted name", actual.getScientificName().getTaxonomicStatus().toString());
    assertEquals("07", "L.1911711@BRAHMS", actual.getTaxonomicEnrichments().get(0).getTaxonId());

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.SummaryObjectUtil#copyPersons(List<Person>
   * persons)}.
   * 
   * Test to verify copyPersons returns a an expected List<SummaryPerson> object
   */
  @Test
  public void testCopyPersons() {

    Organization org = new Organization();
    org.setName("Naturalis");

    List<Organization> gatheringOrganizations = new ArrayList<>();
    gatheringOrganizations.add(org);

    Person person = new Person();
    person.setFullName("Plabon Kakoti");
    person.setOrganization(org);
    person.setAgentText("test");

    List<Person> list = new ArrayList<>();
    list.add(person);

    Object obj = ReflectionUtil.callStatic(SummaryObjectUtil.class, "copyPersons",
        new Class[] {List.class}, new Object[] {list});
    List<SummaryPerson> actualList = (List<SummaryPerson>) obj;
    SummaryPerson actual = actualList.get(0);

    assertNotNull("01", actual);
    assertEquals("02", "Plabon Kakoti", actual.getFullName());
    assertEquals("03", "Naturalis", actual.getOrganization().getName());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copyOrganizations(List<Organization>
   * organizations)}.
   * 
   * Test to verify copyOrganizations returns an expected List<SummaryOrganization> object
   */
  @Test
  public void testCopyOrganizations() {

    Organization org = new Organization();
    org.setName("Naturalis");

    List<Organization> gatheringOrganizations = new ArrayList<>();
    gatheringOrganizations.add(org);

    Object obj = ReflectionUtil.callStatic(SummaryObjectUtil.class, "copyOrganizations",
        new Class[] {List.class}, new Object[] {gatheringOrganizations});
    List<SummaryOrganization> list = (List<SummaryOrganization>) obj;

    SummaryOrganization actual = list.get(0);

    assertNotNull("01", actual);
    assertEquals("02", "Naturalis", actual.getName());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copySummaryVernacularName(nl.naturalis.nba.api.model.VernacularName)}.
   * 
   * Test to verify copySummaryVernacularName returns an expected SummaryVernacularName object
   */
  @Test
  public void testCopySummaryVernacularName() {

    Organization org = new Organization();
    org.setName("Naturalis");

    Expert expert = new Expert();
    expert.setAgentText("Test Agent");
    expert.setFullName("Plabon Kakoti");
    expert.setOrganization(org);

    List<Expert> expertsList = new ArrayList<>();
    expertsList.add(expert);

    VernacularName vn = new VernacularName();
    vn.setName("pinhead");
    vn.setLanguage("English");
    vn.setExperts(expertsList);
    vn.setPreferred(true);

    SummaryVernacularName actual = SummaryObjectUtil.copySummaryVernacularName(vn);

    assertNotNull("01", actual);
    assertEquals("02", "pinhead", actual.getName());
    assertEquals("03", "English", actual.getLanguage());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.SummaryObjectUtil#copyTaxon(nl.naturalis.nba.api.model.Taxon)}.
   * 
   * Test to verify copyTaxon returns an expected SummaryTaxon object
   */
  @Test
  public void testCopyTaxon() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    Monomial monomial = new Monomial();
    monomial.setName("Plantae");
    monomial.setRank("kingdom");

    TaxonomicRank rank = TaxonomicRank.KINGDOM;

    DefaultClassification defaultClassification = new DefaultClassification();
    defaultClassification.set(monomial);

    SourceSystem sourceSystem = SourceSystem.getInstance("COL", "Species 2000 - Catalogue Of Life");

    Taxon taxon = new Taxon();
    taxon.setAcceptedName(scientificName);
    taxon.setDefaultClassification(defaultClassification);
    taxon.setSourceSystem(sourceSystem);
    taxon.setId("6931870@COL");

    SummaryTaxon actual = SummaryObjectUtil.copyTaxon(taxon);

    assertNotNull(actual);
    assertEquals("01", "Bombus affinis Cresson, 1863",
        actual.getAcceptedName().getFullScientificName());
    assertEquals("02", "Larus", actual.getAcceptedName().getGenusOrMonomial());
    assertEquals("03", "Plantae", actual.getDefaultClassification().get(rank));
    assertEquals("04", "COL", actual.getSourceSystem().getCode());

  }

}
