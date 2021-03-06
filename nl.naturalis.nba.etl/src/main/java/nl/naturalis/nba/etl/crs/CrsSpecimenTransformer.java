package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.ETLConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.TransformUtil.sortIdentificationsPreferredFirst;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_URL_START;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_HTTP_URL;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_HTTPS_URL;
import static nl.naturalis.nba.utils.StringUtil.rpad;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createEnrichments;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.AssociatedTaxon;
import nl.naturalis.nba.api.model.BioStratigraphy;
import nl.naturalis.nba.api.model.ChronoStratigraphy;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.LithoStratigraphy;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.NamedArea;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonRelationType;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicRank;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.es.ESDateInput;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.etl.AbstractXMLTransformer;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.etl.MimeTypeCache;
import nl.naturalis.nba.etl.MimeTypeCacheFactory;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.TransformUtil;
import nl.naturalis.nba.etl.normalize.AreaClassNormalizer;
import nl.naturalis.nba.etl.normalize.PhaseOrStageNormalizer;
import nl.naturalis.nba.etl.normalize.SexNormalizer;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;
import nl.naturalis.nba.etl.normalize.TaxonRelationTypeNormalizer;
import nl.naturalis.nba.etl.normalize.UnmappedValueException;
import nl.naturalis.nba.utils.xml.DOMUtil;

/**
 * The transformer component for the CRS specimen import.
 *
 * @author Ayco Holleman
 * @author Tom Gilissen
 */
class CrsSpecimenTransformer extends AbstractXMLTransformer<Specimen> {

    private static final AreaClassNormalizer areaClassNormalizer;
    private static final SpecimenTypeStatusNormalizer tsNormalizer;
    private static final SexNormalizer sexNormalizer;
    private static final PhaseOrStageNormalizer posNormalizer;
    private static final TaxonRelationTypeNormalizer trtNormalizer;
    private static final Field[] geFields;
    private static final String DEFAULT_IMAGE_QUALITY = "ac:GoodQuality";
    private final MimeTypeCache mimetypeCache;

    static {
        areaClassNormalizer = AreaClassNormalizer.getInstance();
        tsNormalizer = SpecimenTypeStatusNormalizer.getInstance();
        sexNormalizer = SexNormalizer.getInstance();
        posNormalizer = PhaseOrStageNormalizer.getInstance();
        trtNormalizer = TaxonRelationTypeNormalizer.getInstance();
        geFields = GatheringEvent.class.getDeclaredFields();
        for (Field f : geFields) {
            f.setAccessible(true);
        }
    }

    private String databaseID;
    private String[] testGenera;
    private boolean enrich = false;

    CrsSpecimenTransformer(ETLStatistics stats) {
        super(stats);
        mimetypeCache = MimeTypeCacheFactory.getInstance().getCache();
        testGenera = getTestGenera();
    }

    protected void setEnrich(boolean enrich) {
        this.enrich = enrich;
    }

    protected boolean doEnrich() {
        return enrich;
    }

    @Override
    protected String getObjectID() {
        /*
         * Side effect: set the database ID of the record as well, so we can provide both the UnitID and the
         * database ID of the specimen when logging messages. We override messagePrefix() to also print out
         * the database ID.
         */
        databaseID = val(input.getRecord(), "identifier");
        return val(input.getRecord(), "abcd:UnitID");
    }

    @Override
    protected List<Specimen> doTransform() {
        Element record = input.getRecord();

        if (hasStatusDeleted()) {
            stats.recordsSkipped++;
            info("Skipping record with status \"deleted\"");
            return null;
        }

        String recordBasis = val(record, "abcd:RecordBasis");
        if (recordBasis == null) {
            stats.recordsSkipped++;
            if (!suppressErrors) {
                warn("Skipping virtual specimen");
            }
            return null;
        }

        List<Element> elems = DOMUtil.getDescendants(record, "ncrsDetermination");

        if (elems == null) {
            stats.recordsRejected++;
            if (!suppressErrors) {
                error("Missing element: <ncrsDetermination>");
            }
            return null;
        }

        if (testGenera != null && !hasTestGenus(elems)) {
            stats.recordsSkipped++;
            return null;
        }

        Specimen specimen = new Specimen();

        String collectionType = val(record, "abcd:CollectionType");
        for (Element e : elems) {
            SpecimenIdentification si = getIdentification(e, collectionType);
            if (si != null) {
                specimen.addIndentification(si);
            }
        }

        if (specimen.getIdentifications() == null) {
            stats.recordsRejected++;
            if (!suppressErrors) {
                error("Invalid or insufficient specimen identification information");
            }
            return null;
        }

        stats.recordsAccepted++;
        stats.objectsProcessed++;

        sortIdentificationsPreferredFirst(specimen);
        if (doEnrich()) {
            enrichIdentification(specimen);
        }

        try {
            String tmp;
            specimen.setSourceSystem(CRS);
            specimen.setUnitID(objectID);
            specimen.setId(getElasticsearchId(CRS, objectID));
            specimen.setSourceSystemId(specimen.getUnitID());
            ThemeCache tsc = ThemeCache.getInstance();
            List<String> themes = tsc.lookup(objectID, SPECIMEN, CRS);
            specimen.setTheme(themes);
            specimen.setUnitGUID(ETLUtil.getSpecimenPurl(objectID));
            specimen.setCollectorsFieldNumber(val(record, "abcd:CollectorsFieldNumber"));
            specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
            specimen.setPreviousSourceID(getPreviousSourceIds());
            specimen.setOwner(SOURCE_INSTITUTION_ID);
            specimen.setSourceID("CRS");
            specimen.setLicenseType(LICENCE_TYPE);
            specimen.setLicense(LICENCE);
            specimen.setRecordBasis(recordBasis);
            specimen.setKindOfUnit(val(record, "abcd:KindOfUnit"));
            specimen.setCollectionType(collectionType);
            specimen.setTitle(val(record, "abcd:Title"));
            specimen.setNumberOfSpecimen(ival(record, "abcd:AccessionSpecimenNumbers"));
            tmp = val(record, "abcd:ObjectPublic");
            specimen.setObjectPublic(tmp == null || tmp.trim().equals("1"));
            tmp = val(record, "abcd:MultiMediaPublic");
            specimen.setMultiMediaPublic(tmp == null || tmp.trim().equals("1"));
            tmp = val(record, "abcd:FromCaptivity");
            specimen.setFromCaptivity(tmp != null && tmp.trim().equals("1"));
            tmp = val(record, "abcd:PreparationType");
            if (tmp == null) {
                tmp = val(record, "abcd:SpecimenMount");
            }
            specimen.setPreparationType(tmp);
            specimen.setPhaseOrStage(getPhaseOrStage());
            specimen.setSex(getSex());
            specimen.setGatheringEvent(getGatheringEvent());
            specimen.setDateLastEdited(getDateLastEdited());
            specimen.setAssociatedMultiMediaUris(getAssociatedMultiMediaUris());
            stats.objectsAccepted++;
            return Collections.singletonList(specimen);
        } catch (Throwable t) {
            handleError(t);
            return null;
        }
    }

    /*
     * Temporary (?) modification to allow for enrichment during the specimen import
     *
     * Retrieve taxonomic data from CoL and NSR and add it to the identification(s)
     */
    private void enrichIdentification(Specimen specimen) {

        // A specimen can have one or more identifications
        // We need to check all identifications
        for (SpecimenIdentification identification : specimen.getIdentifications()) {

            // The scientificNameGroup is the "id" to link with the taxon documents
            String scientificNameGroup = identification.getScientificName().getScientificNameGroup();

            String field = "acceptedName.scientificNameGroup";
            QueryCondition condition = new QueryCondition(field, "EQUALS_IC", scientificNameGroup);
            QuerySpec query = new QuerySpec();
            query.addCondition(condition);
            query.setConstantScore(true);

            TaxonDao dao = new TaxonDao();
            QueryResult<Taxon> result;
            try {
                result = dao.query(query);
            } catch (InvalidQueryException e) {
                throw new ETLRuntimeException(e);
            }

            if (result.getTotalSize() == 0) {
                // No enrichment data available
                continue;
            }

            List<Taxon> taxa = new ArrayList<>();
            for (QueryResultItem<Taxon> item : result) {
                taxa.add(item.getItem());
            }

            List<TaxonomicEnrichment> enrichment = createEnrichments(taxa);
            if (enrichment.size() > 0) {
                identification.setTaxonomicEnrichments(enrichment);
            }

        }

    }

    /**
     * getBestTaxonCoverage returns the taxon coverage of the most specific taxonomic rank
     * as a String.
     * domain : most general rank
     * ...
     * subtribe : most specific rank
     *
     * @param elems : The elements of the ncrsHighername element
     * @return taxonCoverage
     */
    private String getBestTaxonCoverage(List<Element> elems) {
        if (elems == null)
            return null;

        String taxonCoverageStr = null;
        int ordinal = -1;
        for (Element elem : elems) {
            String taxonRankStr = val(elem, "abcd:HigherTaxonRank");
            TaxonomicRank rank = TaxonomicRank.parse(taxonRankStr);
            if (rank != null && rank.ordinal() > ordinal) {
                taxonCoverageStr = val(elem, "abcd:taxonCoverage");
                ordinal = rank.ordinal();
            }
        }
        return taxonCoverageStr;
    }

    /**
     * getBestTaxonRank returns the most specific taxon rank from a
     * collection of higher names
     * <p>
     * domain : most general rank
     * ...
     * subtribe : most specific rank
     *
     * @param elems : The elements of the ncrsHighername element
     * @return TaxonomicRank
     */
    private TaxonomicRank getBestTaxonRank(List<Element> elems) {
        if (elems == null)
            return null;
        TaxonomicRank bestTaxonRank = null;
        int ordinal = -1;
        for (Element elem : elems) {
            String taxonRankStr = val(elem, "abcd:HigherTaxonRank");
            TaxonomicRank taxonRank = TaxonomicRank.parse(taxonRankStr);
            if (taxonRank != null && taxonRank.ordinal() > ordinal) {
                bestTaxonRank = taxonRank;
                ordinal = taxonRank.ordinal();
            }
        }
        return bestTaxonRank;
    }


    private List<ServiceAccessPoint> getAssociatedMultiMediaUris() {

        Element record = input.getRecord();
        List<Element> fileUriElems = DOMUtil.getDescendants(record, "abcd:fileuri");
        if (fileUriElems == null) {
            if (logger.isDebugEnabled()) {
                debug("Missing or empty element <abcd:fileuri>");
            }
            return null;
        }

        /*
         * When MultiMediaPublic is set to 0 (<abcd:MultiMediaPublic>0</abcd:MultiMediaPublic>)
         * this means that no URI's should be added to the document. While that data should not
         * be have been provided by the source system in the first place, we do a security check
         * anyway.
         *
         * NOTE: When the source record contains more than one multimedia item, the field
         * MultiMediaPublic will be available more than once (one for every multimedia item).
         * Since the field multiMediaPublic in the specimen document can be set only once,
         * we check each value provided.
         *
         */
        List<Element> multimediaPublic = DOMUtil.getDescendants(record, "abcd:MultiMediaPublic");
        if (multimediaPublic == null || multimediaPublic.size() == 0) {
            return null;
        }

        List<ServiceAccessPoint> serviceAccessPoints = new ArrayList<>(fileUriElems.size());
        int n = 0;
        for (Element e : fileUriElems) {
            if (multimediaPublic.get(n++).getTextContent().trim().equals("0")) {
                // Multimedia item is not public
                continue;
            }

            // Retrieve uri
            String uriStr = e.getTextContent().trim();
            if (uriStr == null || uriStr.length() == 0)
                continue;

            // Change http urls to https urls, but leave the rest as they are
            if (uriStr.startsWith(MEDIALIB_HTTP_URL) && !uriStr.startsWith(MEDIALIB_HTTPS_URL)) {
                uriStr = uriStr.replace(MEDIALIB_HTTP_URL, MEDIALIB_HTTPS_URL);
            }

            // Test validity and/or encode uri when needed
            try {
                @SuppressWarnings("unused")
                URI uri = new URI(uriStr);
            } catch (URISyntaxException exc) {
                stats.objectsRejected++;
                if (!suppressErrors) {
                    logger.error("Invalid image URL for object {}. URI has been skipped: {}", objectID, uriStr);
                }
                continue;
            }

            // Extract medialib ID
            String medialibId = uriStr.substring(MEDIALIB_URL_START.length());
            int x = medialibId.indexOf('/');
            if (x != -1) {
                medialibId = medialibId.substring(0, x);
            }

            // Retrieve mime type
            String mimeType = mimetypeCache.getMimeType(medialibId);
            if (mimeType != null) {
                serviceAccessPoints.add(new ServiceAccessPoint(uriStr, mimeType, DEFAULT_IMAGE_QUALITY));
            } else if (uriStr.length() > 0 && mimeType == null) {
                logger.error("Multimedia URI from object {} has no mime type! URI has been skipped: {}", objectID, uriStr);
            }
        }
        return (serviceAccessPoints.size() == 0) ? null : serviceAccessPoints;
    }

    private List<String> getPreviousSourceIds() {
        Element record = input.getRecord();
        List<Element> elems = DOMUtil.getDescendants(record, "abcd:PreviousSourceName");
        if (elems == null) {
            return null;
        }
        List<String> sourceIds = new ArrayList<>(elems.size());
        for (Element e : elems) {
            String id = e.getTextContent().trim();
            if (id.length() > 0)
                sourceIds.add(id);
        }
        return (sourceIds.size() == 0) ? null : sourceIds;
    }

    private SpecimenIdentification getIdentification(Element elem, String collectionType) {

        ScientificName sn = getScientificName(elem, collectionType);
        if (sn.getFullScientificName() == null) {
            if (!suppressErrors) {
                warn("No scientific name in identification");
            }
            return null;
        }
        SpecimenIdentification si = new SpecimenIdentification();
        String s = val(elem, "abcd:PreferredFlag");
        si.setPreferred(s == null || s.equals("1"));
        si.setTypeStatus(getTypeStatus(elem));
        si.setDateIdentified(date(elem, "abcd:IdentificationDate"));
        String identifier = val(elem, "abcd:Identifier");
        if (identifier != null)
            si.addIdentifier(new Agent(identifier));
        si.setAssociatedFossilAssemblage(val(elem, "abcd:AssociatedFossilAssemblage"));
        si.setAssociatedMineralName(val(elem, "abcd:AssociatedMineralName"));
        si.setRockMineralUsage(val(elem, "abcd:RockMineralUsage"));
        si.setRockType(val(elem, "abcd:RockType"));
        si.setScientificName(sn);

        // Classification
        List<Monomial> sc = getSystemClassification(elem, si.getScientificName());
        // System classification disabled for specimens and multimedia
        // si.setSystemClassification(sc);
        DefaultClassification dc = DefaultClassification.fromSystemClassification(sc);
        si.setDefaultClassification(dc);

        // Taxon rank (NBAX-276)
        String infrasSubspecificRank = val(elem, "abcd:InfrasubspecificRank");
        String fullScientificNameStr = val(elem, "abcd:FullScientificNameString");
        String taxonCoverageStr = val(elem, "abcd:taxonCoverage");

        // 0. infra subspecific rank
        if (infrasSubspecificRank != null) {
            si.setTaxonRank(infrasSubspecificRank);
        }
        // 1. subspecies
        else if (si.getScientificName().getInfraspecificEpithet() != null && si.getScientificName().getGenusOrMonomial() != null)
            si.setTaxonRank("subspecies");
            // 2. species
        else if (si.getScientificName().getSpecificEpithet() != null && si.getScientificName().getGenusOrMonomial() != null)
            si.setTaxonRank("species");
            // 3. subgenus
        else if (si.getScientificName().getSubgenus() != null && si.getScientificName().getGenusOrMonomial() != null)
            si.setTaxonRank("subgenus");
            // 4. genus
        else if (si.getScientificName().getGenusOrMonomial() != null)
            si.setTaxonRank("genus");
            // 5. genus
        else if (fullScientificNameStr != null && fullScientificNameStr.length() > 0)
            si.setTaxonRank("genus");
            // 6. "higherName"
        else if (taxonCoverageStr != null && taxonCoverageStr.length() > 0) {
            List<Element> higherNames = DOMUtil.getDescendants(elem, "ncrsHighername");
            TaxonomicRank taxonomicRank = getBestTaxonRank(higherNames);
            if (taxonomicRank != null)
                si.setTaxonRank(taxonomicRank.getEnglishName());
        }

        // Vernacular name
        String informalNameStr = val(elem, "abcd:InformalNameString");
        if (informalNameStr != null) {
            si.setVernacularNames(Collections.singletonList(new VernacularName(informalNameStr)));
        }
        return si;
    }

    private ScientificName getScientificName(Element elem, String collectionType) {

        ScientificName sn = new ScientificName();
        sn.setGenusOrMonomial(val(elem, "abcd:GenusOrMonomial"));
        sn.setSubgenus(val(elem, "abcd:Subgenus"));
        sn.setSpecificEpithet(val(elem, "abcd:SpeciesEpithet"));
        String subSpeciesEpithetStr = val(elem, "abcd:subspeciesepithet");
        if (subSpeciesEpithetStr == null) {
            subSpeciesEpithetStr = val(elem, "abcd:InfrasubspecificName");
        }
        sn.setInfraspecificEpithet(subSpeciesEpithetStr);
        sn.setNameAddendum(val(elem, "abcd:NameAddendum"));
        sn.setAuthorshipVerbatim(val(elem, "abcd:AuthorTeamOriginalAndYear"));

        // fullScientificName
        String fullScientificNameStr = val(elem, "abcd:FullScientificNameString");

        // 1. First, try to compile a full scientific name from higher name elements
        if (sn.getGenusOrMonomial() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(sn.getGenusOrMonomial()).append(' ');
            if (sn.getSubgenus() != null) {
                sb.append("(").append(sn.getSubgenus()).append(") ");
            }
            if (sn.getSpecificEpithet() != null) {
                sb.append(sn.getSpecificEpithet()).append(' ');
            }
            if (sn.getInfraspecificEpithet() != null) {
                sb.append(sn.getInfraspecificEpithet()).append(' ');
            }
            if (sn.getAuthorshipVerbatim() != null) {
                sb.append(sn.getAuthorshipVerbatim());
            }
            if (sb.length() != 0) {
                sn.setFullScientificName(sb.toString().trim());
            }
        } else if (fullScientificNameStr != null) {
            sn.setFullScientificName(fullScientificNameStr); // 2. Otherwise, use the string from the import file
        } else {
            // 3. or, when there is nothing else:
            List<Element> elems = DOMUtil.getChildren(elem, "ncrsHighername");
            // 3a. use the best taxonCoverage
            String nextBest = getBestTaxonCoverage(elems);
            // 3b. or the value of abcd:taxonCoverage of the first element "as is", when there is still no value
            if (nextBest == null && elems != null) {
                if (elems.size() > 0)
                    nextBest = val(elems.get(0), "abcd:taxonCoverage");
            }
            sn.setFullScientificName(nextBest);
        }

        // Set ScientificNameGroup
        if (collectionType.equals("Mineralogy and Petrology") || collectionType.equals("Mineralogy") || collectionType.equals("Petrology")) {
            if (sn.getFullScientificName() != null) {
                sn.setScientificNameGroup(sn.getFullScientificName().toLowerCase());
            }
        } else {
            TransformUtil.setScientificNameGroup(sn);
        }
        return sn;
    }

    private static List<Monomial> getSystemClassification(Element elem, ScientificName sn) {
        List<Monomial> lowerClassification = TransformUtil.getMonomialsInName(sn);
        List<Element> elems = DOMUtil.getChildren(elem, "ncrsHighername");
        if (elems == null) {
            return lowerClassification;
        }
        List<Monomial> classification = new ArrayList<>();
        for (Element e : elems) {
            String rank = DOMUtil.getValue(e, "abcd:HigherTaxonRank");
            String name = DOMUtil.getValue(e, "abcd:taxonCoverage");
            classification.add(new Monomial(rank, name));
        }
        if (lowerClassification != null) {
            classification.addAll(lowerClassification);
        }
        if (classification.size() == 0) {
            return null;
        }
        return classification;
    }

    private GatheringEvent getGatheringEvent() {
        Element record = input.getRecord();
        GatheringEvent ge = new GatheringEvent();
        ge.setProjectTitle(val(record, "abcd:ProjectTitle"));
        ge.setWorldRegion(val(record, "abcd:WorldRegion"));
        ge.setCountry(val(record, "abcd:Country"));
        ge.setProvinceState(val(record, "abcd:ProvinceState"));
        ge.setIsland(val(record, "abcd:Island"));
        ge.setLocality(val(record, "abcd:Locality"));
        ge.setLocalityText(val(record, "abcd:LocalityText"));
        ge.setNamedAreas(getNamedAreas());
        ge.setDateTimeBegin(date(record, "abcd:CollectingStartDate"));
        ge.setDateTimeEnd(date(record, "abcd:CollectingEndDate"));
        String s = val(record, "abcd:GatheringAgent");
        if (s != null) {
            ge.setGatheringPersons(Collections.singletonList(new Person(s)));
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
            ge.setSiteCoordinates(Collections.singletonList(new GatheringSiteCoordinates(lat, lon)));
        }
        ge.setAssociatedTaxa(getAssociatedTaxa());
        ge.setChronoStratigraphy(getChronoStratigraphyList());
        ge.setBioStratigraphy(getBioStratigraphyList());
        ge.setLithoStratigraphy(getLithoStratigraphyList());

        for (Field f : geFields) {
            try {
                if (f.get(ge) != null) {
                    return ge;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new ETLRuntimeException(e);
            }
        }

        return null;
    }

    private List<AssociatedTaxon> getAssociatedTaxa() {
        Element record = input.getRecord();
        List<Element> elements = DOMUtil.getDescendants(record, "ncrsSynecology");
        if (elements == null) {
            return null;
        }
        HashMap<TaxonRelationType, String> relationTypeMap = new HashMap<>();
        for (Element element : elements) {
            String scientificOrInformalName = val(element, "abcd:ScientificOrInformalName");
            TaxonRelationType relationType;
            try {
                relationType = getTaxonRelationType(element);
                if (relationType == null) {
                    continue;
                } else if (relationTypeMap.containsKey(relationType) && scientificOrInformalName != null) {
                    relationTypeMap.put(relationType, relationTypeMap.get(relationType).concat(" | " + scientificOrInformalName));
                } else if (scientificOrInformalName != null) {
                    relationTypeMap.put(relationType, scientificOrInformalName);
                }
            } catch (IllegalArgumentException e) {
                if (!suppressErrors) {
                    warn(e.getMessage());
                }
                continue;
            }
        }
        if (relationTypeMap.size() == 0) {
            return null;
        }
        ArrayList<AssociatedTaxon> associatedTaxa = new ArrayList<>();
        for (Entry<TaxonRelationType, String> entry : relationTypeMap.entrySet()) {
            associatedTaxa.add(new AssociatedTaxon(entry.getValue(), entry.getKey()));
        }
        return associatedTaxa;
    }

    private List<NamedArea> getNamedAreas() {
        Element record = input.getRecord();
        List<Element> elements = DOMUtil.getDescendants(record, "ncrsNamedAreas");
        if (elements == null) {
            return null;
        }
        List<NamedArea> namedAreas = new ArrayList<>();
        for (Element element : elements) {
            AreaClass areaClass;
            try {
                areaClass = getAreaClass(element);
            } catch (IllegalArgumentException ex) {
                if (!suppressErrors) {
                    warn(ex.getMessage());
                }
                continue;
            }
            String areaName = val(element, "abcd:AreaName");
            if (areaClass != null && areaName != null) {
                namedAreas.add(new NamedArea(areaClass, areaName));
            }
        }
        return (namedAreas.size() == 0) ? null : namedAreas;
    }

    private List<ChronoStratigraphy> getChronoStratigraphyList() {
        Element record = input.getRecord();
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

    private ChronoStratigraphy getChronoStratigraphyObject(Element e) {
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

    private List<BioStratigraphy> getBioStratigraphyList() {
        Element record = input.getRecord();
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

    private BioStratigraphy getBioStratigraphyObject(Element e) {
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

    private List<LithoStratigraphy> getLithoStratigraphyList() {
        Element record = input.getRecord();
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

    private boolean hasTestGenus(List<Element> elems) {
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

    @Override
    protected String messagePrefix() {
        return super.messagePrefix() + rpad(databaseID, 10, " | ");
    }

    private LithoStratigraphy getLithoStratigraphyObject(Element e) {
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

    private boolean hasStatusDeleted() {
        Element hdr = DOMUtil.getChild(input.getRecord(), "header");
        if (hdr == null || !hdr.hasAttribute("status"))
            return false;
        return hdr.getAttribute("status").equals("deleted");
    }

    private OffsetDateTime getDateLastEdited() {
        Element hdr = DOMUtil.getChild(input.getRecord(), "header");
        String dateStamp = val(hdr, "datestamp");
        if (dateStamp != null) {
            ESDateInput input = new ESDateInput(dateStamp);
            OffsetDateTime odt = input.parseAsOffsetDateTime();
            if (odt == null && !suppressErrors) {
                warn("Invalid date in element <datestamp>: %s", dateStamp);
            }
            return odt;
        }
        return null;
    }

    private String getPhaseOrStage() {
        String raw = val(input.getRecord(), "abcd:PhaseOrStage");
        try {
            return posNormalizer.mapToString(raw);
        } catch (UnmappedValueException e) {
            if (logger.isDebugEnabled()) {
                debug(e.getMessage());
            }
            return null;
        }
    }

    private SpecimenTypeStatus getTypeStatus(Element elem) {
        String raw = val(elem, "abcd:NomenclaturalTypeText");
        if (raw == null) {
            if (logger.isDebugEnabled()) {
                debug("Missing type status");
            }
            return null;
        }
        try {
            return tsNormalizer.map(raw);
        } catch (UnmappedValueException e) {
            if (logger.isDebugEnabled()) {
                debug(e.getMessage());
            }
            return null;
        }
    }

    private Sex getSex() {
        String raw = val(input.getRecord(), "abcd:Sex");
        try {
            return sexNormalizer.map(raw);
        } catch (UnmappedValueException e) {
            if (logger.isDebugEnabled()) {
                debug(e.getMessage());
            }
            return null;
        }
    }

    private AreaClass getAreaClass(Element elem) {
        String raw = val(elem, "abcd:AreaClass");
        try {
            return areaClassNormalizer.map(raw);
        } catch (UnmappedValueException e) {
            if (logger.isDebugEnabled()) {
                debug(e.getMessage());
            }
            return null;
        }
    }

    private TaxonRelationType getTaxonRelationType(Element elem) {
        String raw = val(elem, "abcd:ResultRole");
        try {
            return trtNormalizer.map(raw);
        } catch (UnmappedValueException e) {
            if (logger.isDebugEnabled()) {
                debug(e.getMessage());
            }
            return null;
        }
    }

    private static final String MSG_BAD_DATE = "Invalid date in element %s: \"%s\"";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private OffsetDateTime date(Element e, String tag) {
        String s = val(e, tag);
        if (s == null)
            return null;
        /*
         * Don't be smart with s.toCharArray()
         */
        char[] chars = new char[8];
        Arrays.fill(chars, '0');
        for (int i = 0; i < s.length(); ++i)
            chars[i] = s.charAt(i);
        if (chars[4] == '0' && chars[5] == '0')
            chars[5] = '1';
        if (chars[6] == '0' && chars[7] == '0')
            chars[7] = '1';
        ESDateInput input = new ESDateInput(String.valueOf(chars));
        OffsetDateTime odt = input.parseAsLocalDate(formatter);
        if (odt == null && !suppressErrors) {
            warn(MSG_BAD_DATE, tag, s);
        }
        return odt;
    }

    private Double dval(Element e, String tag) {
        String s = val(e, tag);
        if (s == null)
            return null;
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException exc) {
            if (!suppressErrors)
                warn("Invalid number in element <%s>: \"%s\"", tag, s);
            return null;
        }
    }

    private Integer ival(Element e, String tag) {
        String s = val(e, tag);
        if (s == null)
            return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException exc) {
            if (suppressErrors) {
                warn("Invalid integer in element <%s>: \"%s\"", tag, s);
            }
            return null;
        }
    }

    private boolean bval(Element e, String tag) {
        String s = val(e, tag);
        return (s == null || s.equals("1"));
    }

    private String val(Element e, String tag) {
        String s = DOMUtil.getDescendantValue(e, tag);
        if (s == null) {
            if (logger.isDebugEnabled()) {
                debug("No element <%s> under element <%s>", tag, e.getTagName());
            }
            return null;
        }
        return ((s = s.strip()).length() == 0 ? null : s);
    }

}
