package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryAndHighlightFields;
import nl.naturalis.nda.search.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Iterables;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.internal.InternalSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer.transfer;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.*;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.*;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.SPECIMEN_TYPE;
import static org.elasticsearch.action.search.SearchType.COUNT;
import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.OR;
import static org.elasticsearch.search.aggregations.AggregationBuilders.*;
import static org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.aggregation;

public class BioportalSpecimenDao extends AbstractDao {

    private static final Logger logger = LoggerFactory.getLogger(BioportalSpecimenDao.class);

    private static final Set<String> specimenNameSearchFieldNames = new HashSet<>(Arrays.asList(
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_SUBGENUS,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_GENUS,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_SPECIFIC_EPITHET,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_INFRASPECIFIC_EPITHET,
            IDENTIFICATIONS_SYSTEM_CLASSIFICATION_NAME,
            IDENTIFICATIONS_SCIENTIFIC_NAME_FULL_SCIENTIFIC_NAME,
            //todo quick hack to allow Ruud to get Specimens based on their scientificName without using match query
            IDENTIFICATIONS_SCIENTIFIC_NAME_FULL_SCIENTIFIC_NAME + ".raw",
            IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL,
            IDENTIFICATIONS_SCIENTIFIC_NAME_SUBGENUS,
            IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET,
            IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET,
            IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
            GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT));

    private static final Set<String> specimenNameSearchFieldNames_simpleSearchExceptions = new HashSet<>(Arrays.asList(
            GATHERINGEVENT_DATE_TIME_BEGIN, GATHERINGEVENT_SITECOORDINATES_POINT));

    private static final Set<String> specimenSearchFieldNames = new HashSet<>(Arrays.asList(
            UNIT_ID,
            TYPE_STATUS,
            PHASE_OR_STAGE,
            SEX,
            THEME,
            COLLECTORS_FIELD_NUMBER,
            GATHERINGEVENT_LOCALITY_TEXT,
            GATHERINGEVENT_GATHERING_PERSONS_FULLNAME,
            GATHERINGEVENT_GATHERING_ORGANISATIONS_NAME,
            GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT));

    private static final Set<String> specimenSearchFieldNames_simpleSearchExceptions = new HashSet<>(Arrays.asList(GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT));

    private final BioportalTaxonDao bioportalTaxonDao;
    private final TaxonDao taxonDao;


    public BioportalSpecimenDao(Client esClient, String ndaIndexName, BioportalTaxonDao bioportalTaxonDao, TaxonDao taxonDao, String baseUrl) {
        super(esClient, ndaIndexName, baseUrl);
        this.bioportalTaxonDao = bioportalTaxonDao;
        this.taxonDao = taxonDao;
    }


    /**
     * Retrieves specimens matching a search term. The search term is matched
     * against a predefined set of fields in the Specimen document. These are
     * listed in the Remarks below. Search results must be grouped according to
     * the scientific name of the specimen. This method may entail a geo search
     * (when searching on the location where the specimen was found) N.B. Name
     * resolution is not used in this method
     * <p/>
     * 1. unitID 2. typeStatus 3. phaseOrStage 4. sex 5.
     * gatheringEvent.localityText 6. gatheringEvent.gatheringPersons.fullName
     * 7. gatheringEvent.siteCoordinates.point (= geo search)
     *
     * @param params A {@link QueryParams} object containing: 1. fields ... . A
     *               variable number of filters for fields. For example, the
     *               QueryParams object may contain a key
     *               “defaultClassification.genus” with a value of “Homo” and a key
     *               “defaultClassification.specificEpithet” with a value of
     *               “sapiens”. Fields must be mapped according to the mapping
     *               mechanism described above. Thus, if the QueryParams object
     *               contains a key “genus”, that key must be mapped to the
     *               “defaultClassification.genus” field. 2. _andOr. An enumerated
     *               value with “AND” and “OR” as valid values. “AND” means all
     *               fields must match. “OR” means some fields must match. This is
     *               an optional parameter. By default only some fields must match.
     *               3. _sort. The field to sort on. Fields must be mapped
     *               according to the mapping mechanism described above. Special
     *               sort value: “_score” (sort by relevance). In practice sorting
     *               is only allowed on _score and on
     *               identifications.scientificName.fullScientificName. This is an
     *               optional parameter. By default sorting is done on _score.
     * @return {@link nl.naturalis.nda.search.ResultGroupSet} containing buckets
     * of {@link nl.naturalis.nda.domain.Specimen} with the
     * scientificName as the key
     */
    public SearchResultSet<Specimen> specimenSearch(QueryParams params) {
        //Force OR, cause AND will never be used in simple search
        if (params.containsKey("_search")) {
            params.add("_andOr", "OR");
        }

        return doSpecimenSearch(params, true);
    }


    private SearchResultSet<Specimen> doSpecimenSearch(QueryParams params, boolean highlighting) {
        String sessionId = params.getParam("_session_id");
        params.remove("_session_id");
        evaluateSimpleSearch(params, specimenSearchFieldNames, specimenSearchFieldNames_simpleSearchExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, specimenSearchFieldNames);

        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE, highlighting, sessionId);

        logger.info("*** Total hits = " + searchResponse.getHits().getTotalHits());

        long totalHits = searchResponse.getHits().getTotalHits();
        float minScore = 0;
        if (totalHits > 1) {
            QueryParams copy = params.copy();
            copy.add("_offset", String.valueOf(totalHits - 1));
            minScore = executeExtendedSearch(copy, fieldMappings, SPECIMEN_TYPE, false, sessionId).getHits().getAt(0).getScore();
        }

        return responseToSpecimenSearchResultSet(searchResponse, minScore, sessionId);
    }


    /**
     * Retrieves specimens matching a variable number of criteria. Rather than
     * having one search term and a fixed set of fields to match the search term
     * against, the fields to query and the values to look for are specified as
     * parameters to this method. Nevertheless, the fields will always belong to
     * the list specified in the {@link #specimenNameSearchFieldNames} method.
     * <p/>
     * Name resolution is used to find additional specimens. Specimens must be
     * grouped according to their scientific name.
     *
     * @param params A {@link QueryParams} object containing: 1. fields ... . A
     *               variable number of filters for fields. For example, the
     *               QueryParams object may contain a key
     *               “defaultClassification.genus” with a value of “Homo” and a key
     *               “defaultClassification.specificEpithet” with a value of
     *               “sapiens”. Fields must be mapped according to the mapping
     *               mechanism described above. Thus, if the QueryParams object
     *               contains a key “genus”, that key must be mapped to the
     *               “defaultClassification.genus” field. 2. _andOr. An enumerated
     *               value with “AND” and “OR” as valid values. “AND” means all
     *               fields must match. “OR” means some fields must match. This is
     *               an optional parameter. By default only some fields must match.
     *               3. _sort. The field to sort on. Fields must be mapped
     *               according to the mapping mechanism described above. Special
     *               sort value: “_score” (sort by relevance). In practice sorting
     *               is only allowed on _score and on
     *               identifications.scientificName.fullScientificName. This is an
     *               optional parameter. By default sorting is done on _score.
     * @return
     */
    public ResultGroupSet<Specimen, String> specimenNameSearch(QueryParams params) {
        return doSpecimenNameSearch(params, true);
    }


    private ResultGroupSet<Specimen, String> doSpecimenNameSearch(QueryParams params, boolean highlighting) {
        boolean atLeastOneFieldToQuery = false;
        if (params.containsKey("_search")) {
            params.add("_andOr", "OR");
        }
        String sessionId = params.getParam("_session_id");
        params.remove("_session_id");

        evaluateSimpleSearch(params, specimenNameSearchFieldNames, specimenNameSearchFieldNames_simpleSearchExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> allowedFields = filterAllowedFieldMappings(fields, specimenNameSearchFieldNames);

        QueryAndHighlightFields nameResQuery = buildNameResolutionQuery(allowedFields, params.getParam("_search"), bioportalTaxonDao, highlighting, getOperator(params), sessionId);


        BoolQueryBuilder nonPrebuiltQuery = boolQuery();
        SimpleQueryStringBuilder.Operator operator = getOperator(params);

        LinkedHashMap<String, List<FieldMapping>> nestedFields = new LinkedHashMap<>();
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

        Map<String, HighlightBuilder.Field> highlightFields = nameResQuery == null || nameResQuery.getHighlightFields() == null || nameResQuery.getHighlightFields().isEmpty() ? new HashMap<String, HighlightBuilder.Field>() : nameResQuery.getHighlightFields();

        for (String nestedPath : nestedFields.keySet()) {
            extendQueryWithNestedFieldsWithSameNestedPath(nonPrebuiltQuery, operator, nestedPath, nestedFields.get(nestedPath), highlightFields, highlighting);
            atLeastOneFieldToQuery = true;
        }

        for (FieldMapping field : nonNestedFields) {
            if (!field.getFieldName().contains("dateTime")) {
                extendQueryWithField(nonPrebuiltQuery, operator, field, highlightFields, highlighting);
                atLeastOneFieldToQuery = true;
            }
        }

        atLeastOneFieldToQuery = extractRangeQuery(params, nonPrebuiltQuery, atLeastOneFieldToQuery);

        BoolQueryBuilder completeQuery;

        if (nameResQuery != null && nameResQuery.getQuery() != null) {
            completeQuery = boolQuery();
            extendQueryWithQuery(completeQuery, OR, nonPrebuiltQuery);
            extendQueryWithQuery(completeQuery, OR, nameResQuery.getQuery());
            atLeastOneFieldToQuery = true;
        } else {
            completeQuery = nonPrebuiltQuery;
        }

        NestedFilterBuilder geoShape = null;
        boolean geoSearch = false;
        if (params.containsKey("_geoShape")) {
            geoShape = createGeoShapeFilter(params.getParam("_geoShape"));
            geoSearch = true;
        }


        //BEGIN FIRST QUERY
        SearchRequestBuilder searchRequestBuilder = newSearchRequest().setTypes(SPECIMEN_TYPE).setQuery(filteredQuery(completeQuery, geoShape)).setSearchType(COUNT);
        searchRequestBuilder.setPreference(sessionId);
        searchRequestBuilder.addAggregation(nested("nested").path("identifications")
                .subAggregation(terms("names").field("identifications.scientificName.fullScientificName.raw").size(0).order(aggregation("max_score", false))
                        .subAggregation(max("max_score").script("doc.score"))));

        logger.info(searchRequestBuilder.toString());
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        Map<String, Double> keysAndScores = getKeysAndScoreFromAggregation(searchResponse);


        double minScore = getMinScoreFromAggregation(searchResponse);
        double maxScore = getMaxScoreFromAggregation(searchResponse);
        //END FIRST QUERY

        //BEGIN SECOND QUERY
        if (!keysAndScores.keySet().isEmpty()) {

            NestedFilterBuilder namesFilter = nestedFilter("identifications", createNamesQuery(keysAndScores.keySet()));
            FilteredQueryBuilder newQuery = filteredQuery(completeQuery, namesFilter);

            searchRequestBuilder = newSearchRequest().setTypes(SPECIMEN_TYPE).setQuery(filteredQuery(newQuery, geoShape)).setSearchType(COUNT);
            TopHitsBuilder topHitsBuilder = topHits("top-hits").setSize(10).setFetchSource(true);
            if (!highlightFields.isEmpty()) {
                for (HighlightBuilder.Field highlightField : highlightFields.values()) {
                    topHitsBuilder.addHighlightedField(highlightField);
                }
            }
            searchRequestBuilder
                    .addAggregation(nested("nested").path("identifications")
                            .subAggregation(terms("names").field("identifications.scientificName.fullScientificName.raw").size(10).order(aggregation("max_score", false))
                                    .subAggregation(max("max_score").script("doc.score"))
                                    .subAggregation(reverseNested("reverse")
                                            .subAggregation(topHitsBuilder))));
            searchResponse = searchRequestBuilder.execute().actionGet();
        } else {
            searchResponse = new SearchResponse(InternalSearchResponse.empty(), "", 0, 0, 0, null);
        }
        logger.info(searchRequestBuilder.toString());
        //END SECOND QUERY


        return responseToSpecimenResultGroupSet(searchResponse, minScore, maxScore, sessionId);
    }

    private double getMaxScoreFromAggregation(SearchResponse searchResponse) {
        Nested nested = searchResponse.getAggregations().get("nested");
        Terms terms = nested.getAggregations().get("names");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        if (!buckets.isEmpty()) {
            Terms.Bucket maxBucket = (Terms.Bucket) Iterables.getFirst(buckets, 0);
            if (maxBucket != null) {
                Max max_score = maxBucket.getAggregations().get("max_score");
                return max_score.getValue();

            }
        }
        return 0;
    }

    private double getMinScoreFromAggregation(SearchResponse searchResponse) {
        Nested nested = searchResponse.getAggregations().get("nested");
        Terms terms = nested.getAggregations().get("names");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        if (!buckets.isEmpty()) {
            Terms.Bucket minBucket = (Terms.Bucket) Iterables.getLast(buckets, 0);
            if (minBucket != null) {
                Max max_score = minBucket.getAggregations().get("max_score");
                return max_score.getValue();

            }
        }
        return 0;
    }


    private FilterBuilder createNamesQuery(Set<String> keys) {
        BoolFilterBuilder boolFilterBuilder = boolFilter();
        for (String key : keys) {
            boolFilterBuilder.should(termFilter("identifications.scientificName.fullScientificName.raw", key));
        }
        return boolFilterBuilder;
    }

    private Map<String, Double> getKeysAndScoreFromAggregation(SearchResponse searchResponse) {
        Map<String, Double> temp = new LinkedHashMap<>();
        Nested nested = searchResponse.getAggregations().get("nested");
        Terms terms = nested.getAggregations().get("names");
        Collection<Terms.Bucket> buckets = terms.getBuckets();

        for (Terms.Bucket bucket : buckets) {
            Max max_score = bucket.getAggregations().get("max_score");
            temp.put(bucket.getKey(), max_score.getValue());
        }

        return temp;
    }


    /**
     * Retrieves a single Specimen by its unitID. A specimen retrieved through
     * this method is always retrieved through a REST link in the response from
     * either {@link #specimenNameSearch(QueryParams)} or
     * {@link #specimenSearch(QueryParams)}. This method is aware of the result
     * set generated by those methods and is therefore capable of generating
     * REST links to the previous and next specimen in the result set. All
     * parameters passed to specimenNameSearch or specimenNameSearch will also
     * be passed to this method. Basically, this method has to re-execute the
     * query executed by {@link #specimenNameSearch(QueryParams)} or
     * {@link #specimenSearch(QueryParams)}, pick out the specimen with the
     * specified unitID, and generate REST links to the previous and next
     * specimen in the result set.
     *
     * @param params A {@link QueryParams} object containing: 1. unitID. The
     *               unitID of the specimen. 2. _source. An enumerated value
     *               representing which method was responsible for generating the
     *               result set that contained the currently requested specimen.
     *               Valid values: “SPECIMEN_NAME_SEARCH”, “SPECIMEN_SEARCH” and
     *               “SPECIMEN_EXTENDED_NAME_SEARCH”. This value represents the DAO
     *               method whose query logic to re-execute. 3. fields ... . A
     *               variable number of filters for fields. Will only be set if
     *               _source equals “SPECIMEN_EXTENDED_NAME_SEARCH”. 4. _andOr. An
     *               enumerated value with “AND” and “OR” as valid values. “AND”
     *               means all fields must match. “OR” means some fields must
     *               match. This is an optional parameter. By default only some
     *               fields must match. Will only be set if _source equals
     *               “SPECIMEN_EXTENDED_NAME_SEARCH”. 5. _sort. The field to sort
     *               on. Fields must be mapped according to the mapping mechanism
     *               described above. Special sort value: “_score” (sort by
     *               relevance). In practice sorting is only allowed on _score and
     *               on identifications.scientificName.fullScientificName. This is
     *               an optional parameter. By default sorting is done on _score.
     * @return
     */
    public SearchResultSet<Specimen> getSpecimenDetailWithinSearchResult(QueryParams params) {
        if (!hasText(params.getParam(UNIT_ID))) {
            throw new IllegalArgumentException("unitId required");
        }
        String source = params.getParam("_source");
        if (source.equals("SPECIMEN_NAME_SEARCH")) {
            ResultGroupSet<Specimen, String> specimenResultGroupSet = doSpecimenNameSearch(params, true);
            return createSpecimenDetailSearchResultSet(params, specimenResultGroupSet);
        } else if (source.equals("SPECIMEN_SEARCH")) {
            SearchResultSet<Specimen> specimenSearchResultSet = doSpecimenSearch(params, true);
            return createSpecimenDetailSearchResultSet(params, specimenSearchResultSet);
        }
        throw new RuntimeException(String.format("  Invalid value for query parameter \"_source\": \"%s\"", source));
    }


    // ==================================================== Helpers ====================================================

    protected SearchResultSet<Specimen> createSpecimenDetailSearchResultSet(QueryParams params, SearchResultSet<Specimen> specimenSearchResultSet) {
        SearchResultSet<Specimen> searchResultSet = new SearchResultSet<>();
        String unitID = params.getParam(UNIT_ID);

        SearchResult<Specimen> foundSpecimenForUnitId = null;
        SearchResult<Specimen> previousSpecimen = null;
        SearchResult<Specimen> nextSpecimen = null;

        for (int i = 0; i < specimenSearchResultSet.getSearchResults().size(); ++i) {
            SearchResult<Specimen> sr = specimenSearchResultSet.getSearchResults().get(i);
            if (sr.getResult().getUnitID().equals(unitID)) {
                foundSpecimenForUnitId = sr;
                if (i > 0) {
                    previousSpecimen = specimenSearchResultSet.getSearchResults().get(i - 1);
                }
                if (i < specimenSearchResultSet.getSearchResults().size() - 2) {
                    nextSpecimen = specimenSearchResultSet.getSearchResults().get(i + 1);
                }
                break;
            }
        }

        if (previousSpecimen != null) {
            //TODO NDA-66 specimen link must be to detail base url in result set
            //            foundSpecimenForUnitId.addLink(new Link("_previous", SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET + previousSpecimen.getResult().getUnitID() + queryParamsToUrl(params)));
            foundSpecimenForUnitId.addLink(new Link("_previous", SPECIMEN_DETAIL_BASE_URL + previousSpecimen.getResult().getUnitID()));
        }
        if (nextSpecimen != null) {
            //TODO NDA-66 specimen link must be to detail base url in result set
            //            foundSpecimenForUnitId.addLink(new Link("_next", SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET + nextSpecimen.getResult().getUnitID() + queryParamsToUrl(params)));
            foundSpecimenForUnitId.addLink(new Link("_next", SPECIMEN_DETAIL_BASE_URL + nextSpecimen.getResult().getUnitID()));
        }

        searchResultSet.addSearchResult(foundSpecimenForUnitId);
        //searchResultSet.setQueryParameters(params.copyWithoutGeoShape());
        return searchResultSet;
    }


    protected SearchResultSet<Specimen> createSpecimenDetailSearchResultSet(QueryParams params,
                                                                            ResultGroupSet<Specimen, String> specimenResultGroupSet) {
        SearchResultSet<Specimen> searchResultSet = new SearchResultSet<>();
        String unitID = params.getParam(UNIT_ID);

        SearchResult<Specimen> foundSpecimenForUnitId = null;
        SearchResult<Specimen> previousSpecimen = null;
        SearchResult<Specimen> nextSpecimen = null;

        List<ResultGroup<Specimen, String>> allBuckets = specimenResultGroupSet.getResultGroups();
        for (int currentBucketIndex = 0; currentBucketIndex < allBuckets.size(); currentBucketIndex++) {
            ResultGroup<Specimen, String> bucket = allBuckets.get(currentBucketIndex);
            List<SearchResult<Specimen>> resultsInBucket = bucket.getSearchResults();
            for (int indexInCurrentBucket = 0; indexInCurrentBucket < resultsInBucket.size(); indexInCurrentBucket++) {
                SearchResult<Specimen> searchResult = resultsInBucket.get(indexInCurrentBucket);
                Specimen specimen = searchResult.getResult();
                if (unitID.equals(specimen.getUnitID())) {
                    foundSpecimenForUnitId = searchResult;
                    if (indexInCurrentBucket == 0) {
                        if (currentBucketIndex != 0) {
                            List<SearchResult<Specimen>> previousBucket = allBuckets.get(currentBucketIndex - 1).getSearchResults();
                            previousSpecimen = previousBucket.get(previousBucket.size() - 1);
                        }
                    } else {
                        previousSpecimen = bucket.getSearchResults().get(indexInCurrentBucket - 1);
                    }

                    if (indexInCurrentBucket == resultsInBucket.size() - 1) {
                        if (currentBucketIndex != allBuckets.size() - 1) {
                            List<SearchResult<Specimen>> nextBucket = allBuckets.get(currentBucketIndex + 1).getSearchResults();
                            nextSpecimen = nextBucket.get(0);
                        }
                    } else {
                        nextSpecimen = bucket.getSearchResults().get(indexInCurrentBucket + 1);
                    }
                    break;
                }
            }
            if (foundSpecimenForUnitId != null) {
                break;
            }
        }

        if (previousSpecimen != null) {
            //TODO NDA-66 specimen link must be to detail base url in result set
            //            foundSpecimenForUnitId.addLink(new Link("_previous", SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET + previousSpecimen.getResult().getUnitID() + queryParamsToUrl(params)));
            foundSpecimenForUnitId.addLink(new Link("_previous", SPECIMEN_DETAIL_BASE_URL + previousSpecimen.getResult().getUnitID()));
        }
        if (nextSpecimen != null) {
            //TODO NDA-66 specimen link must be to detail base url in result set
            //            foundSpecimenForUnitId.addLink(new Link("_next", SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET + nextSpecimen.getResult().getUnitID() + queryParamsToUrl(params)));
            foundSpecimenForUnitId.addLink(new Link("_next", SPECIMEN_DETAIL_BASE_URL + nextSpecimen.getResult().getUnitID()));
        }

        searchResultSet.addSearchResult(foundSpecimenForUnitId);
        //searchResultSet.setQueryParameters(params.copyWithoutGeoShape());
        return searchResultSet;
    }


    private SearchResultSet<Specimen> responseToSpecimenSearchResultSet(SearchResponse response, float minScore, String sessionId) {
        float maxScore = response.getHits().getMaxScore();
        SearchResultSet<Specimen> resultSet = new SearchResultSet<>();
        resultSet.setTotalSize(response.getHits().getTotalHits());
        //resultSet.setQueryParameters(params.copyWithoutGeoShape());

        for (SearchHit hit : response.getHits()) {
            ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
            Specimen transfer = transfer(esSpecimen);
            List<Specimen> specimensWithSameAssemblageId = getOtherSpecimensWithSameAssemblageId(transfer, sessionId);
            transfer.setOtherSpecimensInAssemblage(specimensWithSameAssemblageId);
            SearchResult<Specimen> searchResult = new SearchResult<>(transfer);
            resultSet.addSearchResult(searchResult);
            double percentage = ((hit.getScore() - minScore) / (maxScore - minScore)) * 100;
            searchResult.setPercentage(percentage);
            searchResult.addLink(new Link("_specimen", SPECIMEN_DETAIL_BASE_URL + transfer.getUnitID()));

            getTaxonForSpecimenFullScientificName(transfer, searchResult, sessionId);

            enhanceSearchResultWithMatchInfoAndScore(searchResult, hit);
        }
        return resultSet;
    }

    private void getTaxonForSpecimenFullScientificName(Specimen transfer, SearchResult<Specimen> searchResult, String sessionId) {
        List<SpecimenIdentification> identifications = transfer.getIdentifications();
        if (identifications != null) {
            for (SpecimenIdentification identification : identifications) {
                SearchResultSet<Taxon> taxonSearchResultSet = taxonDao.lookupTaxonForScientificName(identification.getScientificName(), sessionId);
                List<SearchResult<Taxon>> searchResults = taxonSearchResultSet.getSearchResults();
                if (searchResults != null) {
                    for (SearchResult<Taxon> taxonSearchResult : searchResults) {
                        Taxon taxon = taxonSearchResult.getResult();
                        searchResult.addLink(new Link("_taxon", TAXON_DETAIL_BASE_URL + createAcceptedNameParams(taxon.getAcceptedName())));
                    }
                }
            }
        }
    }


    private ResultGroupSet<Specimen, String> responseToSpecimenResultGroupSet(SearchResponse response, double minScore, double maxScore, String sessionId) {
        ResultGroupSet<Specimen, String> specimenStringResultGroupSet = new ResultGroupSet<>();

        if (response.getAggregations() != null) {
            Nested nested = response.getAggregations().get("nested");
            Terms terms = nested.getAggregations().get("names");
            Collection<Terms.Bucket> buckets = terms.getBuckets();

            for (Terms.Bucket bucket : buckets) {
                ResultGroup<Specimen, String> resultGroup = new ResultGroup<>();

                String key = bucket.getKey();
                ReverseNested reverse = bucket.getAggregations().get("reverse");
                TopHits topHits = reverse.getAggregations().get("top-hits");
                SearchHits hits = topHits.getHits();

                for (SearchHit hit : hits) {
                    Specimen specimen = transfer(getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class));
                    List<Specimen> specimensWithSameAssemblageId = getOtherSpecimensWithSameAssemblageId(specimen, sessionId);
                    specimen.setOtherSpecimensInAssemblage(specimensWithSameAssemblageId);

                    SearchResult<Specimen> searchResult = new SearchResult<>();
                    searchResult.setResult(specimen);
                    searchResult.addLink(new Link("_specimen", SPECIMEN_DETAIL_BASE_URL + specimen.getUnitID()));
                    enhanceSearchResultWithMatchInfoAndScore(searchResult, hit);
                    getTaxonForSpecimenFullScientificName(specimen, searchResult, sessionId);
                    resultGroup.addSearchResult(searchResult);
                    //todo percentage
                    //double percentage = ((hit.getScore() - minScore) / (maxScore - minScore)) * 100;
                    //if (Double.isNaN(percentage)) {
                    //percentage = 100;
                    //}
                }
                resultGroup.setSharedValue(key);
                specimenStringResultGroupSet.addGroup(resultGroup);
            }
        }
        specimenStringResultGroupSet.setTotalSize(response.getHits().getTotalHits());
        return specimenStringResultGroupSet;
    }

    protected List<Specimen> getOtherSpecimensWithSameAssemblageId(Specimen transfer, String sessionId) {
        List<Specimen> specimensWithSameAssemblageId = new ArrayList<>();
        String assemblageID = transfer.getAssemblageID();
        if (assemblageID != null) {
            SearchResponse searchResponse = newSearchRequest()
                    .setPreference(sessionId)
                    .setTypes(SPECIMEN_TYPE)
                    .setQuery(
                            filteredQuery(matchAllQuery(),
                                    boolFilter().must(termFilter(ASSEMBLAGE_ID, assemblageID)).mustNot(termFilter(UNIT_ID + ".raw", transfer.getUnitID()))))
                    .execute().actionGet();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit searchHitFields : hits) {
                ESSpecimen esSpecimenWithSameAssemblageId = getObjectMapper().convertValue(searchHitFields.getSource(), ESSpecimen.class);
                specimensWithSameAssemblageId.add(transfer(esSpecimenWithSameAssemblageId));
            }
        }
        return specimensWithSameAssemblageId;
    }
}
