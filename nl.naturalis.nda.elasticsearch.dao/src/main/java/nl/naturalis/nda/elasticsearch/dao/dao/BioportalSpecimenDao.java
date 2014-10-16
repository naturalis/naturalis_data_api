package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.ResultGroup;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_VERNACULAR_NAMES_NAME;
import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class BioportalSpecimenDao extends AbstractDao {

    private static final String SIMPLE_SEARCH_PARAM_KEY = "_search";

    public static class SpecimenFields {
        public static final String IDENTIFICATIONS_VERNACULAR_NAMES_NAME = "identifications.vernacularNames.name";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM = "identifications.defaultClassification.kingdom";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM = "identifications.defaultClassification.phylum";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME = "identifications.defaultClassification.className";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER = "identifications.defaultClassification.order";
        public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY = "identifications.defaultClassification.family";
    }

    private static final String[] specimenNameSearchFieldNames = {
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
            "identifications.scientificName.genusOrMonomial",
            "identifications.scientificName.subgenus",
            "identifications.scientificName.specificEpithet",
            "identifications.scientificName.infraspecificEpithet",
            IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
            "gatheringEvent.dateTimeBegin",
            "gatheringEvent.siteCoordinates.point"
    };

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

    private final BioportalTaxonDao taxonDao;

    public BioportalSpecimenDao(Client esClient, String ndaIndexName, BioportalTaxonDao taxonDao) {
        super(esClient, ndaIndexName);
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
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, new ArrayList<>(specimenSearchFieldNames));

        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE);

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
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, Arrays.asList(specimenNameSearchFieldNames));

        QueryBuilder nameResQuery = buildNameResolutionQuery(fieldMappings);
        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE, nameResQuery);

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

    /**
     * @param fields parameters for the query
     * @return null in case of no valid param_keys or no taxons matching the supplied values
     */
    private NestedQueryBuilder buildNameResolutionQuery(List<FieldMapping> fields) {
        if (!hasFieldWithTextWithOneOfNames(fields, SIMPLE_SEARCH_PARAM_KEY, IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM, IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME, IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER,
                IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY)) {
            return null;
        }

        // nameRes = name resolution
        QueryParams nameResTaxonQueryParams = new QueryParams();
        for (FieldMapping field : fields) {
            switch (field.getFieldName()) {
                case SIMPLE_SEARCH_PARAM_KEY:
                    String searchTerm = field.getValue();
                    nameResTaxonQueryParams.add("vernacularNames.name", searchTerm);
                    nameResTaxonQueryParams.add("synonyms.genusOrMonomial", searchTerm);
                    nameResTaxonQueryParams.add("synonyms.specificEpithet", searchTerm);
                    nameResTaxonQueryParams.add("synonyms.infraspecificEpithet", searchTerm);
                    break;
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
        SearchResultSet<Taxon> nameResTaxons = taxonDao.taxonExtendedSearch(nameResTaxonQueryParams);
        if (nameResTaxons.getTotalSize() == 0) {
            return null;
        }

        BoolQueryBuilder nameResQueryBuilder = boolQuery();
        for (SearchResult<Taxon> taxonSearchResult : nameResTaxons.getSearchResults()) {
            Taxon taxon = taxonSearchResult.getResult();
            BoolQueryBuilder scientificNameQuery = boolQuery();
            if (taxon.getValidName().getGenusOrMonomial() != null) {
                scientificNameQuery.must(
                        termQuery("identifications.scientificName.genusOrMonomial",
                                taxon.getValidName().getGenusOrMonomial())
                );
            }
            if (taxon.getValidName().getSpecificEpithet() != null) {
                scientificNameQuery.must(
                        termQuery("identifications.scientificName.specificEpithet",
                                taxon.getValidName().getSpecificEpithet())
                );
            }
            if (taxon.getValidName().getInfraspecificEpithet() != null) {
                scientificNameQuery.must(
                        termQuery("identifications.scientificName.infraspecificEpithet",
                                taxon.getValidName().getInfraspecificEpithet())
                );
            }
            nameResQueryBuilder.should(scientificNameQuery);
        }
        NestedQueryBuilder nestedNameResQuery = nestedQuery("identifications", nameResQueryBuilder);
        nestedNameResQuery.boost(0.5f);
        return nestedNameResQuery;
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

            //todo lookup in Taxon and add link

            List<Specimen> value = stringListEntry.getValue();
            for (Specimen specimen : value) {
                resultGroup.addSearchResult(specimen);
            }
            specimenStringResultGroupSet.addGroup(resultGroup);
        }

        specimenStringResultGroupSet.setTotalSize(response.getHits().getTotalHits());
        return specimenStringResultGroupSet;
    }

    protected List<Specimen> getOtherSpecimensWithSameAssemblageId(Specimen transfer) {
        List<Specimen> specimensWithSameAssemblageId = new ArrayList<>();
        SearchResponse searchResponse = newSearchRequest()
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

    private static boolean hasText(String string) {
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

}
