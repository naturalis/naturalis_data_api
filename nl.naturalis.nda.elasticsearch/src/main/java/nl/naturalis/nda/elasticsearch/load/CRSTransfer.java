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
import org.domainobject.util.debug.BeanPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
		"GenusOrMonomial"			, "genusOrMonomial",
		"Subgenus"					, "subgenus",
		"SpeciesEpithet"			, "speciesEpithet",
		"InfrasubspecificRank"		, "infrasubspecificRank",
		"subspeciesepithet"			, "subspeciesepithet",
		"InfrasubspecificName"		, "infrasubspecificName",
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
		"geodeticDatum"				, "geodeticDatum"
	};
	
	private static final String[] DERMINATION_ELEMENTS_ARRAY = new String[] {
		"PreferredFlag",
		"ScientificName",
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
		"IdentificationQualifier3"		
	};
	//@formatter:on

	static final HashMap<String, String> crsToNda = new HashMap<String, String>(CRS_TO_NDA_TUPLES.length / 2, 1.0F);
	static final HashSet<String> determinationElements = new HashSet<String>(Arrays.asList(DERMINATION_ELEMENTS_ARRAY));

	private static final BeanPrinter bp = new BeanPrinter("C:/tmp/bp.txt");

	static {
		for (int i = 0; i < CRS_TO_NDA_TUPLES.length; i += 2) {
			crsToNda.put(CRS_TO_NDA_TUPLES[i], CRS_TO_NDA_TUPLES[i + 1]);
		}
	}


	public static Specimen createSpecimen(Element record)
	{
		Specimen specimen = new Specimen();
		specimen.setSourceSystemName("CRS");
		specimen.setSourceSystemId(getValue(record, "identifier"));
		setSpecimenFields(specimen, record);
		addDeterminations(specimen, record);
		return specimen;
	}


	private static void setSpecimenFields(Specimen specimen, Element record)
	{
		NodeList abcdElements = record.getElementsByTagNameNS(ABCD_NAMESPACE_URI, "*");
		for (int i = 0; i < abcdElements.getLength(); ++i) {
			Element e = (Element) abcdElements.item(i);
			String tag = e.getLocalName();
			if (determinationElements.contains(tag)) {
				continue;
			}
			String fieldName = crsToNda.get(tag);
			if (fieldName == null) {
				logger.warn("Unexpected element in XML: " + fieldName);
				continue;
			}
			if (fieldName.equals(NOT_MAPPED)) {
				logger.debug("Skipping unmapped element: " + fieldName);
				continue;
			}
			setValue(specimen, e);
		}
	}


	private static void addDeterminations(Specimen specimen, Element record)
	{
		NodeList preferredFlags = record.getElementsByTagNameNS(ABCD_NAMESPACE_URI, "PreferredFlag");
		for (int i = 0; i < preferredFlags.getLength(); ++i) {
			Element preferredFlag = (Element) preferredFlags.item(i);
			Determination determination = createDetermination(preferredFlag);
			specimen.addDetermination(determination);
		}
	}


	private static Determination createDetermination(Element e)
	{
		Determination determination = new Determination();
		while (true) {
			setValue(determination, e);
			System.out.println("XXX: " + e.getNodeName());
			Node n = e.getNextSibling();
			if (n == null) {
				break;
			}
			if (n instanceof Element) {
				e = (Element) n;
				if (e.getLocalName().equals("PreferredFlag")) {
					break;
				}
				if (!determinationElements.contains(e.getLocalName())) {
					break;
				}
			}
		}
		return determination;
	}


	private static void setValue(Object obj, Element e)
	{
		String tag = e.getLocalName();
		String fieldName = crsToNda.get(tag);
		if (fieldName == null) {
			logger.warn("Unexpected element in XML: " + tag);
			return;
		}
		if (fieldName.equals(NOT_MAPPED)) {
			logger.debug("Skipping unmapped element: " + tag);
			return;
		}
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
			throw new HarvestException("No field \"" + fieldName + "\" in class " + obj.getClass().getName());
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}


	private static String getValue(Element ancestor, String descendant)
	{
		NodeList nl = ancestor.getElementsByTagName(descendant);
		if (nl.getLength() == 1) {
			return nl.item(0).getTextContent();
		}
		String ancestorName = ancestor.getTagName() == null ? ancestor.getLocalName() : ancestor.getTagName();
		String pattern = "getValue() requires exactly one descendant. Element %s has %s descendent %s elements";
		throw new HarvestException(String.format(pattern, ancestorName, nl.getLength(), descendant));
	}

}
