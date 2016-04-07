package nl.naturalis.nba.dao.es;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.domainobject.util.ConfigObject;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
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

	private final String[] indices;

	public SpecimenDao()
	{
		ConfigObject config = Registry.getInstance().getConfig();
		indices = config.requiredArray("specimen.index");
	}

	public List<Specimen> findByUnitID(String unitID)
	{
		SearchRequestBuilder request = basicSearchRequest();
		TermQueryBuilder termQueryBuilder = termQuery("unitID", unitID);
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

	private SearchRequestBuilder basicSearchRequest()
	{
		Registry registry = Registry.getInstance();
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		SearchRequestBuilder request = client.prepareSearch(indices);
		request.setTypes(registry.getType(ESSpecimen.class));
		return request;
	}
}
