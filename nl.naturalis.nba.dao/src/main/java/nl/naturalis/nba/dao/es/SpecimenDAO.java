package nl.naturalis.nba.dao.es;

import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.ISpecimenDAO;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Criterion;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.ESClientFactory;
import nl.naturalis.nba.dao.Registry;
import nl.naturalis.nba.dao.es.transfer.SpecimenTransfer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAO implements ISpecimenDAO {

	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(SpecimenDAO.class);
	}

	private final Registry registry;
	private final String esIndex;
	private final String esType;

	public SpecimenDAO()
	{
		registry = Registry.getInstance();
		esIndex = registry.getIndex(ESSpecimen.class);
		esType = registry.getType(ESSpecimen.class);
	}

	@Override
	public Specimen findById(String id)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieving specimen with id \"{}\"", id);
		}
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		GetRequestBuilder request = client.prepareGet();
		request.setIndex(esIndex);
		request.setType(esType);
		request.setId(id);
		GetResponse response = request.execute().actionGet();
		if (!response.isExists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Specimen with id \"{}\" not found", id);
			}
			return null;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Response:\n{}", response.getSourceAsString());
		}
		Map<String, Object> data = response.getSource();
		return createSpecimen(id, data);
	}

	@Override
	public List<Specimen> findByUnitID(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Searching for specimens with UnitID \"{}\"", unitID);
		}
		SearchRequestBuilder request = newSearchRequest();
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	public List<Specimen> findByCollector(String name)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Searching for specimens from collector \"{}\"", name);
		}
		TermQueryBuilder tqb = termQuery("gatheringEvent.gatheringPersons.fullName", name);
		NestedQueryBuilder nqb0 = QueryBuilders.nestedQuery("gatheringEvent.gatheringPersons", tqb);
		ConstantScoreQueryBuilder csq = constantScoreQuery(nqb0);
		SearchRequestBuilder request = newSearchRequest();
		request.setQuery(csq);
		return processSearchRequest(request);
	}
	
	public List<Specimen> query(QuerySpec spec) {
		SearchRequestBuilder request = newSearchRequest();
		BoolQueryBuilder conditions = QueryBuilders.boolQuery();
		ConstantScoreQueryBuilder csq = constantScoreQuery(conditions);
		//Criterion criterion = 
		return null;
	}

	private List<Specimen> processSearchRequest(SearchRequestBuilder request)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		if (logger.isDebugEnabled()) {
			logger.debug("Response:\n{}", response);
		}
		List<Specimen> specimens = new ArrayList<>(hits.length);
		for (SearchHit hit : hits) {
			String id = hit.getId();
			Map<String, Object> data = hit.getSource();
			Specimen specimen = createSpecimen(id, data);
			specimens.add(specimen);
		}
		return specimens;
	}

	private Specimen createSpecimen(String id, Map<String, Object> data)
	{
		ObjectMapper om = registry.getObjectMapper(ESSpecimen.class);
		ESSpecimen esSpecimen = om.convertValue(data, ESSpecimen.class);
		return SpecimenTransfer.transfer(esSpecimen, id);
	}

	private SearchRequestBuilder newSearchRequest()
	{
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		SearchRequestBuilder request = client.prepareSearch(esIndex);
		request.setTypes(registry.getType(ESSpecimen.class));
		return request;
	}
}
