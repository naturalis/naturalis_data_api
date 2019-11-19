package nl.naturalis.nba.dao.mock;

import static nl.naturalis.nba.api.model.Sex.FEMALE;
import static nl.naturalis.nba.api.model.Sex.MALE;
import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.api.model.SourceSystem.XC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

  /**
   * Generates specimen objects used for testing the Aggregation Services
   */
  public class AggregationSpecimensMock {

    public static Specimen specimen01()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("ZMA.MAM.100");
      specimen.setSourceSystem(CRS);
      specimen.setCollectionType("Mollusca");
      String[] collections = new String[] { "Weekdieren" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("adult");
      specimen.setSex(MALE);
      specimen.setRecordBasis("PreservedSpecimen");
      specimen.setNumberOfSpecimen(0);

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Animalia");
      classification.setPhylum("Mollusca");
      classification.setClassName("Gastropoda");
      classification.setOrder("Littorinimorpha");
      classification.setFamily("Naticidae");
      classification.setGenus("Tectonatica");
      classification.setSpecificEpithet("suffusa");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Animalia"));
      systemClassification.add(new Monomial("phylum", "Mollusca"));
      systemClassification.add(new Monomial("class", "Gastropoda"));
      systemClassification.add(new Monomial("order", "Littorinimorpha"));
      systemClassification.add(new Monomial("superfamily", "Naticoidea"));
      systemClassification.add(new Monomial("family", "Naticidae"));
      systemClassification.add(new Monomial("genus", "Tectonatica"));
      systemClassification.add(new Monomial("species", "suffusa"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Tectonatica suffusa (Reeve, 1855)");
      scientificName.setGenusOrMonomial("Tectonatica");
      scientificName.setSpecificEpithet("suffusa");
      scientificName.setAuthorshipVerbatim("Reeve, 1855");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

      specimen.setIdentifications(Arrays.asList(identification));

      return specimen;
    }

    public static Specimen specimen02()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("ZMA.MAM.101");
      specimen.setSourceSystem(CRS);
      specimen.setCollectionType("Mollusca");
      String[] collections = new String[] { "Weekdieren" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("juvenile");
      specimen.setSex(MALE);
      specimen.setRecordBasis("PreservedSpecimen");
      specimen.setNumberOfSpecimen(1);

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Animalia");
      classification.setPhylum("Mollusca");
      classification.setClassName("Bivalvia");
      classification.setOrder("Mytilida");
      classification.setFamily("Mytilidae");
      classification.setGenus("Musculus");
      classification.setSpecificEpithet("koreanus");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Animalia"));
      systemClassification.add(new Monomial("phylum", "Mollusca"));
      systemClassification.add(new Monomial("class", "Bivalvia"));
      systemClassification.add(new Monomial("order", "Mytilida"));
      systemClassification.add(new Monomial("superfamily", "Mytiloidea"));
      systemClassification.add(new Monomial("family", "Mytilidae"));
      systemClassification.add(new Monomial("genus", "Musculus"));
      systemClassification.add(new Monomial("species", "koreanus"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Musculus koreanus Ockelmann, 1983");
      scientificName.setGenusOrMonomial("Musculus");
      scientificName.setSpecificEpithet("koreanus");
      scientificName.setAuthorshipVerbatim("Ockelmann, 1983");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

      specimen.setIdentifications(Arrays.asList(identification));

      return specimen;
    }

    public static Specimen specimen03()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("ZMA.MAM.102");
      specimen.setSourceSystem(CRS);
      specimen.setCollectionType("Mollusca");
      String[] collections = new String[] { "Weekdieren" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("egg");
      specimen.setRecordBasis("PreservedSpecimen");
      specimen.setNumberOfSpecimen(4);

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Animalia");
      classification.setPhylum("Mollusca");
      classification.setClassName("Gastropoda"); //("Cephalopoda");
      classification.setOrder("Ammonoidea");
      classification.setFamily("Ussuritidae");
      classification.setGenus("Ussurites");
      classification.setSpecificEpithet("mansfeldi");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Animalia"));
      systemClassification.add(new Monomial("phylum", "Mollusca"));
      systemClassification.add(new Monomial("class", "Cephalopoda"));
      systemClassification.add(new Monomial("order", "Ammonoidea"));
      systemClassification.add(new Monomial("superfamily", "Phyllocerataceae"));
      systemClassification.add(new Monomial("family", "Ussuritidae"));
      systemClassification.add(new Monomial("genus", "Ussurites"));
      systemClassification.add(new Monomial("species", "mansfeldi"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Ussurites mansfeldi Kummel 1969");
      scientificName.setGenusOrMonomial("Ussurites");
      scientificName.setSpecificEpithet("mansfeldi");
      scientificName.setAuthorshipVerbatim("Kummel 1969");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

      specimen.setIdentifications(Arrays.asList(identification));

      return specimen;
    }

    public static Specimen specimen04()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("RMNH.100");
      specimen.setSourceSystem(CRS);
      specimen.setCollectionType("Hymenoptera");
      specimen.setTheme(Arrays.asList("Bijen en Hommels"));
      specimen.setPhaseOrStage("adult");
      specimen.setSex(FEMALE);
      specimen.setRecordBasis("FossilSpecimen");

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setProjectTitle("Project T. Rex");
//      gathering.setLocalityText("Montana, U.S.A.");
//      gathering.setCountry("United States");
//      gathering.setDateTimeBegin(new ESDateInput("2007-04-03 13:04").parse());
//      gathering.setGatheringPersons(Arrays.asList(edwinVanHuis()));
//
//      GatheringSiteCoordinates coords;
//      coords = new GatheringSiteCoordinates(46.5884, 112.0245);
//      gathering.setSiteCoordinates(Arrays.asList(coords));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Animalia");
      classification.setPhylum("Arthropoda");
      classification.setClassName("Insecta");
      classification.setOrder("Hymenoptera");
      classification.setFamily("Apidae");
      classification.setGenus("Bombus");
      classification.setSpecificEpithet("terrestris");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Animalia"));
      systemClassification.add(new Monomial("phylum", "Arthropoda"));
      systemClassification.add(new Monomial("clade", "Dinosauria"));
      systemClassification.add(new Monomial("order", "Hymenoptera"));
      systemClassification.add(new Monomial("suborder", "Theropoda"));
      systemClassification.add(new Monomial("superfamily", "Apoidea"));
      systemClassification.add(new Monomial("family", "Apidae"));
      systemClassification.add(new Monomial("genus", "Bombus"));
      systemClassification.add(new Monomial("species", "terrestris"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Bombus terrestris (Linnaeus, 1758)");
      scientificName.setGenusOrMonomial("Bombus");
      scientificName.setSpecificEpithet("terrestris");
      scientificName.setAuthorshipVerbatim("Linnaeus, 1758");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));
      return specimen;
    }

    public static Specimen specimen05()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("RGM.853698");
      specimen.setSourceSystem(CRS);
      specimen.setCollectionType("Mineralogy");
      specimen.setRecordBasis("OtherSpecimen");

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setLocalityText("Dorchester, U.K.");
//      gathering.setCountry("United Kingdom");
//      gathering.setDateTimeBegin(null);
//      gathering.setGatheringPersons(Arrays.asList(nathanielWallich()));
//      specimen.setGatheringEvent(gathering);

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Calciet");
      scientificName.setGenusOrMonomial("Calciet");
     
      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("genus");
      identification.setScientificName(scientificName);

      specimen.setIdentifications(Arrays.asList(identification));
      return specimen;
    }

    public static Specimen specimen06()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("RGM.853699");
      specimen.setSourceSystem(CRS);
      specimen.setCollectionType("Mineralogy");
      specimen.setRecordBasis("OtherSpecimen");

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setLocalityText("Dorchester, U.K.");
//      gathering.setCountry("United Kingdom");
//      gathering.setDateTimeBegin(null);
//      gathering.setGatheringPersons(Arrays.asList(nathanielWallich()));
//      specimen.setGatheringEvent(gathering);
      
      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Magnetite");
      scientificName.setGenusOrMonomial("Magnetite");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("genus");
      identification.setScientificName(scientificName);

//    DefaultClassification classification = new DefaultClassification();
//    classification.setClassName("Gastropoda");
//    identification.setDefaultClassification(classification);
      
      specimen.setIdentifications(Arrays.asList(identification));
      
      return specimen;
    }

    
    public static Specimen specimen07()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("L.100");
      specimen.setSourceSystem(BRAHMS);
      specimen.setCollectionType("Botany");
      String[] collections = new String[] { "Plants" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("adult");
      specimen.setSex(MALE);
      specimen.setRecordBasis("Herbarium sheet");
      specimen.setNumberOfSpecimen(0);

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setProjectTitle("Vogels der Lage Landen");
//      gathering.setLocalityText("Duinen, nabij Uitgeest");
//      gathering.setCountry("Netherlands");
//      gathering.setCity("Uitgeest");
//      gathering.setDateTimeBegin(new ESDateInput("2010-04-03 13:04").parse());
//      gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));
//
//      GatheringSiteCoordinates uitgeestCoordinates;
//      uitgeestCoordinates = new GatheringSiteCoordinates(52.531713, 4.705922);
//      gathering.setSiteCoordinates(Arrays.asList(uitgeestCoordinates));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Plantae");
      classification.setPhylum("Tracheophyta");
      classification.setClassName("Magnoliopsida");
      classification.setOrder("Asterales");
      classification.setFamily("Compositae");
      classification.setGenus("Taraxacum");
      classification.setSpecificEpithet("tortilobum");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Plantae"));
      systemClassification.add(new Monomial("phylum", "Tracheophyta"));
      systemClassification.add(new Monomial("class", "Magnoliopsida"));
      systemClassification.add(new Monomial("order", "Asterales"));
      systemClassification.add(new Monomial("family", "Compositae"));
      systemClassification.add(new Monomial("genus", "Taraxacum"));
      systemClassification.add(new Monomial("species", "tortilobum"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Taraxacum tortilobum Florström");
      scientificName.setGenusOrMonomial("Taraxacum");
      scientificName.setSpecificEpithet("tortilobum");
      scientificName.setAuthorshipVerbatim("Florström");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("genus");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));

      return specimen;
    }

    public static Specimen specimen08()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("L.101");
      specimen.setSourceSystem(BRAHMS);
      specimen.setCollectionType("Botany");
      String[] collections = new String[] { "Plants" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("juvenile");
      specimen.setSex(MALE);
      specimen.setRecordBasis("Herbarium sheet");
      specimen.setNumberOfSpecimen(1);

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setProjectTitle("Vogels der Lage Landen");
//      gathering.setLocalityText("In de bossen nabij Aalten");
//      gathering.setCountry("Netherlands");
//      gathering.setCity("Aalten");
//      gathering.setDateTimeBegin(new ESDateInput("2009-04-03 13:04").parse());
//      gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));
//
//      GatheringSiteCoordinates aaltenCoordinates;
//      aaltenCoordinates = new GatheringSiteCoordinates(51.9266666, 6.5806785);
//      gathering.setSiteCoordinates(Arrays.asList(aaltenCoordinates));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Plantae");
      classification.setPhylum("Tracheophyta");
      classification.setClassName("Magnoliopsida");
      classification.setOrder("Asterales");
      classification.setFamily("Compositae");
      classification.setGenus("Taraxacum");
      classification.setSpecificEpithet("lacistophyllum");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Plantae"));
      systemClassification.add(new Monomial("phylum", "Tracheophyta"));
      systemClassification.add(new Monomial("class", "Magnoliopsida"));
      systemClassification.add(new Monomial("order", "Asterales"));
      systemClassification.add(new Monomial("family", "Compositae"));
      systemClassification.add(new Monomial("genus", "Taraxacum"));
      systemClassification.add(new Monomial("species", "lacistophyllum"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Taraxacum lacistophyllum (Dahlst.) Raunk.");
      scientificName.setGenusOrMonomial("Taraxacum");
      scientificName.setSpecificEpithet("lacistophyllum");
      scientificName.setAuthorshipVerbatim("(Dahlst.) Raunk.");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("genus");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));

      return specimen;
    }

    public static Specimen specimen09()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("L.102");
      specimen.setSourceSystem(BRAHMS);
      specimen.setCollectionType("Botany");
      String[] collections = new String[] { "Plants" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("egg");
      specimen.setRecordBasis("PreservedSpecimen");
      specimen.setNumberOfSpecimen(4);

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setCountry("Netherlands");
//      gathering.setCity("Uitgeest");
//      gathering.setDateTimeBegin(new ESDateInput("2008-04-03 13:04").parse());
//      gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg(), vonSiebold()));
//
//      GatheringSiteCoordinates uitgeestCoordinates;
//      uitgeestCoordinates = new GatheringSiteCoordinates(52.531713, 4.705922);
//      gathering.setSiteCoordinates(Arrays.asList(uitgeestCoordinates));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Plantae");
      classification.setClassName("Magnoliopsida");
      classification.setOrder("Asterales");
      classification.setFamily("Compositae");
      classification.setGenus("Taraxacum");
      classification.setSpecificEpithet("officinale");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Plantae"));
      systemClassification.add(new Monomial("phylum", "Tracheophyta"));
      systemClassification.add(new Monomial("class", "Magnoliopsida"));
      systemClassification.add(new Monomial("order", "Asterales"));
      systemClassification.add(new Monomial("family", "Compositae"));
      systemClassification.add(new Monomial("genus", "Taraxacum"));
      systemClassification.add(new Monomial("species", "officinale"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Taraxacum officinale F.H.Wigg.");
      scientificName.setGenusOrMonomial("Taraxacum");
      scientificName.setSpecificEpithet("officinale");
      scientificName.setAuthorshipVerbatim("F.H.Wigg.");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("genus");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));

      return specimen;
    }

    public static Specimen specimen10()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("L.103");
      specimen.setSourceSystem(BRAHMS);
      specimen.setCollectionType("Botany");
      specimen.setTheme(Arrays.asList("Living Dinos"));
      specimen.setPhaseOrStage("adult");
      specimen.setSex(FEMALE);
      specimen.setRecordBasis("PreservedSpecimen");

//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setProjectTitle("Project T. Rex");
//      gathering.setLocalityText("Montana, U.S.A.");
//      gathering.setCountry("United States");
//      gathering.setDateTimeBegin(new ESDateInput("2007-04-03 13:04").parse());
//      gathering.setGatheringPersons(Arrays.asList(edwinVanHuis()));
//
//      GatheringSiteCoordinates coords;
//      coords = new GatheringSiteCoordinates(46.5884, 112.0245);
//      gathering.setSiteCoordinates(Arrays.asList(coords));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Plantae");
      classification.setClassName("Magnoliopsida");
      classification.setOrder("Asterales");
      classification.setFamily("Compositae");
      classification.setGenus("Filago");
      classification.setSpecificEpithet("lutescens");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Plantae"));
      systemClassification.add(new Monomial("phylum", "Tracheophyta"));
      systemClassification.add(new Monomial("class", "Magnoliopsida"));
      systemClassification.add(new Monomial("order", "Asterales"));
      systemClassification.add(new Monomial("family", "Compositae"));
      systemClassification.add(new Monomial("genus", "Taraxacum"));
      systemClassification.add(new Monomial("species", "lutescens"));
      systemClassification.add(new Monomial("subspecies", "lutescens"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Filago lutescens lutescens");
      scientificName.setGenusOrMonomial("Filago");
      scientificName.setSpecificEpithet("lutescens");
      scientificName.setInfraspecificEpithet("lutescens");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));
      return specimen;
    }

    public static Specimen specimen11()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("L   100");
      specimen.setSourceSystem(BRAHMS);
      specimen.setCollectionType("Botany");
      String[] collections = new String[] { "Strange Plants" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("adult");
      specimen.setSex(FEMALE);
      specimen.setRecordBasis("Wood sample");
      
//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setLocalityText("Dorchester, U.K.");
//      gathering.setCountry("United Kingdom");
//      gathering.setDateTimeBegin(null);
//      gathering.setGatheringPersons(Arrays.asList(nathanielWallich()));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Plantae");
      classification.setClassName("Magnoliopsida");
      classification.setOrder("Asterales");
      classification.setFamily("Compositae");
      classification.setGenus("Stevia");
      classification.setSpecificEpithet("lucida");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Plantae"));
      systemClassification.add(new Monomial("phylum", "Tracheophyta"));
      systemClassification.add(new Monomial("class", "Magnoliopsida"));
      systemClassification.add(new Monomial("order", "Asterales"));
      systemClassification.add(new Monomial("family", "Compositae"));
      systemClassification.add(new Monomial("genus", "Stevia"));
      systemClassification.add(new Monomial("species", "lucida"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Stevia lucida Lag.");
      scientificName.setGenusOrMonomial("Stevia");
      scientificName.setSpecificEpithet("lucida");
      scientificName.setAuthorshipVerbatim("Lag.");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));
      return specimen;
    }

    public static Specimen specimen12()
    {
      Specimen specimen = new Specimen();
      specimen.setUnitID("L.123456");
      specimen.setSourceSystem(BRAHMS);
      specimen.setCollectionType("Botany");
      String[] collections = new String[] { "Strange Plants" };
      specimen.setTheme(Arrays.asList(collections));
      specimen.setPhaseOrStage("adult");
      specimen.setSex(FEMALE);
      specimen.setRecordBasis("Wood sample");
      
//      GatheringEvent gathering = new GatheringEvent();
//      gathering.setLocalityText("Dorchester, U.K.");
//      gathering.setCountry("United Kingdom");
//      gathering.setDateTimeBegin(null);
//      gathering.setGatheringPersons(Arrays.asList(nathanielWallich()));

      DefaultClassification classification = new DefaultClassification();
      classification.setKingdom("Plantae");
      classification.setClassName("Magnoliopsida");
      classification.setOrder("Asterales");
      classification.setFamily("Compositae");
      classification.setGenus("Stevia");
      classification.setSpecificEpithet("trifida");

      List<Monomial> systemClassification = new ArrayList<>();
      systemClassification.add(new Monomial("kingdom", "Plantae"));
      systemClassification.add(new Monomial("phylum", "Tracheophyta"));
      systemClassification.add(new Monomial("class", "Magnoliopsida"));
      systemClassification.add(new Monomial("order", "Asterales"));
      systemClassification.add(new Monomial("family", "Compositae"));
      systemClassification.add(new Monomial("genus", "Stevia"));
      systemClassification.add(new Monomial("species", "trifida"));

      ScientificName scientificName = new ScientificName();
      scientificName.setFullScientificName("Stevia trifida Lag.");
      scientificName.setGenusOrMonomial("Stevia");
      scientificName.setSpecificEpithet("trifida");
      scientificName.setAuthorshipVerbatim("Lag.");

      SpecimenIdentification identification = new SpecimenIdentification();
      identification.setTaxonRank("species");
      identification.setDefaultClassification(classification);
      identification.setSystemClassification(systemClassification);
      identification.setScientificName(scientificName);

//      specimen.setGatheringEvent(gathering);
      specimen.setIdentifications(Arrays.asList(identification));
      return specimen;
    }

    
    public static Person vonSiebold()
    {
      Person person = new Person("Philipp Franz von Siebold");
      person.setAgentText("Philipp Franz von Siebold werkte als "
          + "arts in Japan en was, samen met Heinrich Bürger "
          + "de enige die tussen 1823 en 1829 verzamelde in "
          + "het land. De collectie die Von Siebold op zijn "
          + "twee reizen naar Japan bijeenbracht, omvat "
          + "tienduizenden objecten. De voorwerpen zijn "
          + "verspreid over Nederland en Duitsland maar er "
          + "zijn ook voorwerpen in Rusland, Japan en Engeland "
          + "terecht gekomen. Het grootste deel van de objecten "
          + "van de eerste reis naar Japan bevindt zich in Leiden. "
          + "De etnografische objecten staan in het Rijksmuseum "
          + "voor Volkenkunde en de mineralen, botanische en "
          + "zoölogische voorwerpen zijn in Naturalis te vinden. "
          + "Deze unieke verzameling geeft een beeld van de flora "
          + "en fauna in het Japan van de negentiende eeuw maar "
          + "ook van de toewijding van Von Siebold aan zijn "
          + "onderzoek en zijn enorme verzameldrang.");
      return person;
    }

  }
