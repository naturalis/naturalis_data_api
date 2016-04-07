package nl.naturalis.nba.dao.es;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.ESClientFactory;
import nl.naturalis.nba.dao.Registry;
import nl.naturalis.nba.dao.es.transfer.SpecimenTransfer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDao {

	private static ObjectMapper objectMapper;

	public SpecimenDao()
	{
	}

	public List<Specimen> findByUnitID(String unitID)
	{
		SearchRequestBuilder request = basicSearchRequest();
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("unitID", unitID);
		request.setQuery(termQueryBuilder);
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		List<Specimen> specimens = new ArrayList<>(hits.length);
		for (SearchHit hit : hits) {
			Specimen specimen = convert(hit);
			specimens.add(specimen);
		}
		return specimens;
	}

	public static Specimen convert(SearchHit hit)
	{
		ObjectMapper mapper = getObjectMapper();
		Map<String, Object> data = hit.getSource();
		ESSpecimen esSpecimen = mapper.convertValue(data, ESSpecimen.class);
		return SpecimenTransfer.transfer(esSpecimen);
	}

	protected static ObjectMapper getObjectMapper()
	{
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected SearchRequestBuilder basicSearchRequest()
	{
		Registry reg = Registry.getInstance();
		ESClientFactory factory = reg.getESClientFactory();
		Client client = factory.getClient();
		String[] indices = reg.getIndices(ESSpecimen.class);
		String type = reg.getType(ESSpecimen.class);
		SearchRequestBuilder request = client.prepareSearch(indices);
		request.setTypes(type);
		return request;
	}
}
