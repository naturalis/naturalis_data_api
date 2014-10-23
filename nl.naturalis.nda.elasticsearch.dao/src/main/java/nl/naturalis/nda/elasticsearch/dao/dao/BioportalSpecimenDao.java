package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroup;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.search.StringMatchInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.*;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.ASSEMBLAGE_ID;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.COLLECTORS_FIELD_NUMBER;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_DATE_TIME_BEGIN;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_GATHERING_ORGANISATIONS_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_GATHERING_PERSONS_FULLNAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_LOCALITY_TEXT;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.GATHERINGEVENT_SITECOORDINATES_POINT;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.IDENTIFICATIONS_SYSTEM_CLASSIFICATION_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.PHASE_OR_STAGE;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.SEX;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SpecimenFields.TYPE_STATUS;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.SPECIMEN_TYPE;
import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class BioportalSpecimenDao extends AbstractDao {

    // TODO: mark results in Taxon
    // TODO: mark results in BioportalTaxon
    // TODO: mark results in BioportalMultiMediaObjectDao
    // TODO: mark results in SpecimenDao
    // TODO: write test for BioportalSpecimenDao and some others

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
            IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL,
            IDENTIFICATIONS_SCIENTIFIC_NAME_SUBGENUS,
            IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET,
            IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET,
            IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
            GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT
    ));

    private static final Set<String> specimenNameSearchFieldNames_simpleSearchExceptions = new HashSet<>(Arrays.asList(
            GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT
    ));


    private static final Set<String> specimenSearchFieldNames = new HashSet<>(Arrays.asList(
            UNIT_ID,
            TYPE_STATUS,
            PHASE_OR_STAGE,
            SEX,
            COLLECTORS_FIELD_NUMBER,
            GATHERINGEVENT_LOCALITY_TEXT,
            GATHERINGEVENT_GATHERING_PERSONS_FULLNAME,
            GATHERINGEVENT_GATHERING_ORGANISATIONS_NAME,
            GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT)
    );

    private static final Set<String> specimenSearchFieldNames_simpleSearchExceptions = new HashSet<>(Arrays.asList(
            GATHERINGEVENT_DATE_TIME_BEGIN,
            GATHERINGEVENT_SITECOORDINATES_POINT)
    );

    private final BioportalTaxonDao bioportalTaxonDao;
    private final TaxonDao taxonDao;

    public BioportalSpecimenDao(Client esClient, String ndaIndexName, BioportalTaxonDao bioportalTaxonDao,
                                TaxonDao taxonDao) {
        super(esClient, ndaIndexName);
        this.bioportalTaxonDao = bioportalTaxonDao;
        this.taxonDao = taxonDao;
    }

    /**
     * Retrieves specimens matching a search term. The search term is matched against a predefined set of fields in the
     * Specimen document. These are listed in the Remarks below. Search results must be grouped according to the
     * scientific name of the specimen. This method may entail a geo search (when searching on the location where the
     * specimen was found)
     * N.B. Name resolution is not used in this method
     * <p/>
     * 1. unitID
     * 2. typeStatus
     * 3. phaseOrStage
     * 4. sex
     * 5. gatheringEvent.localityText
     * 6. gatheringEvent.gatheringPersons.fullName
     * 7. gatheringEvent.siteCoordinates.point (= geo search)
     *
     * @param params A {@link QueryParams} object containing:
     *               1. fields ... . A variable number of filters for fields. For example, the
     *               QueryParams object may contain a key “defaultClassification.genus” with a value of “Homo” and a
     *               key “defaultClassification.specificEpithet” with a value of “sapiens”. Fields must be mapped
     *               according to the mapping mechanism described above. Thus, if the QueryParams object contains a key
     *               “genus”, that key must be mapped to the “defaultClassification.genus” field.
     *               2. _andOr. An enumerated value with “AND” and “OR” as valid values. “AND” means all fields must
     *               match. “OR” means some fields must match. This is an optional parameter. By default only some
     *               fields must match.
     *               3. _sort. The field to sort on. Fields must be mapped according to the mapping mechanism described
     *               above. Special sort value: “_score” (sort by relevance). In practice sorting is only allowed on
     *               _score and on identifications.scientificName.fullScientificName. This is an optional parameter. By
     *               default sorting is done on _score.
     * @return {@link nl.naturalis.nda.search.ResultGroupSet} containing buckets of {@link
     * nl.naturalis.nda.domain.Specimen} with the scientificName as the key
     */
    public ResultGroupSet<Specimen, String> specimenSearch(QueryParams params) {
        return doSpecimenSearch(params, true);
    }

    private ResultGroupSet<Specimen, String> doSpecimenSearch(QueryParams params, boolean highlighting) {
        evaluateSimpleSearch(params, specimenSearchFieldNames, specimenSearchFieldNames_simpleSearchExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, specimenSearchFieldNames);

        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE, highlighting);

        return responseToSpecimenResultGroupSet(searchResponse, params);
    }

    /**
     * Retrieves specimens matching a variable number of criteria. Rather than having one search term and a fixed set
     * of fields to match the search term against, the fields to query and the values to look for are specified as
     * parameters to this method. Nevertheless, the fields will always belong to the list specified in the
     * {@link #specimenNameSearchFieldNames} method.
     * <p/>
     * Name resolution is used to find additional specimens. Specimens must be grouped according to their scientific
     * name.
     *
     * @param params A {@link QueryParams} object containing:
     *               1. fields ... . A variable number of filters for fields. For example, the
     *               QueryParams object may contain a key “defaultClassification.genus” with a value of “Homo” and a
     *               key “defaultClassification.specificEpithet” with a value of “sapiens”. Fields must be mapped
     *               according to the mapping mechanism described above. Thus, if the QueryParams object contains a key
     *               “genus”, that key must be mapped to the “defaultClassification.genus” field.
     *               2. _andOr. An enumerated value with “AND” and “OR” as valid values. “AND” means all fields must
     *               match. “OR” means some fields must match. This is an optional parameter. By default only some
     *               fields must match.
     *               3. _sort. The field to sort on. Fields must be mapped according to the mapping mechanism described
     *               above. Special sort value: “_score” (sort by relevance). In practice sorting is only allowed on
     *               _score and on identifications.scientificName.fullScientificName. This is an optional parameter. By
     *               default sorting is done on _score.
     * @return
     */
    public ResultGroupSet<Specimen, String> specimenNameSearch(QueryParams params) {
        return doSpecimenNameSearch(params, true);
    }

    private ResultGroupSet<Specimen, String> doSpecimenNameSearch(QueryParams params, boolean highlighting) {
        evaluateSimpleSearch(params, specimenNameSearchFieldNames, specimenNameSearchFieldNames_simpleSearchExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> allowedFields = filterAllowedFieldMappings(fields, specimenNameSearchFieldNames);

        QueryAndHighlightFields nameResQuery = buildNameResolutionQuery(allowedFields, params.getParam("_search"),
                                                                        bioportalTaxonDao, highlighting);
        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, SPECIMEN_TYPE, highlighting, nameResQuery);

        return responseToSpecimenResultGroupSet(searchResponse, params);
    }

    /**
     * Retrieves a single Specimen by its unitID. A specimen retrieved through this method is always retrieved through
     * a REST link in the response from either {@link #specimenNameSearch(QueryParams)} or
     * {@link #specimenSearch(QueryParams)}. This method is aware of the result set generated by those methods and
     * is therefore capable of generating REST links to the previous and next
     * specimen in the result set. All parameters passed to specimenNameSearch or specimenNameSearch will also be
     * passed
     * to
     * this method. Basically, this method has to re-execute the query executed by
     * {@link #specimenNameSearch(QueryParams)} or {@link #specimenSearch(QueryParams)},
     * pick out the specimen with the specified unitID, and generate REST links to the previous and next specimen in
     * the
     * result set.
     *
     * @param params A {@link QueryParams} object containing:
     *               1. unitID. The unitID of the specimen.
     *               2. _source. An enumerated value representing which method was
     *               responsible for generating the result set that contained the currently requested specimen. Valid
     *               values: “SPECIMEN_NAME_SEARCH”, “SPECIMEN_SEARCH” and “SPECIMEN_EXTENDED_NAME_SEARCH”. This value
     *               represents the DAO method whose query logic to re-execute.
     *               3. fields ... . A variable number of filters for fields. Will only be set if _source equals
     *               “SPECIMEN_EXTENDED_NAME_SEARCH”.
     *               4. _andOr. An enumerated value with “AND” and “OR” as valid values. “AND” means all fields must
     *               match. “OR” means some fields must match. This is an optional parameter. By default only some
     *               fields must match. Will only be set if _source equals “SPECIMEN_EXTENDED_NAME_SEARCH”.
     *               5. _sort. The field to sort on. Fields must be mapped according to the mapping mechanism described
     *               above. Special sort value: “_score” (sort by relevance). In practice sorting is only allowed on
     *               _score and on identifications.scientificName.fullScientificName. This is an optional parameter. By
     *               default sorting is done on _score.
     * @return
     */
    public SearchResultSet<Specimen> getSpecimenDetailWithinSearchResult(QueryParams params) {
        if (!hasText(params.getParam(UNIT_ID))) {
            throw new IllegalArgumentException("unitId required");
        }
        String source = params.getParam("_source");
        ResultGroupSet<Specimen, String> specimenResultGroupSet = new ResultGroupSet<>();
        if (source.equals("SPECIMEN_NAME_SEARCH")) {
            specimenResultGroupSet = doSpecimenNameSearch(params, false);
        } else if (source.equals("SPECIMEN_SEARCH")) {
            specimenResultGroupSet = doSpecimenSearch(params, false);
        }

        return createSpecimenDetailSearchResultSet(params, specimenResultGroupSet);
    }

    // ==================================================== Helpers ====================================================

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
                            List<SearchResult<Specimen>> previousBucket = allBuckets.get(currentBucketIndex - 1)
                                                                                    .getSearchResults();
                            previousSpecimen = previousBucket.get(previousBucket.size() - 1);
                        }
                    } else {
                        previousSpecimen = bucket.getSearchResults().get(indexInCurrentBucket - 1);
                    }

                    if (indexInCurrentBucket == resultsInBucket.size() - 1) {
                        if (currentBucketIndex != allBuckets.size() - 1) {
                            List<SearchResult<Specimen>> nextBucket = allBuckets.get(currentBucketIndex + 1)
                                                                                .getSearchResults();
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
            foundSpecimenForUnitId.addLink(new Link(SPECIMEN_DETAIL_BASE_URL + previousSpecimen.getResult().getUnitID(), "_previous"));
        }
        if (nextSpecimen != null) {
            foundSpecimenForUnitId.addLink(new Link(SPECIMEN_DETAIL_BASE_URL + nextSpecimen.getResult().getUnitID(), "_next"));
        }

        searchResultSet.addSearchResult(foundSpecimenForUnitId);
        searchResultSet.setQueryParameters(params.copyWithoutGeoShape());
        return searchResultSet;
    }

    private ResultGroupSet<Specimen, String> responseToSpecimenResultGroupSet(SearchResponse response,
                                                                              QueryParams params) {
        ResultGroupSet<Specimen, String> specimenStringResultGroupSet = new ResultGroupSet<>();
        Map<String, List<Specimen>> tempMapSpecimens = new HashMap<>();
        Map<Specimen, SearchHit> tempMapSearchHits = new HashMap<>();

        for (SearchHit hit : response.getHits()) {
            ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
            Specimen transfer = SpecimenTransfer.transfer(esSpecimen);
            tempMapSearchHits.put(transfer, hit);

            List<Specimen> specimensWithSameAssemblageId = getOtherSpecimensWithSameAssemblageId(transfer);
            transfer.setOtherSpecimensInAssemblage(specimensWithSameAssemblageId);

            List<SpecimenIdentification> identifications = transfer.getIdentifications();
            if (identifications != null) {
                for (SpecimenIdentification specimenIdentification : identifications) {
                    String scientificName = specimenIdentification.getScientificName().getFullScientificName();

                    List<Specimen> specimens;
                    if (tempMapSpecimens.containsKey(scientificName)) {
                        specimens = tempMapSpecimens.get(scientificName);
                    } else {
                        specimens = new ArrayList<>();
                    }
                    specimens.add(transfer);
                    tempMapSpecimens.put(scientificName, specimens);
                }
            }
        }

        for (Map.Entry<String, List<Specimen>> stringListEntry : tempMapSpecimens.entrySet()) {
            ResultGroup<Specimen, String> resultGroup = new ResultGroup<>();
            String scientificName = stringListEntry.getKey();
            resultGroup.setSharedValue(scientificName);
            List<Specimen> specimens = stringListEntry.getValue();

            for (Specimen specimen : specimens) {
                SearchResult<Specimen> searchResult = new SearchResult<>();
                searchResult.setResult(specimen);
                searchResult.addLink(new Link(SPECIMEN_DETAIL_BASE_URL + specimen.getUnitID(), "_specimen"));

                SearchHit hit = tempMapSearchHits.get(specimen);
                if (hit.getHighlightFields() != null) {
                    List<StringMatchInfo> stringMatchInfos = new ArrayList<>();
                    for (Map.Entry<String, HighlightField> highlightFieldEntry : hit.getHighlightFields().entrySet()) {
                        StringMatchInfo stringMatchInfo = new StringMatchInfo();
                        stringMatchInfo.setPath(highlightFieldEntry.getKey());
                        stringMatchInfo.setValueHighlighted(" " + Arrays.asList(highlightFieldEntry.getValue().fragments()));
                        // TODO: setValue
                        stringMatchInfos.add(stringMatchInfo);
                    }
                    searchResult.setMatchInfo(stringMatchInfos);
                }

                List<SpecimenIdentification> identifications = specimen.getIdentifications();
                if (identifications != null) {
                    for (SpecimenIdentification identification : identifications) {
                        SearchResultSet<Taxon> taxonSearchResultSet = taxonDao.lookupTaxonForScientificName(identification.getScientificName());
                        // FIXME: should not result in highlighting
                        List<SearchResult<Taxon>> searchResults = taxonSearchResultSet.getSearchResults();
                        if (searchResults != null) {
                            for (SearchResult<Taxon> taxonSearchResult : searchResults) {
                                Taxon taxon = taxonSearchResult.getResult();
                                searchResult.addLink(new Link(TAXON_DETAIL_BASE_URL + taxon.getAcceptedName().getFullScientificName(), "_taxon"));
                            }
                        }
                    }
                }
                resultGroup.addSearchResult(searchResult);
            }
            specimenStringResultGroupSet.addGroup(resultGroup);
        }

        specimenStringResultGroupSet.setTotalSize(response.getHits().getTotalHits());
        specimenStringResultGroupSet.setQueryParameters(params.copyWithoutGeoShape());
        return specimenStringResultGroupSet;
    }


    protected List<Specimen> getOtherSpecimensWithSameAssemblageId(Specimen transfer) {
        List<Specimen> specimensWithSameAssemblageId = new ArrayList<>();
        SearchResponse searchResponse = newSearchRequest().setTypes(SPECIMEN_TYPE)
                                                          .setQuery(filteredQuery(matchAllQuery(), boolFilter()
                                                                  .must(termFilter(ASSEMBLAGE_ID,
                                                                          transfer.getAssemblageID()))
                                                                  .mustNot(termFilter(UNIT_ID, transfer.getUnitID()))))
                                                          .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit searchHitFields : hits) {
            ESSpecimen esSpecimenWithSameAssemblageId = getObjectMapper().convertValue(searchHitFields.getSource(),
                                                                                       ESSpecimen.class);
            specimensWithSameAssemblageId.add(SpecimenTransfer.transfer(esSpecimenWithSameAssemblageId));
        }
        return specimensWithSameAssemblageId;
    }
}
