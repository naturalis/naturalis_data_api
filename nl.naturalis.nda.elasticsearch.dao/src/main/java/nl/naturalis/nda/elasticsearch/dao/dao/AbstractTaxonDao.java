package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.List;
import java.util.Set;

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
     * Method as generic as possible for internal use
     *
     * @param params search parameters
     * @param allowedFieldNames may be null if you don't want filtering
     * @return search results
     */
    SearchResultSet<Taxon> search(QueryParams params, Set<String> allowedFieldNames) {
        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                                           ? fields
                                           : filterAllowedFieldMappings(fields, allowedFieldNames);

        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, true);
        return responseToTaxonSearchResultSet(searchResponse, params);
    }

    private SearchResultSet<Taxon> responseToTaxonSearchResultSet(SearchResponse searchResponse, QueryParams params) {
        SearchResultSet<Taxon> taxonSearchResultSet = new SearchResultSet<>();
        for (SearchHit hit : searchResponse.getHits()) {
            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            Taxon taxon = TaxonTransfer.transfer(esTaxon);

            taxonSearchResultSet.addSearchResult(taxon);
        }

        // TODO links
        taxonSearchResultSet.setTotalSize(searchResponse.getHits().getTotalHits());
        taxonSearchResultSet.setQueryParameters(params.copyWithoutGeoShape());

        return taxonSearchResultSet;
    }
}
