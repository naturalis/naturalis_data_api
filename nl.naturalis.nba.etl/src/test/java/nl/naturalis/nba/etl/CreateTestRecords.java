package nl.naturalis.nba.etl;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import org.junit.Test;
import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.AssociatedTaxon;
import nl.naturalis.nba.api.model.BioStratigraphy;
import nl.naturalis.nba.api.model.ChronoStratigraphy;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Expert;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.LithoStratigraphy;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.NamedArea;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonRelationType;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySourceSystem;
import nl.naturalis.nba.api.model.summary.SummaryVernacularName;
import nl.naturalis.nba.common.json.JsonUtil;

/**
 * Class that creates a test record for each document type, containing data
 * for each available field
 *
 */
public class CreateTestRecords {
  
  @SuppressWarnings("static-method")
  @Test
  public void createSpecimenObject() throws Exception {
    
    Specimen specimen = generateSpecimen();
    
    // System.out.println(JsonUtil.toPrettyJson(specimen));

  }

  @SuppressWarnings("static-method")
  @Test
  public void createMultiMediaObject() {

    MultiMediaObject multiMediaObject = new MultiMediaObject();
    multiMediaObject.setCreator(reverseString("creator"));
    multiMediaObject.setCopyrightText(reverseString("copyrightText"));
    multiMediaObject.setAssociatedSpecimenReference(reverseString("associatedSpecimenReference"));
    multiMediaObject.setAssociatedTaxonReference(reverseString("associatedTaxonReference"));
    multiMediaObject.setMultiMediaPublic(true);
    
    multiMediaObject.setSubjectParts(Arrays.asList(new String[] {reverseString("subjectParts") + "_1", reverseString("subjectParts") + "_2"}));
    multiMediaObject.setSubjectOrientations(Arrays.asList(new String[] {reverseString("subjectOrientations") + "_1", reverseString("subjectOrientations") + "_2"}));
    multiMediaObject.setPhasesOrStages(Arrays.asList(new String[] {reverseString("phasesOrStages") + "_1", reverseString("phasesOrStages") + "_2"}));
    multiMediaObject.setSexes(Arrays.asList(new String[] {"female", "male"}));
    
    System.out.println(JsonUtil.toPrettyJson(multiMediaObject));    

  }

  private static Specimen generateSpecimen() throws Exception {
    
    Specimen specimen = new Specimen();
    
    // NbaTraceableObject
    specimen.setSourceSystemId(reverseString("sourceSystemId"));
    String prefix = "TEST";
    SourceSystem sourceSystem = SourceSystem.CRS;    
    String recordNumber = Instant.now().getEpochSecond() * 1009 + "";
    specimen.setSourceSystem(sourceSystem);
    specimen.setRecordURI(new URI("https://location/" + prefix + "." + recordNumber));
    
    // Specimen
    specimen.setId(prefix + "." + recordNumber + "@" + sourceSystem);
    specimen.setUnitID(prefix + "."  + recordNumber);
    specimen.setUnitGUID("https://location/" + prefix + "."  + recordNumber);
    
    specimen.setSourceID("CRS");
    specimen.setSourceInstitutionID("Naturalis Biodiversity Center");
    
    specimen.setCollectorsFieldNumber(reverseString("collectorsFieldNumber"));
    specimen.setAssemblageID(reverseString("assemblageID"));
    specimen.setPreviousSourceID( Arrays.asList(new String[] {reverseString("previousSourceID.001"), reverseString("previousSourceID.002"), reverseString("previousSourceID.003")}));
   
    specimen.setOwner(reverseString("owner"));
    specimen.setLicenseType("Copyright"); // Fixed value
    specimen.setLicense("CC0"); // Fixed value
    specimen.setRecordBasis(reverseString("recordBasis"));
    specimen.setKindOfUnit(reverseString("kindOfUnit"));
    specimen.setCollectionType(reverseString("collectionType"));
    specimen.setSex(Sex.FEMALE);
    specimen.setPhaseOrStage(PhaseOrStage.ADULT);
    specimen.setTitle(reverseString("title"));
    specimen.setNotes(reverseString("notes"));
    specimen.setPreparationType(reverseString("preparationType"));
    specimen.setPreviousUnitsText(reverseString("previousUnitsText"));
    specimen.setNumberOfSpecimen(12345);
    specimen.setFromCaptivity(true);
    specimen.setObjectPublic(true);
    specimen.setMultiMediaPublic(true);
    
    Agent acquiredFrom = new Agent();
    acquiredFrom.setAgentText(reverseString("acquiredFrom.agentText"));
    specimen.setAcquiredFrom(acquiredFrom);
    
    specimen.setGatheringEvent(createGatheringEvent());
    specimen.setIdentifications(Arrays.asList(new SpecimenIdentification[] {createSpecimenIdentification(1), createSpecimenIdentification(2)}));
    specimen.setAssociatedMultiMediaUris(Arrays.asList(new ServiceAccessPoint[] {createServiceAccessPoint(1), createServiceAccessPoint(2)}));
    specimen.setTheme(Arrays.asList(new String[] {reverseString("theme") + "_1", reverseString("theme") + "_2", reverseString("theme") + "_3"}));
    
    specimen.setAssociatedMultiMediaObjects(null);
    return specimen;
  }
  
  private static GatheringEvent createGatheringEvent() {
    GatheringEvent gatheringEvent = new GatheringEvent();
    gatheringEvent.setProjectTitle(reverseString("gatheringEvent.projectTitle"));
    gatheringEvent.setWorldRegion(reverseString("gatheringEvent.worldRegion"));
    gatheringEvent.setContinent(reverseString("gatheringEvent.continent"));
    gatheringEvent.setCountry(reverseString("gatheringEvent.country"));
    gatheringEvent.setIso3166Code(reverseString("gatheringEvent.iso3166Code"));
    gatheringEvent.setProvinceState(reverseString("gatheringEvent.provinceState"));
    gatheringEvent.setIsland(reverseString("gatheringEvent.island"));
    gatheringEvent.setLocality(reverseString("gatheringEvent.locality"));
    gatheringEvent.setCity(reverseString("gatheringEvent.city"));
    gatheringEvent.setSublocality(reverseString("gatheringEvent.sublocality"));
    gatheringEvent.setLocalityText(reverseString("gatheringEvent.localityText"));
    gatheringEvent.setDateTimeBegin(OffsetDateTime.now().minusYears(1));
    gatheringEvent.setDateTimeEnd(OffsetDateTime.now());
    gatheringEvent.setMethod(reverseString("method"));
    gatheringEvent.setAltitude(reverseString("altitude"));
    gatheringEvent.setAltitudeUnifOfMeasurement(reverseString("altitudeUnifOfMeasurement"));
    gatheringEvent.setBiotopeText(reverseString("biotopeText"));
    gatheringEvent.setDepth(reverseString("depth"));
    gatheringEvent.setDepthUnitOfMeasurement(reverseString("depthUnitOfMeasurement"));
    
    gatheringEvent.setGatheringPersons(Arrays.asList(new Person[] {createPerson(1), createPerson(2)}));
    gatheringEvent.setGatheringOrganizations(Arrays.asList(new Organization[] {createOrganization(3), createOrganization(4)}));
    gatheringEvent.setSiteCoordinates(Arrays.asList((new GatheringSiteCoordinates[] {createSiteCoordinates(), createSiteCoordinates()})));
    gatheringEvent.setNamedAreas(Arrays.asList(new NamedArea[] {createNamedArea(1), createNamedArea(2)}));
    gatheringEvent.setAssociatedTaxa(Arrays.asList(new AssociatedTaxon[] {createAssociatedTaxon(1), createAssociatedTaxon(2)}));

    gatheringEvent.setChronoStratigraphy(Arrays.asList(new ChronoStratigraphy[] {createChronoStratigraphy(1), createChronoStratigraphy(2)}));
    gatheringEvent.setBioStratigraphy(Arrays.asList(new BioStratigraphy[] {createBioStratigraphy(1), createBioStratigraphy(2)}));
    gatheringEvent.setLithoStratigraphy(Arrays.asList(new LithoStratigraphy[] {createLithoStratigraphy(1), createLithoStratigraphy(2)}));
    return gatheringEvent;
  }
  
  private static MultiMediaGatheringEvent createMultiMediaGatheringEvent() {
    MultiMediaGatheringEvent mmGatheringEvent = (MultiMediaGatheringEvent) createGatheringEvent();
    //mmGatheringEvent.set
    return mmGatheringEvent;
  }

  private static Agent createAgent(int n) {
    return new Agent(reverseString("agentText") + "_" + n);
  }
  
  private static Person createPerson(int n) {
    Person person = new Person (reverseString("fullName") + "_" + n);
    person.setAgentText(reverseString("agentText") + "_" + n);
    person.setOrganization(createOrganization(n));
    return person;
  }
  
  private static Expert createExpert(int n) {
    Expert expert = new Expert();
    expert.setFullName(reverseString("fullName") + "_" + n);
    expert.setAgentText(reverseString("agentText") + "_" + n);
    expert.setOrganization(createOrganization(n));
    return expert;
  }

  
  private static Organization createOrganization(int n) {
    Organization organization = new Organization(reverseString("organization.name") + "_" + n);
    organization.setAgentText(reverseString("organization.agentText") + "_" + n);
    return organization;
  }
  
  private static GatheringSiteCoordinates createSiteCoordinates() {
    Random rand = new Random();
    double lat = rand.nextDouble() * 180 - 90;
    double lon = rand.nextDouble() * 360 - 180;
    GatheringSiteCoordinates siteCoordinates = new GatheringSiteCoordinates(lat, lon);
    siteCoordinates.setGridCellSystem(reverseString("gridCellSystem"));
    siteCoordinates.setGridLatitudeDecimal(Math.floor(lat));
    siteCoordinates.setGridLongitudeDecimal(Math.floor(lon));
    siteCoordinates.setGridCellCode(reverseString("gridCellCode"));
    siteCoordinates.setGridQualifier(reverseString("gridQualifier"));
    return siteCoordinates;
  }
  
  private static NamedArea createNamedArea(int n) {
    Random rand = new Random();
    int item = rand.nextInt(AreaClass.values().length - 1);
    return new NamedArea(Arrays.asList(AreaClass.values()).get(item), reverseString("areaName") + "_" + n);
  }
  
  private static AssociatedTaxon createAssociatedTaxon(int n) {
    Random rand = new Random();
    int item = rand.nextInt(TaxonRelationType.values().length - 1);
    return new AssociatedTaxon(reverseString("name") + "_" + n, Arrays.asList(TaxonRelationType.values()).get(item));
  }
  
  private static ChronoStratigraphy createChronoStratigraphy(int n) {
    ChronoStratigraphy chronoStratigraphy = new ChronoStratigraphy();
    chronoStratigraphy.setYoungRegionalSubstage(reverseString("youngRegionalSubstage") + "_" + n);
    chronoStratigraphy.setYoungRegionalStage(reverseString("youngRegionalStage") + "_" + n);
    chronoStratigraphy.setYoungDatingQualifier(reverseString("youngDatingQualifier") + "_" + n);
    chronoStratigraphy.setYoungInternSystem(reverseString("youngInternSystem") + "_" + n);
    chronoStratigraphy.setYoungInternSubstage(reverseString("youngInternSubstage") + "_" + n);
    chronoStratigraphy.setYoungInternStage(reverseString("youngInternStage") + "_" + n);
    chronoStratigraphy.setYoungInternSeries(reverseString("youngInternSeries") + "_" + n);
    chronoStratigraphy.setYoungInternErathem(reverseString("youngInternErathem") + "_" + n);
    chronoStratigraphy.setYoungInternEonothem(reverseString("youngInternEonothem") + "_" + n);
    chronoStratigraphy.setYoungChronoName(reverseString("youngChronoName") + "_" + n);
    chronoStratigraphy.setYoungCertainty(reverseString("youngCertainty") + "_" + n);
    if (n == 1)
      chronoStratigraphy.setChronoPreferredFlag(true);
    else
      chronoStratigraphy.setChronoPreferredFlag(false);
    chronoStratigraphy.setOldRegionalSubstage(reverseString("oldRegionalSubstage") + "_" + n);
    chronoStratigraphy.setOldRegionalStage(reverseString("oldRegionalStage") + "_" + n);
    chronoStratigraphy.setOldRegionalSeries(reverseString("oldRegionalSeries") + "_" + n);
    chronoStratigraphy.setOldInternSystem(reverseString("oldInternSystem") + "_" + n);
    chronoStratigraphy.setOldInternSubstage(reverseString("oldInternSubstage") + "_" + n);
    chronoStratigraphy.setOldInternStage(reverseString("oldInternStage") + "_" + n);
    chronoStratigraphy.setOldInternSeries(reverseString("oldInternSeries") + "_" + n);
    chronoStratigraphy.setOldInternErathem(reverseString("oldInternErathem") + "_" + n);
    chronoStratigraphy.setOldInternEonothem(reverseString("oldInternEonothem") + "_" + n);
    chronoStratigraphy.setOldChronoName(reverseString("oldChronoName") + "_" + n);
    chronoStratigraphy.setChronoIdentifier(reverseString("chronoIdentifier") + "_" + n);
    chronoStratigraphy.setOldCertainty(reverseString("oldCertainty") + "_" + n);
    return chronoStratigraphy;
  }
  
  private static BioStratigraphy createBioStratigraphy(int n) {
    BioStratigraphy bioStratigraphy = new BioStratigraphy();
    bioStratigraphy.setYoungBioDatingQualifier(reverseString("youngBioDatingQualifier") + "_" + n);
    bioStratigraphy.setYoungBioName(reverseString("youngBioName") + "_" + n);
    bioStratigraphy.setYoungFossilZone(reverseString("youngFossilZone") + "_" + n);
    bioStratigraphy.setYoungFossilSubZone(reverseString("youngFossilSubZone") + "_" + n);
    bioStratigraphy.setYoungBioCertainty(reverseString("youngBioCertainty") + "_" + n);
    bioStratigraphy.setYoungStratType(reverseString("youngStratType") + "_" + n);
    bioStratigraphy.setBioDatingQualifier(reverseString("bioDatingQualifier") + "_" + n);
    if (n == 1)
      bioStratigraphy.setBioPreferredFlag(true);
    else
      bioStratigraphy.setBioPreferredFlag(false);
    bioStratigraphy.setRangePosition(reverseString("rangePosition") + "_" + n);
    bioStratigraphy.setOldBioName(reverseString("oldBioName") + "_" + n);
    bioStratigraphy.setBioIdentifier(reverseString("bioIdentifier") + "_" + n);
    bioStratigraphy.setOldFossilzone(reverseString("oldFossilzone") + "_" + n);
    bioStratigraphy.setOldFossilSubzone(reverseString("oldFossilSubzone") + "_" + n);
    bioStratigraphy.setOldBioCertainty(reverseString("oldBioCertainty") + "_" + n);
    bioStratigraphy.setOldBioStratType(reverseString("oldBioStratType") + "_" + n);
    return bioStratigraphy;
  }
  
  private static LithoStratigraphy createLithoStratigraphy(int n) {
    LithoStratigraphy lithoStratigraphy = new LithoStratigraphy();
    lithoStratigraphy.setQualifier(reverseString("qualifier") + "_" + n);
    if (n == 1)
      lithoStratigraphy.setPreferredFlag(true);      
    else
      lithoStratigraphy.setPreferredFlag(false);
    lithoStratigraphy.setMember2(reverseString("member2") + "_" + n);
    lithoStratigraphy.setMember(reverseString("member") + "_" + n);
    lithoStratigraphy.setInformalName2(reverseString("informalName2") + "_" + n);
    lithoStratigraphy.setInformalName(reverseString("informalName") + "_" + n);
    lithoStratigraphy.setImportedName2(reverseString("importedName2") + "_" + n);
    lithoStratigraphy.setImportedName1(reverseString("importedName1") + "_" + n);
    lithoStratigraphy.setLithoIdentifier(reverseString("lithoIdentifier") + "_" + n);
    lithoStratigraphy.setFormation2(reverseString("formation2") + "_" + n);
    lithoStratigraphy.setFormationGroup2(reverseString("formationGroup2") + "_" + n);
    lithoStratigraphy.setFormationGroup(reverseString("formationGroup") + "_" + n);
    lithoStratigraphy.setFormation(reverseString("formation") + "_" + n);
    lithoStratigraphy.setCertainty2(reverseString("certainty2") + "_" + n);
    lithoStratigraphy.setCertainty(reverseString("certainty") + "_" + n);
    lithoStratigraphy.setBed2(reverseString("bed2") + "_" + n);
    lithoStratigraphy.setBed(reverseString("bed") + "_" + n);
    return lithoStratigraphy;
  }
  
  private static TaxonomicIdentification generateGeneralIdentificationFields(TaxonomicIdentification identification, int n) {
    identification.setTaxonRank(reverseString("taxonRank") + "_" + n);
    identification.setScientificName(createScientificName(n));
    Random rand = new Random();
    int i = rand.nextInt(SpecimenTypeStatus.values().length);
    identification.setTypeStatus(Arrays.asList(SpecimenTypeStatus.values()).get(i));
    identification.setDateIdentified(OffsetDateTime.now());
    identification.setDefaultClassification(createDefaultClassification());
    identification.setSystemClassification(Arrays.asList(new Monomial[] {createSystemClassification(1), createSystemClassification(2)}));
    identification.setVernacularNames(Arrays.asList(new VernacularName[] {createVernacularName(1), createVernacularName(2)}));
    identification.setIdentificationQualifiers(Arrays.asList(new String[] {reverseString("identificationQualifier") + "_1", reverseString("identificationQualifier") + "_2"}));
    identification.setIdentifiers(Arrays.asList(new Agent[] {createAgent(1), createAgent(2)}));
    identification.setTaxonomicEnrichments(Arrays.asList(new TaxonomicEnrichment[] {createTaxonomicEnrichtment(1), createTaxonomicEnrichtment(2)}));
    return identification;
  }
  
  private static Monomial createSystemClassification(int n) {
    return new Monomial(reverseString("name") + "_" + n, reverseString("rank") + "_" + n);
  }
  
  private static VernacularName createVernacularName(int n) {
    VernacularName vernacularName = new VernacularName();
    vernacularName.setName(reverseString("name") + "_" + n);
    vernacularName.setLanguage(reverseString("language") + "_" + n);
    if (n == 1)
      vernacularName.setPreferred(true);
    else
      vernacularName.setPreferred(false);
    vernacularName.setReferences(Arrays.asList(new Reference[] {createReference(1), createReference(2)}));
    vernacularName.setExperts(Arrays.asList(new Expert[] {createExpert(1), createExpert(2)}));
    return vernacularName;
  }
  
  private static SpecimenIdentification createSpecimenIdentification(int n) {
    SpecimenIdentification specimenIdentification = new SpecimenIdentification();
    generateGeneralIdentificationFields(specimenIdentification, n);
    if (n == 1)
      specimenIdentification.setPreferred(true);
    else
      specimenIdentification.setPreferred(false);
    specimenIdentification.setVerificationStatus(reverseString("verificationStatus") + "_" + n);
    specimenIdentification.setRockType(reverseString("rockType") + "_" + n);
    specimenIdentification.setAssociatedFossilAssemblage(reverseString("associatedFossilAssemblage") + "_" + n);
    specimenIdentification.setRockMineralUsage(reverseString("rockMineralUsage") + "_" + n);
    specimenIdentification.setAssociatedMineralName(reverseString("associatedMineralName") + "_" + n);
    specimenIdentification.setRemarks(reverseString("remarks") + "_" + n);
    return specimenIdentification;
  }
  
  private static MultiMediaContentIdentification createMultiMediaContentIdentification(int n) {
    MultiMediaContentIdentification multiMediaContentIdentification = new MultiMediaContentIdentification();
    return multiMediaContentIdentification;
  }
  
  
  private static ScientificName createScientificName(int n) {
    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName(reverseString("fullScientificName"));
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
    scientificName.setGenusOrMonomial(reverseString("genusOrMonomial") + "_" + n);
    scientificName.setSubgenus(reverseString("Subgenus") + "_" + n);
    scientificName.setSpecificEpithet(reverseString("specificEpithet") + "_" + n);
    scientificName.setInfraspecificEpithet(reverseString("infraspecificEpithet") + "_" + n);
    scientificName.setInfraspecificMarker(reverseString("infraspecificMarker") + "_" + n);
    scientificName.setNameAddendum(reverseString("nameAddendum") + "_" + n);
    scientificName.setAuthorshipVerbatim(reverseString("authorshipVerbatim") + "_" + n);
    scientificName.setAuthor(reverseString("author") + "_" + n);
    scientificName.setYear(reverseString("year") + "_" + n);
    scientificName.setScientificNameGroup(reverseString("scientificNameGroup") + "_" + n);
    scientificName.setReferences(Arrays.asList(new Reference[] {createReference(1), createReference(2)}));
    scientificName.setExperts(Arrays.asList(new Expert[] {createExpert(1), createExpert(2)}));
    return scientificName;
  }
  
  private static DefaultClassification createDefaultClassification() {
    DefaultClassification defaultClassification = new DefaultClassification();
    defaultClassification.setKingdom(reverseString("kingdom"));
    defaultClassification.setPhylum(reverseString("phylum"));
    defaultClassification.setClassName(reverseString("className"));
    defaultClassification.setOrder(reverseString("order"));
    defaultClassification.setSuperFamily(reverseString("superFamily"));
    defaultClassification.setFamily(reverseString("family"));
    defaultClassification.setGenus(reverseString("genus"));
    defaultClassification.setSubgenus(reverseString("subgenus"));
    defaultClassification.setSpecificEpithet(reverseString("specificEpithet"));
    defaultClassification.setInfraspecificEpithet(reverseString("infraspecificEpithet"));
    defaultClassification.setInfraspecificRank(reverseString("infraspecificRank"));
    return defaultClassification;
  }
  
  private static Reference createReference(int n) {
    Reference reference = new Reference();
    reference.setTitleCitation(reverseString("titleCitation") + "_" + n);
    reference.setCitationDetail(reverseString("citationDetail") + "_" + n);
    reference.setUri(reverseString("uri") + "_" + n);
    reference.setAuthor(createPerson(1));
    reference.setPublicationDate(OffsetDateTime.now());
    return reference;
  }
  
  private static TaxonomicEnrichment createTaxonomicEnrichtment(int n) {
    TaxonomicEnrichment enrichment = new TaxonomicEnrichment();
    enrichment.setVernacularNames(Arrays.asList(new SummaryVernacularName[] {createSummaryVernacularName(1), createSummaryVernacularName(2)}));
    enrichment.setSynonyms(Arrays.asList(new SummaryScientificName[] {createSummaryScientificName(1), createSummaryScientificName(2)}));
    enrichment.setSourceSystem(new SummarySourceSystem(reverseString("code") + "_" + n));
    enrichment.setTaxonId(reverseString("taxonId") + "_" + n);
    return enrichment;
  }
  
  private static SummaryVernacularName createSummaryVernacularName(int n) {
    SummaryVernacularName vernacularName = new SummaryVernacularName();
    vernacularName.setName(reverseString("name") + "_" + n);
    vernacularName.setLanguage(reverseString("language") + "_" + n);
    return vernacularName;
  }
  
  private static SummaryScientificName createSummaryScientificName(int n) {
    SummaryScientificName scientificName = new SummaryScientificName();
    scientificName.setFullScientificName(reverseString("fullScientificName") + "_" + n);
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
    scientificName.setGenusOrMonomial(reverseString("genusOrMonomial") + "_" + n);
    scientificName.setSubgenus(reverseString("subgenus") + "_" + n);
    scientificName.setSpecificEpithet(reverseString("specificEpithet") + "_" + n);
    scientificName.setInfraspecificEpithet(reverseString("infraspecificEpithet") + "_" + n);
    scientificName.setAuthorshipVerbatim(reverseString("authorshipVerbatim") + "_" + n);
    return scientificName;
  }
  
  private static ServiceAccessPoint createServiceAccessPoint(int n) throws URISyntaxException {
    ServiceAccessPoint serviceAccessPoint = new ServiceAccessPoint();
    serviceAccessPoint.setAccessUri(new URI("https://en.wikipedia.org/wiki/Uniform_Resource_Identifier"));
    serviceAccessPoint.setFormat(reverseString("format") + "_" + n);
    Random rand = new Random();
    int item = rand.nextInt(ServiceAccessPoint.Variant.values().length - 1);    
    serviceAccessPoint.setVariant(Arrays.asList(ServiceAccessPoint.Variant.values()).get(item));
    return serviceAccessPoint;
  }
  
  public static String reverseString(String str) {
    Objects.requireNonNull(str, "str cannot be null");
    String reverse = "";
    for (int i = str.length() - 1; i >= 0; i--) {
      reverse += str.charAt(i);
    }
    return reverse;
  }

}
