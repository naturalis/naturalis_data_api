package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryAndHighlightFields;
import nl.naturalis.nda.elasticsearch.dao.util.SearchParamFieldMapping;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.search.StringMatchInfo;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.BasePolygonBuilder;
import org.elasticsearch.common.geo.builders.MultiPolygonBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.internal.InternalSearchResponse;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.*;
import static org.elasticsearch.common.geo.builders.ShapeBuilder.newMultiPolygon;
import static org.elasticsearch.common.geo.builders.ShapeBuilder.newPolygon;
import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.*;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

/**
 * Abstract base class for all ElasticSearch data access objects.
 *
 * @author ayco_holleman
 */
public abstract class AbstractDao {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

    public static String BASE_URL;
    public static String TAXON_DETAIL_BASE_URL;
    public static String TAXON_DETAIL_BASE_URL_IN_RESULT_SET;
    public static String SPECIMEN_DETAIL_BASE_URL;
    public static String SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET;
    public static String MULTIMEDIA_DETAIL_BASE_URL_TAXON;
    public static String MULTIMEDIA_DETAIL_BASE_URL_SPECIMEN;

    private static ObjectMapper objectMapper;
    private SearchParamFieldMapping searchParamFieldMapping;

    protected final Client esClient;
    protected final String ndaIndexName;

    public AbstractDao(Client esClient, String ndaIndexName, String baseUrl) {
        this.esClient = esClient;
        this.ndaIndexName = ndaIndexName;
        this.searchParamFieldMapping = SearchParamFieldMapping.getInstance();
        BASE_URL = baseUrl;
        TAXON_DETAIL_BASE_URL = BASE_URL + "/taxon/get-taxon/?";
        TAXON_DETAIL_BASE_URL_IN_RESULT_SET = BASE_URL + "/taxon/get-taxon-within-result-set/?";
        SPECIMEN_DETAIL_BASE_URL = BASE_URL + "/specimen/get-specimen/?unitID=";
        SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET = BASE_URL + "/specimen/get-specimen-within-result-set/?unitID=";
        MULTIMEDIA_DETAIL_BASE_URL_TAXON = BASE_URL + "/multimedia/get-multimedia-object-for-taxon-within-result-set/?unitID=";
        MULTIMEDIA_DETAIL_BASE_URL_SPECIMEN = BASE_URL + "/multimedia/get-multimedia-object-for-specimen-within-result-set/?unitID=";
    }

    protected static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static boolean hasText(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private static boolean hasFieldWithTextWithOneOfNames(List<FieldMapping> fields, String... names) {
        List<String> nameList = Arrays.asList(names);
        for (FieldMapping field : fields) {
            if (hasText(field.getFieldName()) && nameList.contains(field.getFieldName()) && hasText(field.getValue())) {
                return true;
            }
        }
        return false;
    }


    protected SearchRequestBuilder newSearchRequest() {
        return esClient.prepareSearch(ndaIndexName);
    }

    protected SearchParamFieldMapping getSearchParamFieldMapping() {
        return searchParamFieldMapping;
    }

    protected SearchResponse executeExtendedSearch(QueryParams params, List<FieldMapping> fields, String type,
                                                   boolean highlighting, String sessionId) {
        return executeExtendedSearch(params, fields, type, highlighting, null, sessionId);
    }

    /**
     * @param params
     * @param fields
     * @param type
     * @param highlighting  whether to use highlighting
     * @param prebuiltQuery ignored if null, appended with AND or OR (from _andOr in params) else
     * @param sessionId
     * @return
     */
    protected SearchResponse executeExtendedSearch(QueryParams params, List<FieldMapping> fields, String type,
                                                   boolean highlighting, QueryAndHighlightFields prebuiltQuery, String sessionId) {


        CreateQuery createQuery = new CreateQuery(params, fields).invoke();
        BoolQueryBuilder query = createQuery.getQuery();
        Map<String, HighlightBuilder.Field> highlightFields = createQuery.getHighlightFields();


        if (prebuiltQuery != null) {
            if (prebuiltQuery.getQuery() != null) {
                query.should(prebuiltQuery.getQuery());
            }
            Map<String, HighlightBuilder.Field> nameResQueryHighlightFields = prebuiltQuery.getHighlightFields();
            if (nameResQueryHighlightFields != null && nameResQueryHighlightFields.size() != 0) {
                for (Map.Entry<String, HighlightBuilder.Field> stringFieldEntry : nameResQueryHighlightFields.entrySet()) {
                    highlightFields.put(stringFieldEntry.getKey(), stringFieldEntry.getValue());
                }
            }
        }


        NestedFilterBuilder geoShape = null;
        boolean geoSearch = false;
        if (params.containsKey("_geoShape")) {
            geoShape = createGeoShapeFilter(params.getParam("_geoShape"));
            geoSearch = true;
        }


        SearchRequestBuilder searchRequestBuilder = newSearchRequest().setTypes(type)
                .setQuery(filteredQuery(query, geoShape))
                .addSort(createFieldSort(params)).setTrackScores(true);
        Integer offSet = getOffSetFromParams(params);
        if (offSet != null) {
            searchRequestBuilder.setFrom(offSet);
        }
        setSize(params, searchRequestBuilder);

        if (!highlightFields.isEmpty()) {
            for (HighlightBuilder.Field highlightField : highlightFields.values()) {
                searchRequestBuilder.addHighlightedField(highlightField);
            }
            searchRequestBuilder.setHighlighterPreTags("<span class=\"search_hit\">").setHighlighterPostTags("</span>");
        }

        searchRequestBuilder.setPreference(sessionId);
        boolean atLeastOneFieldToQuery = query.hasClauses();
        if (geoSearch && !atLeastOneFieldToQuery) {
            searchRequestBuilder.setQuery(filteredQuery(matchAllQuery(), geoShape));
            logger.info(searchRequestBuilder.toString());
            return searchRequestBuilder.execute().actionGet();
        }

        if (!atLeastOneFieldToQuery) {
            return new SearchResponse(InternalSearchResponse.empty(), "", 0, 0, 0, null);
        }

        logger.info(searchRequestBuilder.toString());
        return searchRequestBuilder.execute().actionGet();
    }

    //================================================ Helper methods ==================================================

    FieldSortBuilder createFieldSort(QueryParams params) {
        String sortField = getSortFieldFromQueryParams(params);
        FieldSortBuilder fieldSort = fieldSort(sortField);
        SortOrder sortOrder = getSortOrderFromQueryParams(params);
        if (sortOrder != null) {
            fieldSort.order(sortOrder);
        }
        return fieldSort;
    }

    boolean extractRangeQuery(QueryParams params, BoolQueryBuilder boolQueryBuilder, boolean atLeastOneFieldToQuery) {
        //todo add?
        if (params.containsKey("gatheringEvent.dateTimeBegin") || params.containsKey("gatheringEvent.dateTimeEnd")) {
            extendQueryWithRangeFilter(boolQueryBuilder,
                    params,
                    "gatheringEvent.dateTimeBegin",
                    "gatheringEvent.dateTimeEnd");
            atLeastOneFieldToQuery = true;
        }

        if (params.containsKey("gatheringEvents.dateTimeBegin") || params.containsKey("gatheringEvents.dateTimeEnd")) {
            extendQueryWithRangeFilter(boolQueryBuilder,
                    params,
                    "gatheringEvents.dateTimeBegin",
                    "gatheringEvents.dateTimeEnd");
            atLeastOneFieldToQuery = true;
        }

        return atLeastOneFieldToQuery;
    }

    private void extendQueryWithRangeFilter(BoolQueryBuilder boolQueryBuilder, QueryParams params, String dateTimeBegin,
                                            String dateTimeEnd) {
        //todo add?
        String begin = params.getParam(dateTimeBegin);
        String end = params.getParam(dateTimeEnd);
        if (begin != null) {
            RangeFilterBuilder dateTimeBeginRangeFilterBuilder = rangeFilter(dateTimeBegin)
                    .from(begin);
            boolQueryBuilder.must(filteredQuery(matchAllQuery(), dateTimeBeginRangeFilterBuilder));
        }
        if (end != null) {
            RangeFilterBuilder dateTimeEndRangeFilterBuilder = rangeFilter(dateTimeEnd).to(end);
            boolQueryBuilder.must(filteredQuery(matchAllQuery(), dateTimeEndRangeFilterBuilder));
        }
    }

    NestedFilterBuilder createGeoShapeFilter(String geoShape) {
        GeoJsonObject geo;
        ShapeBuilder shapeBuilder = null;
        try {
            geo = getObjectMapper().readValue(geoShape, GeoJsonObject.class);
            if (geo instanceof MultiPolygon) {
                List<List<List<LngLatAlt>>> coordinatesMultiPolygon = ((MultiPolygon) geo).getCoordinates();
                if (coordinatesMultiPolygon != null) {
                    MultiPolygonBuilder multiPolygonBuilder = newMultiPolygon();
                    for (List<List<LngLatAlt>> lists : coordinatesMultiPolygon) {
                        Coordinate[] polygon = getCoordinatesFromPolygon(lists);
                        BasePolygonBuilder basePolygonBuilder = new PolygonBuilder();
                        basePolygonBuilder.points(polygon);
                        multiPolygonBuilder.polygon(basePolygonBuilder);
                    }
                    shapeBuilder = multiPolygonBuilder;
                }
            } else if (geo instanceof Polygon) {
                List<List<LngLatAlt>> coordinates = ((Polygon) geo).getCoordinates();
                if (coordinates != null) {
                    Coordinate[] polygon = getCoordinatesFromPolygon(coordinates);
                    shapeBuilder = newPolygon().points(polygon);
                }
            }
        } catch (IOException e) {
            logger.info(String.format("Could not get coordinates from provided geoShape %s", geoShape), e);
        }

        if (shapeBuilder != null) {
            return nestedFilter("gatheringEvent.siteCoordinates",
                    geoShapeFilter(
                            "gatheringEvent.siteCoordinates.point",
                            shapeBuilder,
                            ShapeRelation.WITHIN));
        }
        return null;
    }

    private Coordinate[] getCoordinatesFromPolygon(List<List<LngLatAlt>> coordinatesPolygon) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (List<LngLatAlt> lngLatAlts : coordinatesPolygon) {
            for (LngLatAlt lngLatAlt : lngLatAlts) {
                double longitude = lngLatAlt.getLongitude();
                double latitude = lngLatAlt.getLatitude();
                coordinates.add(new Coordinate(longitude, latitude));
            }
        }
        return coordinates.toArray(new Coordinate[coordinates.size()]);
    }

    /**
     * Checks if the given fields are allowed based on the provided list of allowed fields.
     *
     * @param fields        the field to check
     * @param allowedFields list of allowed fields
     * @return a new list with the allowed fields
     */
    protected List<FieldMapping> filterAllowedFieldMappings(List<FieldMapping> fields, Set<String> allowedFields) {
        List<FieldMapping> approvedFields = new ArrayList<>();
        for (FieldMapping field : fields) {
            if (allowedFields.contains(field.getFieldName())) {
                approvedFields.add(field);
            }
        }

        return approvedFields;
    }

    void setSize(QueryParams params, SearchRequestBuilder searchRequestBuilder) {
        if (params.containsKey("_maxResults")) {
            String maxResultsAsString = params.getFirst("_maxResults");
            try {
                Integer maxResults = Integer.valueOf(maxResultsAsString);
                searchRequestBuilder.setSize(maxResults);
            } catch (NumberFormatException e) {
                logger.debug("Could not parse _maxResults value '" + maxResultsAsString + "'. Using 50.");
                searchRequestBuilder.setSize(50);
            }
        }
    }

    /**
     * Get the operator from the query params.
     *
     * @param params the query params
     * @return the operator from the params, if not found {@link Operator#OR} is returned
     */
    protected Operator getOperator(QueryParams params) {
        String operatorValue = params.getParam("_andOr");
        Operator operator = AND;
        if (operatorValue != null && !operatorValue.isEmpty()) {
            operator = valueOf(operatorValue);
        }
        return operator;
    }

    protected String getSortFieldFromQueryParams(QueryParams params) {
        String sortParam = params.getParam("_sort");
        String sortField = "_score";
        if (sortParam != null && !sortParam.isEmpty() && !sortParam.equalsIgnoreCase("_score")) {
            sortField = sortParam + ".raw";
        }
        return sortField;
    }

    /**
     * Get the offSet from the params. If no offSet is provided, null will be returned.
     *
     * @param params the query params
     * @return the offSet if available, null otherwise
     */
    Integer getOffSetFromParams(QueryParams params) {
        String offSetParam = params.getParam("_offset");
        if (hasText(offSetParam)) {
            return Integer.parseInt(offSetParam);
        }

        return null;
    }

    /**
     * Get the sort order from the params. If no order is provided, null will be returned.
     *
     * @param params the query params
     * @return the sort order if available, null otherwise
     */
    SortOrder getSortOrderFromQueryParams(QueryParams params) {
        String sortOrderParam = params.getParam("_sortDirection");
        SortOrder sortOrder = null;
        if (hasText(sortOrderParam)) {
            if (SortOrder.ASC.name().equals(sortOrderParam)) {
                sortOrder = SortOrder.ASC;
            }
            if (SortOrder.DESC.name().equals(sortOrderParam)) {
                sortOrder = SortOrder.DESC;
            }
        }
        return sortOrder;
    }

    /**
     * @param fields       parameters for the query
     * @param simpleSearch
     * @param taxonDao     @return null in case of no valid param_keys or no taxons matching the supplied values
     * @param highlight
     * @param operator     only used in case of extended search
     * @param sessionId
     */
    protected QueryAndHighlightFields buildNameResolutionQuery(List<FieldMapping> fields,
                                                               String simpleSearch,
                                                               BioportalTaxonDao taxonDao,
                                                               boolean highlight,
                                                               Operator operator,
                                                               String sessionId) {
        if (!hasFieldWithTextWithOneOfNames(fields,
                IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY) && !hasText(simpleSearch)) {
            return null;
        }

        // nameRes = name resolution
        QueryParams nameResTaxonQueryParams = new QueryParams();
        if (hasText(simpleSearch)) {
            nameResTaxonQueryParams.add("vernacularNames.name", simpleSearch);
            nameResTaxonQueryParams.add("synonyms.genusOrMonomial", simpleSearch);
            nameResTaxonQueryParams.add("synonyms.specificEpithet", simpleSearch);
            nameResTaxonQueryParams.add("synonyms.infraspecificEpithet", simpleSearch);
        }
        for (FieldMapping field : fields) {
            switch (field.getFieldName()) {
                case IDENTIFICATIONS_VERNACULAR_NAMES_NAME:
                    nameResTaxonQueryParams.add("vernacularNames.name", field.getValue());
                    break;
                case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM:
                    nameResTaxonQueryParams.add("defaultClassification.kingdom", field.getValue());
                    break;
                case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME:
                    nameResTaxonQueryParams.add("defaultClassification.className", field.getValue());
                    break;
                case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY:
                    nameResTaxonQueryParams.add("defaultClassification.family", field.getValue());
                    break;
                case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER:
                    nameResTaxonQueryParams.add("defaultClassification.order", field.getValue());
                    break;
                case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM:
                    nameResTaxonQueryParams.add("defaultClassification.phylum", field.getValue());
                    break;
            }
        }
        if (nameResTaxonQueryParams.size() == 0) {
            return null; // otherwise we would get an all-query
        }
        if (hasText(simpleSearch)) {
            nameResTaxonQueryParams.add("_andOr", "OR");
        } else {
            nameResTaxonQueryParams.add("_andOr", operator.name());
        }
        nameResTaxonQueryParams.add("_maxResults", "50");
        SearchResultSet<Taxon> nameResTaxons = taxonDao.searchReturnsResultSet(nameResTaxonQueryParams, null, null, true, sessionId); // no field filtering
        if (nameResTaxons.getTotalSize() == 0) {
            return null;
        }

        QueryAndHighlightFields queryAndHighlightFields = new QueryAndHighlightFields();
        BoolQueryBuilder nameResQueryBuilder = boolQuery();
        for (SearchResult<Taxon> taxonSearchResult : nameResTaxons.getSearchResults()) {
            Taxon taxon = taxonSearchResult.getResult();
            BoolQueryBuilder scientificNameQuery = boolQuery();

            addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery, highlight, taxon.getValidName().getGenusOrMonomial(), IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL);
            addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery, highlight, taxon.getValidName().getSpecificEpithet(), IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET);
            addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery, highlight, taxon.getValidName().getInfraspecificEpithet(), IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET);

            nameResQueryBuilder.should(scientificNameQuery);
        }
        NestedQueryBuilder nestedNameResQuery = nestedQuery("identifications", nameResQueryBuilder);
        nestedNameResQuery.boost(0.5f);

        queryAndHighlightFields.setQuery(nestedNameResQuery);
        return queryAndHighlightFields;
    }

    private void addMustQueryWithHighlightSupport(QueryAndHighlightFields highlightFieldsContainer,
                                                  BoolQueryBuilder query,
                                                  boolean highlight,
                                                  String fieldValue,
                                                  String fieldName) {
        if (fieldValue != null) {
            MatchQueryBuilder localQuery = matchQuery(fieldName, fieldValue);
            query.must(localQuery);
            if (highlight) {
                highlightFieldsContainer.addHighlightField(fieldName, createHighlightField(fieldName, localQuery));
            }
        }
    }

    HighlightBuilder.Field createHighlightField(String fieldName, QueryBuilder highlightQuery) {
        HighlightBuilder.Field field = new HighlightBuilder.Field(fieldName);
        field.highlightQuery(highlightQuery);
        return field;
    }

    /**
     * @param params                          parameters as passed to dao, may or may not include a _search entry
     * @param searchFieldNames                all fields on which can be search in (extended) search
     * @param simpleSearchFieldNameExceptions do no use these in simple search
     */
    protected void evaluateSimpleSearch(QueryParams params,
                                        Set<String> searchFieldNames,
                                        Set<String> simpleSearchFieldNameExceptions) {
        String simpleSearchTerm = params.getParam("_search");
        if (searchFieldNames == null || searchFieldNames.isEmpty()) {
            return;
        }
        if (simpleSearchFieldNameExceptions == null) {
            simpleSearchFieldNameExceptions = Collections.emptySet();
        }
        if (hasText(simpleSearchTerm)) {
            for (String searchField : searchFieldNames) {
                if (!simpleSearchFieldNameExceptions.contains(searchField)) {
                    params.add(searchField, simpleSearchTerm);
                }
            }
        }
    }

    protected void enhanceSearchResultWithMatchInfoAndScore(SearchResult<?> searchResult, SearchHit hit) {
        searchResult.setScore(hit.getScore());

        if (hit.getHighlightFields() != null) {
            LinkedHashMap<String, StringMatchInfo> stringMatchInfos = new LinkedHashMap<>();
            for (Map.Entry<String, HighlightField> highlightFieldEntry : hit.getHighlightFields().entrySet()) {
                StringMatchInfo stringMatchInfo = new StringMatchInfo();

                String fieldName = getFieldName(highlightFieldEntry);
                stringMatchInfo.setPath(fieldName);

                StringBuilder match = new StringBuilder();
                for (Text matchText : highlightFieldEntry.getValue().fragments()) {
                    match.append(matchText.string());
                }
                stringMatchInfo.setValueHighlighted(match.toString());

                // TODO: setValue
                stringMatchInfos.put(fieldName, stringMatchInfo);
            }
            List<StringMatchInfo> stringMatchInfoList = new ArrayList<>(stringMatchInfos.size());
            stringMatchInfoList.addAll(stringMatchInfos.values());
            searchResult.setMatchInfo(stringMatchInfoList);
        }
    }

    private String getFieldName(Map.Entry<String, HighlightField> highlightFieldEntry) {
        String key = highlightFieldEntry.getKey();

        int lastIndexOfNgramSuffix = key.lastIndexOf(SearchParamFieldMapping.NGRAM_SUFFIX);
        if (lastIndexOfNgramSuffix > 0) {
            return key.substring(0, lastIndexOfNgramSuffix);
        }
        return key;
    }

    protected String createAcceptedNameParams(ScientificName acceptedName) {
        StringBuilder stringBuilder = new StringBuilder();

        if (acceptedName != null) {
            boolean found = false;
            if (hasText(acceptedName.getGenusOrMonomial())) {
                found = true;
                stringBuilder.append("genus=").append(acceptedName.getGenusOrMonomial());
            }
            if (hasText(acceptedName.getSubgenus())) {
                if (found) {
                    stringBuilder.append("&");
                }
                stringBuilder.append("subgenus=").append(acceptedName.getSubgenus());
                found = true;
            }
            if (hasText(acceptedName.getSpecificEpithet())) {
                if (found) {
                    stringBuilder.append("&");
                }
                stringBuilder.append("specificEpithet=").append(acceptedName.getSpecificEpithet());
                found = true;
            }
            if (hasText(acceptedName.getInfraspecificEpithet())) {
                if (found) {
                    stringBuilder.append("&");
                }
                stringBuilder.append("infraspecificEpithet=").append(acceptedName.getInfraspecificEpithet());
            }
        }
        return stringBuilder.toString();
    }

    class CreateQuery {
        private QueryParams params;
        private List<FieldMapping> fields;
        private BoolQueryBuilder query;
        private Map<String, HighlightBuilder.Field> highlightFields;

        public CreateQuery(QueryParams params, List<FieldMapping> fields) {
            this.params = params;
            this.fields = fields;
        }

        public BoolQueryBuilder getQuery() {
            return query;
        }

        public Map<String, HighlightBuilder.Field> getHighlightFields() {
            return highlightFields;
        }

        public CreateQuery invoke() {
            Map<String, List<FieldMapping>> aliasFields = new LinkedHashMap<>();
            Map<String, List<FieldMapping>> nonAliasFields = new LinkedHashMap<>();
            for (FieldMapping field : fields) {
                if (field.isFromAlias()) {
                    List<FieldMapping> temp = aliasFields.get(field.getAliasName());
                    if (temp == null) {
                        temp = new ArrayList<>();
                    }
                    temp.add(field);
                    aliasFields.put(field.getAliasName(), temp);
                } else {
                    List<FieldMapping> temp = nonAliasFields.get(field.getFieldName());
                    if (temp == null) {
                        temp = new ArrayList<>();
                    }
                    temp.add(field);
                    nonAliasFields.put(field.getFieldName(), temp);
                }
            }

            List<String> tempKeys = new ArrayList<>();
            for (Map.Entry<String, List<FieldMapping>> stringListEntry : aliasFields.entrySet()) {
                if (stringListEntry.getValue().size() <= 1) {
                    tempKeys.add(stringListEntry.getKey());
                    nonAliasFields.put(stringListEntry.getKey(), stringListEntry.getValue());
                }
            }

            for (String tempKey : tempKeys) {
                aliasFields.remove(tempKey);
            }

            Map<String, List<FieldMapping>> tempAllowedFields = new LinkedHashMap<>();
            for (List<FieldMapping> fieldMappings : aliasFields.values()) {
                for (FieldMapping field : fieldMappings) {
                    String fieldName = field.getFieldName();
                    int lastIndex = fieldName.lastIndexOf(".");
                    if (lastIndex != -1) {
                        String groupPath = fieldName.substring(0, lastIndex);
                        List<FieldMapping> groupedFields = tempAllowedFields.get(groupPath);
                        if (groupedFields == null) {
                            groupedFields = new ArrayList<>();
                        }
                        groupedFields.add(field);
                        tempAllowedFields.put(groupPath, groupedFields);
                    } else {
                        tempAllowedFields.put(fieldName, Arrays.asList(field));
                    }
                }
            }


            Operator operator = getOperator(params);
            query = new BoolQueryBuilder();
            highlightFields = new HashMap<>();

            for (List<FieldMapping> fieldMappings : nonAliasFields.values()) {
                for (FieldMapping fieldMapping : fieldMappings) {
                    Float boostValue = fieldMapping.getBoostValue();
                    if (boostValue == null) {
                        boostValue = 1f;
                    }
                    String fieldValue = fieldMapping.getValue();
                    String fieldName = fieldMapping.getFieldName();
                    String nestedPath = fieldMapping.getNestedPath();
                    boolean isNested = nestedPath != null && nestedPath.trim().length() > 0;
                    boolean hasNgram = fieldMapping.hasNGram() != null && fieldMapping.hasNGram();

                    QueryBuilder tempQuery;
                    if (isNested) {
                        if (hasNgram) {
                            tempQuery = boolQuery()
                                    .should(nestedQuery(nestedPath, matchQuery(fieldName, fieldValue)).boost(boostValue))
                                    .should(nestedQuery(nestedPath, matchQuery(fieldName + "ngram", fieldValue)).boost(boostValue));
                            highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                        } else {
                            tempQuery = nestedQuery(nestedPath, matchQuery(fieldName, fieldValue).boost(boostValue));
                        }
                    } else {
                        if (hasNgram) {
                            tempQuery = boolQuery()
                                    .should(matchQuery(fieldName, fieldValue).boost(boostValue))
                                    .should(matchQuery(fieldName + ".ngram", fieldValue).boost(boostValue));
                            highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                        } else {
                            tempQuery = matchQuery(fieldName, fieldValue).boost(boostValue);
                        }
                    }
                    highlightFields.put(fieldMapping.getFieldName(), createHighlightField(fieldMapping.getFieldName(), matchQuery(fieldMapping.getFieldName(), fieldMapping.getValue())));
                    if (operator.equals(AND)) {
                        query.must(tempQuery);
                    } else {
                        query.should(tempQuery);
                    }
                }
            }

            Map<String, List<FieldMapping>> aliasFieldMappings = new LinkedHashMap<>();
            for (List<FieldMapping> fieldMappings : aliasFields.values()) {
                if (fieldMappings.size() <= 1) {
                    FieldMapping fieldMapping = fieldMappings.get(0);
                    Float boostValue = fieldMapping.getBoostValue();
                    if (boostValue == null) {
                        boostValue = 1f;
                    }
                    String fieldValue = fieldMapping.getValue();
                    String fieldName = fieldMapping.getFieldName();
                    String nestedPath = fieldMapping.getNestedPath();
                    boolean isNested = nestedPath != null && nestedPath.trim().length() > 0;
                    boolean hasNgram = fieldMapping.hasNGram() != null && fieldMapping.hasNGram();

                    QueryBuilder tempQuery;
                    if (isNested) {
                        if (hasNgram) {
                            tempQuery = boolQuery()
                                    .should(nestedQuery(nestedPath, matchQuery(fieldName, fieldValue)).boost(boostValue))
                                    .should(nestedQuery(nestedPath, matchQuery(fieldName + "ngram", fieldValue)).boost(boostValue));
                            highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                        } else {
                            tempQuery = nestedQuery(nestedPath, matchQuery(fieldName, fieldValue).boost(boostValue));
                        }
                    } else {
                        if (hasNgram) {
                            tempQuery = boolQuery()
                                    .should(matchQuery(fieldName, fieldValue).boost(boostValue))
                                    .should(matchQuery(fieldName + ".ngram", fieldValue).boost(boostValue));
                            highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                        } else {
                            tempQuery = matchQuery(fieldName, fieldValue).boost(boostValue);
                        }
                    }
                    highlightFields.put(fieldMapping.getFieldName(), createHighlightField(fieldMapping.getFieldName(), matchQuery(fieldMapping.getFieldName(), fieldMapping.getValue())));
                    if (operator.equals(AND)) {
                        query.must(tempQuery);
                    } else {
                        query.should(tempQuery);
                    }
                } else {
                    for (FieldMapping fieldMapping : fieldMappings) {
                        String fieldName = fieldMapping.getFieldName();
                        int lastIndex = fieldName.lastIndexOf(".");
                        String groupPath = fieldName.substring(0, lastIndex);

                        List<FieldMapping> fieldMappingList = aliasFieldMappings.get(groupPath);
                        if (fieldMappingList == null) {
                            fieldMappingList = new ArrayList<>();
                        }
                        fieldMappingList.add(fieldMapping);
                        aliasFieldMappings.put(groupPath, fieldMappingList);
                    }
                }
            }

            List<FieldMapping> tempSmallAliasFieldMapppings = new ArrayList<>();
            List<QueryBuilder> aliasQueries = new ArrayList<>();
            for (List<FieldMapping> fieldMappings : aliasFieldMappings.values()) {
                List<QueryBuilder> tempInnerAliasQueries = new ArrayList<>();
                if (fieldMappings.size() <= 1) {
                    tempSmallAliasFieldMapppings.add(fieldMappings.get(0));
                } else {
                    for (FieldMapping fieldMapping : fieldMappings) {
                        QueryBuilder queryBuilder;
                        Float boostValue = fieldMapping.getBoostValue();
                        if (boostValue == null) {
                            boostValue = 1f;
                        }
                        String fieldValue = fieldMapping.getValue();
                        String fieldName = fieldMapping.getFieldName();
                        String nestedPath = fieldMapping.getNestedPath();
                        boolean isNested = nestedPath != null && nestedPath.trim().length() > 0;
                        boolean hasNgram = fieldMapping.hasNGram() != null && fieldMapping.hasNGram();

                        if (isNested) {
                            if (hasNgram) {
                                queryBuilder = boolQuery()
                                        .should(nestedQuery(nestedPath, matchQuery(fieldName, fieldValue)).boost(boostValue))
                                        .should(nestedQuery(nestedPath, matchQuery(fieldName + ".ngram", fieldValue)).boost(boostValue));
                                highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                            } else {
                                queryBuilder = nestedQuery(nestedPath, matchQuery(fieldName, fieldValue).boost(boostValue));
                            }
                        } else {
                            if (hasNgram) {
                                queryBuilder = boolQuery()
                                        .should(matchQuery(fieldName, fieldValue).boost(boostValue))
                                        .should(matchQuery(fieldName + ".ngram", fieldValue).boost(boostValue));
                                highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                            } else {
                                queryBuilder = matchQuery(fieldName, fieldValue).boost(boostValue);
                            }
                        }
                        highlightFields.put(fieldMapping.getFieldName(), createHighlightField(fieldMapping.getFieldName(), matchQuery(fieldMapping.getFieldName(), fieldMapping.getValue())));
                        tempInnerAliasQueries.add(queryBuilder);
                    }
                    BoolQueryBuilder actualAliasQuery = boolQuery();
                    for (QueryBuilder tempInnerAliasQuery : tempInnerAliasQueries) {
                        if (operator.equals(AND)) {
                            actualAliasQuery.must(tempInnerAliasQuery);
                        } else {
                            actualAliasQuery.should(tempInnerAliasQuery);
                        }
                    }
                    aliasQueries.add(actualAliasQuery);
                }
            }

            Map<String, List<FieldMapping>> temps = new LinkedHashMap<>();
            for (String aliasKey : aliasFields.keySet()) {
                for (FieldMapping fieldMapping : tempSmallAliasFieldMapppings) {
                    if (fieldMapping.getAliasName().equalsIgnoreCase(aliasKey)) {
                        List<FieldMapping> fieldMappings = temps.get(aliasKey);
                        if (fieldMappings == null) {
                            fieldMappings = new LinkedList<>();
                        }
                        fieldMappings.add(fieldMapping);
                        temps.put(aliasKey, fieldMappings);
                    }
                }
            }

            for (List<FieldMapping> fieldMappings : temps.values()) {
                List<QueryBuilder> tempInnerAliasQueries = new ArrayList<>();
                for (FieldMapping fieldMapping : fieldMappings) {
                    QueryBuilder queryBuilder;
                    Float boostValue = fieldMapping.getBoostValue();
                    if (boostValue == null) {
                        boostValue = 1f;
                    }
                    String fieldValue = fieldMapping.getValue();
                    String fieldName = fieldMapping.getFieldName();
                    String nestedPath = fieldMapping.getNestedPath();
                    boolean isNested = nestedPath != null && nestedPath.trim().length() > 0;
                    boolean hasNgram = fieldMapping.hasNGram() != null && fieldMapping.hasNGram();

                    if (isNested) {
                        if (hasNgram) {
                            queryBuilder = boolQuery()
                                    .should(nestedQuery(nestedPath, matchQuery(fieldName, fieldValue)).boost(boostValue))
                                    .should(nestedQuery(nestedPath, matchQuery(fieldName + ".ngram", fieldValue)).boost(boostValue));
                            highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                        } else {
                            queryBuilder = nestedQuery(nestedPath, matchQuery(fieldName, fieldValue).boost(boostValue));
                        }
                    } else {
                        if (hasNgram) {
                            queryBuilder = boolQuery()
                                    .should(matchQuery(fieldName, fieldValue).boost(boostValue))
                                    .should(matchQuery(fieldName + ".ngram", fieldValue).boost(boostValue));
                            highlightFields.put(fieldMapping.getFieldName() + ".ngram", createHighlightField(fieldMapping.getFieldName() + ".ngram", matchQuery(fieldMapping.getFieldName() + ".ngram", fieldMapping.getValue())));
                        } else {
                            queryBuilder = matchQuery(fieldName, fieldValue).boost(boostValue);
                        }
                    }
                    highlightFields.put(fieldMapping.getFieldName(), createHighlightField(fieldMapping.getFieldName(), matchQuery(fieldMapping.getFieldName(), fieldMapping.getValue())));
                    tempInnerAliasQueries.add(queryBuilder);
                }
                BoolQueryBuilder actualAliasQuery = boolQuery();
                for (QueryBuilder tempInnerAliasQuery : tempInnerAliasQueries) {
                    if (operator.equals(AND)) {
                        actualAliasQuery.must(tempInnerAliasQuery);
                    } else {
                        actualAliasQuery.should(tempInnerAliasQuery);
                    }
                }
                aliasQueries.add(actualAliasQuery);
            }


            for (QueryBuilder aliasQuery : aliasQueries) {
                query.should(aliasQuery);
            }
            return this;
        }
    }
}
