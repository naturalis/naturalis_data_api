package nl.naturalis.nba.dao.mock;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummaryGatheringEvent;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimenIdentification;

public class ScientificNameGroupMock {

	public static ScientificNameGroup sngLarusFuscus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus");
		sng.addSpecimen(specimenLarusFuscusAalten());
		sng.addSpecimen(specimenLarusFuscusBreda());
		sng.addSpecimen(specimenLarusFuscusDenHelder1());
		sng.addSpecimen(specimenLarusFuscusDenHelder2());
		sng.addSpecimen(specimenLarusFuscusDenHelder3());
		sng.addSpecimen(specimenLarusFuscusZwolle());
		return sng;
	}

	public static ScientificNameGroup sngLarusFuscusFuscus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus fuscus");
		return sng;
	}

	public static ScientificNameGroup sngLarusFuscusArgentatus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus argentatus");
		return sng;
	}

	public static ScientificNameGroup sngParusMajor()
	{
		ScientificNameGroup sng = new ScientificNameGroup("parus major");
		sng.addSpecimen(specimenParusMajorAmsterdam());
		sng.addSpecimen(specimenParusMajorZwolle());
		return sng;
	}

	public static ScientificNameGroup sngFelixFelix()
	{
		ScientificNameGroup sng = new ScientificNameGroup("felix felix");
		return sng;
	}

	public static ScientificNameGroup sngMalusSylvestris()
	{
		ScientificNameGroup sng = new ScientificNameGroup("malus sylvestris");
		return sng;
	}

	/**
	 * A specimen found in Aalten with two identifications.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenLarusFuscusAalten()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.1000");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Aalten");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
		ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscusNoAuthor());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Breda with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenLarusFuscusBreda()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.1001");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Breda");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Den Helder with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenLarusFuscusDenHelder1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.1002");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Den Helder");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Den Helder with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenLarusFuscusDenHelder2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.1003");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Den Helder");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Den Helder with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenLarusFuscusDenHelder3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.1004");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Den Helder");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Zwolle with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenLarusFuscusZwolle()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.1005");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Zwolle");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Amsterdam with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenParusMajorAmsterdam()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.2001");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Amsterdam");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snParusMajor());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Zwolle with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenParusMajorZwolle()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.2002");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Zwolle");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snParusMajor());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	/**
	 * A specimen found in Zwolle with one identification.
	 * 
	 * @return
	 */
	public static SummarySpecimen specimenFelixFelixBreda()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("ZMA.MAM.3005");
		SummaryGatheringEvent ge = new SummaryGatheringEvent();
		ge.setLocalityText("Breda");
		specimen.setGatheringEvent(ge);
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snParusMajor());
		specimen.addMatchingIdentification(ssi);
		return specimen;
	}

	public static SummaryScientificName snLarusFuscus()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus (Linnaeus 1752)");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		return ssn;
	}

	public static SummaryScientificName snLarusFuscusNoAuthor()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		return ssn;
	}

	public static SummaryScientificName snLarusFuscusFuscus()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus fuscus (Linnaeus 1752)");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		ssn.setInfraspecificEpithet("fuscus");
		return ssn;
	}

	public static SummaryScientificName snParusMajor()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Parus major");
		ssn.setGenusOrMonomial("Parus");
		ssn.setSpecificEpithet("major");
		return ssn;
	}

	public static SummaryScientificName snFelixFelix()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Felix felix");
		ssn.setGenusOrMonomial("Felix");
		ssn.setSpecificEpithet("felix");
		return ssn;
	}

}
