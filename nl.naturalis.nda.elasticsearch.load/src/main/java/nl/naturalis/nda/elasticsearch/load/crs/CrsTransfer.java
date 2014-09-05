package nl.naturalis.nda.elasticsearch.load.crs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.GatheringEvent;
import nl.naturalis.nda.domain.GatheringSiteCoordinates;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class CrsTransfer {

	private static final Logger logger = LoggerFactory.getLogger(CrsTransfer.class);
	private static final String ABCD_NAMESPACE_URI = "http://rs.tdwg.org/abcd/2.06/b/";

	private static final SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat("yyyyMMdd");


	public static Specimen transfer(Element recordElement)
	{
		final Specimen specimen = new Specimen();
		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setSourceSystemId(val(recordElement, "UnitID"));
		specimen.setUnitID(val(recordElement, "UnitID"));
		specimen.setUnitGUID(val(recordElement, "UnitGUID"));
		specimen.setSourceInstitutionID(val(recordElement, "SourceInstitutionID"));
		specimen.setRecordBasis(val(recordElement, "RecordBasis"));
		specimen.setKindOfUnit(val(recordElement, "KindOfUnit"));
		specimen.setCollectionType(val(recordElement, "CollectionType"));
		specimen.setSex(val(recordElement, "Sex"));
		specimen.setPhaseOrStage(val(recordElement, "PhaseOrStage"));
		specimen.setAccessionSpecimenNumbers(val(recordElement, "AccessionSpecimenNumbers"));
		specimen.setTitle(val(recordElement, "Title"));
		String s = val(recordElement, "ObjectPublic");
		specimen.setObjectPublic(s != null && s.trim().equals("1"));
		s = val(recordElement, "MultiMediaPublic");
		specimen.setMultiMediaPublic(s != null && s.trim().equals("1"));
		List<Element> determinationElements = DOMUtil.getChildren(recordElement, "ncrsDetermination");
		for (Element e : determinationElements) {
			specimen.addIndentification(transferIdentification(e));
		}
		specimen.setGatheringEvent(transferGatheringEvent(recordElement));
		return specimen;
	}


	public static GatheringEvent transferGatheringEvent(Element recordElement)
	{
		final GatheringEvent ge = new GatheringEvent();
		ge.setWorldRegion(val(recordElement, "WorldRegion"));
		ge.setCountry(val(recordElement, "Country"));
		ge.setProvinceState(val(recordElement, "ProvinceState"));
		ge.setIsland(val(recordElement, "Island"));
		ge.setLocality(val(recordElement, "Locality"));
		ge.setLocalityText(val(recordElement, "LocalityText"));
		ge.setDateTimeBegin(date(recordElement,"CollectingStartDate"));
		ge.setDateTimeEnd(date(recordElement,"CollectingEndDate"));
		String s = val(recordElement, "GatheringAgent");
		if (s != null) {
			ge.addGatheringAgent(new Agent(s));
		}
		Double lat = dval(recordElement, "LatitudeDecimal");
		Double lon = dval(recordElement, "LongitudeDecimal");
		if (lat != null || lon != null) {
			ge.addSiteCoordinates(new GatheringSiteCoordinates(lat, lon));
		}
		return ge;
	}


	public static SpecimenIdentification transferIdentification(Element determinationElement)
	{
		final SpecimenIdentification si = new SpecimenIdentification();
		String s = val(determinationElement, "NameAddendum");
		si.setPreferred(s != null && s.equals("1"));
		si.setDateIdentified(date(determinationElement, "IdentificationDate"));

		ScientificName sn = transferScientificName(determinationElement);
		si.setScientificName(sn);

		String infraspecificRank = val(determinationElement, "InfrasubspecificRank");

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

		String taxonCoverage = val(determinationElement, "taxonCoverage");
		String higherTaxonRank = val(determinationElement, "HigherTaxonRank");

		if (taxonCoverage != null && higherTaxonRank != null) {
			Monomial monomial = new Monomial(higherTaxonRank, taxonCoverage);
			si.setSystemClassification(Arrays.asList(monomial));
			DefaultClassification.Rank rank = DefaultClassification.Rank.forName(higherTaxonRank);
			if (rank != null) {
				dc.set(rank, taxonCoverage);
			}
		}

		return si;
	}


	private static ScientificName transferScientificName(Element determinationElement)
	{
		final ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(determinationElement, "FullScientificNameString"));
		sn.setGenusOrMonomial(val(determinationElement, "GenusOrMonomial"));
		sn.setSubgenus(val(determinationElement, "Subgenus"));
		sn.setSpecificEpithet(val(determinationElement, "SpeciesEpithet"));
		String s = val(determinationElement, "subspeciesepithet");
		if (s == null) {
			s = val(determinationElement, "InfrasubspecificName");
		}
		sn.setInfraspecificEpithet(s);
		sn.setNameAddendum(val(determinationElement, "NameAddendum"));
		sn.setAuthorshipVerbatim(val(determinationElement, "AuthorTeamOriginalAndYear"));
		return sn;
	}


	private static Date date(Element e, String tag)
	{
		String s = val(e, tag);
		if (s == null) {
			return null;
		}
		try {
			return DATE_FORMAT0.parse(s);
		}
		catch (ParseException e1) {
			// TODO try another format
			logger.warn(String.format("Invalid date in element %s: \"%s\"", tag, s));
			return null;
		}
	}


	private static Double dval(Element e, String tag)
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


	private static String val(Element e, String tag)
	{
		String s = DOMUtil.getDescendantValue(e, tag, ABCD_NAMESPACE_URI);
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
