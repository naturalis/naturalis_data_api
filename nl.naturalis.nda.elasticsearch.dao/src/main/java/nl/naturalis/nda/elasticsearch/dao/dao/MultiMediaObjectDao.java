package nl.naturalis.nda.elasticsearch.dao.dao;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.MULTI_MEDIA_OBJECT_TYPE;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.UNIT_ID;
//import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.transfer.MultiMediaObjectTransfer;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilteredQueryBuilder;
//import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;

public class MultiMediaObjectDao extends AbstractDao {
//
//	public MultiMediaObjectDao(Client esClient, String ndaIndexName, String baseUrl)
//	{
//		super(esClient, ndaIndexName, baseUrl);
//	}
//
//
//	public boolean exists(String unitID)
//	{
//		TermFilterBuilder condition = termFilter(UNIT_ID + ".raw", unitID);
//		FilteredQueryBuilder query = filteredQuery(matchAllQuery(), condition);
//		SearchRequestBuilder request = newSearchRequest();
//		request.setTypes(MULTI_MEDIA_OBJECT_TYPE);
//		request.setQuery(query);
//		SearchResponse response = request.execute().actionGet();
//		return response.getHits().getHits().length != 0;
//	}
//
//
//	/**
//	 * Get the plain {@code Specimen} object corresponding to the specified
//	 * UnitID.
//	 * 
//	 * @param unitID
//	 * @return
//	 */
//	public MultiMediaObject find(String unitID)
//	{
//		TermFilterBuilder condition = termFilter(UNIT_ID + ".raw", unitID);
//		FilteredQueryBuilder query = filteredQuery(matchAllQuery(), condition);
//		SearchRequestBuilder request = newSearchRequest();
//		request.setTypes(MULTI_MEDIA_OBJECT_TYPE);
//		request.setQuery(query);
//		SearchResponse response = request.execute().actionGet();
//
//		if (response.getHits().getHits().length == 0) {
//			return null;
//		}
//		SearchHit hit = response.getHits().getHits()[0];
//		ESMultiMediaObject result = getObjectMapper().convertValue(hit.getSource(), ESMultiMediaObject.class);
//		return MultiMediaObjectTransfer.transfer(result);
//	}
}
