package nl.naturalis.nda.elasticsearch.load;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import nl.naturalis.nda.domain.Determination;
import nl.naturalis.nda.domain.Specimen;

import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CRSTransfer {

	private static final Logger logger = LoggerFactory.getLogger(CRSTransfer.class);

	private static final String ABCD_NAMESPACE_URI = "http://rs.tdwg.org/abcd/2.06/b/";
	private static final String NOT_MAPPED = "@";

	//@formatter:off
	private static final String[] CRS_TO_NDA_TUPLES = new String[] {
		NOT_MAPPED					, "sourceSystemName",
		"identifier"				, "sourceSystemId",
		"UnitID"					, "specimenId",
		"RecordBasis"				, "recordBasis",
		"KindOfUnit"				, "kindOfUnit",
		"SourceInstitutionID"		, "sourceInstitutionID",
		"UnitGUID"					, "phylum",
		"Sex"						, "sex",
		"PhaseOrStage"				, "phaseOrStage",
		"AccessionSpecimenNumbers"	, "accessionSpecimenNumbers",
		"Altitude"					, "altitude",
		"Depth"						, "depth",
		"PreferredFlag"				, "preferred",
		"ScientificName"			, "scientificName",
		"HigherTaxonRank"			, "higherTaxonRank",
		"GenusOrMonomial"			, "genusOrMonomial",
		"Subgenus"					, "subgenus",
		"SpeciesEpithet"			, "speciesEpithet",
		"InfrasubspecificRank"		, "infraSubspecificRank",
		"subspeciesepithet"			, "subspeciesEpithet",
		"InfrasubspecificName"		, "infraSubspecificName",
		"AuthorTeamOriginalAndYear"	, "authorTeamOriginalAndYear",
		"TypeStatus"				, "typeStatus",
		"NameAddendum"				, "nameAddendum",
		"IdentificationQualifier1"	, "identificationQualifier1",
		"IdentificationQualifier2"	, "identificationQualifier2",
		"IdentificationQualifier3"	, "identificationQualifier3",
		"GatheringAgent"			, "gatheringAgent",
		"WorldRegion"				, "worldRegion",
		"Country"					, "country",
		"ProvinceState"				, "provinceState",
		"Locality"					, "locality",
		"ObjectPublic"				, "publicObject",
		"AltitudeUnit"				, "altitudeUnit",
		"DepthUnit"					, "depthUnit",
		"CollectingStartDate"		, "collectingStartDate",
		"CollectingEndDate"			, "collectingEndDate",
		"Title"						, "title",
		"taxonCoverage"				, "taxonCoverage",
		"MultiMediaPublic"			, "multiMediaPublic",
		"LatitudeDecimal"			, "latitudeDecimal",
		"LongitudeDecimal"			, "longitudeDecimal",
		"geodeticDatum"				, "geodeticDatum",
		"taxonCoverage"				, NOT_MAPPED
	};
	
	private static final String[] DERMINATION_ELEMENTS_ARRAY = new String[] {
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
	};
	//@formatter:on

	static final HashMap<String, String> mapping = new HashMap<String, String>(CRS_TO_NDA_TUPLES.length / 2, 1.0F);
	static final HashSet<String> determinationElements = new HashSet<String>(Arrays.asList(DERMINATION_ELEMENTS_ARRAY));

	static {
		for (int i = 0; i < CRS_TO_NDA_TUPLES.length; i += 2) {
			mapping.put(CRS_TO_NDA_TUPLES[i], CRS_TO_NDA_TUPLES[i + 1]);
		}
	}


	public static Specimen createSpecimen(Element record)
	{
		Specimen specimen = new Specimen();
		specimen.setSourceSystemName("CRS");
		specimen.setSourceSystemId(getValue(record, "identifier"));
		Element[] abcdElements = getAbcdElements(record);
		setSpecimenFields(specimen, abcdElements);
		addDeterminations(specimen, abcdElements);
		return specimen;
	}


	private static void setSpecimenFields(Specimen specimen, Element[] abcdElements)
	{
		for (int i = 0; i < abcdElements.length; ++i) {
			Element e = abcdElements[i];
			String tag = e.getLocalName();
			if (determinationElements.contains(tag)) {
				continue;
			}
			String field = mapping.get(tag);
			if (field == null || field.equals(NOT_MAPPED)) {
				logger.info("Skipping unmapped element: " + tag);
				continue;
			}
			setValue(specimen, e);
		}
	}


	private static void addDeterminations(Specimen specimen, Element[] abcd)
	{
		Determination determination = null;
		for (int i = 0; i < abcd.length; ++i) {
			if (abcd[i].getLocalName().equals("PreferredFlag")) {
				determination = new Determination();
				specimen.addDetermination(determination);
			}
			String tag = abcd[i].getLocalName();
			if (determinationElements.contains(tag)) {
				String field = mapping.get(tag);
				if (field == null || field.equals(NOT_MAPPED)) {
					logger.info("Skipping unmapped element: " + tag);
				}
				else {
					setValue(determination, abcd[i]);
				}
			}
		}
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


	private static void setValue(Object obj, Element e)
	{
		String tag = e.getLocalName();
		String fieldName = mapping.get(tag);
		String val = e.getTextContent();
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			Class<?> type = field.getType();
			if (type == String.class) {
				field.set(obj, val);
			}
			else if (type == int.class || type == Integer.class) {
				val = val.trim().equals("") ? "0" : val;
				Integer i = Integer.valueOf(val);
				field.set(obj, i);
			}
			else if (type == boolean.class || type == Boolean.class) {
				val = val.trim().equals("") ? "false" : val;
				Boolean b = new Boolean(StringUtil.asBoolean(val));
				field.set(obj, b);
			}
			else if (type == Date.class) {
				////////////////////////////////
				// TODO: create date from string
				////////////////////////////////
				field.set(obj, new Date());
			}
			else {
				throw new HarvestException("Cannot set fields of type " + type.getName());
			}
		}
		catch (NoSuchFieldException t) {
			String pattern = "Skipping bad mapping: no field \"%s\" in class %s";
			logger.warn(String.format(pattern, fieldName, obj.getClass().getSimpleName()));
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}


	private static Element getDescendant(Element ancestor, String descendant)
	{
		NodeList nl = ancestor.getElementsByTagName(descendant);
		if (nl.getLength() == 1 && nl.item(0) instanceof Element) {
			return (Element) nl.item(0);
		}
		String name = ancestor.getTagName() == null ? ancestor.getLocalName() : ancestor.getTagName();
		String pattern = "getValue() requires %s to be a unique element under %s (found %s descendants with same name)";
		throw new HarvestException(String.format(pattern, descendant, name, nl.getLength()));
	}


	private static String getValue(Element ancestor, String descendant)
	{
		return getDescendant(ancestor, descendant).getTextContent();
	}

}
