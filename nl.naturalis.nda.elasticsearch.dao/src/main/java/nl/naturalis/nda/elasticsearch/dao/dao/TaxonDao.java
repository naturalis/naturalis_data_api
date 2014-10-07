package nl.naturalis.nda.elasticsearch.dao.dao;

import java.util.Iterator;
import java.util.Map;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;

public class TaxonDao extends AbstractDao {

	public static void main(String[] args)
	{
		//Client esClient = nodeBuilder().node().client();
		Client esClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		TaxonDao dao = new TaxonDao(esClient, "nda");
		QueryParams params = new QueryParams();
		SearchResultSet<Taxon> result = dao.findByScientificName("Chionodes tragicella (von Heyden, 1865)");
	}


	public TaxonDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}


	public SearchResultSet<Taxon> taxonSearch(QueryParams params)
	{
		SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
		return rs;
	}


	public SearchResultSet<Taxon> findByIds(String ids)
	{
		return new SearchResultSet<Taxon>();
	}


	public SearchResultSet<Taxon> findByScientificName(String name)
	{
		SearchRequestBuilder request = esClient.prepareSearch();
		request.setTypes(LuceneType.TAXON.toString());
		TermFilterBuilder filter = FilterBuilders.termFilter("acceptedName.fullScientificName.raw", name);
		FilteredQueryBuilder query = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter);
		request.setQuery(query);
		SearchResponse response = request.execute().actionGet();
		SearchResultSet<Taxon> result = new SearchResultSet<Taxon>();
		result.setTotalSize(response.getHits().getTotalHits());
		Iterator<SearchHit> iterator = response.getHits().iterator();
		while (iterator.hasNext()) {
			SearchHit hit = iterator.next();
			Map<String, Object> source = hit.getSource();
			ESTaxon esTaxon = getObjectMapper().convertValue(source, ESTaxon.class);
			Taxon taxon = TaxonTransfer.transfer(esTaxon);
			result.addSearchResult(taxon);
		}
		return result;
	}


	public SearchResultSet<Taxon> getTaxonDetailWithinResultSet(QueryParams params)
	{
		SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
		return rs;
	}

}
