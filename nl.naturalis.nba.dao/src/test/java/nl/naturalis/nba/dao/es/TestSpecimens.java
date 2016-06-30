package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.api.model.PhaseOrStage.ADULT;
import static nl.naturalis.nba.api.model.PhaseOrStage.EGG;
import static nl.naturalis.nba.api.model.PhaseOrStage.JUVENILE;
import static nl.naturalis.nba.api.model.Sex.FEMALE;
import static nl.naturalis.nba.api.model.Sex.MALE;
import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.api.model.SourceSystem.NDFF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.es.types.ESGatheringEvent;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

/**
 * Generates 5 Specimen objects used for testing the SpecimenDAO.
 * 
 * @author Ayco Holleman
 *
 */
class TestSpecimens {

	static ESSpecimen parusMajorSpecimen01()
	{
		ESSpecimen specimen = new ESSpecimen();
		specimen.setUnitID("ZMA.MAM.100");
		specimen.setSourceSystem(CRS);
		String[] collections = new String[] { "Altenburg", "Living Dinos" };
		specimen.setTheme(Arrays.asList(collections));
		specimen.setPhaseOrStage(ADULT);
		specimen.setSex(MALE);
		specimen.setRecordBasis("Preserved specimen");
		ESGatheringEvent gathering = new ESGatheringEvent();
		gathering.setProjectTitle("Vogels der Lage Landen");
		gathering.setLocalityText("Duinen, nabij Uitgeest");
		gathering.setCountry("Netherlands");
		gathering.setCity("Uitgeest");
		gathering.setDateTimeBegin(datetime("2010/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));

		DefaultClassification classification = new DefaultClassification();
		classification.setKingdom("Animalia");
		classification.setPhylum("Chordata");
		classification.setClassName("Aves");
		classification.setOrder("Passeriformes");
		classification.setFamily("Paridae");
		classification.setGenus("Parus");
		classification.setSpecificEpithet("major");

		List<Monomial> systemClassification = new ArrayList<>();
		systemClassification.add(new Monomial("kingdom", "Animalia"));
		systemClassification.add(new Monomial("phylum", "Chordata"));
		systemClassification.add(new Monomial("class", "Aves"));
		systemClassification.add(new Monomial("order", "Passeriformes"));
		systemClassification.add(new Monomial("family", "Paridae"));
		systemClassification.add(new Monomial("genus", "Parus"));
		systemClassification.add(new Monomial("species", "major"));

		ScientificName scientificName = new ScientificName();
		scientificName.setFullScientificName("Parus major");
		scientificName.setAuthorshipVerbatim("Linnaeus, 1752");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));

		return specimen;
	}

	static ESSpecimen larusFuscusSpecimen01()
	{
		ESSpecimen specimen = new ESSpecimen();
		specimen.setUnitID("ZMA.MAM.101");
		specimen.setSourceSystem(CRS);
		String[] collections = new String[] { "Altenburg", "Living Dinos" };
		specimen.setTheme(Arrays.asList(collections));
		specimen.setPhaseOrStage(JUVENILE);
		specimen.setSex(MALE);
		specimen.setRecordBasis("Preserved specimen");
		ESGatheringEvent gathering = new ESGatheringEvent();
		gathering.setProjectTitle("Vogels der Lage Landen");
		gathering.setLocalityText("In de duinen, nabij Uitgeest");
		gathering.setCountry("Netherlands");
		gathering.setCity("Uitgeest");
		gathering.setDateTimeBegin(datetime("2009/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));

		DefaultClassification classification = new DefaultClassification();
		classification.setKingdom("Animalia");
		classification.setPhylum("Chordata");
		classification.setClassName("Aves");
		classification.setOrder("Charadriiformes");
		classification.setFamily("Laridae");
		classification.setGenus("Larus");
		classification.setSpecificEpithet("fuscus");
		classification.setInfraspecificEpithet("fuscus");

		List<Monomial> systemClassification = new ArrayList<>();
		systemClassification.add(new Monomial("kingdom", "Animalia"));
		systemClassification.add(new Monomial("phylum", "Chordata"));
		systemClassification.add(new Monomial("class", "Aves"));
		systemClassification.add(new Monomial("order", "Charadriiformes"));
		systemClassification.add(new Monomial("family", "Laridae"));
		systemClassification.add(new Monomial("genus", "Larus"));
		systemClassification.add(new Monomial("species", "fuscus"));
		systemClassification.add(new Monomial("subspecies", "fuscus"));

		ScientificName scientificName = new ScientificName();
		scientificName.setFullScientificName("Larus f. fuscus");
		scientificName.setAuthorshipVerbatim("Linnaeus, 1752");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));

		return specimen;
	}

	static ESSpecimen larusFuscusSpecimen02()
	{
		ESSpecimen specimen = new ESSpecimen();
		specimen.setUnitID("309801857");
		specimen.setSourceSystem(NDFF);
		specimen.setPhaseOrStage(EGG);
		ESGatheringEvent gathering = new ESGatheringEvent();
		gathering.setCountry("Netherlands");
		gathering.setCity("Hiversum");
		gathering.setDateTimeBegin(datetime("2008/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));

		DefaultClassification classification = new DefaultClassification();
		classification.setKingdom("Animalia");
		classification.setPhylum("Chordata");
		classification.setClassName("Aves");
		classification.setOrder("Charadriiformes");
		classification.setFamily("Laridae");
		classification.setGenus("Larus");
		classification.setSpecificEpithet("fuscus");
		classification.setInfraspecificEpithet("fuscus");

		List<Monomial> systemClassification = new ArrayList<>();
		systemClassification.add(new Monomial("kingdom", "Animalia"));
		systemClassification.add(new Monomial("phylum", "Chordata"));
		systemClassification.add(new Monomial("class", "Aves"));
		systemClassification.add(new Monomial("order", "Charadriiformes"));
		systemClassification.add(new Monomial("family", "Laridae"));
		systemClassification.add(new Monomial("genus", "Larus"));
		systemClassification.add(new Monomial("species", "fuscus"));
		systemClassification.add(new Monomial("subspecies", "fuscus"));

		ScientificName scientificName = new ScientificName();
		scientificName.setFullScientificName("Larus f. fuscus");
		scientificName.setAuthorshipVerbatim("Linnaeus, 1752");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));

		return specimen;
	}

	static ESSpecimen tRexSpecimen01()
	{
		ESSpecimen specimen = new ESSpecimen();
		specimen.setUnitID("RMNH.100");
		specimen.setSourceSystem(CRS);
		specimen.setTheme(Arrays.asList("Living Dinos"));
		specimen.setPhaseOrStage(ADULT);
		specimen.setSex(FEMALE);
		specimen.setRecordBasis("Preserved specimen");
		ESGatheringEvent gathering = new ESGatheringEvent();
		gathering.setProjectTitle("Project T. Rex");
		gathering.setLocalityText("Montana, U.S.A.");
		gathering.setCountry("United States");
		gathering.setDateTimeBegin(datetime("2007/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(edwinVanHuis()));

		DefaultClassification classification = new DefaultClassification();
		classification.setKingdom("Animalia");
		classification.setPhylum("Chordata");
		classification.setOrder("Saurischia");
		classification.setFamily("Tyrannosauridae");
		classification.setGenus("Tyrannosaurus");
		classification.setSpecificEpithet("rex");

		List<Monomial> systemClassification = new ArrayList<>();
		systemClassification.add(new Monomial("kingdom", "Animalia"));
		systemClassification.add(new Monomial("phylum", "Chordata"));
		systemClassification.add(new Monomial("clade", "Dinosauria"));
		systemClassification.add(new Monomial("order", "Saurischia"));
		systemClassification.add(new Monomial("suborder", "Theropoda"));
		systemClassification.add(new Monomial("family", "Tyrannosauridae"));
		systemClassification.add(new Monomial("subfamily", "Tyrannosaurinae"));
		systemClassification.add(new Monomial("genus", "Tyrannosaurus"));
		systemClassification.add(new Monomial("species", "rex"));

		ScientificName scientificName = new ScientificName();
		scientificName.setFullScientificName("Tyrannosaurus rex");
		scientificName.setAuthorshipVerbatim("Osborn, 1905");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));
		return specimen;
	}

	static ESSpecimen malusSylvestrisSpecimen01()
	{
		ESSpecimen specimen = new ESSpecimen();
		specimen.setUnitID("L   100");
		specimen.setSourceSystem(BRAHMS);
		String[] collections = new String[] { "Strange Plants" };
		specimen.setTheme(Arrays.asList(collections));
		specimen.setPhaseOrStage(ADULT);
		specimen.setSex(FEMALE);
		specimen.setRecordBasis("Herbarium sheet");
		ESGatheringEvent gathering = new ESGatheringEvent();
		gathering.setLocalityText("Dorchester, U.K.");
		gathering.setCountry("United Kingdom");
		gathering.setDateTimeBegin(null);
		gathering.setGatheringPersons(Arrays.asList(nathanielWallich()));

		DefaultClassification classification = new DefaultClassification();
		classification.setKingdom("Plantae");
		classification.setOrder("Rosales");
		classification.setFamily("Rosaceae");
		classification.setGenus("Malus");
		classification.setSpecificEpithet("sylvestris");

		List<Monomial> systemClassification = new ArrayList<>();
		systemClassification.add(new Monomial("kingdom", "Plantae"));
		systemClassification.add(new Monomial("Angiosperms"));
		systemClassification.add(new Monomial("Eudicots"));
		systemClassification.add(new Monomial("Rosids"));
		systemClassification.add(new Monomial("order", "Rosales"));
		systemClassification.add(new Monomial("family", "Rosaceae"));
		systemClassification.add(new Monomial("genus", "Malus"));
		systemClassification.add(new Monomial("species", "sylvestris"));

		ScientificName scientificName = new ScientificName();
		scientificName.setFullScientificName("Malus sylvestris");
		scientificName.setAuthorshipVerbatim("(L.) Mill.");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));
		return specimen;
	}

	private static Person ruudAltenBurg()
	{
		Person person = new Person("Altenburg, R.");
		person.setAgentText("Also likes David Bowie");
		return person;
	}

	private static Person edwinVanHuis()
	{
		Person person = new Person("E. van Huis");
		person.setAgentText("Director of NBC Naturalis");
		return person;
	}

	private static Person nathanielWallich()
	{
		Person person = new Person("Nathaniel Wallich");
		person.setAgentText(null);
		return person;
	}

	private static Date datetime(String s)
	{
		try {
			return new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(s);
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
