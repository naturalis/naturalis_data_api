package nl.naturalis.nba.etl.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.LicenseType;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.ServiceAccessPoint.Variant;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.ResourceType;

public class DataMockUtil {

  public static MultiMediaObject generateMultiMediaMockObj() throws URISyntaxException {

    MultiMediaObject multiMediaObject = new MultiMediaObject();
    multiMediaObject.setId("L.1911711_2107143681@BRAHMS");
    multiMediaObject.setSourceInstitutionID("Naturalis Biodiversity Center");
    multiMediaObject.setSourceID("Brahms");
    multiMediaObject.setOwner("Naturalis Biodiversity Center");
    multiMediaObject.setLicenseType(LicenseType.parse("Copyright"));
    multiMediaObject.setLicense(License.parse("CC0"));
    multiMediaObject.setUnitID("L.1911711_2107143681");
    multiMediaObject.setCollectionType("Botany");
    multiMediaObject.setTitle("RMNH.AVES");
    multiMediaObject.setCaption("MMO Test Object");
    multiMediaObject.setDescription("MMO Test Object");
    SourceSystem sourceSystem = SourceSystem.BRAHMS;
    multiMediaObject.setSourceSystem(sourceSystem);

    URI uri = new URI("http://medialib.naturalis.nl/file/id/L.1911711/format/large");
    Variant variant = Variant.MEDIUM_QUALITY;

    ServiceAccessPoint serviceAccessPoint = new ServiceAccessPoint();
    serviceAccessPoint.setAccessUri(uri);
    serviceAccessPoint.setFormat("image/jpeg");
    serviceAccessPoint.setVariant(variant);
    List<ServiceAccessPoint> serviceAccessPoints = new ArrayList<>();
    serviceAccessPoints.add(serviceAccessPoint);

    multiMediaObject.setServiceAccessPoints(serviceAccessPoints);

    ResourceType type = ResourceType.SOUND;

    multiMediaObject.setType(type);
    multiMediaObject.setTaxonCount(3);
    multiMediaObject.setCreator("Naturalis Biodiversity Center");
    multiMediaObject.setCopyrightText("Copyright..");
    multiMediaObject.setAssociatedSpecimenReference("L.1911711@BRAHMS");
    multiMediaObject.setAssociatedTaxonReference("");
    multiMediaObject.setMultiMediaPublic(true);

    List<String> subParts = new ArrayList<>();
    subParts.add("");
    subParts.add("");

    List<String> subjectOrientations = new ArrayList<>();
    subjectOrientations.add("Or1");
    subjectOrientations.add("Or2");

    List<String> phasesOrStages = new ArrayList<>();
    phasesOrStages.add("embryo");
    phasesOrStages.add("pupa");

    List<String> sexes = new ArrayList<>();
    sexes.add("male");
    sexes.add("female");

    Organization org = new Organization();
    org.setName("Naturalis");

    List<Organization> gatheringOrganizations = new ArrayList<>();
    gatheringOrganizations.add(org);

    OffsetDateTime time = OffsetDateTime.parse("2011-12-03T10:15:30Z");

    Person person = new Person();
    person.setFullName("Wiesbaur, SJ");
    person.setOrganization(org);
    person.setAgentText("test");

    List<Person> gatheringPersons = new ArrayList<>();
    gatheringPersons.add(person);

    GatheringSiteCoordinates siteCoordinates = new GatheringSiteCoordinates();
    siteCoordinates.setLongitudeDecimal(5.016667);
    siteCoordinates.setLatitudeDecimal(51.433333);

    List<GatheringSiteCoordinates> coordinates = new ArrayList<>();
    coordinates.add(siteCoordinates);

    MultiMediaGatheringEvent gatheringEvent = new MultiMediaGatheringEvent();
    gatheringEvent.setDateTimeBegin(time);
    gatheringEvent.setGatheringOrganizations(gatheringOrganizations);
    gatheringEvent.setGatheringPersons(gatheringPersons);
    gatheringEvent.setLocality("Kalksburg bei Wien.");
    gatheringEvent.setLocalityText("Unknown; Kalksburg bei Wien.");
    gatheringEvent.setSiteCoordinates(coordinates);

    List<MultiMediaGatheringEvent> gatheringEvents = new ArrayList<>();
    gatheringEvents.add(gatheringEvent);

    DefaultClassification classification = new DefaultClassification();
    classification.setKingdom("Plantae");
    classification.setClassName("Magnoliopsidae");
    classification.setOrder("Rosales");
    classification.setFamily("Rosaceae");
    classification.setGenus("Rosa");
    classification.setSpecificEpithet("canina");
    classification.setInfraspecificEpithet("calophylla");

    List<Monomial> systemClassification = new ArrayList<>();
    systemClassification.add(new Monomial("kingdom", "Plantae"));
    systemClassification.add(new Monomial("Magnoliopsidae"));;
    systemClassification.add(new Monomial("order", "Rosales"));
    systemClassification.add(new Monomial("family", "Rosaceae"));
    systemClassification.add(new Monomial("genus", "Rosa"));

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Rosa canina L. f. calophylla");
    scientificName.setGenusOrMonomial("Rosa");
    scientificName.setSpecificEpithet("canina");
    scientificName.setInfraspecificEpithet("calophylla");
    scientificName.setScientificNameGroup("rosa canina calophylla");
    scientificName.setInfraspecificMarker("f.");

    MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
    identification.setDefaultClassification(classification);
    identification.setSystemClassification(systemClassification);
    identification.setScientificName(scientificName);

    List<MultiMediaContentIdentification> identifications = new ArrayList<>();
    identifications.add(identification);

    multiMediaObject.setSubjectParts(subjectOrientations);
    multiMediaObject.setSubjectOrientations(subjectOrientations);
    multiMediaObject.setPhasesOrStages(phasesOrStages);
    multiMediaObject.setSexes(sexes);
    multiMediaObject.setGatheringEvents(gatheringEvents);
    multiMediaObject.setIdentifications(identifications);

    return multiMediaObject;
  }

  public static Specimen generateSpecimenMockObject() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Rosa canina L. f. calophylla");
    scientificName.setGenusOrMonomial("Rosa");
    scientificName.setSpecificEpithet("canina");
    scientificName.setInfraspecificEpithet("calophylla");
    scientificName.setScientificNameGroup("rosa canina calophylla");
    scientificName.setInfraspecificMarker("f.");

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

    SourceSystem sourceSystem = SourceSystem.getInstance("BRAHMS", "Naturalis - Botany catalogues");
    Sex sex = Sex.MIXED;

    PhaseOrStage phaseOrStage = PhaseOrStage.PUPA;

    OffsetDateTime time = OffsetDateTime.parse("2011-12-03T10:15:30Z");

    Organization org = new Organization();
    org.setName("Naturalis");

    List<Organization> gatheringOrganizations = new ArrayList<>();
    gatheringOrganizations.add(org);

    Person person = new Person();
    person.setFullName("Wiesbaur, SJ");
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
    gatheringEvent.setLocality("Kalksburg bei Wien.");
    gatheringEvent.setLocalityText("Unknown; Kalksburg bei Wien.");
    gatheringEvent.setSiteCoordinates(coordinates);

    Specimen specimen = new Specimen();
    specimen.addIndentification(identification);
    specimen.setSourceSystemId("L.1911711");
    specimen.setId("L.1911711@BRAHMS");
    specimen.setUnitID("L.1911711");
    specimen.setCollectorsFieldNumber("Wiesbaur, SJ  s.n. ");
    specimen.setAssemblageID("2983117@BRAHMS");
    specimen.setCollectionType("Botany");
    specimen.setSex(sex);
    specimen.setPhaseOrStage(phaseOrStage);
    specimen.setSourceSystem(sourceSystem);
    specimen.setLicense(License.parse("CC0"));
    specimen.setSourceInstitutionID("Naturalis Biodiversity Center");
    specimen.setMultiMediaPublic(false);
    specimen.setObjectPublic(false);
    specimen.setLicenseType(LicenseType.parse("Copyright"));
    specimen.setUnitGUID("http://data.biodiversitydata.nl/naturalis/specimen/L.1911711");
    specimen.setGatheringEvent(gatheringEvent);

    return specimen;

  }

}
