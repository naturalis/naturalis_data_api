package nl.naturalis.nba.dao.es;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.search.*;
import nl.naturalis.nba.dao.es.transfer.TaxonTransfer;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.util.FieldMapping;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import static nl.naturalis.nba.dao.es.util.ESConstants.TAXON_TYPE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class for Taxon functionality.
 *
 * @author Roberto van der Linden
 */
public class AbstractTaxonDao extends AbstractDao {
//
//    public AbstractTaxonDao(Client esClient, String ndaIndexName, String baseUrl) {
//        super(esClient, ndaIndexName, baseUrl);
//    }
//
//    /**
//     * Method as generic as possible for internal use.
//     * <p/>
//     * Evaluates simple search parameter.
//     *
//     * @param params                          search parameters
//     * @param allowedFieldNames               may be null if you don't want filtering
//     * @param simpleSearchFieldNameExceptions
//     * @param highlighting                    @return search results
//     */
//    ResultGroupSet<Taxon, String> search(QueryParams params, Set<String> allowedFieldNames,
//                                         Set<String> simpleSearchFieldNameExceptions,
//                                         boolean highlighting,
//                                         String sessionId) {
//        evaluateSimpleSearch(params, allowedFieldNames, simpleSearchFieldNameExceptions);
//        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
//        List<FieldMapping> allowedFields = (allowedFieldNames == null)
//                ? fields
//                : filterAllowedFieldMappings(fields, allowedFieldNames);
//        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, highlighting, sessionId);
//
//        long totalHits = searchResponse.getHits().getTotalHits();
//        float minScore = 0;
//        if (totalHits > 1) {
//            QueryParams copy = params.copy();
//            copy.add("_offset", String.valueOf(totalHits - 1));
//            minScore = executeExtendedSearch(copy, allowedFields, TAXON_TYPE, highlighting, sessionId).getHits().getAt(0).getScore();
//        }
//
//        return responseToTaxonSearchResultGroupSet(searchResponse, params, minScore);
//    }
//
//    SearchResultSet<Taxon> searchReturnsResultSet(QueryParams params, Set<String> allowedFieldNames,
//                                                  Set<String> simpleSearchFieldNameExceptions, boolean highlighting, String sessionId) {
//        evaluateSimpleSearch(params, allowedFieldNames, simpleSearchFieldNameExceptions);
//        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
//        List<FieldMapping> allowedFields = (allowedFieldNames == null)
//                ? fields
//                : filterAllowedFieldMappings(fields, allowedFieldNames);
//        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, highlighting, sessionId);
//
//        long totalHits = searchResponse.getHits().getTotalHits();
//        float minScore = 0;
//        if (totalHits > 1) {
//            QueryParams copy = params.copy();
//            copy.add("_offset", String.valueOf(totalHits - 1));
//            minScore = executeExtendedSearch(copy, allowedFields, TAXON_TYPE, highlighting, sessionId).getHits().getAt(0).getScore();
//        }
//
//        ResultGroupSet<Taxon, String> taxonStringResultGroupSet = responseToTaxonSearchResultGroupSet(searchResponse, params, minScore);
//        return resultGroupSetToResultSet(taxonStringResultGroupSet);
//    }
//
//    private SearchResultSet<Taxon> resultGroupSetToResultSet(ResultGroupSet<Taxon, String> taxonStringResultGroupSet) {
//        SearchResultSet<Taxon> resultSet = new SearchResultSet<>();
//        List<ResultGroup<Taxon, String>> resultGroups = taxonStringResultGroupSet.getResultGroups();
//        if (resultGroups != null && !resultGroups.isEmpty()) {
//            for (ResultGroup<Taxon, String> resultGroup : resultGroups) {
//                List<SearchResult<Taxon>> searchResults = resultGroup.getSearchResults();
//                if (searchResults != null && !searchResults.isEmpty()) {
//                    for (SearchResult<Taxon> searchResult : searchResults) {
//                        resultSet.addSearchResult(searchResult);
//                    }
//                }
//            }
//
//        }
//        resultSet.setTotalSize(taxonStringResultGroupSet.getTotalSize());
//        resultSet.setQueryParameters(taxonStringResultGroupSet.getQueryParameters());
//        return resultSet;
//    }
//
//    protected ResultGroupSet<Taxon, String> responseToTaxonSearchResultGroupSet(SearchResponse searchResponse, QueryParams params, float minScore) {
//        float maxScore = searchResponse.getHits().getMaxScore();
//
//        ResultGroupSet<Taxon, String> taxonSearchResultGroupSet = new ResultGroupSet<>();
//        LinkedHashMap<String, SearchResultSet<Taxon>> nameToTaxons = new LinkedHashMap<>();
//
//        for (SearchHit hit : searchResponse.getHits()) {
//            Taxon taxon = TaxonTransfer.transfer(getObjectMapper().convertValue(hit.getSource(), ESTaxon.class));
//
//            String taxonName = createAcceptedNameParams(taxon.getAcceptedName());
//            if (!nameToTaxons.containsKey(taxonName)) {
//                nameToTaxons.put(taxonName, new SearchResultSet<Taxon>());
//            }
//
//            SearchResultSet<Taxon> taxonsForName = nameToTaxons.get(taxonName);
//            SearchResult<Taxon> searchResult = new SearchResult<>();
//
//            //TODO NDA-66 taxon link must be to detail base url in result set
//            searchResult.addLink(new Link("_taxon", TAXON_DETAIL_BASE_URL + createAcceptedNameParams(taxon.getAcceptedName())));
//            searchResult.setResult(taxon);
//            enhanceSearchResultWithMatchInfoAndScore(searchResult, hit);
//            double percentage = ((hit.getScore() - minScore) / (maxScore - minScore)) * 100;
//            /*
//             * Jira:	NDA_294
//             * By: 		Reinier.Kartowikromo
//             * Date: 	14-08-2015
//             * Problem: This problem occured if hit.getScore, minScore and maxScore has the same value.
//             * 			The result will always be zero(0) and that's a "NaN" as result.
//             * 
//             * Description(Solution): 	Checked if percentage is a valid floatnumber. 
//             * 							If valid then the return value is percentage else value is "0".
//             * */
//            if (Double.isNaN(percentage))
//            {
//            	searchResult.setPercentage(0.0);
//            }
//            else
//            {
//            	searchResult.setPercentage(percentage);
//            }
//
//            taxonsForName.addSearchResult(searchResult);
//        }
//
//        for (Map.Entry<String, SearchResultSet<Taxon>> nameSearchResultSetEntry : nameToTaxons.entrySet()) {
//            ResultGroup<Taxon, String> group = new ResultGroup<>();
//            group.setSharedValue(nameSearchResultSetEntry.getKey());
//            for (SearchResult<Taxon> searchResult : nameSearchResultSetEntry.getValue().getSearchResults()) {
//                group.addSearchResult(searchResult);
//            }
//            taxonSearchResultGroupSet.addGroup(group);
//        }
//
//        //taxonSearchResultGroupSet.setQueryParameters(params.copyWithoutGeoShape());
//        taxonSearchResultGroupSet.setTotalSize(searchResponse.getHits().getTotalHits());
//        
//        return taxonSearchResultGroupSet;
//    }
//
}
