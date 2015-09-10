package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.PURL_SERVER_BASE_URL;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.*;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.AbstractXMLTransformer;
import nl.naturalis.nda.elasticsearch.load.DocumentType;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;
import nl.naturalis.nda.elasticsearch.load.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * The transformation component for the CRS specimen import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsSpecimenTransformer extends AbstractXMLTransformer<ESSpecimen> {

	private static final Logger logger;
	private static final SpecimenTypeStatusNormalizer tsNormalizer;
	private static final SexNormalizer sexNormalizer;
	private static final PhaseOrStageNormalizer posNormalizer;

	private static final String PURL_START = PURL_SERVER_BASE_URL + "/naturalis/specimen/";

	static {
		logger = Registry.getInstance().getLogger(CrsSpecimenTransformer.class);
		tsNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		sexNormalizer = SexNormalizer.getInstance();
		posNormalizer = PhaseOrStageNormalizer.getInstance();
	}

	CrsSpecimenTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	@Override
	public List<ESSpecimen> transform(XMLRecordInfo recInf)
	{
		stats.recordsProcessed++;

		this.recInf = recInf;
		Element record = recInf.getElement();

		if (hasStatusDeleted()) {
			stats.recordsSkipped++;
			if (logger.isInfoEnabled()) {
				String id = val(record, "identifier");
				String fmt = "Skipping record with status \"deleted\" (database id: %s)";
				logger.info(String.format(fmt, id));
			}
			return null;
		}

		objectID = val(record, "abcd:UnitID");

		if (objectID == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				String id = val(record, "identifier");
				String fmt = "Missing UnitID (database id: %s)";
				logger.error(String.format(fmt, id));
			}
			return null;
		}

		String recordBasis = val(record, "abcd:RecordBasis");
		if (recordBasis == null) {
			stats.recordsSkipped++;
			if (!suppressErrors)
				warn("Skipping virtual specimen");
			return null;
		}

		List<Element> elems = DOMUtil.getDescendants(record, "ncrsDetermination");
		if (elems == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing element: <ncrsDetermination>");
			return null;
		}

		stats.recordsAccepted++;
		stats.objectsProcessed++;

		ESSpecimen specimen = new ESSpecimen();

		for (Element e : elems) {
			SpecimenIdentification si = getIdentification(e);
			if (si != null) {
				specimen.addIndentification(si);
			}
		}

		if (specimen.getIdentifications() == null) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid or insufficient specimen identification information");
			return null;
		}

		Collections.sort(specimen.getIdentifications(), new Comparator<SpecimenIdentification>() {
			public int compare(SpecimenIdentification o1, SpecimenIdentification o2)
			{
				return o1.isPreferred() ? 1 : o2.isPreferred() ? -1 : 0;
			}
		});

		try {
			String temp;
			specimen.setSourceSystem(SourceSystem.CRS);
			specimen.setUnitID(objectID);
			specimen.setSourceSystemId(specimen.getUnitID());
			ThemeCache tsc = ThemeCache.getInstance();
			List<String> themes = tsc.lookup(objectID, DocumentType.SPECIMEN, SourceSystem.CRS);
			specimen.setTheme(themes);
			specimen.setUnitGUID(PURL_START + LoadUtil.urlEncode(objectID));
			specimen.setCollectorsFieldNumber(val(record, "abcd:CollectorsFieldNumber"));
			specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
			specimen.setOwner(SOURCE_INSTITUTION_ID);
			specimen.setSourceID("CRS");
			specimen.setLicenceType(LICENCE_TYPE);
			specimen.setLicence(LICENCE);
			specimen.setRecordBasis(recordBasis);
			specimen.setKindOfUnit(val(record, "abcd:KindOfUnit"));
			specimen.setCollectionType(val(record, "abcd:CollectionType"));
			specimen.setTitle(val(record, "abcd:Title"));
			specimen.setNumberOfSpecimen(ival(record, "abcd:AccessionSpecimenNumbers"));
			temp = val(record, "abcd:ObjectPublic");
			specimen.setObjectPublic(temp == null || temp.trim().equals("1"));
			temp = val(record, "abcd:MultiMediaPublic");
			specimen.setMultiMediaPublic(temp == null || temp.trim().equals("1"));
			temp = val(record, "abcd:FromCaptivity");
			specimen.setFromCaptivity(temp != null && temp.trim().equals("1"));
			temp = val(record, "abcd:PreparationType");
			if (temp == null) {
				temp = val(record, "abcd:SpecimenMount");
			}
			specimen.setPreparationType(temp);
			specimen.setPhaseOrStage(getPhaseOrStage(record));
			specimen.setTypeStatus(getTypeStatus(record));
			specimen.setSex(getSex(record));
			specimen.setGatheringEvent(getGatheringEvent());
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			if (!suppressErrors)
				logger.error(t.toString());
			if (logger.isDebugEnabled())
				debug("Stacktrace:", t);
			return null;
		}
		return Arrays.asList(specimen);
	}

	private SpecimenIdentification getIdentification(Element elem)
	{

		ScientificName sn = getScientificName(elem);
		if (sn.getFullScientificName() == null) {
			if (!suppressErrors)
				warn("No scientific name in identification");
			return null;
		}
		SpecimenIdentification si = new SpecimenIdentification();
		String s = val(elem, "abcd:PreferredFlag");
		si.setPreferred(s == null || s.equals("1"));
		si.setDateIdentified(date(elem, "abcd:IdentificationDate"));
		si.setAssociatedFossilAssemblage(val(elem, "abcd:AssociatedFossilAssemblage"));
		si.setAssociatedMineralName(val(elem, "abcd:AssociatedMineralName"));
		si.setRockMineralUsage(val(elem, "abcd:RockMineralUsage"));
		si.setRockType(val(elem, "abcd:RockType"));
		si.setScientificName(getScientificName(elem));
		List<Monomial> sc = getSystemClassification(elem, si.getScientificName());
		si.setSystemClassification(sc);
		DefaultClassification dc = DefaultClassification.fromSystemClassification(sc);
		si.setDefaultClassification(dc);
		String infraspecificRank = val(elem, "abcd:InfrasubspecificRank");
		if (infraspecificRank != null)
			si.setTaxonRank(infraspecificRank);
		else if (si.getScientificName().getInfraspecificEpithet() != null)
			si.setTaxonRank("subspecies");
		else if (si.getScientificName().getSpecificEpithet() != null)
			si.setTaxonRank("species");
		else
			si.setTaxonRank("genus");
		s = val(elem, "abcd:InformalNameString");
		if (s != null) {
			si.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		return si;
	}

	private ScientificName getScientificName(Element elem)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(elem, "abcd:FullScientificNameString"));
		sn.setGenusOrMonomial(val(elem, "abcd:GenusOrMonomial"));
		sn.setSubgenus(val(elem, "abcd:Subgenus"));
		sn.setSpecificEpithet(val(elem, "abcd:SpeciesEpithet"));
		String s = val(elem, "abcd:subspeciesepithet");
		if (s == null) {
			s = val(elem, "abcd:InfrasubspecificName");
		}
		sn.setInfraspecificEpithet(s);
		sn.setNameAddendum(val(elem, "abcd:NameAddendum"));
		sn.setAuthorshipVerbatim(val(elem, "abcd:AuthorTeamOriginalAndYear"));
		if (sn.getFullScientificName() == null) {
			StringBuilder sb = new StringBuilder();
			if (sn.getGenusOrMonomial() != null) {
				sb.append(sn.getGenusOrMonomial()).append(' ');
			}
			else {
				String taxonCoverage = val(elem, "abcd:taxonCoverage");
				if (taxonCoverage != null) {
					sb.append(taxonCoverage).append(' ');
				}
			}
			if (sn.getSubgenus() != null)
				sb.append(sn.getSubgenus()).append(' ');
			if (sn.getSpecificEpithet() != null)
				sb.append(sn.getSpecificEpithet()).append(' ');
			if (sn.getInfraspecificEpithet() != null)
				sb.append(sn.getInfraspecificEpithet()).append(' ');
			if (sn.getAuthorshipVerbatim() != null) {
				if (sn.getAuthorshipVerbatim().charAt(0) != '(')
					sb.append('(');
				sb.append(sn.getAuthorshipVerbatim());
				if (sn.getAuthorshipVerbatim().charAt(sn.getAuthorshipVerbatim().length() - 1) != ')')
					sb.append(')');
			}
			if (sb.length() != 0)
				sn.setFullScientificName(sb.toString().trim());
		}
		return sn;
	}

	private static List<Monomial> getSystemClassification(Element elem, ScientificName sn)
	{
		List<Monomial> lowerClassification = TransferUtil.getMonomialsInName(sn);
		List<Element> elems = DOMUtil.getChildren(elem, "ncrsHighername");
		if (elems == null)
			return lowerClassification;
		List<Monomial> classification = new ArrayList<>(elems.size() + lowerClassification.size());
		for (Element e : elems) {
			String rank = DOMUtil.getValue(e, "abcd:HigherTaxonRank");
			String name = DOMUtil.getValue(e, "abcd:taxonCoverage");
			classification.add(new Monomial(rank, name));
		}
		classification.addAll(lowerClassification);
		return classification;
	}

	private ESGatheringEvent getGatheringEvent()
	{
		Element record = recInf.getElement();
		ESGatheringEvent ge = new ESGatheringEvent();
		ge.setProjectTitle(val(record, "abcd:ProjectTitle"));
		ge.setWorldRegion(val(record, "abcd:WorldRegion"));
		ge.setCountry(val(record, "abcd:Country"));
		ge.setProvinceState(val(record, "abcd:ProvinceState"));
		ge.setIsland(val(record, "abcd:Island"));
		ge.setLocality(val(record, "abcd:Locality"));
		ge.setLocalityText(val(record, "abcd:LocalityText"));
		ge.setDateTimeBegin(date(record, "abcd:CollectingStartDate"));
		ge.setDateTimeEnd(date(record, "abcd:CollectingEndDate"));
		String s = val(record, "abcd:GatheringAgent");
		if (s != null) {
			ge.setGatheringPersons(Arrays.asList(new Person(s)));
		}
		Double lat = dval(record, "abcd:LatitudeDecimal");
		Double lon = dval(record, "abcd:LongitudeDecimal");
		if (lon != null && (lon < -180 || lon > 180)) {
			if (!suppressErrors)
				warn("Invalid longitude: " + lon);
			lon = null;
		}
		if (lat != null && (lat < -90 || lat > 90)) {
			if (!suppressErrors)
				warn("Invalid latitude: " + lat);
			lat = null;
		}
		if (lat != null || lon != null) {
			ge.setSiteCoordinates(Arrays.asList(new ESGatheringSiteCoordinates(lat, lon)));
		}
		ge.setChronoStratigraphy(getChronoStratigraphyList());
		ge.setBioStratigraphy(getBioStratigraphyList());
		ge.setLithoStratigraphy(getLithoStratigraphyList());
		return ge;
	}

	private List<ChronoStratigraphy> getChronoStratigraphyList()
	{
		Element record = recInf.getElement();
		List<Element> elems = DOMUtil.getDescendants(record, "ncrsChronoStratigraphy");
		if (elems == null) {
			return null;
		}
		List<ChronoStratigraphy> result = new ArrayList<>(elems.size());
		for (Element e : elems) {
			ChronoStratigraphy one = getChronoStratigraphyObject(e);
			result.add(one);
		}
		return result;
	}

	private ChronoStratigraphy getChronoStratigraphyObject(Element e)
	{
		ChronoStratigraphy one = new ChronoStratigraphy();
		one.setYoungRegionalSubstage(val(e, "abcd:YoungRegionalSubstage"));
		one.setYoungRegionalStage(val(e, "abcd:YoungRegionalStage"));
		one.setYoungRegionalSeries(val(e, "abcd:YoungRegionalSeries"));
		one.setYoungDatingQualifier(val(e, "abcd:YoungDatingQualifier"));
		one.setYoungInternSystem(val(e, "abcd:YoungInternSystem"));
		one.setYoungInternSubstage(val(e, "abcd:youngInternSubstage"));
		one.setYoungInternStage(val(e, "abcd:YoungInternStage"));
		one.setYoungInternSeries(val(e, "abcd:YoungInternSeries"));
		one.setYoungInternErathem(val(e, "abcd:YoungInternErathem"));
		one.setYoungInternEonothem(val(e, "abcd:YoungInternEonothem"));
		one.setYoungChronoName(val(e, "abcd:YoungChronoName"));
		one.setYoungCertainty(val(e, "abcd:YoungCertainty"));
		one.setOldDatingQualifier(val(e, "abcd:OldDatingQualifier"));
		one.setChronoPreferredFlag(bval(e, "abcd:ChronoPreferredFlag"));
		one.setOldRegionalSubstage(val(e, "abcd:OldRegionalSubstage"));
		one.setOldRegionalStage(val(e, "abcd:OldRegionalStage"));
		one.setOldRegionalSeries(val(e, "abcd:OldRegionalSeries"));
		one.setOldInternSystem(val(e, "abcd:OldInternSystem"));
		one.setOldInternSubstage(val(e, "abcd:OldInternSubstage"));
		one.setOldInternStage(val(e, "abcd:OldInternStage"));
		one.setOldInternSeries(val(e, "abcd:OldInternSeries"));
		one.setOldInternErathem(val(e, "abcd:OldInternErathem"));
		one.setOldInternEonothem(val(e, "abcd:OldInternEonothem"));
		one.setOldChronoName(val(e, "abcd:OldChronoName"));
		one.setChronoIdentifier(val(e, "abcd:ChronoIdentifier"));
		one.setOldCertainty(val(e, "abcd:OldCertainty"));
		return one;
	}

	private List<BioStratigraphy> getBioStratigraphyList()
	{
		Element record = recInf.getElement();
		List<Element> elems = DOMUtil.getDescendants(record, "ncrsBioStratigraphy");
		if (elems == null) {
			return null;
		}
		List<BioStratigraphy> result = new ArrayList<>(elems.size());
		for (Element e : elems) {
			BioStratigraphy one = getBioStratigraphyObject(e);
			result.add(one);
		}
		return result;
	}

	private BioStratigraphy getBioStratigraphyObject(Element e)
	{
		BioStratigraphy one = new BioStratigraphy();
		one.setYoungBioDatingQualifier(val(e, "abcd:YoungBioDatingQualifier"));
		one.setYoungBioName(val(e, "abcd:YoungBioName"));
		one.setYoungFossilZone(val(e, "abcd:YoungFossilZone"));
		one.setYoungFossilSubZone(val(e, "abcd:YoungFossilSubZone"));
		one.setYoungBioCertainty(val(e, "abcd:YoungBioCertainty"));
		one.setYoungStratType(val(e, "abcd:YoungStratType"));
		one.setBioDatingQualifier(val(e, "abcd:BioDatingQualifier"));
		one.setBioPreferredFlag(bval(e, "abcd:BioPreferredFlag"));
		one.setRangePosition(val(e, "abcd:RangePosition"));
		one.setOldBioName(val(e, "abcd:OldBioName"));
		one.setBioIdentifier(val(e, "abcd:BioIdentifier"));
		one.setOldFossilzone(val(e, "abcd:OldFossilzone"));
		one.setOldFossilSubzone(val(e, "abcd:OldFossilSubzone"));
		one.setOldBioCertainty(val(e, "abcd:OldBioCertainty"));
		one.setOldBioStratType(val(e, "abcd:OldBioStratType"));
		return one;
	}

	public List<LithoStratigraphy> getLithoStratigraphyList()
	{
		Element record = recInf.getElement();
		List<Element> lithoStratigraphyElements = DOMUtil.getDescendants(record, "ncrsLithoStratigraphy");
		if (lithoStratigraphyElements == null) {
			return null;
		}
		List<LithoStratigraphy> result = new ArrayList<>(lithoStratigraphyElements.size());
		for (Element e : lithoStratigraphyElements) {
			LithoStratigraphy one = getLithoStratigraphyObject(e);
			result.add(one);
		}
		return result;
	}

	private LithoStratigraphy getLithoStratigraphyObject(Element e)
	{
		LithoStratigraphy one = new LithoStratigraphy();
		one.setQualifier(val(e, "abcd:Qualifier"));
		one.setPreferredFlag(bval(e, "abcd:PreferredFlag"));
		one.setMember2(val(e, "abcd:Member2"));
		one.setMember(val(e, "abcd:Member"));
		one.setInformalName2(val(e, "abcd:InformalName2"));
		one.setInformalName(val(e, "abcd:InformalName"));
		one.setImportedName2(val(e, "abcd:ImportedName2"));
		one.setImportedName1(val(e, "abcd:ImportedName1"));
		one.setLithoIdentifier(val(e, "abcd:LithoIdentifier"));
		one.setFormation2(val(e, "abcd:Formation2"));
		one.setFormationGroup2(val(e, "abcd:FormationGroup2"));
		one.setFormationGroup(val(e, "abcd:FormationGroup"));
		one.setFormation(val(e, "abcd:Formation"));
		one.setCertainty2(val(e, "abcd:Certainty2"));
		one.setCertainty(val(e, "abcd:Certainty"));
		one.setBed2(val(e, "abcd:Bed2"));
		one.setBed(val(e, "abcd:Bed"));
		return one;
	}

	private boolean hasStatusDeleted()
	{
		Element hdr = DOMUtil.getChild(recInf.getElement(), "header");
		if (!hdr.hasAttribute("status"))
			return false;
		return hdr.getAttribute("status").equals("deleted");
	}

	private String getPhaseOrStage(Element record)
	{
		return posNormalizer.getNormalizedValue(val(record, "abcd:PhaseOrStage"));
	}

	private String getTypeStatus(Element record)
	{
		return tsNormalizer.getNormalizedValue(val(record, "abcd:TypeStatus"));
	}

	private String getSex(Element record)
	{
		return sexNormalizer.getNormalizedValue(val(record, "abcd:Sex"));
	}

	private Date date(Element e, String tag)
	{
		return TransferUtil.parseDate(val(e, tag));
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

	private int ival(Element e, String tag)
	{
		String s = val(e, tag);
		if (s == null)
			return 0;
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException exc) {
			if (suppressErrors)
				warn("Invalid integer in element <%s>: \"%s\"", tag, s);
			return 0;
		}
	}

	private boolean bval(Element e, String tag)
	{
		String s = val(e, tag);
		return (s == null || s.equals("1"));
	}

	private String val(Element e, String tag)
	{
		String s = DOMUtil.getDescendantValue(e, tag);
		if (s == null) {
			if (logger.isDebugEnabled())
				debug("No element <%s> under element <%s>", tag, e.getTagName());
			return null;
		}
		return ((s = s.trim()).length() == 0 ? null : s);
	}

}
