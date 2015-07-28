package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.MedialibMimeTypeCache.MEDIALIB_URL_START;

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
import nl.naturalis.nda.elasticsearch.load.MedialibMimeTypeCache;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;
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
	private static final MedialibMimeTypeCache mimetypeCache = MedialibMimeTypeCache.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(CrsMultiMediaTransfer.class);


	public static List<ESMultiMediaObject> transfer(Element recordElement, CrsMultiMediaImporter crsMultiMediaImporter)
	{
		String identifier = val(recordElement, "identifier");
		Element dcElement = DOMUtil.getDescendant(recordElement, "oai_dc:dc");
		
		String recordBasis = val(dcElement, "abcd:RecordBasis");
		if(recordBasis == null) {
			++crsMultiMediaImporter.recordsSkipped;
			if(logger.isInfoEnabled()) {
				logger.info("Skipping virtual specimen record with id " + identifier);
			}
			return null;
		}
		
		List<Element> mediaFileElements = DOMUtil.getDescendants(dcElement, "frmDigitalebestanden");
		String associatedSpecimenReference = val(dcElement, "ac:associatedSpecimenReference");
		if (mediaFileElements == null) {
			++crsMultiMediaImporter.recordsRejected;
			String fmt = "Missing element <frmDigitalebestanden> for record with identifier %s (%s)";
			logger.error(String.format(fmt, identifier, associatedSpecimenReference));
			return null;
		}
		List<Element> determinationElements = DOMUtil.getDescendants(dcElement, "ncrsDetermination");
		if (determinationElements == null) {
			++crsMultiMediaImporter.recordsRejected;
			String fmt = "Missing <ncrsDetermination> element for record with identifier %s (%s)";
			logger.error(String.format(fmt, identifier, associatedSpecimenReference));
			return null;
		}
		List<MultiMediaContentIdentification> identifications = getIdentifications(determinationElements, crsMultiMediaImporter, identifier,
				associatedSpecimenReference);
		if (identifications == null) {
			++crsMultiMediaImporter.recordsRejected;
			String fmt = "Missing non-empty <ncrsDetermination> element for record with identifier %s (%s)";
			logger.error(String.format(fmt, identifier, associatedSpecimenReference));
			return null;
		}

		++crsMultiMediaImporter.recordsInvestigated;
		crsMultiMediaImporter.multimediaProcessed += mediaFileElements.size();
		ESGatheringEvent gatheringEvent = getGatheringEvent(dcElement);

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

			String unitID;

			String contentType = null;

			if (url.startsWith(MEDIALIB_URL_START)) {
				// Tease out the unitID from the URL
				unitID = url.substring(MEDIALIB_URL_START.length());
				int x = unitID.indexOf('/');
				if (x != -1) {
					unitID = unitID.substring(0, x);
					// NBA must link to large medialib images, but the CRS OAI interface
					// spits out links to the small images 
					url = url.replace("/small", "/large");
				}
				if (title == null) {
					title = unitID;
					if (logger.isDebugEnabled()) {
						String msg = String.format("Missing title for record with identifier %s. Set to specimen UnitID: %s", identifier, title);
						logger.debug(msg);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Retrieving content type for URL " + url);
				}
				contentType = mimetypeCache.getMimeType(unitID);
			}
			else {
				if (title == null) {
					title = associatedSpecimenReference + ':' + String.valueOf(url.hashCode()).replace('-', '0');
					if (logger.isDebugEnabled()) {
						String msg = String.format("Missing title for record with identifier %s. Assigned title: %s", identifier, title);
						logger.debug(msg);
					}
				}
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

			if (!themeCheckDone) {
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


	private static List<MultiMediaContentIdentification> getIdentifications(List<Element> determinationElements,
			CrsMultiMediaImporter crsMultiMediaImporter, String identifier, String associatedSpecimenReference)
	{
		ArrayList<MultiMediaContentIdentification> identifications = null;
		for (Element e : determinationElements) {
			//			String string = val(e, "abcd:PreferredFlag");
			//			if (string != null && !string.equals("1")) {
			//				continue;
			//			}
			ScientificName sn = new ScientificName();
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
				if (sb.length() > 0) {
					sn.setFullScientificName(sb.toString().trim());
				}
			}
			if (sn.getFullScientificName() == null) {
				++crsMultiMediaImporter.multimediaRejected;
				String fmt = "Missing scientific name in identification for record with identifier %s (%s)";
				logger.error(String.format(fmt, identifier, associatedSpecimenReference));
				continue;
			}

			MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
			identification.setScientificName(sn);
			identification.setDefaultClassification(TransferUtil.extractClassificiationFromName(sn));

			String string = val(e, "abcd:IdentificationQualifier1");
			if (string != null) {
				List<String> qualifiers = new ArrayList<String>(3);
				qualifiers.add(string);
				string = val(e, "abcd:IdentificationQualifier2");
				if (string != null) {
					qualifiers.add(string);
				}
				string = val(e, "abcd:IdentificationQualifier3");
				if (string != null) {
					qualifiers.add(string);
				}
				if (qualifiers.size() != 0) {
					identification.setIdentificationQualifiers(qualifiers);
				}
			}

			string = val(e, "dwc:vernacularName");
			if (string != null) {
				identification.setVernacularNames(Arrays.asList(new VernacularName(string)));
			}

			if (identifications == null) {
				identifications = new ArrayList<MultiMediaContentIdentification>(determinationElements.size());
			}

			identifications.add(identification);

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
