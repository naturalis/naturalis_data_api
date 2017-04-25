package nl.naturalis.nba.dao.mock;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static nl.naturalis.nba.utils.StringUtil.lpad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
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

	private static final String ZMA_MAM = "ZMA.MAM.";
	private static final String WAG = "WAG.";
	private static final String CRS = "CRS";
	private static final String BRAHMS = "BRAHMS";

	private static TreeSet<Specimen> specimens = new TreeSet<>(new Comparator<Specimen>() {

		@Override
		public int compare(Specimen s1, Specimen s2)
		{
			return s1.getUnitID().compareTo(s2.getUnitID());
		}
	});

	private static int unitIdCounter = 0;

	public static void saveAll()
	{
		sngLarusFuscus = sngLarusFuscus();
		sngLarusFuscusFuscus = sngLarusFuscusFuscus();
		sngLarusFuscusArgentatus = sngLarusFuscusArgentatus();
		sngParusMajor = sngParusMajor();
		sngFelixFelix = sngFelixFelix();
		sngMalusSylvestris = sngMalusSylvestris();

		deleteIndex(DocumentType.SPECIMEN);
		deleteIndex(DocumentType.SCIENTIFIC_NAME_GROUP);
		createIndex(DocumentType.SPECIMEN);
		createIndex(DocumentType.SCIENTIFIC_NAME_GROUP);
		createType(DocumentType.SPECIMEN);
		createType(DocumentType.SCIENTIFIC_NAME_GROUP);

		DaoTestUtil.saveScientificNameGroups(sngLarusFuscus, sngLarusFuscusFuscus,
				sngLarusFuscusArgentatus, sngParusMajor, sngFelixFelix, sngMalusSylvestris);

		DaoTestUtil.saveSpecimens(specimens);

	}

	/* ************************************************* */
	/* ************* SCIENTIFIC NAME GROUPS ************ */
	/* ************************************************* */

	public static ScientificNameGroup sngLarusFuscus()
	{
		// NB name groups are always all lowercase
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus");
		addSpecimen(sng, larusFuscusAalten1());
		addSpecimen(sng, larusFuscusAalten2());
		addSpecimen(sng, larusFuscusAalten3());
		addSpecimen(sng, larusFuscusAalten4());
		addSpecimen(sng, larusFuscusAalten5());
		addSpecimen(sng, larusFuscusBreda());
		addSpecimen(sng, larusFuscusDenHelder1());
		addSpecimen(sng, larusFuscusDenHelder2());
		addSpecimen(sng, larusFuscusDenHelder3());
		addSpecimen(sng, larusFuscusDenHelder4());
		addSpecimen(sng, larusFuscusDenHelder5());
		addSpecimen(sng, larusFuscusDenHelder6());
		addSpecimen(sng, larusFuscusDenHelder7());
		addSpecimen(sng, larusFuscusDenHelder8());
		addSpecimen(sng, larusFuscusZwolle1());
		addSpecimen(sng, larusFuscusZwolle2());
		return sng;
	}

	public static ScientificNameGroup sngLarusFuscusFuscus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus fuscus");
		addSpecimen(sng, larusFuscusFuscusDenHelder1());
		addSpecimen(sng, larusFuscusFuscusDenHelder2());
		addSpecimen(sng, larusFuscusFuscusDenHelder3());
		addSpecimen(sng, larusFuscusFuscusDenHelder4());
		addSpecimen(sng, larusFuscusFuscusDenHelder5());
		addSpecimen(sng, larusFuscusFuscusDenHelder6());
		addSpecimen(sng, larusFuscusFuscusDenHelder7());
		addSpecimen(sng, larusFuscusFuscusDenHelder8());
		addSpecimen(sng, larusFuscusFuscusDenHelder9());
		addSpecimen(sng, larusFuscusFuscusDenHelder10());
		addSpecimen(sng, larusFuscusFuscusAmterdam1());
		addSpecimen(sng, larusFuscusFuscusAmterdam2());
		addSpecimen(sng, larusFuscusFuscusAmterdam3());
		addSpecimen(sng, larusFuscusFuscusAmterdam4());
		addSpecimen(sng, larusFuscusFuscusAmterdam5());
		addSpecimen(sng, larusFuscusFuscusAmterdam6());
		return sng;
	}

	public static ScientificNameGroup sngLarusFuscusArgentatus()
	{
		ScientificNameGroup sng = new ScientificNameGroup("larus fuscus argentatus");
		addSpecimen(sng, larusFuscusArgentatusAmsterdam1());
		addSpecimen(sng, larusFuscusArgentatusAmsterdam2());
		return sng;
	}

	public static ScientificNameGroup sngParusMajor()
	{
		ScientificNameGroup sng = new ScientificNameGroup("parus major");
		addSpecimen(sng, parusMajorAmsterdam1());
		addSpecimen(sng, parusMajorAmsterdam2());
		addSpecimen(sng, parusMajorAmsterdam3());
		addSpecimen(sng, parusMajorZwolle1());
		addSpecimen(sng, parusMajorZwolle2());
		addSpecimen(sng, parusMajorZwolle3());
		return sng;
	}

	public static ScientificNameGroup sngFelixFelix()
	{
		ScientificNameGroup sng = new ScientificNameGroup("felix felix");
		addSpecimen(sng, felixFelixBreda1());
		addSpecimen(sng, felixFelixBreda2());
		addSpecimen(sng, felixFelixBreda3());
		addSpecimen(sng, felixFelixRotterdam1());
		addSpecimen(sng, felixFelixRotterdam2());
		return sng;
	}

	public static ScientificNameGroup sngMalusSylvestris()
	{
		ScientificNameGroup sng = new ScientificNameGroup("malus sylvestris");
		addSpecimen(sng, malusSylvestrusApeldoorn1());
		addSpecimen(sng, malusSylvestrusApeldoorn2());
		addSpecimen(sng, malusSylvestrusApeldoorn3());
		addSpecimen(sng, malusSylvestrusApeldoorn4());
		addSpecimen(sng, malusSylvestrusApeldoorn5());
		addSpecimen(sng, malusSylvestrusApeldoorn6());
		addSpecimen(sng, malusSylvestrusApeldoorn7());
		addSpecimen(sng, malusSylvestrusApeldoorn8());
		addSpecimen(sng, malusSylvestrusRotterdam1());
		addSpecimen(sng, malusSylvestrusRotterdam2());
		addSpecimen(sng, malusSylvestrusRotterdam3());
		addSpecimen(sng, malusSylvestrusRotterdam4());
		return sng;
	}

	private static void addSpecimen(ScientificNameGroup sng, SummarySpecimen ss)
	{
		sng.addSpecimen(ss);
		sng.setSpecimenCount(sng.getSpecimens().size());
		specimens.add(copySpecimen(ss));
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
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAalten(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusAalten4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAalten(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusAalten5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAalten(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusBreda()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInBreda(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder7()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusDenHelder8()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusZwolle1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInZwolle(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusZwolle2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInZwolle(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder7()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder8()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		// Let's not have a gathering person this time
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder9()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInDenHelder(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusDenHelder10()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusFuscusAmterdam5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsLarusFuscusFuscusLinnaeus1752(specimen);
		return specimen;
	}

	public static SummarySpecimen larusFuscusArgentatusAmsterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorAmsterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorAmsterdam3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorAmsterdam4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.EGG);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInAmsterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorZwolle1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInZwolle(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorZwolle2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInZwolle(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsParusMajor(specimen);
		return specimen;
	}

	public static SummarySpecimen parusMajorZwolle3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInBreda(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixBreda2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInBreda(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixBreda3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.ADULT);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, ZMA_MAM, CRS);
		foundInRotterdam(specimen);
		// No Collector
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen felixFelixRotterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		specimen.setPhaseOrStage(PhaseOrStage.JUVENILE);
		setUnitID(specimen, ZMA_MAM, CRS);
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
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsFelixFelix(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByRobertRedford(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn5()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn6()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn7()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusApeldoorn8()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInApeldoorn(specimen);
		foundByRobertRedford(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam1()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInRotterdam(specimen);
		foundByJuliaRoberts(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam2()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInRotterdam(specimen);
		foundByKirstenDunst(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam3()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInRotterdam(specimen);
		foundByRobertDeNiro(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	public static SummarySpecimen malusSylvestrusRotterdam4()
	{
		SummarySpecimen specimen = new SummarySpecimen();
		setUnitID(specimen, WAG, BRAHMS);
		foundInRotterdam(specimen);
		foundByRobertRedford(specimen);
		identifiedAsMalusSylvestris(specimen);
		return specimen;
	}

	private static void setUnitID(SummarySpecimen specimen, String prefix, String postfix)
	{
		if (specimen.getUnitID() == null) {
			String unitID = prefix + lpad(++unitIdCounter, 5, '0');
			specimen.setUnitID(unitID);
			specimen.setId(unitID + '@' + postfix);
		}
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

	private static void identifiedAsMalusSylvestris(SummarySpecimen specimen)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setScientificName(snMalusSylvestris());
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

	/* ************************************************* */
	/* ********* SPECIMEN CONVERSION METHODS *********** */
	/* ************************************************* */

	/*
	 * NB conversion from summary objects to full-blown objects is not
	 * necessarily complete!!! Only what's needed for the unit tests is copied
	 * over to the full-blown object.
	 */

	private static Specimen copySpecimen(SummarySpecimen summary)
	{
		Specimen specimen = new Specimen();
		specimen.setId(summary.getId());
		specimen.setUnitID(summary.getUnitID());
		if (summary.getMatchingIdentifications() != null) {
			for (SummarySpecimenIdentification si : summary.getMatchingIdentifications()) {
				specimen.addIndentification(copyIdentification(si));
			}
		}
		if (summary.getOtherIdentifications() != null) {
			for (SummarySpecimenIdentification si : summary.getOtherIdentifications()) {
				specimen.addIndentification(copyIdentification(si));
			}
		}
		specimen.setCollectorsFieldNumber(summary.getCollectorsFieldNumber());
		specimen.setPhaseOrStage(summary.getPhaseOrStage());
		specimen.setSex(summary.getSex());
		specimen.setGatheringEvent(copyGatheringEvent(summary.getGatheringEvent()));
		return specimen;
	}

	private static SpecimenIdentification copyIdentification(SummarySpecimenIdentification ssi)
	{
		SpecimenIdentification si = new SpecimenIdentification();
		si.setTypeStatus(ssi.getTypeStatus());
		si.setDefaultClassification(ssi.getDefaultClassification());
		si.setScientificName(copyScientificName(ssi.getScientificName()));
		si.setTaxonomicEnrichments(ssi.getTaxonomicEnrichments());
		return si;
	}

	private static ScientificName copyScientificName(SummaryScientificName ssn)
	{
		ScientificName sn = new ScientificName();
		sn.setAuthorshipVerbatim(ssn.getAuthorshipVerbatim());
		sn.setFullScientificName(ssn.getFullScientificName());
		sn.setGenusOrMonomial(ssn.getGenusOrMonomial());
		sn.setInfraspecificEpithet(ssn.getInfraspecificEpithet());
		sn.setSpecificEpithet(ssn.getSpecificEpithet());
		sn.setSubgenus(ssn.getSubgenus());
		sn.setTaxonomicStatus(ssn.getTaxonomicStatus());
		setScientificNameGroup(sn);
		return sn;
	}

	private static GatheringEvent copyGatheringEvent(SummaryGatheringEvent sge)
	{
		if (sge == null) {
			return null;
		}
		GatheringEvent ge = new GatheringEvent();
		ge.setDateTimeBegin(sge.getDateTimeBegin());
		ge.setDateTimeEnd(sge.getDateTimeEnd());
		ge.setGatheringOrganizations(sge.getGatheringOrganizations());
		ge.setGatheringPersons(copyGatheringPersons(sge.getGatheringPersons()));
		ge.setLocalityText(sge.getLocalityText());
		return ge;
	}

	private static List<Person> copyGatheringPersons(List<SummaryPerson> persons)
	{
		if (persons == null) {
			return null;
		}
		List<Person> summaries = new ArrayList<>(persons.size());
		for (SummaryPerson p : persons) {
			Person sp = new Person();
			sp.setFullName(p.getFullName());
			summaries.add(sp);
		}
		return summaries;
	}

	// TODO share code with TransformUtil.setScientificNameGroup
	private static void setScientificNameGroup(ScientificName sn)
	{
		String s0 = sn.getGenusOrMonomial();
		s0 = s0 == null ? "?" : s0.toLowerCase();
		String s1 = sn.getSpecificEpithet();
		s1 = s1 == null ? "?" : s1.toLowerCase();
		String s2 = sn.getInfraspecificEpithet();
		if (s2 == null) {
			sn.setScientificNameGroup(s0 + " " + s1);
		}
		else {
			sn.setScientificNameGroup(s0 + " " + s1 + " " + s2.toLowerCase());
		}
	}

}
