package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class TaxonDao extends AbstractDao {

    public static void main(String[] args) {
        Settings settings = ImmutableSettings.settingsBuilder().put(CLUSTER_NAME_PROPERTY, CLUSTER_NAME_PROPERTY_VALUE)
                                             .build();
        Client esClient = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(ES_HOST,
                                                                                                           ES_PORT));

        TaxonDao dao = new TaxonDao(esClient, INDEX_NAME);
        SearchResultSet<Taxon> taxonDetail = dao.getTaxonDetail("Hyphomonas oceanitis Weiner et al. 1985");
        for (SearchResult<Taxon> taxon : taxonDetail.getSearchResults()) {
            System.out.println(taxon.getResult().getAcceptedName().getGenusOrMonomial());
        }
    }

    public TaxonDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    /**
     * Retrieves Taxon documents by scientific name. Since the Taxon document type is populated from two source systems
     * (CoL and NSR), a search by scientific name may result in 0, 1 or at most 2 search results.
     *
     * @param acceptedName The accepted name of the taxon (maps to field acceptedName.fullScientificName)
     * @return
     */
    public SearchResultSet<Taxon> getTaxonDetail(String acceptedName) {
        SearchResponse response = newSearchRequest()
                .setTypes(TAXON_TYPE)
                .setQuery(filteredQuery(
                                  matchAllQuery(),
                                  termFilter(
                                          "acceptedName.fullScientificName.raw",
                                          acceptedName
                                  )
                          )
                )
                .execute().actionGet();
        SearchResultSet<Taxon> resultSet = new SearchResultSet<>();
        SearchHit hit = response.getHits().getHits()[0];
        if (hit != null) {
            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            Taxon taxon = TaxonTransfer.transfer(esTaxon);
            resultSet.addSearchResult(taxon);
        }
        resultSet.setTotalSize(response.getHits().getTotalHits());
        return resultSet;
    }
}
