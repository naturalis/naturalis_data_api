package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroup;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.TAXON_TYPE;

/**
 * Abstract class for Taxon functionality.
 *
 * @author Roberto van der Linden
 */
public class AbstractTaxonDao extends AbstractDao {

    public AbstractTaxonDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    /**
     * Method as generic as possible for internal use.
     * <p/>
     * Evaluates simple search parameter.
     *
     * @param params                          search parameters
     * @param allowedFieldNames               may be null if you don't want filtering
     * @param simpleSearchFieldNameExceptions
     * @param highlighting                    @return search results
     */
    ResultGroupSet<Taxon, String> search(QueryParams params, Set<String> allowedFieldNames,
                                         Set<String> simpleSearchFieldNameExceptions, boolean highlighting) {
        evaluateSimpleSearch(params, allowedFieldNames, simpleSearchFieldNameExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                ? fields
                : filterAllowedFieldMappings(fields, allowedFieldNames);
        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, highlighting);

        long totalHits = searchResponse.getHits().getTotalHits();
        float minScore = 0;
        if (totalHits > 1) {
            QueryParams copy = params.copy();
            copy.putSingle("_offset", String.valueOf(totalHits - 1));
            minScore = executeExtendedSearch(copy, allowedFields, TAXON_TYPE, highlighting).getHits().getAt(0).getScore();
        }

        return responseToTaxonSearchResultGroupSet(searchResponse, params, minScore);
    }

    SearchResultSet<Taxon> searchReturnsResultSet(QueryParams params, Set<String> allowedFieldNames,
                                                  Set<String> simpleSearchFieldNameExceptions, boolean highlighting) {
        evaluateSimpleSearch(params, allowedFieldNames, simpleSearchFieldNameExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                ? fields
                : filterAllowedFieldMappings(fields, allowedFieldNames);
        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, highlighting);

        long totalHits = searchResponse.getHits().getTotalHits();
        float minScore = 0;
        if (totalHits > 1) {
            QueryParams copy = params.copy();
            copy.putSingle("_offset", String.valueOf(totalHits - 1));
            minScore = executeExtendedSearch(copy, allowedFields, TAXON_TYPE, highlighting).getHits().getAt(0).getScore();
        }

        ResultGroupSet<Taxon, String> taxonStringResultGroupSet = responseToTaxonSearchResultGroupSet(searchResponse, params, minScore);
        return resultGroupSetToResultSet(taxonStringResultGroupSet);
    }

    private SearchResultSet<Taxon> resultGroupSetToResultSet(ResultGroupSet<Taxon, String> taxonStringResultGroupSet) {
        SearchResultSet<Taxon> resultSet = new SearchResultSet<>();
        List<ResultGroup<Taxon, String>> resultGroups = taxonStringResultGroupSet.getResultGroups();
        if (resultGroups != null && !resultGroups.isEmpty()) {
            List<SearchResult<Taxon>> searchResults = resultGroups.get(0).getSearchResults();
            if (searchResults != null && !searchResults.isEmpty()) {
                for (SearchResult<Taxon> searchResult : searchResults) {
                    resultSet.addSearchResult(searchResult);
                }
            }
        }
        resultSet.setTotalSize(taxonStringResultGroupSet.getTotalSize());
        resultSet.setQueryParameters(taxonStringResultGroupSet.getQueryParameters());
        return resultSet;
    }

    protected ResultGroupSet<Taxon, String> responseToTaxonSearchResultGroupSet(SearchResponse searchResponse, QueryParams params, float minScore) {
        float maxScore = searchResponse.getHits().getMaxScore();

        ResultGroupSet<Taxon, String> taxonSearchResultGroupSet = new ResultGroupSet<>();

        Map<String, SearchResultSet<Taxon>> nameToTaxons = new HashMap<>();

        for (SearchHit hit : searchResponse.getHits()) {
            SearchResult<Taxon> searchResult = new SearchResult<>();

            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            String taxonName = createAcceptedNameParams(esTaxon.getAcceptedName());
            if (!nameToTaxons.containsKey(taxonName)) {
                nameToTaxons.put(taxonName, new SearchResultSet<Taxon>());
            }
            SearchResultSet<Taxon> taxonsForName = nameToTaxons.get(taxonName);

            Taxon taxon = TaxonTransfer.transfer(esTaxon);
            //TODO NDA-66 taxon link must be to detail base url in result set
            searchResult.addLink(new Link("_taxon", TAXON_DETAIL_BASE_URL + createAcceptedNameParams(esTaxon.getAcceptedName())));
            searchResult.setResult(taxon);
            double percentage = ((hit.getScore() - minScore) / (maxScore - minScore)) * 100;
            searchResult.setPercentage(percentage);
            enhanceSearchResultWithMatchInfoAndScore(searchResult, hit);

            taxonsForName.addSearchResult(searchResult);
        }

        for (Map.Entry<String, SearchResultSet<Taxon>> nameSearchResultSetEntry : nameToTaxons.entrySet()) {
            ResultGroup<Taxon, String> group = new ResultGroup<>();
            group.setSharedValue(nameSearchResultSetEntry.getKey());
            for (SearchResult<Taxon> searchResult : nameSearchResultSetEntry.getValue().getSearchResults()) {
                group.addSearchResult(searchResult);
            }
            taxonSearchResultGroupSet.addGroup(group);
        }

        taxonSearchResultGroupSet.setQueryParameters(params.copyWithoutGeoShape());
        taxonSearchResultGroupSet.setTotalSize(searchResponse.getHits().getTotalHits());

        return taxonSearchResultGroupSet;
    }

}
