package nl.naturalis.nda.elasticsearch.load.crs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class CrsMultiMediaTransfer {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final SexNormalizer sexNormalizer = SexNormalizer.getInstance();
	private static final PhaseOrStageNormalizer phaseOrStageNormalizer = PhaseOrStageNormalizer.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(CrsMultiMediaTransfer.class);


	public static List<ESMultiMediaObject> transfer(Element recordElement)
	{
		Element dcElement = DOMUtil.getDescendant(recordElement, "oai_dc:dc");
		List<Element> mediaFileElements = DOMUtil.getDescendants(dcElement, "frmDigitalebestanden");
		if (mediaFileElements == null) {
			// Wired but it happens
			return new ArrayList<ESMultiMediaObject>(0);
		}
		List<MultiMediaContentIdentification> identifications = getIdentifications(dcElement);
		ESGatheringEvent gatheringEvent = getGatheringEvent(dcElement);
		String associatedSpecimenReference = val(dcElement, "ac:associatedSpecimenReference");
		String phaseOrStage = phaseOrStageNormalizer.getNormalizedValue(val(recordElement, "dwc:lifeStage"));
		List<String> phaseOrStages = phaseOrStage == null ? null : Arrays.asList(phaseOrStage);
		String typeStatus = typeStatusNormalizer.getNormalizedValue(val(recordElement, "abcd:TypeStatus"));
		String sex = sexNormalizer.getNormalizedValue(val(recordElement, "abcd:Sex"));
		List<String> sexes = sex == null ? null : Arrays.asList(sex);
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(mediaFileElements.size());
		for (Element mediaFileElement : mediaFileElements) {
			String url = val(mediaFileElement, "abcd:fileuri");
			if (url == null) {
				logger.error("No Image URL for record with identifier " + val(recordElement, "identifier"));
				continue;
			}
			String title = val(mediaFileElement, "dc:title");
			if (title == null) {
				logger.error("Missing title for record with identifier " + val(recordElement, "identifier"));
				continue;
			}
			ESMultiMediaObject mmo = new ESMultiMediaObject();
			mmos.add(mmo);
			mmo.addServiceAccessPoint(new ServiceAccessPoint(url, "JPG", Variant.GOOD_QUALITY));
			mmo.setSourceSystem(SourceSystem.CRS);
			mmo.setSourceSystemId(title);
			mmo.setUnitID(title);
			mmo.setTitle(title);
			mmo.setCaption(title);
			mmo.setAssociatedSpecimenReference(associatedSpecimenReference);
			mmo.setSpecimenTypeStatus(typeStatus);
			mmo.setGatheringEvents(Arrays.asList(gatheringEvent));
			mmo.setIdentifications(identifications);
			mmo.setSexes(sexes);
			mmo.setPhasesOrStages(phaseOrStages);
			mmo.setMultiMediaPublic(bval(mediaFileElement, "abcd:MultiMediaPublic"));
			mmo.setCreator(val(mediaFileElement, "dc:creator"));
		}
		return mmos;
	}


	private static ESGatheringEvent getGatheringEvent(Element dcElement)
	{
		ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(val(dcElement, "Iptc4xmpExt:WorldRegion"));
		ge.setCountry(val(dcElement, "Iptc4xmpExt:CountryName"));
		ge.setProvinceState(val(dcElement, "Iptc4xmpExt:ProvinceState"));
		ge.setSublocality(val(dcElement, "Iptc4xmpExt:Sublocation"));
		Double lat = dval(dcElement, "dwc:decimalLatitude");
		if (lat != null && (lat < -90 || lat > 90)) {
			logger.error("Invalid latitude: " + lat);
			lat = null;
		}
		Double lon = dval(dcElement, "dwc:decimalLongitude");
		if (lon != null && (lon < -180 || lon > 180)) {
			logger.error("Invalid latitude: " + lon);
			lon = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}
		String s = val(dcElement, "abcd:GatheringAgent");
		if (s != null) {
			Person agent = new Person();
			ge.setGatheringPersons(Arrays.asList(agent));
			agent.setFullName(s);
		}
		return ge;
	}


	private static List<MultiMediaContentIdentification> getIdentifications(Element dcElement)
	{
		List<Element> elems = DOMUtil.getDescendants(dcElement, "ncrsDetermination");
		List<MultiMediaContentIdentification> identifications = new ArrayList<MultiMediaContentIdentification>(elems.size());
		for (Element e : elems) {
			MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
			identifications.add(identification);
			ScientificName sn = new ScientificName();
			identification.setScientificName(sn);
			sn.setFullScientificName(val(e, "dwc:scientificName"));
			sn.setGenusOrMonomial(val(e, "abcd:GenusOrMonomial"));
			sn.setSpecificEpithet(val(e, "abcd:SpeciesEpithet"));
			sn.setInfraspecificEpithet(val(e, "abcd:subspeciesepithet"));
			sn.setNameAddendum(val(e, "abcd:NameAddendum"));
			sn.setAuthorshipVerbatim(val(e, "dwc:nameAccordingTo"));
			identification.setDefaultClassification(TransferUtil.extractClassificiationFromName(sn));
			String s = val(e, "abcd:IdentificationQualifier1");
			if (s != null) {
				List<String> qualifiers = new ArrayList<String>(3);
				identification.setIdentificationQualifiers(qualifiers);
				qualifiers.add(s);
				s = val(e, "abcd:IdentificationQualifier2");
				if (s != null) {
					qualifiers.add(s);
				}
				s = val(e, "abcd:IdentificationQualifier3");
				if (s != null) {
					qualifiers.add(s);
				}
			}
		}
		return identifications;
	}


	private static Double dval(Element e, String tag)
	{
		return CrsSpecimenTransfer.dval(e, tag);
	}


	private static boolean bval(Element e, String tag)
	{
		String s = val(e, tag);
		return (s == null || s.equals("1"));
	}


	private static String val(Element e, String tag)
	{
		return CrsSpecimenTransfer.val(e, tag);
	}

}
