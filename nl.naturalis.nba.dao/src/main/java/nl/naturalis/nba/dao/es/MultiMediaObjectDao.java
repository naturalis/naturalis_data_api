package nl.naturalis.nba.dao.es;

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
