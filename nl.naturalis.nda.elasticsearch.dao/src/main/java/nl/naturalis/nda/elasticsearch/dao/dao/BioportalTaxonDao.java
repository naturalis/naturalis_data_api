package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.List;

public class BioportalTaxonDao extends AbstractDao {

    public BioportalTaxonDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    public SearchResultSet<Taxon> taxonExtendedSearch(QueryParams params) {
        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);

        SearchResponse searchResponse = executeExtendedSearch(params, fields, TAXON_TYPE);

        SearchResultSet<Taxon> taxonSearchResultSet = new SearchResultSet<>();
        for (SearchHit hit : searchResponse.getHits()) {
            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            Taxon taxon = TaxonTransfer.transfer(esTaxon);

            taxonSearchResultSet.addSearchResult(taxon);
        }

        //TODO implement
//        taxonSearchResultSet.setSearchTerms();
        taxonSearchResultSet.setTotalSize(searchResponse.getHits().getTotalHits());

        return taxonSearchResultSet;
    }
}
