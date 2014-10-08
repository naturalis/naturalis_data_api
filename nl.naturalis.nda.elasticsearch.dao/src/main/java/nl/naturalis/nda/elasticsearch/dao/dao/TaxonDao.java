package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

import static nl.naturalis.nda.elasticsearch.dao.dao.LuceneType.TAXON;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class TaxonDao extends AbstractDao {

    public static void main(String[] args) {
        //Client esClient = nodeBuilder().node().client();
        Client esClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        TaxonDao dao = new TaxonDao(esClient, "nda");
        QueryParams params = new QueryParams();
        SearchResultSet<Taxon> result = dao.findByScientificName("Chionodes tragicella (von Heyden, 1865)");
    }


    public TaxonDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }


    public SearchResultSet<Taxon> taxonSearch(QueryParams params) {
        SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
        return rs;
    }


    public SearchResultSet<Taxon> findByIds(String ids) {
        return new SearchResultSet<Taxon>();
    }


    public SearchResultSet<Taxon> findByScientificName(String name) {
        SearchResponse response = esClient.prepareSearch("nda")
                                          .setTypes(TAXON.toString())
                                          .setQuery(filteredQuery(matchAllQuery(),
                                                                  termFilter(
                                                                          "acceptedName.fullScientificName.raw",
                                                                          name)
                                                    )
                                          )
                                          .execute().actionGet();

        SearchResultSet<Taxon> result = new SearchResultSet<>();
        result.setTotalSize(response.getHits().getTotalHits());
        for (SearchHit hit : response.getHits()) {
            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            Taxon taxon = TaxonTransfer.transfer(esTaxon);
            result.addSearchResult(taxon);
        }
        return result;
    }


    public SearchResultSet<Taxon> getTaxonDetailWithinResultSet(QueryParams params) {
        SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
        return rs;
    }
}
