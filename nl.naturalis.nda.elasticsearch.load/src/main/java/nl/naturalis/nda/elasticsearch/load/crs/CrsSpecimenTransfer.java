package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.BioStratigraphy;
import nl.naturalis.nda.domain.ChronoStratigraphy;
import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.LithoStratigraphy;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.DocumentType;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class CrsSpecimenTransfer {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final SexNormalizer sexNormalizer = SexNormalizer.getInstance();
	private static final PhaseOrStageNormalizer phaseOrStageNormalizer = PhaseOrStageNormalizer.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(CrsSpecimenTransfer.class);


	public static ESSpecimen transfer(Element recordElement)
	{
		String unitId = val(recordElement, "abcd:UnitID");
		List<Element> determinationElements = DOMUtil.getDescendants(recordElement, "ncrsDetermination");
		if (determinationElements == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Missing <ncrsDetermination> element for specimen with unitID " + unitId);
			}
			return null;
		}

		final ESSpecimen specimen = new ESSpecimen();

		String string;

		for (Element e : determinationElements) {
			SpecimenIdentification si = transferIdentification(e, unitId);
			if (si != null) {
				specimen.addIndentification(transferIdentification(e, unitId));
			}
		}

		if (specimen.getIdentifications() == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Missing non-empty <ncrsDetermination> element for specimen with unitID " + unitId);
			}
			return null;
		}

		Collections.sort(specimen.getIdentifications(), new Comparator<SpecimenIdentification>() {

			@Override
			public int compare(SpecimenIdentification o1, SpecimenIdentification o2)
			{
				if (o1.isPreferred()) {
					return -1;
				}
				if (o2.isPreferred()) {
					return 1;
				}
				return 0;
			}

		});

		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setUnitID(unitId);
		specimen.setSourceSystemId(specimen.getUnitID());
		ThematicSearchConfig tsc = ThematicSearchConfig.getInstance();
		List<String> themes = tsc.getThemesForDocument(specimen.getUnitID(), DocumentType.SPECIMEN, SourceSystem.CRS);
		specimen.setTheme(themes);
		specimen.setUnitGUID(val(recordElement, "abcd:UnitGUID"));
		specimen.setCollectorsFieldNumber(val(recordElement, "abcd:CollectorsFieldNumber"));
		//specimen.setSourceInstitutionID(val(recordElement, "abcd:SourceInstitutionID"));
		specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		specimen.setOwner(SOURCE_INSTITUTION_ID);
		specimen.setSourceID("CRS");
		specimen.setLicenceType(LICENCE_TYPE);
		specimen.setLicence(LICENCE);
		specimen.setRecordBasis(val(recordElement, "abcd:RecordBasis"));
		specimen.setKindOfUnit(val(recordElement, "abcd:KindOfUnit"));
		specimen.setCollectionType(val(recordElement, "abcd:CollectionType"));
		specimen.setTitle(val(recordElement, "abcd:Title"));
		specimen.setNumberOfSpecimen(ival(recordElement, "abcd:AccessionSpecimenNumbers"));
		string = val(recordElement, "abcd:ObjectPublic");
		specimen.setObjectPublic(string == null || string.trim().equals("1"));
		string = val(recordElement, "abcd:MultiMediaPublic");
		specimen.setMultiMediaPublic(string == null || string.trim().equals("1"));
		string = val(recordElement, "abcd:FromCaptivity");
		specimen.setFromCaptivity(string != null && string.trim().equals("1"));
		string = val(recordElement, "abcd:PreparationType");
		if (string == null) {
			string = val(recordElement, "abcd:SpecimenMount");
		}
		specimen.setPreparationType(string);
		specimen.setPhaseOrStage(phaseOrStageNormalizer.getNormalizedValue(val(recordElement, "abcd:PhaseOrStage")));
		specimen.setTypeStatus(typeStatusNormalizer.getNormalizedValue(val(recordElement, "abcd:TypeStatus")));
		specimen.setSex(sexNormalizer.getNormalizedValue(val(recordElement, "abcd:Sex")));
		specimen.setGatheringEvent(transferGatheringEvent(recordElement));
		return specimen;
	}


	public static ESGatheringEvent transferGatheringEvent(Element recordElement)
	{
		final ESGatheringEvent ge = new ESGatheringEvent();
		ge.setProjectTitle(val(recordElement, "abcd:ProjectTitle"));
		ge.setWorldRegion(val(recordElement, "abcd:WorldRegion"));
		ge.setCountry(val(recordElement, "abcd:Country"));
		ge.setProvinceState(val(recordElement, "abcd:ProvinceState"));
		ge.setIsland(val(recordElement, "abcd:Island"));
		ge.setLocality(val(recordElement, "abcd:Locality"));
		ge.setLocalityText(val(recordElement, "abcd:LocalityText"));
		ge.setDateTimeBegin(date(recordElement, "abcd:CollectingStartDate"));
		ge.setDateTimeEnd(date(recordElement, "abcd:CollectingEndDate"));
		String s = val(recordElement, "abcd:GatheringAgent");
		if (s != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(s)));
		}
		Double lat = dval(recordElement, "abcd:LatitudeDecimal");
		Double lon = dval(recordElement, "abcd:LongitudeDecimal");
		if (lon != null && (lon < -180 || lon > 180)) {
			logger.error("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90 || lat > 90)) {
			logger.error("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}

		ge.setChronoStratigraphy(getChronoStratigraphyList(recordElement));
		ge.setBioStratigraphy(getBioStratigraphyList(recordElement));
		ge.setLithoStratigraphy(getLithoStratigraphyList(recordElement));

		return ge;
	}


	public static List<ChronoStratigraphy> getChronoStratigraphyList(Element recordElement)
	{

		List<Element> chronoStratigraphyElements = DOMUtil.getDescendants(recordElement, "ncrsChronoStratigraphy");
		if (chronoStratigraphyElements == null) {
			return null;
		}
		List<ChronoStratigraphy> result = new ArrayList<ChronoStratigraphy>(chronoStratigraphyElements.size());
		for (Element e : chronoStratigraphyElements) {
			ChronoStratigraphy one = getChronoStratigraphyObject(e);
			result.add(one);
		}
		return result;
	}


	private static ChronoStratigraphy getChronoStratigraphyObject(Element e)
	{
		ChronoStratigraphy one = new ChronoStratigraphy();
		one.setYoungRegionalSubstage(val(e, "abcd:YoungRegionalSubstage"));
		one.setYoungRegionalStage(val(e, "abcd:YoungRegionalStage"));
		one.setYoungRegionalSeries(val(e, "abcd:YoungRegionalSeries"));
		one.setYoungDatingQualifier(val(e, "abcd:YoungDatingQualifier"));
		one.setYoungInternSystem(val(e, "abcd:YoungInternSystem"));
		one.setYoungInternSubstage(val(e, "abcd:youngInternSubstage"));
		one.setYoungInternStage(val(e, "abcd:YoungInternStage"));
		one.setYoungInternSeries(val(e, "abcd:YoungInternSeries"));
		one.setYoungInternErathem(val(e, "abcd:YoungInternErathem"));
		one.setYoungInternEonothem(val(e, "abcd:YoungInternEonothem"));
		one.setYoungChronoName(val(e, "abcd:YoungChronoName"));
		one.setYoungCertainty(val(e, "abcd:YoungCertainty"));
		one.setOldDatingQualifier(val(e, "abcd:OldDatingQualifier"));
		one.setChronoPreferredFlag(bval(e, "abcd:ChronoPreferredFlag"));
		one.setOldRegionalSubstage(val(e, "abcd:OldRegionalSubstage"));
		one.setOldRegionalStage(val(e, "abcd:OldRegionalStage"));
		one.setOldRegionalSeries(val(e, "abcd:OldRegionalSeries"));
		one.setOldInternSystem(val(e, "abcd:OldInternSystem"));
		one.setOldInternSubstage(val(e, "abcd:OldInternSubstage"));
		one.setOldInternStage(val(e, "abcd:OldInternStage"));
		one.setOldInternSeries(val(e, "abcd:OldInternSeries"));
		one.setOldInternErathem(val(e, "abcd:OldInternErathem"));
		one.setOldInternEonothem(val(e, "abcd:OldInternEonothem"));
		one.setOldChronoName(val(e, "abcd:OldChronoName"));
		one.setChronoIdentifier(val(e, "abcd:ChronoIdentifier"));
		one.setOldCertainty(val(e, "abcd:OldCertainty"));

		return one;
	}


	public static List<BioStratigraphy> getBioStratigraphyList(Element recordElement)
	{
		List<Element> bioStratigraphyElements = DOMUtil.getDescendants(recordElement, "ncrsBioStratigraphy");
		if (bioStratigraphyElements == null) {
			return null;
		}
		List<BioStratigraphy> result = new ArrayList<BioStratigraphy>(bioStratigraphyElements.size());
		for (Element e : bioStratigraphyElements) {
			BioStratigraphy one = getBioStratigraphyObject(e);
			result.add(one);
		}
		return result;
	}


	private static BioStratigraphy getBioStratigraphyObject(Element e)
	{
		BioStratigraphy one = new BioStratigraphy();
		one.setYoungBioDatingQualifier(val(e, "abcd:YoungBioDatingQualifier"));
		one.setYoungBioName(val(e, "abcd:YoungBioName"));
		one.setYoungFossilZone(val(e, "abcd:YoungFossilZone"));
		one.setYoungFossilSubZone(val(e, "abcd:YoungFossilSubZone"));
		one.setYoungBioCertainty(val(e, "abcd:YoungBioCertainty"));
		one.setYoungStratType(val(e, "abcd:YoungStratType"));
		one.setBioDatingQualifier(val(e, "abcd:BioDatingQualifier"));
		one.setBioPreferredFlag(bval(e, "abcd:BioPreferredFlag"));
		one.setRangePosition(val(e, "abcd:RangePosition"));
		one.setOldBioName(val(e, "abcd:OldBioName"));
		one.setBioIdentifier(val(e, "abcd:BioIdentifier"));
		one.setOldFossilzone(val(e, "abcd:OldFossilzone"));
		one.setOldFossilSubzone(val(e, "abcd:OldFossilSubzone"));
		one.setOldBioCertainty(val(e, "abcd:OldBioCertainty"));
		one.setOldBioStratType(val(e, "abcd:OldBioStratType"));

		return one;
	}


	public static List<LithoStratigraphy> getLithoStratigraphyList(Element recordElement)
	{
		List<Element> lithoStratigraphyElements = DOMUtil.getDescendants(recordElement, "ncrsLithoStratigraphy");
		if (lithoStratigraphyElements == null) {
			return null;
		}
		List<LithoStratigraphy> result = new ArrayList<LithoStratigraphy>(lithoStratigraphyElements.size());
		for (Element e : lithoStratigraphyElements) {
			LithoStratigraphy one = getLithoStratigraphyObject(e);
			result.add(one);
		}
		return result;
	}


	private static LithoStratigraphy getLithoStratigraphyObject(Element e)
	{
		LithoStratigraphy one = new LithoStratigraphy();
		one.setQualifier(val(e, "abcd:Qualifier"));
		one.setPreferredFlag(bval(e, "abcd:PreferredFlag"));
		one.setMember2(val(e, "abcd:Member2"));
		one.setMember(val(e, "abcd:Member"));
		one.setInformalName2(val(e, "abcd:InformalName2"));
		one.setInformalName(val(e, "abcd:InformalName"));
		one.setImportedName2(val(e, "abcd:ImportedName2"));
		one.setImportedName1(val(e, "abcd:ImportedName1"));
		one.setLithoIdentifier(val(e, "abcd:LithoIdentifier"));
		one.setFormation2(val(e, "abcd:Formation2"));
		one.setFormationGroup2(val(e, "abcd:FormationGroup2"));
		one.setFormationGroup(val(e, "abcd:FormationGroup"));
		one.setFormation(val(e, "abcd:Formation"));
		one.setCertainty2(val(e, "abcd:Certainty2"));
		one.setCertainty(val(e, "abcd:Certainty"));
		one.setBed2(val(e, "abcd:Bed2"));
		one.setBed(val(e, "abcd:Bed"));

		return one;
	}


	public static SpecimenIdentification transferIdentification(Element determinationElement, String unitID)
	{

		ScientificName sn = getScientificName(determinationElement);
		if (sn.getFullScientificName() == null) {
			String fmt = "Missing scientific name in identification for record with UnitID %s";
			logger.error(String.format(fmt, unitID));
			return null;
		}

		final SpecimenIdentification identification = new SpecimenIdentification();

		String s = val(determinationElement, "abcd:PreferredFlag");
		identification.setPreferred(s == null || s.equals("1"));
		identification.setDateIdentified(date(determinationElement, "abcd:IdentificationDate"));
		identification.setAssociatedFossilAssemblage(val(determinationElement, "abcd:AssociatedFossilAssemblage"));
		identification.setAssociatedMineralName(val(determinationElement, "abcd:AssociatedMineralName"));
		identification.setRockMineralUsage(val(determinationElement, "abcd:RockMineralUsage"));
		identification.setRockType(val(determinationElement, "abcd:RockType"));

		identification.setScientificName(getScientificName(determinationElement));
		identification.setSystemClassification(getSystemClassification(determinationElement, identification.getScientificName()));
		DefaultClassification dc = DefaultClassification.fromSystemClassification(identification.getSystemClassification());
		identification.setDefaultClassification(dc);

		String infraspecificRank = val(determinationElement, "abcd:InfrasubspecificRank");

		if (infraspecificRank != null) {
			identification.setTaxonRank(infraspecificRank);
		}
		else if (identification.getScientificName().getInfraspecificEpithet() != null) {
			identification.setTaxonRank("subspecies");
		}
		else if (identification.getScientificName().getSpecificEpithet() != null) {
			identification.setTaxonRank("species");
		}
		else {
			identification.setTaxonRank("genus");
		}

		s = val(determinationElement, "abcd:InformalNameString");
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}

		return identification;
	}


	private static ScientificName getScientificName(Element determinationElement)
	{
		final ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(determinationElement, "abcd:FullScientificNameString"));
		sn.setGenusOrMonomial(val(determinationElement, "abcd:GenusOrMonomial"));
		sn.setSubgenus(val(determinationElement, "abcd:Subgenus"));
		sn.setSpecificEpithet(val(determinationElement, "abcd:SpeciesEpithet"));
		String s = val(determinationElement, "abcd:subspeciesepithet");
		if (s == null) {
			s = val(determinationElement, "abcd:InfrasubspecificName");
		}
		sn.setInfraspecificEpithet(s);
		sn.setNameAddendum(val(determinationElement, "abcd:NameAddendum"));
		sn.setAuthorshipVerbatim(val(determinationElement, "abcd:AuthorTeamOriginalAndYear"));
		if (sn.getFullScientificName() == null) {
			StringBuilder sb = new StringBuilder();
			if (sn.getGenusOrMonomial() != null) {
				sb.append(sn.getGenusOrMonomial()).append(' ');
			}
			else {
				String taxonCoverage = val(determinationElement, "abcd:taxonCoverage");
				if (taxonCoverage != null) {
					sb.append(taxonCoverage).append(' ');
				}
			}
			if (sn.getSubgenus() != null) {
				sb.append(sn.getSubgenus()).append(' ');
			}
			if (sn.getSpecificEpithet() != null) {
				sb.append(sn.getSpecificEpithet()).append(' ');
			}
			if (sn.getInfraspecificEpithet() != null) {
				sb.append(sn.getInfraspecificEpithet()).append(' ');
			}
			if (sn.getAuthorshipVerbatim() != null) {
				if (sn.getAuthorshipVerbatim().charAt(0) != '(') {
					sb.append('(');
				}
				sb.append(sn.getAuthorshipVerbatim());
				if (sn.getAuthorshipVerbatim().charAt(sn.getAuthorshipVerbatim().length() - 1) != ')') {
					sb.append(')');
				}
			}
			if (sb.length() != 0) {
				sn.setFullScientificName(sb.toString().trim());
			}
		}
		return sn;
	}


	private static List<Monomial> getSystemClassification(Element determinationElement, ScientificName sn)
	{
		List<Monomial> lowerClassification = TransferUtil.getMonomialsInName(sn);
		List<Element> elems = DOMUtil.getChildren(determinationElement, "ncrsHighername");
		if (elems == null) {
			return lowerClassification;
		}
		List<Monomial> systemClassification = new ArrayList<Monomial>(elems.size() + lowerClassification.size());
		for (Element e : elems) {
			String rank = DOMUtil.getValue(e, "abcd:HigherTaxonRank");
			String name = DOMUtil.getValue(e, "abcd:taxonCoverage");
			systemClassification.add(new Monomial(rank, name));
		}
		systemClassification.addAll(lowerClassification);
		return systemClassification;
	}


	static Date date(Element e, String tag)
	{
		return TransferUtil.parseDate(val(e, tag));
	}


	static Double dval(Element e, String tag)
	{
		String s = val(e, tag);
		if (s == null) {
			return null;
		}
		try {
			return Double.valueOf(s);
		}
		catch (NumberFormatException exc) {
			logger.warn(String.format("Invalid number in element %s: \"%s\"", tag, s));
			return null;
		}
	}


	static int ival(Element e, String tag)
	{
		String s = val(e, tag);
		if (s == null) {
			return 0;
		}
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException exc) {
			logger.warn(String.format("Invalid integer in element %s: \"%s\"", tag, s));
			return 0;
		}
	}


	static String val(Element e, String tag)
	{
		String s = DOMUtil.getDescendantValue(e, tag);
		if (s == null) {
			logger.trace(String.format("No element \"%s\" under element \"%s\"", tag, e.getTagName()));
			return null;
		}
		s = s.trim();
		if (s.length() == 0) {
			return null;
		}
		return s;
	}


	private static boolean bval(Element e, String tag)
	{
		String s = val(e, tag);
		return (s == null || s.equals("1"));
	}

}
