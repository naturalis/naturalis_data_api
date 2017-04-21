package nl.naturalis.nba.dao.mock;

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
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

/**
 * Generates 5 Specimen objects used for testing the SpecimenDAO.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenMock {

	public static Specimen parusMajorSpecimen01()
	{
		Specimen specimen = new Specimen();
		specimen.setUnitID("ZMA.MAM.100");
		specimen.setSourceSystem(CRS);
		String[] collections = new String[] { "Altenburg", "Living Dinos" };
		specimen.setTheme(Arrays.asList(collections));
		specimen.setPhaseOrStage(ADULT);
		specimen.setSex(MALE);
		specimen.setRecordBasis("Preserved specimen");
		GatheringEvent gathering = new GatheringEvent();
		gathering.setProjectTitle("Vogels der Lage Landen");
		gathering.setLocalityText("Duinen, nabij Uitgeest");
		gathering.setCountry("Netherlands");
		gathering.setCity("Uitgeest");
		gathering.setDateTimeBegin(datetime("2010/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));

		GatheringSiteCoordinates uitgeestCoordinates;
		uitgeestCoordinates = new GatheringSiteCoordinates(52.531713, 4.705922);
		gathering.setSiteCoordinates(Arrays.asList(uitgeestCoordinates));

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
		scientificName.setGenusOrMonomial("Parus");
		scientificName.setSpecificEpithet("major");
		scientificName.setAuthorshipVerbatim("Linnaeus, 1752");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));

		return specimen;
	}

	public static Specimen larusFuscusSpecimen01()
	{
		Specimen specimen = new Specimen();
		specimen.setUnitID("ZMA.MAM.101");
		specimen.setSourceSystem(CRS);
		String[] collections = new String[] { "Altenburg", "Living Dinos" };
		specimen.setTheme(Arrays.asList(collections));
		specimen.setPhaseOrStage(JUVENILE);
		specimen.setSex(MALE);
		specimen.setRecordBasis("Preserved specimen");
		GatheringEvent gathering = new GatheringEvent();
		gathering.setProjectTitle("Vogels der Lage Landen");
		gathering.setLocalityText("In de bossen nabij Aalten");
		gathering.setCountry("Netherlands");
		gathering.setCity("Aalten");
		gathering.setDateTimeBegin(datetime("2009/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg()));

		GatheringSiteCoordinates aaltenCoordinates;
		aaltenCoordinates = new GatheringSiteCoordinates(51.9266666, 6.5806785);
		gathering.setSiteCoordinates(Arrays.asList(aaltenCoordinates));

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
		scientificName.setGenusOrMonomial("Larus");
		scientificName.setSpecificEpithet("fuscus");
		scientificName.setAuthorshipVerbatim("Linnaeus, 1752");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));

		return specimen;
	}

	public static Specimen larusFuscusSpecimen02()
	{
		Specimen specimen = new Specimen();
		specimen.setUnitID("309801857");
		specimen.setSourceSystem(NDFF);
		specimen.setPhaseOrStage(EGG);
		specimen.setNumberOfSpecimen(4);
		GatheringEvent gathering = new GatheringEvent();
		gathering.setCountry("Netherlands");
		gathering.setCity("Uitgeest");
		gathering.setDateTimeBegin(datetime("2008/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(ruudAltenBurg(), vonSiebold()));

		GatheringSiteCoordinates uitgeestCoordinates;
		uitgeestCoordinates = new GatheringSiteCoordinates(52.531713, 4.705922);
		gathering.setSiteCoordinates(Arrays.asList(uitgeestCoordinates));

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
		scientificName.setGenusOrMonomial("Larus");
		scientificName.setSpecificEpithet("fuscus");
		scientificName.setAuthorshipVerbatim("Linnaeus, 1752");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));

		return specimen;
	}

	public static Specimen tRexSpecimen01()
	{
		Specimen specimen = new Specimen();
		specimen.setUnitID("RMNH.100");
		specimen.setSourceSystem(CRS);
		specimen.setTheme(Arrays.asList("Living Dinos"));
		specimen.setPhaseOrStage(ADULT);
		specimen.setSex(FEMALE);
		specimen.setRecordBasis("FossileSpecimen");
		GatheringEvent gathering = new GatheringEvent();
		gathering.setProjectTitle("Project T. Rex");
		gathering.setLocalityText("Montana, U.S.A.");
		gathering.setCountry("United States");
		gathering.setDateTimeBegin(datetime("2007/04/03 13:04"));
		gathering.setGatheringPersons(Arrays.asList(edwinVanHuis()));

		GatheringSiteCoordinates coords;
		coords = new GatheringSiteCoordinates(46.5884, 112.0245);
		gathering.setSiteCoordinates(Arrays.asList(coords));

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
		scientificName.setGenusOrMonomial("Tyrannosaurus");
		scientificName.setSpecificEpithet("rex");
		scientificName.setAuthorshipVerbatim("Osborn, 1905");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));
		return specimen;
	}

	public static Specimen malusSylvestrisSpecimen01()
	{
		Specimen specimen = new Specimen();
		specimen.setUnitID("L   100");
		specimen.setSourceSystem(BRAHMS);
		String[] collections = new String[] { "Strange Plants" };
		specimen.setTheme(Arrays.asList(collections));
		specimen.setPhaseOrStage(ADULT);
		specimen.setSex(FEMALE);
		specimen.setRecordBasis("Herbarium sheet");
		GatheringEvent gathering = new GatheringEvent();
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
		scientificName.setGenusOrMonomial("Malus");
		scientificName.setSpecificEpithet("sylvestris");
		scientificName.setAuthorshipVerbatim("(L.) Mill.");

		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setDefaultClassification(classification);
		identification.setSystemClassification(systemClassification);
		identification.setScientificName(scientificName);

		specimen.setGatheringEvent(gathering);
		specimen.setIdentifications(Arrays.asList(identification));
		return specimen;
	}

	public static Person ruudAltenBurg()
	{
		Person person = new Person("Altenburg, R.");
		person.setAgentText("Also likes David Bowie");
		return person;
	}

	public static Person edwinVanHuis()
	{
		Person person = new Person("E. van Huis");
		person.setAgentText("Director of NBC Naturalis");
		return person;
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

	public static Person nathanielWallich()
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
