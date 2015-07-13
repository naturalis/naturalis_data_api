package nl.naturalis.nda.elasticsearch.dao.dao;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.MULTI_MEDIA_OBJECT_TYPE;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.transfer.MultiMediaObjectTransfer;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;

public class MultiMediaObjectDao extends AbstractDao {

	public MultiMediaObjectDao(Client esClient, String ndaIndexName, String baseUrl)
	{
		super(esClient, ndaIndexName, baseUrl);
	}


	public MultiMediaObject[] getMultiMediaForSpecimen(String specimenUnitID)
	{
		TermFilterBuilder filter = termFilter("associatedSpecimenReference.raw", specimenUnitID);
		FilteredQueryBuilder query = filteredQuery(matchAllQuery(), filter);
		SearchRequestBuilder request = newSearchRequest().setTypes(MULTI_MEDIA_OBJECT_TYPE).setQuery(query);
		SearchResponse response = request.execute().actionGet();
		SearchHit[] results = response.getHits().getHits();
		if (results.length == 0) {
			return null;
		}
		MultiMediaObject[] multimedia = new MultiMediaObject[results.length];
		for (int i = 0; i < results.length; ++i) {
			ESMultiMediaObject tmp = getObjectMapper().convertValue(results[i].getSource(), ESMultiMediaObject.class);
			multimedia[i] = MultiMediaObjectTransfer.transfer(tmp);
		}
		return multimedia;
	}

}
