package nl.naturalis.nda.elasticsearch.dao.dao;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import nl.naturalis.nda.elasticsearch.dao.exception.InvalidQueryException;
import nl.naturalis.nda.elasticsearch.dao.util.MultiValuedProperties;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
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
		//props.add("offset", "10");
		//props.add("sex", "female");
		//props.add("sourceSystemId", "6809111");
		props.add("specimenId", "50004");
		System.out.println(dao.listSpecimens(props));
	}

	private static Client localClient;


	public static final Client getDefaultLocalClient()
	{
		if (localClient == null) {
			localClient = nodeBuilder().local(true).client(false).node().client();
			localClient.admin().cluster().prepareHealth().setWaitForGreenStatus().execute().actionGet();
		}
		return localClient;
	}

	final Client esClient;


	public SpecimenDao()
	{
		this.esClient = getDefaultLocalClient();
	}


	public String listSpecimens(MultiValuedProperties properties)
	{
		SearchRequestBuilder srb = esClient.prepareSearch("specimen");
		srb.setSize(30);
		TermQueryBuilder termQuery = QueryBuilders.termQuery("sourceSystemName", "CRS");
		MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
		TermFilterBuilder termFilter = FilterBuilders.termFilter("sourceSystemId", "6809111");
		AndFilterBuilder andFilter = FilterBuilders.andFilter();
		for (String[] kv : properties.keyValuePairs()) {
			if (kv[0].equals("offset")) {
				try {
					srb.setFrom(Integer.parseInt(kv[1]));
				}
				catch (NumberFormatException e) {
					throw new InvalidQueryException("Parameter \"offset\" must be an integer");
				}
			}
			else {
				andFilter.add(FilterBuilders.termFilter(kv[0], kv[1]));
			}
		}
		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(matchAllQuery, andFilter);
		srb.setQuery(filteredQuery);
		SearchResponse response = srb.execute().actionGet();
		return response.toString();
	}

}
