package nl.naturalis.nda.elasticsearch.load.crs;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
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

	public static void main(String[] args)
	{
		System.out.println("Hallo");
	}

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final SexNormalizer sexNormalizer = SexNormalizer.getInstance();
	private static final PhaseOrStageNormalizer phaseOrStageNormalizer = PhaseOrStageNormalizer.getInstance();
	private static final ThematicSearchConfig thematicSearchConfig = ThematicSearchConfig.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(CrsSpecimenTransfer.class);


	public static ESSpecimen transfer(Element recordElement)
	{
		final ESSpecimen specimen = new ESSpecimen();
		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setUnitID(val(recordElement, "abcd:UnitID"));
		specimen.setSourceSystemId(specimen.getUnitID());
		List<String> themes = thematicSearchConfig.getThemesForDocument(specimen.getUnitID(), DocumentType.SPECIMEN);
		specimen.setThemes(themes);
		specimen.setUnitGUID(val(recordElement, "abcd:UnitGUID"));
		specimen.setCollectorsFieldNumber(val(recordElement, "abcd:CollectorsFieldNumber"));
		specimen.setSourceInstitutionID(val(recordElement, "abcd:SourceInstitutionID"));
		specimen.setRecordBasis(val(recordElement, "abcd:RecordBasis"));
		specimen.setKindOfUnit(val(recordElement, "abcd:KindOfUnit"));
		specimen.setCollectionType(val(recordElement, "abcd:CollectionType"));
		specimen.setTitle(val(recordElement, "abcd:Title"));
		specimen.setNumberOfSpecimen(ival(recordElement, "abcd:AccessionSpecimenNumbers"));
		String s = val(recordElement, "abcd:ObjectPublic");
		specimen.setObjectPublic(s == null || s.trim().equals("1"));
		s = val(recordElement, "abcd:MultiMediaPublic");
		specimen.setMultiMediaPublic(s == null || s.trim().equals("1"));
		s = val(recordElement, "abcd:FromCaptivity");
		specimen.setFromCaptivity(s != null && s.trim().equals("1"));
		s = val(recordElement, "abcd:PreparationType");
		if (s == null) {
			s = val(recordElement, "abcd:SpecimenMount");
		}
		specimen.setPreparationType(s);
		specimen.setPhaseOrStage(phaseOrStageNormalizer.getNormalizedValue(val(recordElement, "abcd:PhaseOrStage")));
		specimen.setTypeStatus(typeStatusNormalizer.getNormalizedValue(val(recordElement, "abcd:TypeStatus")));
		specimen.setSex(sexNormalizer.getNormalizedValue(val(recordElement, "abcd:Sex")));
		List<Element> determinationElements = DOMUtil.getChildren(recordElement, "ncrsDetermination");
		for (Element e : determinationElements) {
			specimen.addIndentification(transferIdentification(e));
		}
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
		return ge;
	}


	public static SpecimenIdentification transferIdentification(Element determinationElement)
	{
		final SpecimenIdentification si = new SpecimenIdentification();
		String s = val(determinationElement, "abcd:NameAddendum");
		si.setPreferred(s != null && s.equals("1"));
		si.setDateIdentified(date(determinationElement, "abcd:IdentificationDate"));
		si.setAssociatedFossilAssemblage(val(determinationElement, "abcd:AssociatedFossilAssemblage"));
		si.setAssociatedMineralName(val(determinationElement, "abcd:AssociatedMineralName"));
		si.setRockMineralUsage(val(determinationElement, "abcd:RockMineralUsage"));
		si.setRockType(val(determinationElement, "abcd:RockType"));

		ScientificName sn = transferScientificName(determinationElement);
		si.setScientificName(sn);

		String infraspecificRank = val(determinationElement, "abcd:InfrasubspecificRank");

		if (infraspecificRank != null) {
			si.setTaxonRank(infraspecificRank);
		}
		else if (sn.getInfraspecificEpithet() != null) {
			si.setTaxonRank("subspecies");
		}
		else if (sn.getSpecificEpithet() != null) {
			si.setTaxonRank("species");
		}
		else {
			si.setTaxonRank("genus");
		}

		DefaultClassification dc = new DefaultClassification();
		dc.setGenus(sn.getGenusOrMonomial());
		dc.setSpecificEpithet(sn.getSpecificEpithet());
		dc.setInfraspecificRank(infraspecificRank);
		dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());

		String taxonCoverage = val(determinationElement, "abcd:taxonCoverage");
		String higherTaxonRank = val(determinationElement, "abcd:HigherTaxonRank");

		if (taxonCoverage != null && higherTaxonRank != null) {
			Monomial monomial = new Monomial(higherTaxonRank, taxonCoverage);
			si.setSystemClassification(Arrays.asList(monomial));
			DefaultClassification.Rank rank = DefaultClassification.Rank.forName(higherTaxonRank);
			if (rank != null) {
				dc.set(rank, taxonCoverage);
			}
		}

		s = val(determinationElement, "abcd:InformalNameString");
		if (s != null) {
			si.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}

		return si;
	}


	private static ScientificName transferScientificName(Element determinationElement)
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
		return sn;
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
			logger.debug(String.format("No element \"%s\" under element \"%s\"", tag, e.getTagName()));
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
			logger.debug(String.format("No element \"%s\" under element \"%s\"", tag, e.getTagName()));
			return null;
		}
		s = s.trim();
		if (s.length() == 0) {
			return null;
		}
		return s;
	}

}
