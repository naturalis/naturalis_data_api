package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.domain.ServiceAccessPoint.Variant.MEDIUM_QUALITY;
import static nl.naturalis.nda.domain.SourceSystem.CRS;
import static nl.naturalis.nda.elasticsearch.load.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.MimeTypeCache.MEDIALIB_URL_START;
import static org.domainobject.util.StringUtil.rpad;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.MimeTypeCache;
import nl.naturalis.nda.elasticsearch.load.MimeTypeCacheFactory;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.w3c.dom.Element;

class CrsMultiMediaTransfer {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final SexNormalizer sexNormalizer = SexNormalizer.getInstance();
	private static final PhaseOrStageNormalizer phaseOrStageNormalizer = PhaseOrStageNormalizer.getInstance();

	private static final Logger logger = Registry.getInstance().getLogger(CrsMultiMediaTransfer.class);

	private final CrsMultiMediaImporter crsMultiMediaImporter;
	private final MimeTypeCache mimetypeCache;
	private final ThemeCache themeCache;
	private final boolean suppressErrors;

	private String identifier;
	private String specimenID;
	private List<Element> mediaFileElements;
	private List<MultiMediaContentIdentification> identifications;
	private ESMultiMediaObject first;
	private ServiceAccessPoint sap;


	CrsMultiMediaTransfer(CrsMultiMediaImporter crsMultiMediaImporter)
	{
		this.crsMultiMediaImporter = crsMultiMediaImporter;
		themeCache = ThemeCache.getInstance();
		mimetypeCache = MimeTypeCacheFactory.getInstance().getCache();
		suppressErrors = StringUtil.isTrue(System.getProperty("crs.suppress-errors", "0"));
	}


	public List<ESMultiMediaObject> transfer(Element recordElement)
	{
		identifier = val(recordElement, "identifier");
		Element dcElement = DOMUtil.getDescendant(recordElement, "oai_dc:dc");
		if (!checkXmlRecord(dcElement)) {
			return null;
		}
		++crsMultiMediaImporter.recordsInvestigated;
		List<ESMultiMediaObject> mmos = new ArrayList<>(mediaFileElements.size());
		first = null;
		for (Element mediaFileElement : mediaFileElements) {
			++crsMultiMediaImporter.multimediaProcessed;
			String[] urlInfo = getUrlInfo(mediaFileElement);
			if (urlInfo == null) {
				continue;
			}
			ESMultiMediaObject mmo;
			try {
				mmo = initialize(dcElement);
				sap = new ServiceAccessPoint(urlInfo[0], urlInfo[1], MEDIUM_QUALITY);
				String unitID = specimenID + '_' + String.valueOf(urlInfo[0].hashCode()).replace('-', '0');
				String title = getTitle(mediaFileElement, unitID);
				mmo.addServiceAccessPoint(sap);
				mmo.setSourceSystemId(unitID);
				mmo.setUnitID(unitID);
				mmo.setTitle(title);
				mmo.setCaption(title);
				mmo.setMultiMediaPublic(bval(mediaFileElement, "abcd:MultiMediaPublic"));
				mmo.setCreator(val(mediaFileElement, "dc:creator"));
			}
			catch (Throwable t) {
				rejectMultiMedia(t.toString());
				continue;
			}
			mmos.add(mmo);
		}
		return mmos;
	}


	private String[] getUrlInfo(Element mediaFileElement)
	{
		String url = val(mediaFileElement, "abcd:fileuri");
		if (url == null) {
			rejectMultiMedia("Missing or empty element <abcd:fileuri>");
			return null;
		}
		String contentType;
		if (url.startsWith(MEDIALIB_URL_START)) {
			url = url.replace("/small", "/large");
			String medialibID = url.substring(MEDIALIB_URL_START.length());
			int x = medialibID.indexOf('/');
			if (x != -1) {
				medialibID = medialibID.substring(0, x);
			}
			contentType = mimetypeCache.getMimeType(medialibID);
		}
		else {
			try {
				new URI(url);
				++crsMultiMediaImporter.nonMedialibUrls;
				if (logger.isDebugEnabled()) {
					debug("Encountered a non-medialib URL: %s", url);
				}
			}
			catch (URISyntaxException e) {
				rejectMultiMedia("Invalid image URL: " + url);
				return null;
			}
			contentType = TransferUtil.guessMimeType(url);
		}
		return new String[] { url, contentType };
	}


	private ESMultiMediaObject initialize(Element dcElement)
	{
		if (first == null) {
			first = new ESMultiMediaObject();
			first.setGatheringEvents(Arrays.asList(getGatheringEvent(dcElement)));
			String s = phaseOrStageNormalizer.getNormalizedValue(val(dcElement, "dwc:lifeStage"));
			first.setPhasesOrStages(s == null ? null : Arrays.asList(s));
			s = typeStatusNormalizer.getNormalizedValue(val(dcElement, "abcd:TypeStatus"));
			first.setSpecimenTypeStatus(s);
			s = sexNormalizer.getNormalizedValue(val(dcElement, "dwc:sex"));
			first.setSexes(s == null ? null : Arrays.asList(s));
			first.setCollectionType(val(dcElement, "abcd:CollectionType"));
			first.setSourceSystem(CRS);
			first.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
			first.setSourceID(CRS.getCode());
			first.setLicence(LICENCE);
			first.setLicenceType(LICENCE_TYPE);
			first.setAssociatedSpecimenReference(specimenID);
			first.setIdentifications(identifications);
			List<String> themes = themeCache.lookup(specimenID, MULTI_MEDIA_OBJECT, CRS);
			first.setTheme(themes);
			return first;
		}
		ESMultiMediaObject next = new ESMultiMediaObject();
		next.setGatheringEvents(first.getGatheringEvents());
		next.setPhasesOrStages(first.getPhasesOrStages());
		next.setSpecimenTypeStatus(first.getSpecimenTypeStatus());
		next.setSexes(first.getSexes());
		next.setCollectionType(first.getCollectionType());
		next.setSourceSystem(first.getSourceSystem());
		next.setLicence(first.getLicence());
		next.setLicence(first.getLicenceType());
		next.setAssociatedSpecimenReference(first.getAssociatedSpecimenReference());
		next.setTheme(first.getTheme());
		return next;
	}


	private boolean checkXmlRecord(Element dcElement)
	{
		specimenID = val(dcElement, "ac:associatedSpecimenReference");
		if (specimenID == null) {
			rejectRecord("Missing or empty element <ac:associatedSpecimenReference>");
			return false;
		}
		String recordBasis = val(dcElement, "abcd:RecordBasis");
		if (recordBasis == null) {
			skipVirtualSpecimen();
			return false;
		}
		mediaFileElements = DOMUtil.getDescendants(dcElement, "frmDigitalebestanden");
		if (mediaFileElements == null) {
			rejectRecord("Missing or empty element <frmDigitalebestanden>");
			return false;
		}
		List<Element> determinationElements = DOMUtil.getDescendants(dcElement, "ncrsDetermination");
		if (determinationElements == null) {
			rejectRecord("Missing or empty element <ncrsDetermination>");
			return false;
		}
		identifications = getIdentifications(determinationElements);
		if (identifications == null) {
			rejectRecord("Invalid/insufficient specimen determination data");
			return false;
		}
		return true;
	}


	private List<MultiMediaContentIdentification> getIdentifications(List<Element> determinationElements)
	{
		ArrayList<MultiMediaContentIdentification> identifications = null;
		for (Element e : determinationElements) {
			ScientificName sn = getScientificName(e);
			if (sn.getFullScientificName() == null) {
				concatenateEpithets(sn, val(e, "ac:taxonCoverage"));
			}
			if (sn.getFullScientificName() == null) {
				warn("Missing scientific name");
				continue;
			}
			MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
			identification.setScientificName(sn);
			identification.setDefaultClassification(TransferUtil.extractClassificiationFromName(sn));
			identification.setIdentificationQualifiers(getIdentificationQualifiers(e));
			identification.setVernacularNames(getVernacularNames(e));
			if (identifications == null) {
				identifications = new ArrayList<>(determinationElements.size());
			}
			identifications.add(identification);
		}
		return identifications;
	}


	private void skipVirtualSpecimen()
	{
		++crsMultiMediaImporter.recordsSkipped;
		if (logger.isInfoEnabled()) {
			info("Skipping virtual specimen");
		}
	}


	private void rejectMultiMedia(String pattern, Object... args)
	{
		++crsMultiMediaImporter.multimediaRejected;
		error(pattern, args);
	}


	private void rejectRecord(String pattern, Object... args)
	{
		++crsMultiMediaImporter.recordsRejected;
		error(pattern, args);
	}


	private void error(String pattern, Object... args)
	{
		if (!suppressErrors) {
			logger.error(String.format(prefixPattern(pattern), prefixArgs(args)));
		}
	}


	private void warn(String pattern, Object... args)
	{
		if (!suppressErrors) {
			logger.warn(String.format(prefixPattern(pattern), prefixArgs(args)));
		}
	}


	private void info(String pattern, Object... args)
	{
		logger.info(String.format(prefixPattern(pattern), prefixArgs(args)));
	}


	private void debug(String pattern, Object... args)
	{
		logger.debug(String.format(prefixPattern(pattern), prefixArgs(args)));
	}


	private String prefixPattern(String pattern)
	{
		return rpad(specimenID, 18, " | ") + rpad(identifier, 7, " | ") + pattern;
	}


	private Object[] prefixArgs(Object... args)
	{
		Object[] newArgs = new Object[args.length + 2];
		newArgs[0] = specimenID;
		newArgs[1] = identifier;
		for (int i = 0; i < args.length; ++i) {
			newArgs[i + 2] = args[i];
		}
		return newArgs;
	}


	private ESGatheringEvent getGatheringEvent(Element dcElement)
	{
		ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(val(dcElement, "Iptc4xmpExt:WorldRegion"));
		ge.setCountry(val(dcElement, "Iptc4xmpExt:CountryName"));
		ge.setProvinceState(val(dcElement, "Iptc4xmpExt:ProvinceState"));
		ge.setSublocality(val(dcElement, "Iptc4xmpExt:Sublocation"));
		Double lat = dval(dcElement, "dwc:decimalLatitude");
		if (lat != null && (lat < -90 || lat > 90)) {
			warn("Invalid latitude: " + lat);
			lat = null;
		}
		Double lon = dval(dcElement, "dwc:decimalLongitude");
		if (lon != null && (lon < -180 || lon > 180)) {
			warn("Invalid latitude: " + lon);
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


	private static ScientificName getScientificName(Element e)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(e, "dwc:scientificName"));
		sn.setGenusOrMonomial(val(e, "abcd:GenusOrMonomial"));
		sn.setSpecificEpithet(val(e, "abcd:SpeciesEpithet"));
		sn.setInfraspecificEpithet(val(e, "abcd:subspeciesepithet"));
		sn.setNameAddendum(val(e, "abcd:NameAddendum"));
		sn.setAuthorshipVerbatim(val(e, "dwc:nameAccordingTo"));
		return sn;
	}


	private String getTitle(Element mediaFileElement, String unitID)
	{
		String title = val(mediaFileElement, "dc:title");
		if (title == null) {
			title = unitID;
			if (logger.isDebugEnabled()) {
				debug("Missing or empty element <dc:title>");
			}
		}
		return title;
	}


	private static List<String> getIdentificationQualifiers(Element e)
	{
		List<String> qualifiers = null;
		String string = val(e, "abcd:IdentificationQualifier1");
		if (string != null) {
			qualifiers = new ArrayList<>(3);
			qualifiers.add(string);
			string = val(e, "abcd:IdentificationQualifier2");
			if (string != null) {
				qualifiers.add(string);
			}
			string = val(e, "abcd:IdentificationQualifier3");
			if (string != null) {
				qualifiers.add(string);
			}
		}
		return qualifiers;
	}


	private static List<VernacularName> getVernacularNames(Element e)
	{
		String string = val(e, "dwc:vernacularName");
		if (string != null) {
			return Arrays.asList(new VernacularName(string));
		}
		return null;
	}


	private static void concatenateEpithets(ScientificName sn, String taxonCoverage)
	{
		StringBuilder sb = new StringBuilder(64);
		if (sn.getGenusOrMonomial() != null) {
			sb.append(sn.getGenusOrMonomial()).append(' ');
		}
		else if (taxonCoverage != null) {
			sb.append(taxonCoverage).append(' ');
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
