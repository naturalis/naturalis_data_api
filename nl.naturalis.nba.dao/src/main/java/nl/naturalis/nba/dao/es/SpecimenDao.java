package nl.naturalis.nba.dao.es;

import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ArrayUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.ESClientFactory;
import nl.naturalis.nba.dao.Registry;
import nl.naturalis.nba.dao.es.transfer.SpecimenTransfer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDao {

	private static final Logger logger = Registry.getInstance().getLogger(SpecimenDao.class);

	private final Registry registry;
	private final String[] esIndices;
	private final String esType;

	public SpecimenDao()
	{
		registry = Registry.getInstance();
		esIndices = registry.getIndices(ESSpecimen.class);
		esType = registry.getType(ESSpecimen.class);
	}

	public List<Specimen> findByUnitID(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Searching for specimens with UnitID \"{}\"", unitID);
		}
		SearchRequestBuilder request = basicSearchRequest();
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		if (logger.isDebugEnabled()) {
			logger.debug("Generated query:\n" + request.toString());
		}
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		if (logger.isDebugEnabled()) {
			logger.debug("Number of hits: {}", hits.length);
		}
		List<Specimen> specimens = new ArrayList<>(hits.length);
		for (SearchHit hit : hits) {
			Specimen specimen = convert(hit);
			specimens.add(specimen);
		}
		return specimens;
	}

	public List<Specimen> findByUnitID2(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Searching for specimens with UnitID \"{}\"", unitID);
		}
		SearchRequestBuilder request = basicSearchRequest();
		request.setSource("{\"query\" : {\"match_all\": {}}}");
		if (logger.isDebugEnabled()) {
			logger.debug("Generated query:\n" + request.toString());
		}
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		if (logger.isDebugEnabled()) {
			logger.debug("Number of hits: {}", hits.length);
		}
		List<Specimen> specimens = new ArrayList<>(hits.length);
		for (SearchHit hit : hits) {
			Specimen specimen = convert(hit);
			specimens.add(specimen);
		}
		return specimens;
	}

	private Specimen convert(SearchHit hit)
	{
		Map<String, Object> data = hit.getSource();
		ObjectMapper om = registry.getObjectMapper(ESSpecimen.class);
		ESSpecimen esSpecimen = om.convertValue(data, ESSpecimen.class);
		return SpecimenTransfer.transfer(esSpecimen);
	}

	private SearchRequestBuilder basicSearchRequest()
	{
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		SearchRequestBuilder request = client.prepareSearch(esIndices);
		request.setTypes(registry.getType(ESSpecimen.class));
		if (logger.isTraceEnabled()) {
			String s = ArrayUtil.implode(esIndices);
			logger.trace("Target indices: {}", s);
			logger.trace("Target type: {}", esType);
		}
		return request;
	}
}
