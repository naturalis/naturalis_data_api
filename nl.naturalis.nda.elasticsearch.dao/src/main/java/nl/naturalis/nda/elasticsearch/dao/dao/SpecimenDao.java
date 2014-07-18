package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.elasticsearch.dao.exception.InvalidQueryException;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class SpecimenDao extends AbstractDao {

	public static void main(String[] args)
	{
		SpecimenDao dao = new SpecimenDao("nda");
		QueryParams params = new QueryParams();
		//props.add("offset", "10");
		//props.add("sex", "female");
		//props.add("sourceSystemId", "6809111");
		//props.add("specimenId", "50004");
		params.add("specimenId", "RMNH.MAM.51251");
		System.out.println(dao.listSpecimens(params));
	}

	private static final String TYPE = "specimen";


	public SpecimenDao(String ndaIndexName)
	{
		super(ndaIndexName);
	}


	public SpecimenDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}


	public String listSpecimens(QueryParams properties)
	{
		SearchRequestBuilder srb = newSearchRequest();
		srb.setTypes(TYPE);
		srb.setSize(30);
		MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
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
