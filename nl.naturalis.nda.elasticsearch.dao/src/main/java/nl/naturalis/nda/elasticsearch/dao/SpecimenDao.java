package nl.naturalis.nda.elasticsearch.dao;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

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
		System.out.println(dao.listSpecimens());
	}

	final Client esClient;


	public SpecimenDao()
	{
		this.esClient = nodeBuilder().node().client();
	}


	public String listSpecimens()
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
