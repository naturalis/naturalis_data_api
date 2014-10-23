package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.List;
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
     * @param params            search parameters
     * @param allowedFieldNames may be null if you don't want filtering
     * @param simpleSearchFieldNameExceptions
     * @param highlighting  @return search results
     */
    SearchResultSet<Taxon> search(QueryParams params, Set<String> allowedFieldNames,
                                  Set<String> simpleSearchFieldNameExceptions, boolean highlighting) {
        evaluateSimpleSearch(params, allowedFieldNames, simpleSearchFieldNameExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                ? fields
                : filterAllowedFieldMappings(fields, allowedFieldNames);
        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, highlighting);
        return responseToTaxonSearchResultSet(searchResponse, params);
    }

    protected SearchResultSet<Taxon> responseToTaxonSearchResultSet(SearchResponse searchResponse, QueryParams params) {
        SearchResultSet<Taxon> taxonSearchResultSet = new SearchResultSet<>();
        for (SearchHit hit : searchResponse.getHits()) {
            SearchResult<Taxon> searchResult = new SearchResult<>();

            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            Taxon taxon = TaxonTransfer.transfer(esTaxon);
            searchResult.addLink(new Link("_taxon", TAXON_DETAIL_BASE_URL + taxon.getAcceptedName().getFullScientificName()));
            searchResult.setResult(taxon);

            enhanceSearchResultWithMatchInfo(searchResult, hit);

            taxonSearchResultSet.addSearchResult(searchResult);
        }

        taxonSearchResultSet.setTotalSize(searchResponse.getHits().getTotalHits());
        taxonSearchResultSet.setQueryParameters(params.copyWithoutGeoShape());

        return taxonSearchResultSet;
    }

}
