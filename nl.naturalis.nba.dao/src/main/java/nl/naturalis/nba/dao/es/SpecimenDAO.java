package nl.naturalis.nba.dao.es;

import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.query.ConditionTranslator;
import nl.naturalis.nba.dao.es.query.ConditionTranslatorFactory;
import nl.naturalis.nba.dao.es.transfer.SpecimenTransfer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAO implements ISpecimenAPI {

	private static final Logger logger;

	private static final Registry registry;
	private static final ObjectMapperLocator objMapperLocator;
	private static final ObjectMapper spObjMapper;
	private static final String spIndex;
	private static final String spType;

	static {
		logger = Registry.getInstance().getLogger(SpecimenDAO.class);
		registry = Registry.getInstance();
		objMapperLocator = ObjectMapperLocator.getInstance();
		spObjMapper = objMapperLocator.getObjectMapper(ESSpecimen.class);
		spIndex = registry.getIndex(ESSpecimen.class);
		spType = registry.getType(ESSpecimen.class);
	}

	public SpecimenDAO()
	{
	}

	@Override
	public Specimen find(String id)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("find(\"{}\")", id);
		}
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		GetRequestBuilder request = client.prepareGet();
		request.setIndex(spIndex);
		request.setType(spType);
		request.setId(id);
		GetResponse response = request.execute().actionGet();
		if (!response.isExists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Specimen with id \"{}\" not found", id);
			}
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Response:\n{}", response.getSourceAsString());
		}
		Map<String, Object> data = response.getSource();
		return createSpecimen(id, data);
	}

	@Override
	public boolean exists(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("exists(\"{}\")", unitID);
		}
		SearchRequestBuilder request = newSearchRequest();
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		request.setSize(0);
		SearchResponse response = request.execute().actionGet();
		return response.getHits().getTotalHits() != 0;
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("findByUnitID(\"{}\")", unitID);
		}
		SearchRequestBuilder request = newSearchRequest();
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	public Specimen[] findByCollector(String name)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("findByCollector(\"{}\")", name);
		}
		TermQueryBuilder tq = termQuery("gatheringEvent.gatheringPersons.fullName", name);
		NestedQueryBuilder nq = nestedQuery("gatheringEvent.gatheringPersons", tq);
		ConstantScoreQueryBuilder csq = constantScoreQuery(nq);
		SearchRequestBuilder request = newSearchRequest();
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	public Specimen[] query(QuerySpec spec) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Query using QuerySpec:\n{}", dump(spec));
		}
		Condition condition = spec.getCondition();
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(condition, ESSpecimen.class);
		QueryBuilder query = ct.translate();
		ConstantScoreQueryBuilder csq = constantScoreQuery(query);
		SearchRequestBuilder request = newSearchRequest();
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	public String save(Specimen specimen, boolean immediate)
	{
		String id = specimen.getId();
		if (logger.isDebugEnabled()) {
			String pattern = "New save request (index={};type={};id={})";
			logger.debug(pattern, spIndex, spType, id);
		}
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		IndexRequestBuilder request = client.prepareIndex(spIndex, spType, id);
		ESSpecimen esSpecimen = SpecimenTransfer.save(specimen);
		byte[] source = JsonUtil.serialize(esSpecimen);
		request.setSource(source);
		IndexResponse response = request.execute().actionGet();
		if (immediate) {
			IndicesAdminClient iac = client.admin().indices();
			RefreshRequestBuilder rrb = iac.prepareRefresh(spIndex);
			rrb.execute().actionGet();
		}
		return response.getId();
	}

	private static Specimen[] processSearchRequest(SearchRequestBuilder request)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		if (logger.isDebugEnabled()) {
			logger.debug("Processing response:\n{}", response);
		}
		Specimen[] specimens = new Specimen[hits.length];
		for (int i = 0; i < hits.length; ++i) {
			String id = hits[i].getId();
			Map<String, Object> data = hits[i].getSource();
			Specimen specimen = createSpecimen(id, data);
			specimens[i] = specimen;
		}
		return specimens;
	}

	private static Specimen createSpecimen(String id, Map<String, Object> data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating Specimen instance with id {}", id);
		}
		ESSpecimen esSpecimen = spObjMapper.convertValue(data, ESSpecimen.class);
		return SpecimenTransfer.load(esSpecimen, id);
	}

	private static SearchRequestBuilder newSearchRequest()
	{
		if (logger.isDebugEnabled()) {
			String pattern = "New search request (index={};type={})";
			logger.debug(pattern, spIndex, spType);
		}
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		SearchRequestBuilder request = client.prepareSearch(spIndex);
		request.setTypes(spType);
		return request;
	}

	static String dump(Object obj)
	{
		ObjectWriter ow = spObjMapper.writerWithDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new DaoException(e);
		}
	}

}
