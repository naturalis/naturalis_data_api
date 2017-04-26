package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.ServiceAccessPoint.Variant.MEDIUM_QUALITY;
import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.ETLConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_URL_START;
import static nl.naturalis.nba.etl.normalize.Normalizer.NOT_MAPPED;
import static nl.naturalis.nba.utils.DOMUtil.getChild;
import static nl.naturalis.nba.utils.DOMUtil.getDescendant;
import static nl.naturalis.nba.utils.DOMUtil.getDescendantValue;
import static nl.naturalis.nba.utils.DOMUtil.getDescendants;
import static nl.naturalis.nba.utils.StringUtil.rpad;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.AbstractXMLTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.MimeTypeCache;
import nl.naturalis.nba.etl.MimeTypeCacheFactory;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.TransformUtil;
import nl.naturalis.nba.etl.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nba.etl.normalize.SexNormalizer;

/**
 * The transformer component for the CRS multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsMultiMediaTransformer extends AbstractXMLTransformer<MultiMediaObject> {

	private static class MultiMediaInfo {

		private String url;
		private String mimeType;
		private String medialibId;
	}

	private final PhaseOrStageNormalizer posNormalizer;
	private final SexNormalizer sexNormalizer;
	private final MimeTypeCache mimetypeCache;
	private final ThemeCache themeCache;

	private String databaseID;
	private String[] testGenera;
	private MultiMediaObject first;

	CrsMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
		themeCache = ThemeCache.getInstance();
		mimetypeCache = MimeTypeCacheFactory.getInstance().getCache();
		posNormalizer = PhaseOrStageNormalizer.getInstance();
		sexNormalizer = SexNormalizer.getInstance();
		testGenera = getTestGenera();
	}

	@Override
	protected String getObjectID()
	{
		Element dc = getDescendant(input.getRecord(), "oai_dc:dc");
		/*
		 * This is actually the UnitID of the specimen, but that's all we got.
		 */
		return val(dc, "ac:associatedSpecimenReference");
	}

	@Override
	protected String messagePrefix()
	{
		return super.messagePrefix() + rpad(databaseID, 10, "| ");
	}

	@Override
	protected boolean skipRecord()
	{
		/*
		 * Side effect: set the database identifier of the record, so we can provide both
		 * the UnitID and the database ID of the specimen when logging messages. We
		 * override messagePrefix() to also print out the database ID.
		 */
		databaseID = val(input.getRecord(), "identifier");
		if (hasStatusDeleted()) {
			if (logger.isDebugEnabled()) {
				debug("Skipping record with status \"deleted\"");
			}
			return true;
		}
		Element oaiDcElem = getDescendant(input.getRecord(), "oai_dc:dc");
		if (val(oaiDcElem, "abcd:RecordBasis") == null) {
			if (logger.isDebugEnabled()) {
				debug("Skipping virtual specimen");
			}
			return true;
		}
		
		if (getDescendant(oaiDcElem, "frmDigitalebestanden") == null) {
			if (logger.isDebugEnabled()) {
				debug("Missing or empty element <frmDigitalebestanden>");
			}
			return true;
		}
		
		List<Element> elems = getDescendants(oaiDcElem, "ncrsDetermination");
		if (elems == null) {
			if (logger.isDebugEnabled()) {
				debug("Missing or empty element <ncrsDetermination>");
			}
			return true;
		}
		
		return testGenera != null && !hasTestGenus(elems);
	}

	@Override
	protected List<MultiMediaObject> doTransform()
	{
		Element oaiDcElem = getDescendant(input.getRecord(), "oai_dc:dc");
		List<Element> frmDigitaleBestandenElems = getDescendants(oaiDcElem,
				"frmDigitalebestanden");
		List<Element> ncsrDeterminationElems = getDescendants(oaiDcElem,
				"ncrsDetermination");
		ArrayList<MultiMediaContentIdentification> identifications;
		identifications = getIdentifications(ncsrDeterminationElems);
		if (identifications == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Invalid/insufficient specimen identification information");
			}
			return null;
		}
		stats.recordsAccepted++;
		first = null;
		ArrayList<MultiMediaObject> mmos = new ArrayList<>(
				frmDigitaleBestandenElems.size());
		for (int i = 0; i < frmDigitaleBestandenElems.size(); i++) {
			try {
				stats.objectsProcessed++;
				Element frmDigitaleBestandenElem = frmDigitaleBestandenElems.get(i);
				MultiMediaInfo info = getMultiMediaInfo(frmDigitaleBestandenElem);
				if (info == null) {
					continue;
				}
				MultiMediaObject mmo = initialize(oaiDcElem, identifications);
				ServiceAccessPoint sap;
				sap = new ServiceAccessPoint(info.url, info.mimeType, MEDIUM_QUALITY);
				mmo.addServiceAccessPoint(sap);
				String unitID;
				if (info.medialibId == null) {
					unitID = objectID + '_' + i;
				}
				else {
					unitID = info.medialibId;
				}
				mmo.setUnitID(unitID);
				mmo.setSourceSystemId(unitID);
				String title = getTitle(frmDigitaleBestandenElem, unitID);
				mmo.setTitle(title);
				mmo.setCaption(title);
				mmo.setMultiMediaPublic(
						bval(frmDigitaleBestandenElem, "abcd:MultiMediaPublic"));
				mmo.setCreator(val(frmDigitaleBestandenElem, "dc:creator"));
				mmos.add(mmo);
				stats.objectsAccepted++;
			}
			catch (Throwable t) {
				handleError(t);
			}
		}
		return mmos;
	}

	private MultiMediaObject initialize(Element oaiDcElem,
			ArrayList<MultiMediaContentIdentification> identifications)
	{
		if (first == null) {
			first = new MultiMediaObject();
			first.setGatheringEvents(Arrays.asList(getGatheringEvent(oaiDcElem)));
			String temp = getPhaseOrStage(oaiDcElem);
			first.setPhasesOrStages(temp == null ? null : Arrays.asList(temp));
			temp = getSex(oaiDcElem);
			first.setSexes(temp == null ? null : Arrays.asList(temp));
			first.setCollectionType(val(oaiDcElem, "abcd:CollectionType"));
			first.setSourceSystem(CRS);
			first.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
			first.setSourceID(CRS.getCode());
			first.setLicense(LICENCE);
			first.setLicenseType(LICENCE_TYPE);
			String specimenId = getElasticsearchId(CRS, objectID);
			first.setAssociatedSpecimenReference(specimenId);
			first.setIdentifications(identifications);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, CRS);
			first.setTheme(themes);
			return first;
		}
		return initializeFromFirst();
	}

	/*
	 * Create a new multimedia object, initialized with the values from the first
	 * multimedia object of the specimen record we are processing.
	 */
	private MultiMediaObject initializeFromFirst()
	{
		MultiMediaObject next = new MultiMediaObject();
		next.setGatheringEvents(first.getGatheringEvents());
		next.setPhasesOrStages(first.getPhasesOrStages());
		next.setSexes(first.getSexes());
		next.setCollectionType(first.getCollectionType());
		next.setSourceSystem(first.getSourceSystem());
		next.setLicense(first.getLicense());
		next.setLicenseType(first.getLicenseType());
		next.setAssociatedSpecimenReference(first.getAssociatedSpecimenReference());
		next.setTheme(first.getTheme());
		next.setIdentifications(first.getIdentifications());
		return next;
	}

	private ArrayList<MultiMediaContentIdentification> getIdentifications(
			List<Element> ncsrDeterminationElems)
	{
		ArrayList<MultiMediaContentIdentification> identifications = null;
		for (Element ncrsDeterminationElem : ncsrDeterminationElems) {
			ScientificName sn = getScientificName(ncrsDeterminationElem);
			if (sn.getFullScientificName() == null) {
				concatEpithets(sn, val(ncrsDeterminationElem, "ac:taxonCoverage"));
			}
			if (sn.getFullScientificName() == null) {
				if (logger.isDebugEnabled()) {
					debug("Missing scientific name");
				}
				continue;
			}
			MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
			mmci.setScientificName(sn);
			mmci.setDefaultClassification(
					TransformUtil.extractClassificiationFromName(sn));
			mmci.setIdentificationQualifiers(getQualifiers(ncrsDeterminationElem));
			mmci.setVernacularNames(getVernacularNames(ncrsDeterminationElem));
			if (identifications == null) {
				identifications = new ArrayList<>(ncsrDeterminationElems.size());
			}
			identifications.add(mmci);
		}
		return identifications;
	}

	private MultiMediaGatheringEvent getGatheringEvent(Element oaiDcElem)
	{
		MultiMediaGatheringEvent ge = new MultiMediaGatheringEvent();
		ge.setWorldRegion(val(oaiDcElem, "Iptc4xmpExt:WorldRegion"));
		ge.setCountry(val(oaiDcElem, "Iptc4xmpExt:CountryName"));
		ge.setProvinceState(val(oaiDcElem, "Iptc4xmpExt:ProvinceState"));
		ge.setSublocality(val(oaiDcElem, "Iptc4xmpExt:Sublocation"));
		Double lat = dval(oaiDcElem, "dwc:decimalLatitude");
		if (lat != null && (lat < -90 || lat > 90)) {
			if (!suppressErrors) {
				warn("Invalid latitude: " + lat);
			}
			lat = null;
		}
		Double lon = dval(oaiDcElem, "dwc:decimalLongitude");
		if (lon != null && (lon < -180 || lon > 180)) {
			if (!suppressErrors) {
				warn("Invalid latitude: " + lon);
			}
			lon = null;
		}
		if (lat != null || lon != null) {
			GatheringSiteCoordinates coords;
			coords = new GatheringSiteCoordinates(lat, lon);
			ge.setSiteCoordinates(Arrays.asList(coords));
		}
		String s = val(oaiDcElem, "abcd:GatheringAgent");
		if (s != null) {
			Person agent = new Person();
			ge.setGatheringPersons(Arrays.asList(agent));
			agent.setFullName(s);
		}
		return ge;
	}

	private ScientificName getScientificName(Element ncrsDeterminationElem)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(ncrsDeterminationElem, "dwc:scientificName"));
		sn.setGenusOrMonomial(val(ncrsDeterminationElem, "abcd:GenusOrMonomial"));
		sn.setSpecificEpithet(val(ncrsDeterminationElem, "abcd:SpeciesEpithet"));
		sn.setInfraspecificEpithet(val(ncrsDeterminationElem, "abcd:subspeciesepithet"));
		sn.setNameAddendum(val(ncrsDeterminationElem, "abcd:NameAddendum"));
		sn.setAuthorshipVerbatim(val(ncrsDeterminationElem, "dwc:nameAccordingTo"));
		TransformUtil.setScientificNameGroup(sn);
		return sn;
	}

	private String getTitle(Element frmDigitalebestandenElem, String unitID)
	{
		String title = val(frmDigitalebestandenElem, "dc:title");
		if (title == null) {
			title = unitID;
			if (logger.isDebugEnabled()) {
				debug("Missing or empty element <dc:title>");
			}
		}
		return title;
	}

	private List<String> getQualifiers(Element e)
	{
		String s = val(e, "abcd:IdentificationQualifier1");
		if (s == null)
			return null;
		ArrayList<String> qualifiers = new ArrayList<>(3);
		qualifiers.add(s);
		s = val(e, "abcd:IdentificationQualifier2");
		if (s != null)
			qualifiers.add(s);
		s = val(e, "abcd:IdentificationQualifier3");
		if (s != null)
			qualifiers.add(s);
		return qualifiers;
	}

	private List<VernacularName> getVernacularNames(Element ncrsDeterminationElem)
	{
		String s = val(ncrsDeterminationElem, "dwc:vernacularName");
		if (s != null) {
			return Arrays.asList(new VernacularName(s));
		}
		return null;
	}

	private MultiMediaInfo getMultiMediaInfo(Element frmDigitalebestandenElem)
	{
		String url = val(frmDigitalebestandenElem, "abcd:fileuri");
		if (url == null) {
			stats.objectsSkipped++;
			if (logger.isDebugEnabled()) {
				debug("Missing or empty element <abcd:fileuri>");
			}
			return null;
		}
		MultiMediaInfo info = new MultiMediaInfo();
		if (url.startsWith(MEDIALIB_URL_START)) {
			/*
			 * HACK: attempt to repair bad medialib URLs where MEDIALIB_URL_START occurs
			 * twice at the beginning
			 */
			if (url.substring(MEDIALIB_URL_START.length())
					.startsWith(MEDIALIB_URL_START)) {
				url = url.substring(MEDIALIB_URL_START.length());
			}
			// Extract medialib ID
			String medialibId = url.substring(MEDIALIB_URL_START.length());
			int x = medialibId.indexOf('/');
			if (x != -1) {
				medialibId = medialibId.substring(0, x);
			}
			info.medialibId = medialibId;
			// Discard original URL and reconstruct from scratch
			url = MEDIALIB_URL_START + medialibId + "/format/large";
			info.url = url;
			info.mimeType = mimetypeCache.getMimeType(medialibId);
		}
		else {
			if (!suppressErrors) {
				warn("Encountered non-medialib URL: %s", url);
			}
			info.url = url;
			info.mimeType = TransformUtil.guessMimeType(url);
		}
		try {
			@SuppressWarnings("unused")
			URI dummy = new URI(url);
		}
		catch (URISyntaxException exc) {
			stats.objectsRejected++;
			if (!suppressErrors) {
				error("Invalid image URL: " + url);
			}
			return null;
		}
		return info;
	}

	private static void concatEpithets(ScientificName sn, String taxonCoverage)
	{
		StringBuilder sb = new StringBuilder(64);
		if (sn.getGenusOrMonomial() != null)
			sb.append(sn.getGenusOrMonomial()).append(' ');
		else if (taxonCoverage != null)
			sb.append(taxonCoverage).append(' ');
		if (sn.getSubgenus() != null)
			sb.append(sn.getSubgenus()).append(' ');
		if (sn.getSpecificEpithet() != null)
			sb.append(sn.getSpecificEpithet()).append(' ');
		if (sn.getInfraspecificEpithet() != null)
			sb.append(sn.getInfraspecificEpithet()).append(' ');
		if (sb.length() > 0)
			sn.setFullScientificName(sb.toString().trim());
	}

	private boolean hasStatusDeleted()
	{
		Element hdr = getChild(input.getRecord(), "header");
		if (!hdr.hasAttribute("status"))
			return false;
		return hdr.getAttribute("status").equals("deleted");
	}

	private String getPhaseOrStage(Element oaiDcElem)
	{
		String raw = val(oaiDcElem, "dwc:lifeStage");
		if (raw == null) {
			return null;
		}
		String result = posNormalizer.normalize(raw);
		if (result == NOT_MAPPED) {
			if (logger.isDebugEnabled()) {
				debug("Ignoring rogue value for PhaseOrStage: " + raw);
			}
			return null;
		}
		return result;
	}

	private String getSex(Element oaiDcElem)
	{
		String raw = val(oaiDcElem, "dwc:sex");
		if (raw == null) {
			return null;
		}
		String result = sexNormalizer.normalize(raw);
		if (result == NOT_MAPPED) {
			if (logger.isDebugEnabled()) {
				debug("Ignoring rogue value for Sex: " + raw);
			}
			return null;
		}
		return result;
	}

	private boolean hasTestGenus(List<Element> elems)
	{
		for (Element elem : elems) {
			String genus = val(elem, "abcd:GenusOrMonomial");
			if (genus == null) {
				continue;
			}
			genus = genus.toLowerCase();
			for (String testGenus : testGenera) {
				if (genus.equals(testGenus)) {
					return true;
				}
			}
		}
		return false;
	}

	private Double dval(Element e, String tag)
	{
		String s = val(e, tag);
		if (s == null)
			return null;
		try {
			return Double.valueOf(s);
		}
		catch (NumberFormatException exc) {
			if (!suppressErrors)
				warn("Invalid number in element <%s>: \"%s\"", tag, s);
			return null;
		}
	}

	private boolean bval(Element e, String tag)
	{
		String s = val(e, tag);
		return (s == null || s.equals("1"));
	}

	private String val(Element e, String tag)
	{
		String s = getDescendantValue(e, tag);
		if (s == null) {
			if (logger.isDebugEnabled())
				debug("No element <%s> under element <%s>", tag, e.getTagName());
			return null;
		}
		return ((s = s.trim()).length() == 0 ? null : s);
	}


}
