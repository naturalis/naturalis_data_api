package nl.naturalis.nda.elasticsearch.dao.dao;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import nl.naturalis.nda.elasticsearch.dao.util.MultiValuedProperties;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

public class SpecimenDao {

	public static void main(String[] args)
	{
		SpecimenDao dao = new SpecimenDao();
		MultiValuedProperties props = new MultiValuedProperties();
		System.out.println(dao.listSpecimens(props));
	}

	final Client esClient;


	public SpecimenDao()
	{
		this.esClient = nodeBuilder().node().client();
	}


	public String listSpecimens(MultiValuedProperties properties)
	{
		SearchRequestBuilder srb = esClient.prepareSearch("specimen");
		TermQueryBuilder termQuery = QueryBuilders.termQuery("sourceSystemName", "CRS");
		MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
		TermFilterBuilder termFilter = FilterBuilders.termFilter("sex", "male");
		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(matchAllQuery, termFilter);
		srb.setSize(100);
		srb.setQuery(filteredQuery);
		SearchResponse response = srb.execute().actionGet();
		return response.toString();
	}

}
