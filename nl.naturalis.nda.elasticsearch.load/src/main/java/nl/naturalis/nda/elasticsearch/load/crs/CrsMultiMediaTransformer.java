package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.domain.ServiceAccessPoint.Variant.MEDIUM_QUALITY;
import static nl.naturalis.nda.domain.SourceSystem.CRS;
import static nl.naturalis.nda.elasticsearch.load.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.MimeTypeCache.MEDIALIB_URL_START;
import static nl.naturalis.nda.elasticsearch.load.normalize.Normalizer.ROGUE_VALUE;
import static org.domainobject.util.DOMUtil.getChild;
import static org.domainobject.util.DOMUtil.getDescendant;
import static org.domainobject.util.DOMUtil.getDescendantValue;
import static org.domainobject.util.DOMUtil.getDescendants;
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
import nl.naturalis.nda.elasticsearch.load.AbstractXMLTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.MimeTypeCache;
import nl.naturalis.nda.elasticsearch.load.MimeTypeCacheFactory;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.TransformUtil;
import nl.naturalis.nda.elasticsearch.load.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.w3c.dom.Element;

/**
 * The transformer component for the CRS multimedia import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsMultiMediaTransformer extends AbstractXMLTransformer<ESMultiMediaObject> {

	private final PhaseOrStageNormalizer posNormalizer;
	private final SpecimenTypeStatusNormalizer tsNormalizer;
	private final SexNormalizer sexNormalizer;
	private final MimeTypeCache mimetypeCache;
	private final ThemeCache themeCache;

	private String databaseID;
	private List<Element> mediaFileElems;
	private List<MultiMediaContentIdentification> identifications;
	private ESMultiMediaObject first;

	CrsMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
		themeCache = ThemeCache.getInstance();
		mimetypeCache = MimeTypeCacheFactory.getInstance().getCache();
		posNormalizer = PhaseOrStageNormalizer.getInstance();
		tsNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		sexNormalizer = SexNormalizer.getInstance();
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
		return super.messagePrefix() + rpad(databaseID, 12, " | ");
	}

	@Override
	protected boolean skipRecord()
	{
		/*
		 * Side effect: set the database identifier of the record, so we can
		 * provide both the UnitID and the database ID of the specimen when
		 * logging messages. We override messagePrefix() to also print out the
		 * database ID.
		 */
		databaseID = val(input.getRecord(), "identifier");
		if (hasStatusDeleted()) {
			if (!suppressErrors)
				warn("Skipping record with status \"deleted\"");
			return true;
		}
		Element dc = getDescendant(input.getRecord(), "oai_dc:dc");
		if (val(dc, "abcd:RecordBasis") == null) {
			if (!suppressErrors)
				warn("Skipping virtual specimen");
			return true;
		}
		return false;
	}

	@Override
	protected List<ESMultiMediaObject> doTransform()
	{
		Element dc = getDescendant(input.getRecord(), "oai_dc:dc");
		mediaFileElems = getDescendants(dc, "frmDigitalebestanden");
		if (mediaFileElems == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing or empty element <frmDigitalebestanden>");
			return null;
		}
		List<Element> determinationElements = getDescendants(dc, "ncrsDetermination");
		if (determinationElements == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing or empty element <ncrsDetermination>");
			return null;
		}
		identifications = getIdentifications(determinationElements);
		if (identifications == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Invalid/insufficient specimen identification information");
			return null;
		}
		stats.recordsAccepted++;
		first = null;
		List<ESMultiMediaObject> mmos = new ArrayList<>(mediaFileElems.size());
		for (Element elem : mediaFileElems) {
			stats.objectsProcessed++;
			String[] urlInfo = getUrlInfo(elem);
			if (urlInfo == null) {
				continue;
			}
			ESMultiMediaObject mmo;
			try {
				mmo = initialize();
				String url = urlInfo[0];
				String mimetype = urlInfo[1];
				ServiceAccessPoint sap;
				sap = new ServiceAccessPoint(url, mimetype, MEDIUM_QUALITY);
				mmo.addServiceAccessPoint(sap);
				String unitID = getUnitID(url);
				mmo.setUnitID(unitID);
				mmo.setSourceSystemId(unitID);
				String title = getTitle(elem, unitID);
				mmo.setTitle(title);
				mmo.setCaption(title);
				mmo.setMultiMediaPublic(bval(elem, "abcd:MultiMediaPublic"));
				mmo.setCreator(val(elem, "dc:creator"));
				mmos.add(mmo);
				stats.objectsAccepted++;
			}
			catch (Throwable t) {
				handleError(t);
			}
		}
		return mmos;
	}

	private ESMultiMediaObject initialize()
	{
		if (first == null) {
			Element dc = getDescendant(input.getRecord(), "oai_dc:dc");
			first = new ESMultiMediaObject();
			first.setGatheringEvents(Arrays.asList(getGatheringEvent(dc)));
			String temp = getPhaseOrStage(dc);
			first.setPhasesOrStages(temp == null ? null : Arrays.asList(temp));
			first.setSpecimenTypeStatus(getTypeStatus(dc));
			temp = getSex(dc);
			first.setSexes(temp == null ? null : Arrays.asList(temp));
			first.setCollectionType(val(dc, "abcd:CollectionType"));
			first.setSourceSystem(CRS);
			first.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
			first.setSourceID(CRS.getCode());
			first.setLicence(LICENCE);
			first.setLicenceType(LICENCE_TYPE);
			first.setAssociatedSpecimenReference(objectID);
			first.setIdentifications(identifications);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, CRS);
			first.setTheme(themes);
			return first;
		}
		return initializeFromFirst();
	}

	/*
	 * Create a new multimedia object, initialized with the values from the
	 * first multimedia object of the specimen record we are processing.
	 */
	private ESMultiMediaObject initializeFromFirst()
	{
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

	private List<MultiMediaContentIdentification> getIdentifications(List<Element> elems)
	{
		ArrayList<MultiMediaContentIdentification> identifications = null;
		for (Element e : elems) {
			ScientificName sn = getScientificName(e);
			if (sn.getFullScientificName() == null) {
				concatEpithets(sn, val(e, "ac:taxonCoverage"));
			}
			if (sn.getFullScientificName() == null) {
				if (!suppressErrors)
					warn("Missing scientific name");
				continue;
			}
			MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
			mmci.setScientificName(sn);
			mmci.setDefaultClassification(TransformUtil.extractClassificiationFromName(sn));
			mmci.setIdentificationQualifiers(getQualifiers(e));
			mmci.setVernacularNames(getVernacularNames(e));
			if (identifications == null) {
				identifications = new ArrayList<>(elems.size());
			}
			identifications.add(mmci);
		}
		return identifications;
	}

	private ESGatheringEvent getGatheringEvent(Element e)
	{
		ESGatheringEvent ge = new ESGatheringEvent();
		ge.setWorldRegion(val(e, "Iptc4xmpExt:WorldRegion"));
		ge.setCountry(val(e, "Iptc4xmpExt:CountryName"));
		ge.setProvinceState(val(e, "Iptc4xmpExt:ProvinceState"));
		ge.setSublocality(val(e, "Iptc4xmpExt:Sublocation"));
		Double lat = dval(e, "dwc:decimalLatitude");
		if (lat != null && (lat < -90 || lat > 90)) {
			if (!suppressErrors)
				warn("Invalid latitude: " + lat);
			lat = null;
		}
		Double lon = dval(e, "dwc:decimalLongitude");
		if (lon != null && (lon < -180 || lon > 180)) {
			if (!suppressErrors)
				warn("Invalid latitude: " + lon);
			lon = null;
		}
		if (lat != null || lon != null) {
			ESGatheringSiteCoordinates coords;
			coords = new ESGatheringSiteCoordinates(lat, lon);
			ge.setSiteCoordinates(Arrays.asList(coords));
		}
		String s = val(e, "abcd:GatheringAgent");
		if (s != null) {
			Person agent = new Person();
			ge.setGatheringPersons(Arrays.asList(agent));
			agent.setFullName(s);
		}
		return ge;
	}

	private ScientificName getScientificName(Element e)
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

	private String getTitle(Element e, String unitID)
	{
		String title = val(e, "dc:title");
		if (title == null) {
			title = unitID;
			if (logger.isDebugEnabled())
				debug("Missing or empty element <dc:title>");
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

	private List<VernacularName> getVernacularNames(Element e)
	{
		String s = val(e, "dwc:vernacularName");
		if (s != null)
			return Arrays.asList(new VernacularName(s));
		return null;
	}

	private String getUnitID(String url)
	{
		int hash = url.hashCode();
		String postfix = String.valueOf(hash).replace('-', '0');
		return objectID + '_' + postfix;
	}

	private String[] getUrlInfo(Element e)
	{
		String url = val(e, "abcd:fileuri");
		if (url == null) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Missing or empty element <abcd:fileuri>");
			return null;
		}
		String mime;
		if (url.startsWith(MEDIALIB_URL_START)) {
			/*
			 * HACK: attempt to repair bad medialib URLs where
			 * MEDIALIB_URL_START occurs twice
			 */
			if (url.substring(MEDIALIB_URL_START.length()).startsWith(MEDIALIB_URL_START))
				url = url.substring(MEDIALIB_URL_START.length());
			// Extract medialib ID
			String medialibId = url.substring(MEDIALIB_URL_START.length());
			int x = medialibId.indexOf('/');
			if (x != -1) {
				medialibId = medialibId.substring(0, x);
			}
			// Discard original URL and reconstruct from scratch
			url = MEDIALIB_URL_START + medialibId + "/format/large";
			mime = mimetypeCache.getMimeType(medialibId);
		}
		else {
			if (!suppressErrors)
				warn("Encountered non-medialib URL: %s", url);
			mime = TransformUtil.guessMimeType(url);
		}
		try {
			new URI(url);
		}
		catch (URISyntaxException exc) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid image URL: " + url);
			return null;
		}
		return new String[] { url, mime };
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

	private String getPhaseOrStage(Element record)
	{
		String raw = val(record, "abcd:PhaseOrStage");
		if (raw == null)
			return null;
		String result = posNormalizer.normalize(raw);
		if (result == ROGUE_VALUE) {
			warn("Ignoring rogue value for PhaseOrStage: " + raw);
			return null;
		}
		return result;
	}

	private String getTypeStatus(Element record)
	{
		String raw = val(record, "abcd:TypeStatus");
		if (raw == null)
			return null;
		String result = tsNormalizer.normalize(raw);
		if (result == ROGUE_VALUE) {
			warn("Ignoring rogue value for TypeStatus: " + raw);
			return null;
		}
		return result;
	}

	private String getSex(Element record)
	{
		String raw = val(record, "abcd:Sex");
		if (raw == null)
			return null;
		String result = sexNormalizer.normalize(raw);
		if (result == ROGUE_VALUE) {
			warn("Ignoring rogue value for Sex: " + raw);
			return null;
		}
		return result;
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