package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.DocumentType;
import nl.naturalis.nda.elasticsearch.load.InvalidDataException;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.http.SimpleHttpHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class CrsMultiMediaTransfer {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final SexNormalizer sexNormalizer = SexNormalizer.getInstance();
	private static final PhaseOrStageNormalizer phaseOrStageNormalizer = PhaseOrStageNormalizer.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(CrsMultiMediaTransfer.class);
	private static final String MEDIALIB_URL_START = "http://medialib.naturalis.nl/file/id/";
	private static final SimpleHttpHead httpHead = new SimpleHttpHead();


	public static List<ESMultiMediaObject> transfer(Element recordElement, CrsMultiMediaImporter crsMultiMediaImporter)
	{
		String identifier = val(recordElement, "identifier");
		Element dcElement = DOMUtil.getDescendant(recordElement, "oai_dc:dc");
		List<Element> mediaFileElements = DOMUtil.getDescendants(dcElement, "frmDigitalebestanden");
		if (mediaFileElements == null) {
			++crsMultiMediaImporter.recordsRejected;
			logger.error("Missing element <frmDigitalebestanden> for record with identifier " + identifier);
			return null;
		}
		crsMultiMediaImporter.multimediaProcessed += mediaFileElements.size();
		List<MultiMediaContentIdentification> identifications = getIdentifications(dcElement);
		ESGatheringEvent gatheringEvent = getGatheringEvent(dcElement);
		String associatedSpecimenReference = val(dcElement, "ac:associatedSpecimenReference");

		String phaseOrStage = phaseOrStageNormalizer.getNormalizedValue(val(recordElement, "dwc:lifeStage"));
		List<String> phaseOrStages = phaseOrStage == null ? null : Arrays.asList(phaseOrStage);
		String typeStatus = typeStatusNormalizer.getNormalizedValue(val(recordElement, "abcd:TypeStatus"));
		String sex = sexNormalizer.getNormalizedValue(val(recordElement, "abcd:Sex"));

		ThematicSearchConfig tsc = ThematicSearchConfig.getInstance();
		boolean themeCheckDone = false;			
		List<String> themes = null;

		List<String> sexes = sex == null ? null : Arrays.asList(sex);
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(mediaFileElements.size());
		for (Element mediaFileElement : mediaFileElements) {			

			String title = val(mediaFileElement, "dc:title");
			String url = val(mediaFileElement, "abcd:fileuri");
			if (url == null) {
				++crsMultiMediaImporter.multimediaRejected;
				String msg = String.format("Missing media URL for record with identifier %s (title=%s)", identifier, title);
				logger.error(msg);
				continue;
			}
			if (title == null) {
				++crsMultiMediaImporter.multimediaRejected;
				String msg = String.format("Missing title for record with identifier %s (title=%s)", identifier, title);
				logger.error(msg);
				continue;
			}

			String unitID;

			String contentType = null;

			if (url.startsWith(MEDIALIB_URL_START)) {
				unitID = url.substring(MEDIALIB_URL_START.length() + 1);
				int x = unitID.indexOf('/');
				if (x != -1) {
					unitID = unitID.substring(0, x);
					url = url.replace("/small", "/large");
				}
				logger.debug("Retrieving content type for URL " + url);
				//contentType = httpHead.setBaseUrl(url).execute().getHttpResponse().getFirstHeader("Content-Type").getValue();
			}
			else {
				unitID = title;
			}

			ESMultiMediaObject mmo = new ESMultiMediaObject();
			mmos.add(mmo);

			mmo.addServiceAccessPoint(new ServiceAccessPoint(url, contentType, Variant.MEDIUM_QUALITY));
			mmo.setSourceSystem(SourceSystem.CRS);
			mmo.setSourceSystemId(unitID);
			mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
			mmo.setOwner(SOURCE_INSTITUTION_ID);
			mmo.setSourceID("CRS");
			mmo.setLicenceType(LICENCE_TYPE);
			mmo.setLicence(LICENCE);
			mmo.setUnitID(unitID);
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
			
			if(!themeCheckDone) {
				themes = tsc.getThemesForDocument(associatedSpecimenReference, DocumentType.MULTI_MEDIA_OBJECT, SourceSystem.CRS);
				themeCheckDone = true;
			}		
			mmo.setTheme(themes);
			
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
			logger.warn("Invalid latitude: " + lat);
			lat = null;
		}
		Double lon = dval(dcElement, "dwc:decimalLongitude");
		if (lon != null && (lon < -180 || lon > 180)) {
			logger.warn("Invalid latitude: " + lon);
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
		if (elems == null) {
			String specimenId = DOMUtil.getDescendantValue(dcElement, "ac:associatedSpecimenReference");
			logger.debug("No determinations for specimen with unitID " + specimenId);
			return null;
		}
		List<MultiMediaContentIdentification> identifications = new ArrayList<MultiMediaContentIdentification>(elems.size());
		for (Element e : elems) {
			String s = val(e, "abcd:PreferredFlag");
			if (s != null && !s.equals("1")) {
				continue;
			}
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
			if (sn.getFullScientificName() == null) {
				StringBuilder sb = new StringBuilder();
				if (sn.getGenusOrMonomial() != null) {
					sb.append(sn.getGenusOrMonomial()).append(' ');
				}
				else {
					String taxonCoverage = val(e, "ac:taxonCoverage");
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
				sn.setFullScientificName(sb.toString().trim());
			}
			identification.setDefaultClassification(TransferUtil.extractClassificiationFromName(sn));
			s = val(e, "abcd:IdentificationQualifier1");
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

			s = val(e, "dwc:vernacularName");
			if (s != null) {
				identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
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
