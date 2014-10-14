package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.ResultGroup;
import nl.naturalis.nda.search.ResultGroupSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class BioportalSpecimenDao extends AbstractDao {

    private static final Logger logger = LoggerFactory.getLogger(BioportalSpecimenDao.class);

    private static final String[] specimenNameSearchFieldNames = {
            "identifications.defaultClassification.kingdom",
            "identifications.defaultClassification.phylum",
            "identifications.defaultClassification.className",
            "identifications.defaultClassification.order",
            "identifications.defaultClassification.family",
            "identifications.systemClassification.name",
            "identifications.scientificName.genusOrMonomial",
            "identifications.scientificName.specificEpithet",
            "identifications.scientificName.infraspecificEpithet",
            "gatheringEvent.gatheringPersons.fullName",
            "gatheringEvent.dateTimeBegin",
            "gatheringEvent.siteCoordinates.point"
    };

    private static final Set<String> specimenSearchFieldNames = new HashSet<>(Arrays.asList(
            "unitID",
            "typeStatus",
            "phaseOrStage",
            "sex",
            "gatheringEvent.localityText",
            "gatheringEvent.gatheringPersons.fullName",
            "gatheringEvent.siteCoordinates.point")
    );

    public BioportalSpecimenDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    public static void main(String[] args) throws JsonProcessingException {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put(CLUSTER_NAME_PROPERTY, CLUSTER_NAME_PROPERTY_VALUE)
                .build();
        Client esClient = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(ES_HOST, ES_PORT));

        BioportalSpecimenDao dao = new BioportalSpecimenDao(esClient, INDEX_NAME);

        logger.info("\n");
        String term = "Meijer, W.";
        String orderField = "gatheringEvent.dateTimeBegin";
        logger.info("------ Firing 'specimenNameSearch' query ------");
        logger.info("Searching specimens with term: '" + term + "' and ordering by field: '" + orderField + "'");
        ResultGroupSet<Specimen, String> specimenSearchResultSet = dao.specimenSearch(term, orderField);
        logger.info(getObjectMapper().writeValueAsString(specimenSearchResultSet));

        logger.info("\n");
        logger.info("------ Firing 'specimenNameSearch' query ------");
        QueryParams params = new QueryParams();
        params.add("unitID", "0191413");
//        params.add("gatheringEvent.gatheringPersons.fullName", "Meijer, W.");
        params.add("gatheringAgent", "Meijer, W.");
        params.add("_andOr", "OR");
        logger.info("Searching specimens with params: '" + params.toString() + "'");

        ResultGroupSet<Specimen, String> specimenExtendedNameSearchResultSet = dao.specimenNameSearch(params);
        logger.info("Found: " + specimenExtendedNameSearchResultSet.getTotalSize());
        logger.info(getObjectMapper().writeValueAsString(specimenExtendedNameSearchResultSet));
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
     * @param searchTerm The search term to match
     * @param sortField  The field to sort on. Fields must be mapped according to the mapping
     *                   mechanism described above. Special sort value: “_score” (sort by relevance). In practice
     *                   sorting is only allowed on _score and on identifications.scientificName.fullScientificName.
     *                   This is an optional parameter. By default sorting is done on _score.
     * @return {@link nl.naturalis.nda.search.ResultGroupSet} containing buckets of {@link
     * nl.naturalis.nda.domain.Specimen} with the scientificName as the key
     */
    public ResultGroupSet<Specimen, String> specimenSearch(String searchTerm, String sortField) {
        //todo geo search on field:
        if (sortField == null || sortField.trim().equalsIgnoreCase("")) {
            sortField = "_score";
        }
        FieldSortBuilder fieldSort = fieldSort(sortField);
        SearchResponse response = newSearchRequest()
                .setTypes(SPECIMEN_TYPE)
                .setQuery(
                        multiMatchQuery(
                                searchTerm,
                                specimenSearchFieldNames.toArray(new String[specimenSearchFieldNames.size()])
                        )
                )
                .addSort(fieldSort)
                .execute().actionGet();

        return responseToSpecimenResultGroupSet(response);
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
        // TODO: name resolution, i.e. searching taxons and extends params, OR do a second search with retrieved taxons
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, Arrays.asList(specimenNameSearchFieldNames));

        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE);

        return responseToSpecimenResultGroupSet(searchResponse);

        // TODO: mark results from name resolution
    }

    /**
     * Retrieves specimens matching a variable number of criteria. Rather than having one search term and a fixed set
     * of fields to match the search term against, the fields to query and the values to look for are specified as
     * parameters to this method. Nevertheless, the fields will always belong to the list specified in the
     * {@link #specimenSearch(String, String)} method.
     * <p/>
     * N.B. Name resolution is not used in this method
     *
     * @param params A {@link QueryParams} object containing:
     *               1. fields ... . A variable number of filters for fields. For example, the
     *               QueryParams object may contain a key “defaultClassification.genus” with a value of “Homo” and a key
     *               “defaultClassification.specificEpithet” with a value of “sapiens”. Fields must be mapped according
     *               to the mapping mechanism described above. Thus, if the QueryParams object contains a key “genus”,
     *               that key must be mapped to the “defaultClassification.genus” field.
     *               2. _andOr. An enumerated value with “AND” and “OR” as valid values. “AND” means all fields must
     *               match. “OR” means some fields must match. This is an optional parameter. By default only some
     *               fields must match.
     *               3. _sort. The field to sort on. Fields must be mapped according to the mapping mechanism described
     *               above. Special sort value: “_score” (sort by relevance). In practice sorting is only allowed on
     *               _score and on identifications.scientificName.fullScientificName. This is an optional parameter. By
     *               default sorting is done on _score.
     * @return
     */
    public ResultGroupSet<Specimen, String> specimenExtendedSearch(QueryParams params) {
        List<FieldMapping> fields = getSearchParamFieldMapping().getSpecimenMappingForFields(params);
        List<FieldMapping> fieldMappings = filterAllowedFieldMappings(fields, new ArrayList<>(specimenSearchFieldNames));

        SearchResponse searchResponse = executeExtendedSearch(params, fieldMappings, SPECIMEN_TYPE);

        return responseToSpecimenResultGroupSet(searchResponse);
    }

    /**
     * Retrieves a single Specimen by its unitID. A specimen retrieved through this method is always retrieved through
     * a REST link in the response from either {@link #specimenNameSearch(QueryParams)} or
     * {@link #specimenSearch(String, String)}. This method is aware of the result set generated by those methods and
     * is therefore capable of generating REST links to the previous and next
     * specimen in the result set. All parameters passed to specimenNameSearch or specimenNameSearch will also be passed to
     * this method. Basically, this method has to re-execute the query executed by
     * {@link #specimenNameSearch(QueryParams)} or {@link #specimenSearch(String, String)},
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
    public ResultGroupSet<Specimen, String> getSpecimenDetailWithinSearchResult(QueryParams params) {
        String sortField = getScoreFieldFromQueryParams(params);
        FieldSortBuilder fieldSort = fieldSort(sortField);
        //todo needs to be implemented

        return responseToSpecimenResultGroupSet(null);
    }

    // ==================================================== Helpers ====================================================

    private ResultGroupSet<Specimen, String> responseToSpecimenResultGroupSet(SearchResponse response) {
        ResultGroupSet<Specimen, String> specimenStringResultGroupSet = new ResultGroupSet<>();
        HashMap<String, List<Specimen>> tempMap = new HashMap<>();

        for (SearchHit hit : response.getHits()) {
            ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
            Specimen transfer = SpecimenTransfer.transfer(esSpecimen);

            for (SpecimenIdentification specimenIdentification : transfer.getIdentifications()) {
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

        for (Map.Entry<String, List<Specimen>> stringListEntry : tempMap.entrySet()) {
            ResultGroup<Specimen, String> resultGroup = new ResultGroup<>();
            resultGroup.setSharedValue(stringListEntry.getKey());
            List<Specimen> value = stringListEntry.getValue();
            for (Specimen specimen : value) {
                resultGroup.addSearchResult(specimen);
            }
            specimenStringResultGroupSet.addGroup(resultGroup);
        }

        specimenStringResultGroupSet.setTotalSize(response.getHits().getTotalHits());
        return specimenStringResultGroupSet;
    }

}
