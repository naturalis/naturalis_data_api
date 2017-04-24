package nl.naturalis.nba.dao.mock;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static nl.naturalis.nba.utils.StringUtil.lpad;

import java.util.Arrays;

import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummaryGatheringEvent;
import nl.naturalis.nba.api.model.summary.SummaryPerson;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimenIdentification;
import nl.naturalis.nba.dao.DaoTestUtil;
import nl.naturalis.nba.dao.DocumentType;

public class ScientificNameGroupMock {

	public static ScientificNameGroup sngLarusFuscus;
	public static ScientificNameGroup sngLarusFuscusFuscus;
	public static ScientificNameGroup sngLarusFuscusArgentatus;
	public static ScientificNameGroup sngParusMajor;
	public static ScientificNameGroup sngFelixFelix;
	public static ScientificNameGroup sngMalusSylvestris;

	private static int unitIdCounter = 0;

	public static void saveAll()
	{
		sngLarusFuscus = sngLarusFuscus();
		sngLarusFuscusFuscus = sngLarusFuscusFuscus();
		sngLarusFuscusArgentatus = sngLarusFuscusArgentatus();
		sngParusMajor = sngParusMajor();
		sngFelixFelix = sngFelixFelix();
		sngMalusSylvestris = sngMalusSylvestris();

		deleteIndex(DocumentType.SCIENTIFIC_NAME_GROUP);
		createIndex(DocumentType.SCIENTIFIC_NAME_GROUP);
		createType(DocumentType.SCIENTIFIC_NAME_GROUP);

		DaoTestUtil.saveScientificNameGroups(sngLarusFuscus, sngLarusFuscusFuscus,
				sngLarusFuscusArgentatus, sngParusMajor, sngFelixFelix, sngMalusSylvestris);

	}

	/* ************************************************* */
	/* ************* SCIENTIFIC NAME GROUPS ************ */
	/* ************************************************* */

	public static ScientificNameGroup sngLarusFuscus()
	{
		// NB name groups are always all lowercase
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus");
		sng.addSpecimen(larusFuscusAalten1());
		sng.addSpecimen(larusFuscusAalten2());
		sng.addSpecimen(larusFuscusAalten3());
		sng.addSpecimen(larusFuscusAalten4());
		sng.addSpecimen(larusFuscusAalten5());
		sng.addSpecimen(larusFuscusBreda());
		sng.addSpecimen(larusFuscusDenHelder1());
		sng.addSpecimen(larusFuscusDenHelder2());
		sng.addSpecimen(larusFuscusDenHelder3());
		sng.addSpecimen(larusFuscusDenHelder4());
		sng.addSpecimen(larusFuscusDenHelder5());
		sng.addSpecimen(larusFuscusDenHelder6());
		sng.addSpecimen(larusFuscusDenHelder7());
		sng.addSpecimen(larusFuscusDenHelder8());
		sng.addSpecimen(larusFuscusZwolle1());
		sng.addSpecimen(larusFuscusZwolle2());
		return sng;
	}

	public static ScientificNameGroup sngLarusFuscusFuscus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus fuscus");
		sng.addSpecimen(larusFuscusFuscusDenHelder1());
		sng.addSpecimen(larusFuscusFuscusDenHelder2());
		sng.addSpecimen(larusFuscusFuscusDenHelder3());
		sng.addSpecimen(larusFuscusFuscusDenHelder4());
		sng.addSpecimen(larusFuscusFuscusDenHelder5());
		sng.addSpecimen(larusFuscusFuscusDenHelder6());
		sng.addSpecimen(larusFuscusFuscusDenHelder7());
		sng.addSpecimen(larusFuscusFuscusDenHelder8());
		sng.addSpecimen(larusFuscusFuscusDenHelder9());
		sng.addSpecimen(larusFuscusFuscusDenHelder10());
		sng.addSpecimen(larusFuscusFuscusAmterdam1());
		sng.addSpecimen(larusFuscusFuscusAmterdam2());
		sng.addSpecimen(larusFuscusFuscusAmterdam3());
		sng.addSpecimen(larusFuscusFuscusAmterdam4());
		sng.addSpecimen(larusFuscusFuscusAmterdam5());
		sng.addSpecimen(larusFuscusFuscusAmterdam6());
		return sng;
	}

	public static ScientificNameGroup sngLarusFuscusArgentatus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus argentatus");
		sng.addSpecimen(larusFuscusArgentatusAmsterdam1());
		sng.addSpecimen(larusFuscusArgentatusAmsterdam2());
		return sng;
	}

	public static ScientificNameGroup sngParusMajor()
	{
		ScientificNameGroup sng = new ScientificNameGroup("parus major");
		sng.addSpecimen(parusMajorAmsterdam1());
		sng.addSpecimen(parusMajorAmsterdam2());
		sng.addSpecimen(parusMajorAmsterdam3());
		sng.addSpecimen(parusMajorZwolle1());
		sng.addSpecimen(parusMajorZwolle2());
		sng.addSpecimen(parusMajorZwolle3());
		return sng;
	}

	public static ScientificNameGroup sngFelixFelix()
	{
		ScientificNameGroup sng = new ScientificNameGroup("felix felix");
		sng.addSpecimen(felixFelixBreda1());
		sng.addSpecimen(felixFelixBreda2());
		sng.addSpecimen(felixFelixBreda3());
		sng.addSpecimen(felixFelixRotterdam1());
		sng.addSpecimen(felixFelixRotterdam2());
		return sng;
	}

	public static ScientificNameGroup sngMalusSylvestris()
	{
		ScientificNameGroup sng = new ScientificNameGroup("malus sylvestris");
		sng.addSpecimen(malusSylvestrusApeldoorn1());
		sng.addSpecimen(malusSylvestrusApeldoorn2());
		sng.addSpecimen(malusSylvestrusApeldoorn3());
		sng.addSpecimen(malusSylvestrusApeldoorn4());
		sng.addSpecimen(malusSylvestrusApeldoorn5());
		sng.addSpecimen(malusSylvestrusApeldoorn6());
		sng.addSpecimen(malusSylvestrusApeldoorn7());
		sng.addSpecimen(malusSylvestrusApeldoorn8());
		sng.addSpecimen(malusSylvestrusRotterdam1());
		sng.addSpecimen(malusSylvestrusRotterdam2());
		sng.addSpecimen(malusSylvestrusRotterdam3());
		sng.addSpecimen(malusSylvestrusRotterdam4());
		return sng;
	}

	/* ************************************************* */
	/* ******************** SPECIMENS ****************** */
	/* ************************************************* */

	//////////////////////////////
	// LARUS FUSCUS
	//////////////////////////////

	public static SummarySpecimen larusFuscusAalten1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAalten(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		identifiedAsLarusFuscus(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusAalten2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAalten(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		alsoIdentifiedAsFooBar(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusAalten3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAalten(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusAalten4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAalten(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusAalten5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAalten(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusBreda()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInBreda(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	//////////////////////////////
	// LARUS FUSCUS FUSCUS
	//////////////////////////////

	public static SummarySpecimen larusFuscusDenHelder4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder7()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder8()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusZwolle1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInZwolle(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusZwolle2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInZwolle(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder7()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder8()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		// Let's not have a gathering person this time
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder9()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder10()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		alsoIdentifiedAsLarusFuscus(specimen);
		alsoIdentifiedAsFooBar(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		alsoIdentifiedAsLarusFuscus(specimen);
		alsoIdentifiedAsFooBar(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		alsoIdentifiedAsLarusFuscus(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		// No collector
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		alsoIdentifiedAsFooBar(specimen);
		return specimen;
	}

	//////////////////////////////
	// LARUS FUSCUS ARGENTATUS
	//////////////////////////////

	public static SummarySpecimen larusFuscusArgentatusAmsterdam1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusArgentatusAmsterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	//////////////////////////////
	// PARUS MAJOR
	//////////////////////////////

	public static SummarySpecimen parusMajorAmsterdam1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorAmsterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorAmsterdam3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorAmsterdam4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInAmsterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorZwolle1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInZwolle(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorZwolle2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInZwolle(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorZwolle3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInZwolle(specimen);
		foundByRobertRedford(specimen);
		identifiedAsParusMajor(specimen);
		alsoIdentifiedAsFooBar(specimen);
		return specimen;
	}

	//////////////////////////////
	// FELIX FELIX
	//////////////////////////////

	public static SummarySpecimen felixFelixBreda1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInBreda(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixBreda2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInBreda(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixBreda3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInBreda(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsFelixFelix(specimen);
		alsoIdentifiedAsFooBar(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixRotterdam1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInRotterdam(specimen);
		// No Collector
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixRotterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.JUVENILE);
		specimen.setUnitID("ZMA.MAM." + lpad(unitIdCounter, 5, '0'));
		foundInRotterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	//////////////////////////////
	// MALUS SYLVESTRIS
	//////////////////////////////

	public static SummarySpecimen malusSylvestrusApeldoorn1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByRobertRedford(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn7()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn8()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInApeldoorn(specimen);
		foundByRobertRedford(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInRotterdam(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInRotterdam(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInRotterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setUnitID("WAG." + lpad(unitIdCounter, 5, '0'));
		foundInRotterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	/* ************************************************* */
	/* **************** IDENTIFICATIONS **************** */
	/* ************************************************* */

	private static void identifiedAsFelixFelix(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snFelixFelix());
		specimen.addMatchingIdentification(ssi);
	}

	private static void identifiedAsLarusFuscus(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addMatchingIdentification(ssi);
	}

	private static void identifiedAsLarusFuscusFuscusLinnaeus1752(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscusFuscusLinnaeus1752());
		specimen.addMatchingIdentification(ssi);
	}

	private static void identifiedAsLarusFuscusLinnaeus1752(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscusLinnaeus1752());
		specimen.addMatchingIdentification(ssi);
	}

	private static void identifiedAsParusMajor(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snParusMajor());
		specimen.addMatchingIdentification(ssi);
	}

	private static void alsoIdentifiedAsLarusFuscus(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snLarusFuscus());
		specimen.addOtherIdentification(ssi);
	}

	private static void alsoIdentifiedAsFooBar(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snFooBar());
		specimen.addOtherIdentification(ssi);
	}

	/* ************************************************* */
	/* ************* SCIENTIFIC NAME OBJECTS *********** */
	/* ************************************************* */

	public static SummaryScientificName snLarusFuscus()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		return ssn;
	}

	public static SummaryScientificName snLarusFuscusLinnaeus1752()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus (Linnaeus 1752)");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		return ssn;
	}

	public static SummaryScientificName snLarusFuscusFuscusLinnaeus1752()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus fuscus (Linnaeus 1752)");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		ssn.setInfraspecificEpithet("fuscus");
		return ssn;
	}

	public static SummaryScientificName snLarusFuscusArgentatus()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Larus fuscus argentatus");
		ssn.setGenusOrMonomial("Larus");
		ssn.setSpecificEpithet("fuscus");
		ssn.setInfraspecificEpithet("argentatus");
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

	public static SummaryScientificName snMalusSylvestris()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Malus sylvestris");
		ssn.setGenusOrMonomial("Malus");
		ssn.setSpecificEpithet("sylvestris");
		return ssn;
	}

	public static SummaryScientificName snMalusPumila()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Malus pumila");
		ssn.setGenusOrMonomial("Malus");
		ssn.setSpecificEpithet("pumila");
		return ssn;
	}

	public static SummaryScientificName snMalusPumilaMiller1768()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Malus pumila (Miller 1768)");
		ssn.setGenusOrMonomial("Malus");
		ssn.setSpecificEpithet("pumila");
		return ssn;
	}

	public static SummaryScientificName snFooBar()
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setFullScientificName("Foo bar");
		ssn.setGenusOrMonomial("Foo");
		ssn.setSpecificEpithet("bar");
		return ssn;
	}

	/* ************************************************* */
	/* ***************** GATHERING EVENTS ************** */
	/* ************************************************* */

	private static SummarySpecimen foundInAalten(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Aalten");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	private static SummarySpecimen foundInAmsterdam(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Amsterdam");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	private static SummarySpecimen foundInApeldoorn(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Apeldoorn");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	private static SummarySpecimen foundInBreda(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Breda");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	private static SummarySpecimen foundInDenHelder(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Den Helder");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	private static SummarySpecimen foundInRotterdam(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Rotterdam");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	private static SummarySpecimen foundInZwolle(SummarySpecimen specimen)
	{
		SummaryGatheringEvent sge = new SummaryGatheringEvent();
		sge.setLocalityText("Zwolle");
		specimen.setGatheringEvent(sge);
		return specimen;
	}

	/* ************************************************* */
	/* **************** GATHERING PERSONS ************** */
	/* ************************************************* */

	private static void foundByRobertDeNiro(SummarySpecimen specimen)
	{
		SummaryGatheringEvent ge = specimen.getGatheringEvent();
		if (ge == null) {
			ge = new SummaryGatheringEvent();
			specimen.setGatheringEvent(ge);
		}
		SummaryPerson person = new SummaryPerson();
		person.setFullName("Robert De Niro");
		ge.setGatheringPersons(Arrays.asList(person));
	}

	private static void foundByJuliaRoberts(SummarySpecimen specimen)
	{
		SummaryGatheringEvent ge = specimen.getGatheringEvent();
		if (ge == null) {
			ge = new SummaryGatheringEvent();
			specimen.setGatheringEvent(ge);
		}
		SummaryPerson person = new SummaryPerson();
		person.setFullName("Julia Roberts");
		ge.setGatheringPersons(Arrays.asList(person));
	}

	private static void foundByRobertRedford(SummarySpecimen specimen)
	{
		SummaryGatheringEvent ge = specimen.getGatheringEvent();
		if (ge == null) {
			ge = new SummaryGatheringEvent();
			specimen.setGatheringEvent(ge);
		}
		SummaryPerson person = new SummaryPerson();
		person.setFullName("Robert Redford");
		ge.setGatheringPersons(Arrays.asList(person));
	}

	private static void foundByKirstenDunst(SummarySpecimen specimen)
	{
		SummaryGatheringEvent ge = specimen.getGatheringEvent();
		if (ge == null) {
			ge = new SummaryGatheringEvent();
			specimen.setGatheringEvent(ge);
		}
		SummaryPerson person = new SummaryPerson();
		person.setFullName("Kirsten Dunst");
		ge.setGatheringPersons(Arrays.asList(person));
	}

}
