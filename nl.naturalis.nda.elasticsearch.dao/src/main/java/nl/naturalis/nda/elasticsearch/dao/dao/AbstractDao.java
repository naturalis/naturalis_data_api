package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.SearchParamFieldMapping;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.BasePolygonBuilder;
import org.elasticsearch.common.geo.builders.MultiPolygonBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.IDENTIFICATIONS_VERNACULAR_NAMES_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_DATE_TIME_BEGIN;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_DATE_TIME_END;
import static org.elasticsearch.common.geo.builders.ShapeBuilder.newMultiPolygon;
import static org.elasticsearch.common.geo.builders.ShapeBuilder.newPolygon;
import static org.elasticsearch.index.query.FilterBuilders.geoShapeFilter;
import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.AND;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.OR;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.valueOf;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

/**
 * Abstract base class for all ElasticSearch data access objects.
 *
 * @author ayco_holleman
 */
public abstract class AbstractDao {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

    public static final String BASE_URL = "http://10.42.1.149:8080/nl.naturalis.nda.service.rest/api";
    public static final String TAXON_DETAIL_BASE_URL = BASE_URL + "/taxon/detail/?acceptedName=";
    public static final String SPECIMEN_DETAIL_BASE_URL = BASE_URL + "/specimen/detail/?unitID=";
    public static final String MULTIMEDIA_DETAIL_BASE_URL = BASE_URL + "/multimedia/detail/?unitID=";

    private static ObjectMapper objectMapper;
    private SearchParamFieldMapping searchParamFieldMapping;
    
    protected final Client esClient;
    protected final String ndaIndexName;

    public AbstractDao(Client esClient, String ndaIndexName) {
        this.esClient = esClient;
        this.ndaIndexName = ndaIndexName;
        this.searchParamFieldMapping = SearchParamFieldMapping.getInstance();
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
                                                   boolean highlighting) {
        return executeExtendedSearch(params, fields, type, highlighting, null);
    }

    /**
     * @param params
     * @param fields
     * @param type
     * @param highlighting  whether to use highlighting
     * @param prebuiltQuery ignored if null, appended with AND or OR (from _andOr in params) else
     * @return
     */
    // TODO: remove extraHighlightFields
    protected SearchResponse executeExtendedSearch(QueryParams params, List<FieldMapping> fields, String type,
                                                   boolean highlighting, QueryAndHighlightFields prebuiltQuery) {
        String sortField = getScoreFieldFromQueryParams(params);
        FieldSortBuilder fieldSort = fieldSort(sortField);
        SortOrder sortOrder = getSortOrderFromQueryParams(params);
        if (sortOrder != null) {
            fieldSort.order(sortOrder);
        }

        BoolQueryBuilder boolQueryBuilder = boolQuery();
        Operator operator = getOperator(params);

        Map<String, List<FieldMapping>> nestedFields = new HashMap<>();
        List<FieldMapping> nonNestedFields = new ArrayList<>();
        for (FieldMapping field : fields) {
            String nestedPath = field.getNestedPath();
            if (nestedPath != null && nestedPath.trim().length() > 0) {
                List<FieldMapping> fieldMappings = new ArrayList<>();
                if (nestedFields.containsKey(nestedPath)) {
                    fieldMappings = nestedFields.get(nestedPath);
                }

                fieldMappings.add(field);
                nestedFields.put(nestedPath, fieldMappings);
            } else {
                nonNestedFields.add(field);
            }
        }

        boolean atLeastOneFieldToQuery = false;

        for (String nestedPath : nestedFields.keySet()) {
            extendQueryWithNestedFieldsWithSameNestedPath(boolQueryBuilder, operator, nestedPath, nestedFields.get(
                    nestedPath));
            atLeastOneFieldToQuery = true;
        }

        for (FieldMapping field : nonNestedFields) {
            if (!field.getFieldName().contains("dateTime")) {
                extendQueryWithField(boolQueryBuilder, operator, field);
                atLeastOneFieldToQuery = true;
            }
        }

        if (prebuiltQuery != null && prebuiltQuery.getQuery() != null) {
            extendQueryWithQuery(boolQueryBuilder, operator, prebuiltQuery.getQuery());
            atLeastOneFieldToQuery = true;
        }

        if (params.containsKey("_geoShape")) {
            extendQueryWithGeoShapeFilter(boolQueryBuilder, params.getParam("_geoShape"));
            atLeastOneFieldToQuery = true;
        }

        atLeastOneFieldToQuery = extractRangeQuery(params, boolQueryBuilder, atLeastOneFieldToQuery);

        SearchRequestBuilder searchRequestBuilder = newSearchRequest().setTypes(type)
                                                                      .setQuery(filteredQuery(boolQueryBuilder, null))
                                                                      .addSort(fieldSort);
        Integer offSet = getOffSetFromParams(params);
        if (offSet != null) {
            searchRequestBuilder.setFrom(offSet);
        }
        setSize(params, searchRequestBuilder);

        if (highlighting) {
            for (FieldMapping fieldMapping : fields) {
                String fieldValue = fieldMapping.getValue();
                String fieldName = fieldMapping.getFieldName();
                if (fieldValue != null
                        && !GATHERINGEVENT_DATE_TIME_BEGIN.equals(fieldName)
                        && !GATHERINGEVENT_DATE_TIME_END.equals(fieldName)) {
                    searchRequestBuilder.addHighlightedField(createHighlightField(fieldName, fieldValue));
                }
            }
            if (prebuiltQuery != null) {
                for (HighlightBuilder.Field highlightField : prebuiltQuery.getHighlightFields()) {
                    searchRequestBuilder.addHighlightedField(highlightField);
                }
            }
            // TODO: add NGRAM query to highlighting
        }

        if (!atLeastOneFieldToQuery) {
            return new SearchResponse(InternalSearchResponse.empty(), "", 0, 0, 0, null);
        }

        logger.info(searchRequestBuilder.toString());
        return searchRequestBuilder.execute().actionGet();
    }

    //================================================ Helper methods ==================================================

    private boolean extractRangeQuery(QueryParams params, BoolQueryBuilder boolQueryBuilder, boolean atLeastOneFieldToQuery) {
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

    private void extendQueryWithGeoShapeFilter(BoolQueryBuilder boolQueryBuilder, String geoShape) {
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
            boolQueryBuilder.must(nestedQuery("gatheringEvent.siteCoordinates",
                                              geoShapeFilter(
                                                      "gatheringEvent.siteCoordinates.point",
                                                      shapeBuilder,
                                                      ShapeRelation.WITHIN)));
        }
    }

    private Coordinate[] getCoordinatesFromPolygon(List<List<LngLatAlt>> coordinatesPolygon) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (List<LngLatAlt> lngLatAlts : coordinatesPolygon) {
            for (LngLatAlt lngLatAlt : lngLatAlts) {
                double longitude = lngLatAlt.getLongitude();
                double latitude = lngLatAlt.getLatitude();
                coordinates.add(new Coordinate(latitude, longitude));
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

    private void setSize(QueryParams params, SearchRequestBuilder searchRequestBuilder) {
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

    private void extendQueryWithNestedFieldsWithSameNestedPath(BoolQueryBuilder boolQueryBuilder, Operator operator,
                                                               String nestedPath, List<FieldMapping> fields) {
        BoolQueryBuilder nestedBoolQueryBuilder = boolQuery();
        for (FieldMapping field : fields) {
            if (!field.getFieldName().contains("dateTime")) {
                extendQueryWithField(nestedBoolQueryBuilder, operator, field);
            }
        }

        NestedQueryBuilder nestedQueryBuilder = nestedQuery(nestedPath, nestedBoolQueryBuilder);

        extendQueryWithQuery(boolQueryBuilder, operator, nestedQueryBuilder);
    }

    private void extendQueryWithField(BoolQueryBuilder boolQueryBuilder, Operator operator, FieldMapping field) {
        if (field.getValue() != null) {
            Float boostValue = field.getBoostValue();
            MatchQueryBuilder matchQueryBuilder = matchQuery(field.getFieldName(), field.getValue());
            if (boostValue != null) {
                matchQueryBuilder.boost(boostValue);
            }

            extendQueryWithQuery(boolQueryBuilder, operator, matchQueryBuilder);

            if (field.hasNGram() != null && field.hasNGram()) {
                matchQueryBuilder = matchQuery(field.getFieldName() + ".ngram", field.getValue());
                extendQueryWithQuery(boolQueryBuilder, operator, matchQueryBuilder);
            }
        }
    }

    private void extendQueryWithQuery(BoolQueryBuilder boolQueryBuilder, Operator operator,
                                      QueryBuilder nameResolutionQuery) {
        if (operator == AND) {
            boolQueryBuilder.must(nameResolutionQuery);
        } else {
            boolQueryBuilder.should(nameResolutionQuery);
        }
    }

    /**
     * Get the operator from the query params.
     *
     * @param params the query params
     * @return the operator from the params, if not found {@link Operator#OR} is returned
     */
    private Operator getOperator(QueryParams params) {
        String operatorValue = params.getParam("_andOr");
        Operator operator = OR;
        if (operatorValue != null && !operatorValue.isEmpty()) {
            operator = valueOf(operatorValue);
        }
        return operator;
    }

    protected String getScoreFieldFromQueryParams(QueryParams params) {
        List<String> sortParam = params.get("_score");
        String sortField = "_score";
        if (sortParam != null && !sortParam.isEmpty()) {
            String sort = sortParam.get(0);
            if (sort == null || sort.trim().equalsIgnoreCase("")) {
                sortField = "_score";
            }
        }
        return sortField;
    }

    /**
     * Get the offSet from the params. If no offSet is provided, null will be returned.
     *
     * @param params the query params
     * @return the offSet if available, null otherwise
     */
    private Integer getOffSetFromParams(QueryParams params) {
        String offSetParam = params.getParam("_offSet");
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
    private SortOrder getSortOrderFromQueryParams(QueryParams params) {
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
     */
    protected QueryAndHighlightFields buildNameResolutionQuery(List<FieldMapping> fields, String simpleSearch,
                                                               BioportalTaxonDao taxonDao, boolean highlight) {
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
        nameResTaxonQueryParams.add("_andOr", "OR");
        nameResTaxonQueryParams.add("_maxResults", "50");
        SearchResultSet<Taxon> nameResTaxons = taxonDao.search(nameResTaxonQueryParams,
                                                               null,
                                                               false); // no field filtering
        if (nameResTaxons.getTotalSize() == 0) {
            return null;
        }

        QueryAndHighlightFields queryAndHighlightFields = new QueryAndHighlightFields();
        BoolQueryBuilder nameResQueryBuilder = boolQuery();
        for (SearchResult<Taxon> taxonSearchResult : nameResTaxons.getSearchResults()) {
            Taxon taxon = taxonSearchResult.getResult();
            BoolQueryBuilder scientificNameQuery = boolQuery();

            addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery, highlight,
                    taxon.getValidName().getGenusOrMonomial(),
                    IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL);
            addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery, highlight,
                    taxon.getValidName().getSpecificEpithet(),
                    IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET);
            addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery, highlight,
                    taxon.getValidName().getInfraspecificEpithet(),
                    IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET);

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
                highlightFieldsContainer.addHighlightField(createHighlightField(fieldName, fieldValue));
            }
        }
    }

    private HighlightBuilder.Field createHighlightField(String fieldName, String fieldValue) {
        HighlightBuilder.Field field = new HighlightBuilder.Field(fieldName);
        field.highlightQuery(matchQuery(fieldName, fieldValue));
        return field;
    }
}
