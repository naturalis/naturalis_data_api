package nl.naturalis.nba.dao.es;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.search.*;
import nl.naturalis.nba.dao.es.util.ESConstants;
import nl.naturalis.nba.dao.es.util.FieldMapping;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
//import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.*;

import static nl.naturalis.nba.dao.es.util.ESConstants.Fields.SOURCE_SYSTEM_ID;
import static nl.naturalis.nba.dao.es.util.ESConstants.Fields.TaxonFields.*;
//import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

public class TaxonDao extends AbstractTaxonDao {
//
//    public TaxonDao(Client esClient, String ndaIndexName, String baseUrl) {
//        super(esClient, ndaIndexName, baseUrl);
//    }
//
//    /**
//     * Retrieves Taxon documents by scientific name. Since the Taxon document type is populated from two source systems
//     * (CoL and NSR), a search by scientific name may result in 0, 1 or at most 2 search results.
//     *
//     * @param params params containing the the fields with their values
//     * @return the search results
//     */
//    public SearchResultSet<Taxon> getTaxonDetail(QueryParams params) {
//        String sessionId = params.getParam("_session_id");
//        params.remove("_session_id");
//        Map<String, String> fields = fieldNamesToValues(params);
//        String genus = fields.get(ACCEPTEDNAME_GENUS_OR_MONOMIAL);
//        String subgenus = fields.get(ACCEPTEDNAME_SUBGENUS);
//        String specificEpithet = fields.get(ACCEPTEDNAME_SPECIFIC_EPITHET);
//        String infraSpecificEpithet = fields.get(ACCEPTEDNAME_INFRASPECIFIC_EPITHET);
//
//        boolean validArgument = (hasText(genus) && hasText(specificEpithet));
//        if (!validArgument) {
//            throw new IllegalArgumentException("In accepted name, there should be a full scientific name or a" +
//                    " genus and specific epithet.");
//        }
//
//        ScientificName scientificName = new ScientificName();
//        scientificName.setGenusOrMonomial(genus);
//        scientificName.setSpecificEpithet(specificEpithet);
//        scientificName.setInfraspecificEpithet(infraSpecificEpithet);
//        scientificName.setSubgenus(subgenus);
//        return lookupTaxonForScientificName(scientificName, sessionId);
//    }
//
//    SearchResultSet<Taxon> lookupTaxonForSystemSourceId(String sourceSystemId, String sessionId) {
//        if (!hasText(sourceSystemId)) {
//            return new SearchResultSet<>();
//        }
//        QueryParams params = new QueryParams();
//        params.add(SOURCE_SYSTEM_ID, sourceSystemId);
//        return searchReturnsResultSet(params, Collections.singleton(SOURCE_SYSTEM_ID), Collections.<String>emptySet(), false, sessionId);
//    }
//
//    /**
//     * Search for a taxon based on the provided information in the scientific name
//     *
//     * @param scientificName scientificName containing the information for the lookup
//     * @return a SearchResultSet with the taxon if found
//     */
//    SearchResultSet<Taxon> lookupTaxonForScientificName(ScientificName scientificName, String sessionId) {
//        String genusOrMonomial = scientificName.getGenusOrMonomial();
//        String subgenus = scientificName.getSubgenus();
//        String specificEpithet = scientificName.getSpecificEpithet();
//        String infraspecificEpithet = scientificName.getInfraspecificEpithet();
//
//
//        BoolQueryBuilder boolQueryBuilder = null;
//        BoolFilterBuilder boolFilterBuilder = boolFilter();
//        if (hasText(genusOrMonomial) && hasText(specificEpithet)) {
//            boolQueryBuilder = boolQuery();
//            BoolQueryBuilder acceptNameBoolQueryBuilder = boolQuery();
//            acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_GENUS_OR_MONOMIAL, genusOrMonomial));
//            acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_SPECIFIC_EPITHET, specificEpithet));
//            if (hasText(infraspecificEpithet)) {
//                acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_INFRASPECIFIC_EPITHET, infraspecificEpithet));
//            } else {
//                boolFilterBuilder.must(missingFilter(ACCEPTEDNAME_INFRASPECIFIC_EPITHET));
//            }
//            if (hasText(subgenus)) {
//                acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_SUBGENUS, subgenus));
//            } else {
//                boolFilterBuilder.must(missingFilter(ACCEPTEDNAME_SUBGENUS));
//            }
//            boolQueryBuilder.should(acceptNameBoolQueryBuilder);
//        }
//
//        SearchRequestBuilder searchRequestBuilder = null;
//        if(boolQueryBuilder != null) {
//            if (boolFilterBuilder.hasClauses()) {
//                searchRequestBuilder = newSearchRequest().setTypes(ESConstants.TAXON_TYPE).setQuery(
//                        filteredQuery(boolQueryBuilder, boolFilterBuilder)
//                );
//            } else {
//                searchRequestBuilder = newSearchRequest().setTypes(ESConstants.TAXON_TYPE).setQuery(
//                        filteredQuery(boolQueryBuilder, null)
//                );
//            }
//        }
//
//        ResultGroupSet<Taxon, String> taxonStringResultGroupSet = new ResultGroupSet<>();
//        if(searchRequestBuilder != null) {
//            searchRequestBuilder.setPreference(sessionId);
//            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
//            taxonStringResultGroupSet = responseToTaxonSearchResultGroupSet(searchResponse, new QueryParams(), 0);
//        }
//
//        SearchResultSet<Taxon> resultSet = new SearchResultSet<>();
//        List<ResultGroup<Taxon, String>> resultGroups = taxonStringResultGroupSet.getResultGroups();
//        if (resultGroups != null && !resultGroups.isEmpty()) {
//            List<SearchResult<Taxon>> searchResults = resultGroups.get(0).getSearchResults();
//            if (searchResults != null && !searchResults.isEmpty()) {
//                for (SearchResult<Taxon> searchResult : searchResults) {
//                    resultSet.addSearchResult(searchResult);
//                }
//            }
//        }
//        resultSet.setTotalSize(taxonStringResultGroupSet.getTotalSize());
//        return resultSet;
//    }
//
//    private Map<String, String> fieldNamesToValues(QueryParams params) {
//        List<FieldMapping> mappingsForFields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
//        LinkedHashMap<String, String> map = new LinkedHashMap<>();
//        for (FieldMapping fieldMapping : mappingsForFields) {
//            String fieldName = fieldMapping.getFieldName();
//            String value = fieldMapping.getValue();
//            if (hasText(fieldName) && hasText(value)) {
//                map.put(fieldName, value);
//            }
//        }
//        return map;
//    }
}