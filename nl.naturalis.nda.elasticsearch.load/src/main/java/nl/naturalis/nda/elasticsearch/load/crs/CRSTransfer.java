package nl.naturalis.nda.elasticsearch.load.crs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import nl.naturalis.nda.elasticsearch.estypes.ESCrsDetermination;
import nl.naturalis.nda.elasticsearch.estypes.ESCrsSpecimen;
import nl.naturalis.nda.elasticsearch.load.HarvestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Data Transfer Object converting XML elements to NDA domain objects.
 * 
 * @author ayco_holleman
 * 
 */
class CRSTransfer {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CRSTransfer.class);
	private static final String ABCD_NAMESPACE_URI = "http://rs.tdwg.org/abcd/2.06/b/";

	//@formatter:off
	private static final HashSet<String> determinationElements = new HashSet<String>(Arrays.asList(
			"PreferredFlag",
			"ScientificName",
			"HigherTaxonRank",
			"GenusOrMonomial",
			"Subgenus",
			"SpeciesEpithet",
			"InfrasubspecificRank",
			"subspeciesepithet",
			"InfrasubspecificName",
			"AuthorTeamOriginalAndYear",
			"TypeStatus",
			"NameAddendum",
			"IdentificationQualifier1",
			"IdentificationQualifier2",
			"IdentificationQualifier3",
			"taxonCoverage"
	));
	//@formatter:on

	public static ESCrsSpecimen createSpecimen(Element record)
	{
		final ESCrsSpecimen specimen = new ESCrsSpecimen();
		specimen.setSystemID(getValue(record, "identifier"));
		specimen.setUnitID(getAbcdValue(record, "UnitID"));
		specimen.setRecordBasis(getAbcdValue(record, "RecordBasis"));
		specimen.setKindOfUnit(getAbcdValue(record, "KindOfUnit"));
		specimen.setSourceInstitutionID(getAbcdValue(record, "SourceInstitutionID"));
		specimen.setUnitGUID(getAbcdValue(record, "UnitGUID"));
		specimen.setSex(getAbcdValue(record, "Sex"));
		specimen.setPhaseOrStage(getAbcdValue(record, "PhaseOrStage"));
		specimen.setAccessionSpecimenNumbers(getAbcdValue(record, "AccessionSpecimenNumbers"));
		specimen.setAltitude(getAbcdValue(record, "Altitude"));
		specimen.setDepth(getAbcdValue(record, "Depth"));
		specimen.setGatheringAgent(getAbcdValue(record, "GatheringAgent"));
		specimen.setWorldRegion(getAbcdValue(record, "WorldRegion"));
		specimen.setCountry(getAbcdValue(record, "Country"));
		specimen.setProvinceState(getAbcdValue(record, "ProvinceState"));
		specimen.setLocality(getAbcdValue(record, "Locality"));
		String val = getAbcdValue(record, "ObjectPublic");
		specimen.setObjectPublic(val != null && val.trim().equals("1"));
		specimen.setAltitudeUnit(getAbcdValue(record, "AltitudeUnit"));
		specimen.setDepthUnit(getAbcdValue(record, "DepthUnit"));
		specimen.setCollectingStartDate(getAbcdValue(record, "CollectingStartDate"));
		specimen.setCollectingEndDate(getAbcdValue(record, "CollectingEndDate"));
		specimen.setTitle(getAbcdValue(record, "Title"));
		specimen.setTaxonCoverage(getAbcdValue(record, "taxonCoverage"));
		specimen.setMultiMediaPublic(getAbcdValue(record, "MultiMediaPublic"));
		specimen.setLatitudeDecimal(getAbcdValue(record, "LatitudeDecimal"));
		specimen.setLongitudeDecimal(getAbcdValue(record, "LongitudeDecimal"));
		specimen.setGeodeticDatum(getAbcdValue(record, "geodeticDatum"));
		specimen.setUrl(getAbcdValue(record, "Url"));
		return specimen;
	}


	//	private static void addDeterminations(CrsSpecimen specimen, Element[] abcd)
	//	{
	//		CrsDetermination determination = null;
	//		for (int i = 0; i < abcd.length; ++i) {
	//			String tag = abcd[i].getLocalName();
	//			String val = abcd[i].getTextContent();
	//			if (tag.equals("PreferredFlag")) {
	//				determination = new CrsDetermination();
	//				specimen.addDetermination(determination);
	//				determination.setPreferred(val.equals("1"));
	//			}
	//			else if (determinationElements.contains(tag)) {
	//				if (tag.equals("ScientificName")) {
	//					determination.setScientificName(val);
	//				}
	//				else if (tag.equals("HigherTaxonRank")) {
	//					determination.setHigherTaxonRank(val);
	//				}
	//				else if (tag.equals("GenusOrMonomial")) {
	//					determination.setGenusOrMonomial(val);
	//				}
	//				else if (tag.equals("Subgenus")) {
	//					determination.setSubgenus(val);
	//				}
	//				else if (tag.equals("SpeciesEpithet")) {
	//					determination.setSpeciesEpithet(val);
	//				}
	//				else if (tag.equals("InfrasubspecificRank")) {
	//					determination.setInfraSubspecificRank(val);
	//				}
	//				else if (tag.equals("subspeciesepithet")) {
	//					determination.setSubspeciesEpithet(val);
	//				}
	//				else if (tag.equals("InfrasubspecificName")) {
	//					determination.setInfraSubspecificName(val);
	//				}
	//				else if (tag.equals("AuthorTeamOriginalAndYear")) {
	//					determination.setAuthorTeamOriginalAndYear(val);
	//				}
	//				else if (tag.equals("TypeStatus")) {
	//					determination.setTypeStatus(val);
	//				}
	//				else if (tag.equals("NameAddendum")) {
	//					determination.setNameAddendum(val);
	//				}
	//				else if (tag.equals("IdentificationQualifier1")) {
	//					determination.setIdentificationQualifier1(val);
	//				}
	//				else if (tag.equals("IdentificationQualifier2")) {
	//					determination.setIdentificationQualifier2(val);
	//				}
	//				else if (tag.equals("IdentificationQualifier3")) {
	//					determination.setIdentificationQualifier3(val);
	//				}
	//			}
	//		}
	//	}

	public static List<ESCrsDetermination> getDeterminations(Element record)
	{
		Element[] abcd = getAbcdElements(record);
		List<ESCrsDetermination> determinations = new ArrayList<ESCrsDetermination>(4);
		ESCrsDetermination determination = null;
		for (int i = 0; i < abcd.length; ++i) {
			String tag = abcd[i].getLocalName();
			String val = abcd[i].getTextContent();
			if (tag.equals("PreferredFlag")) {
				determination = new ESCrsDetermination();
				determination.setPreferred(val.equals("1"));
				determinations.add(determination);
			}
			else if (determinationElements.contains(tag)) {
				if (tag.equals("ScientificName")) {
					determination.setScientificName(val);
				}
				else if (tag.equals("HigherTaxonRank")) {
					determination.setHigherTaxonRank(val);
				}
				else if (tag.equals("GenusOrMonomial")) {
					determination.setGenusOrMonomial(val);
				}
				else if (tag.equals("Subgenus")) {
					determination.setSubgenus(val);
				}
				else if (tag.equals("SpeciesEpithet")) {
					determination.setSpeciesEpithet(val);
				}
				else if (tag.equals("InfrasubspecificRank")) {
					determination.setInfraSubspecificRank(val);
				}
				else if (tag.equals("subspeciesepithet")) {
					determination.setSubspeciesEpithet(val);
				}
				else if (tag.equals("InfrasubspecificName")) {
					determination.setInfraSubspecificName(val);
				}
				else if (tag.equals("AuthorTeamOriginalAndYear")) {
					determination.setAuthorTeamOriginalAndYear(val);
				}
				else if (tag.equals("TypeStatus")) {
					determination.setTypeStatus(val);
				}
				else if (tag.equals("NameAddendum")) {
					determination.setNameAddendum(val);
				}
				else if (tag.equals("IdentificationQualifier1")) {
					determination.setIdentificationQualifier1(val);
				}
				else if (tag.equals("IdentificationQualifier2")) {
					determination.setIdentificationQualifier2(val);
				}
				else if (tag.equals("IdentificationQualifier3")) {
					determination.setIdentificationQualifier3(val);
				}
			}
		}
		Collections.sort(determinations, new Comparator<ESCrsDetermination>() {
			@Override
			public int compare(ESCrsDetermination d1, ESCrsDetermination d2)
			{
				if (d1.isPreferred() && !d2.isPreferred()) {
					return -1;
				}
				if (d2.isPreferred() && !d1.isPreferred()) {
					return 1;
				}
				return 0;
			}
		});
		return determinations;
	}


	private static Element[] getAbcdElements(Element record)
	{
		NodeList nl = record.getElementsByTagNameNS(ABCD_NAMESPACE_URI, "*");
		Element[] elements = new Element[nl.getLength()];
		for (int i = 0; i < elements.length; ++i) {
			elements[i] = (Element) nl.item(i);
		}
		return elements;
	}


	private static String getAbcdValue(Element record, String tag)
	{
		NodeList nl = record.getElementsByTagNameNS(ABCD_NAMESPACE_URI, tag);
		return nl.getLength() == 0 ? null : nl.item(0).getTextContent();
	}


	static Element getDescendant(Element ancestor, String descendant)
	{
		NodeList nl = ancestor.getElementsByTagName(descendant);
		if (nl.getLength() == 1 && nl.item(0) instanceof Element) {
			return (Element) nl.item(0);
		}
		String name = ancestor.getTagName() == null ? ancestor.getLocalName() : ancestor.getTagName();
		String pattern = "getValue() requires %s to be a unique element under %s (found %s descendants with same name)";
		throw new HarvestException(String.format(pattern, descendant, name, nl.getLength()));
	}


	static String getValue(Element ancestor, String descendant)
	{
		return getDescendant(ancestor, descendant).getTextContent();
	}

}
