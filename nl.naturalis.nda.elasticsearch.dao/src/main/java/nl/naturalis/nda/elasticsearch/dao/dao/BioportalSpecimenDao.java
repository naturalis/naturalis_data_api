package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.*;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.*;
import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class BioportalSpecimenDao extends AbstractDao {

    public static class SpecimenFields {
        public static final String IDENTIFICATIONS_VERNACULAR_NAMES_NAME = "identifications.vernacularNames.name";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM = "identifications.defaultClassification.kingdom";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM = "identifications.defaultClassification.phylum";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME = "identifications.defaultClassification.className";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER = "identifications.defaultClassification.order";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY = "identifications.defaultClassification.family";
    }

    private static final Set<String> specimenNameSearchFieldNames = new HashSet<>(Arrays.asList(
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY,
            "identifications.defaultClassification.genus",
            "identifications.defaultClassification.subgenus",
            "identifications.defaultClassification.specificEpithet",
            "identifications.defaultClassification.infraspecificEpithet",
            "identifications.systemClassification.name",
            IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL,
            "identifications.scientificName.subgenus",
            IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET,
            IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET,
            IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
            "gatheringEvent.dateTimeBegin",
            "gatheringEvent.siteCoordinates.point"
    ));

    private static final Set<String> specimenSearchFieldNames = new HashSet<>(Arrays.asList(
            "unitID",
            "typeStatus",
            "phaseOrStage",
            "sex",
            "collectorsFieldNumber",
            "gatheringEvent.localityText",
            "gatheringEvent.gatheringPersons.fullName",
            "gatheringEvent.gatheringOrganisations.name",
            "gatheringEvent.dateTimeBegin",
            "gatheringEvent.siteCoordinates.point")
    );

    private final BioportalTaxonDao bioportalTaxonDao;
    private final TaxonDao taxonDao;

    public BioportalSpecimenDao(Client esClient, String ndaIndexName, BioportalTaxonDao bioportalTaxonDao, TaxonDao taxonDao) {
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
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, specimenSearchFieldNames);

        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE, false);

        return responseToSpecimenResultGroupSet(searchResponse);
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
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> allowedFields = filterAllowedFieldMappings(fields, specimenNameSearchFieldNames);

        QueryBuilder nameResQuery = buildNameResolutionQuery(allowedFields, params.getParam("_search"), bioportalTaxonDao);
        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, SPECIMEN_TYPE, true, nameResQuery,
                Arrays.asList(IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL,
                        IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET,
                        IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET));

        return responseToSpecimenResultGroupSet(searchResponse);
        // TODO: mark results from name resolution
    }

    /**
     * Retrieves a single Specimen by its unitID. A specimen retrieved through this method is always retrieved through
     * a REST link in the response from either {@link #specimenNameSearch(QueryParams)} or
     * {@link #specimenSearch(QueryParams)}. This method is aware of the result set generated by those methods and
     * is therefore capable of generating REST links to the previous and next
     * specimen in the result set. All parameters passed to specimenNameSearch or specimenNameSearch will also be passed to
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
        String source = params.getParam("_source");
        ResultGroupSet<Specimen, String> specimenResultGroupSet = new ResultGroupSet<>();
        if (source.equals("SPECIMEN_NAME_SEARCH")) {
            specimenResultGroupSet = specimenNameSearch(params);
        } else if (source.equals("SPECIMEN_SEARCH")) {
            specimenResultGroupSet = specimenSearch(params);
        }

        return createSpecimenDetailSearchResultSet(params, specimenResultGroupSet);
    }

    // ==================================================== Helpers ====================================================

    protected SearchResultSet<Specimen> createSpecimenDetailSearchResultSet(QueryParams params,
                                                                            ResultGroupSet<Specimen, String> specimenResultGroupSet) {
        SearchResultSet<Specimen> searchResultSet = new SearchResultSet<>();
        List<Link> links = new ArrayList<>();

        String unitID = params.getParam("unitID");

        Specimen foundSpecimenForUnitId = null;
        SearchResult<Specimen> previousSpecimen = null;
        SearchResult<Specimen> nextSpecimen = null;

        List<ResultGroup<Specimen, String>> allBuckets = specimenResultGroupSet.getResultGroups();
        for (int currentBucketIndex = 0; currentBucketIndex < allBuckets.size(); currentBucketIndex++) {
            ResultGroup<Specimen, String> bucket = allBuckets.get(currentBucketIndex);
            List<SearchResult<Specimen>> resultsInBucket = bucket.getSearchResults();
            for (int indexInCurrentBucket = 0; indexInCurrentBucket < resultsInBucket.size(); indexInCurrentBucket++) {
                SearchResult searchResult = resultsInBucket.get(indexInCurrentBucket);
                Specimen specimen = (Specimen) searchResult.getResult();
                if (unitID.equals(specimen.getUnitID())) {
                    foundSpecimenForUnitId = specimen;
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

        //TODO Change links to correct url and href
        if (previousSpecimen != null) {
            links.add(new Link("http://test.nl?unitId=" + previousSpecimen.getResult().getUnitID(), "_previous"));
        }
        if (nextSpecimen != null) {
            links.add(new Link("http://test.nl?unitId=" + nextSpecimen.getResult().getUnitID(), "_next"));
        }

        searchResultSet.addSearchResult(foundSpecimenForUnitId);
        searchResultSet.setLinks(links);
        return searchResultSet;
    }

    private ResultGroupSet<Specimen, String> responseToSpecimenResultGroupSet(SearchResponse response) {
        // TODO links
        // TODO searchTerms
        ResultGroupSet<Specimen, String> specimenStringResultGroupSet = new ResultGroupSet<>();
        HashMap<String, List<Specimen>> tempMap = new HashMap<>();

        for (SearchHit hit : response.getHits()) {
            ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
            Specimen transfer = SpecimenTransfer.transfer(esSpecimen);

            List<Specimen> specimensWithSameAssemblageId = getOtherSpecimensWithSameAssemblageId(transfer);
            transfer.setOtherSpecimensInAssemblage(specimensWithSameAssemblageId);

            List<SpecimenIdentification> identifications = transfer.getIdentifications();
            if (identifications != null) {
                for (SpecimenIdentification specimenIdentification : identifications) {
                    String scientificName = specimenIdentification.getScientificName().getFullScientificName();

                    List<Specimen> specimens;
                    if (tempMap.containsKey(scientificName)) {
                        specimens = tempMap.get(scientificName);
                    } else {
                        specimens = new ArrayList<>();
                    }
                    specimens.add(transfer);
                    tempMap.put(scientificName, specimens);
                }
            }
        }

        for (Map.Entry<String, List<Specimen>> stringListEntry : tempMap.entrySet()) {
            ResultGroup<Specimen, String> resultGroup = new ResultGroup<>();
            String scientificName = stringListEntry.getKey();
            resultGroup.setSharedValue(scientificName);
            List<Specimen> specimens = stringListEntry.getValue();

            for (Specimen specimen : specimens) {
                resultGroup.addSearchResult(specimen);
                List<SpecimenIdentification> identifications = specimen.getIdentifications();
                if (identifications != null) {
                    for (SpecimenIdentification identification : identifications) {
                        String genusOrMonomial = identification.getScientificName().getGenusOrMonomial();
                        String specificEpithet = identification.getScientificName().getSpecificEpithet();
                        String infraspecificEpithet = identification.getScientificName().getInfraspecificEpithet();
                        SearchResultSet<Taxon> taxonSearchResultSet = lookupTaxonForScientificName(genusOrMonomial, specificEpithet, infraspecificEpithet);
                        List<SearchResult<Taxon>> searchResults = taxonSearchResultSet.getSearchResults();
                        if (searchResults != null) {
                            for (SearchResult<Taxon> taxonSearchResult : searchResults) {
                                Taxon taxon = taxonSearchResult.getResult();
                                // TODO fix correct link
                                resultGroup.addLink(new Link("http://test.nl?fullScientificName=" + taxon.getAcceptedName().getFullScientificName(), "taxon"));
                            }
                        }
                    }
                }
            }
            specimenStringResultGroupSet.addGroup(resultGroup);
        }

        specimenStringResultGroupSet.setTotalSize(response.getHits().getTotalHits());
        return specimenStringResultGroupSet;
    }

    private SearchResultSet<Taxon> lookupTaxonForScientificName(String genusOrMonomial, String specificEpithet, String infraspecificEpithet) {
        QueryParams queryParams = new QueryParams();
        queryParams.add(IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL, genusOrMonomial);
        queryParams.add(IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET, specificEpithet);
        queryParams.add(IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET, infraspecificEpithet);
        return taxonDao.getTaxonDetail(queryParams);
    }

    protected List<Specimen> getOtherSpecimensWithSameAssemblageId(Specimen transfer) {
        List<Specimen> specimensWithSameAssemblageId = new ArrayList<>();
        SearchResponse searchResponse = newSearchRequest().setTypes("Specimen")
                .setQuery(filteredQuery(matchAllQuery(), boolFilter()
                        .must(termFilter("assemblageID", transfer.getAssemblageID()))
                        .mustNot(termFilter("unitID", transfer.getUnitID()))))
                .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit searchHitFields : hits) {
            ESSpecimen esSpecimenWithSameAssemblageId = getObjectMapper().convertValue(searchHitFields.getSource(), ESSpecimen.class);
            specimensWithSameAssemblageId.add(SpecimenTransfer.transfer(esSpecimenWithSameAssemblageId));
        }
        return specimensWithSameAssemblageId;
    }

}
