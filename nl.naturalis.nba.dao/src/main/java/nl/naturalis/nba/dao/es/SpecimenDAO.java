package nl.naturalis.nba.dao.es;

import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.query.ConditionTranslator;
import nl.naturalis.nba.dao.es.query.ConditionTranslatorFactory;
import nl.naturalis.nba.dao.es.transfer.SpecimenTransfer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAO implements ISpecimenAPI {

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
	public Specimen find(String id)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("findById(\"{}\")", id);
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
		if (logger.isDebugEnabled()) {
			logger.debug("Response:\n{}", response.getSourceAsString());
		}
		Map<String, Object> data = response.getSource();
		return createSpecimen(id, data);
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

	private Specimen[] processSearchRequest(SearchRequestBuilder request)
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

	private Specimen createSpecimen(String id, Map<String, Object> data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating Specimen instance with id {}", id);
		}
		ObjectMapper om = registry.getObjectMapper(ESSpecimen.class);
		ESSpecimen esSpecimen = om.convertValue(data, ESSpecimen.class);
		return SpecimenTransfer.transfer(esSpecimen, id);
	}

	private SearchRequestBuilder newSearchRequest()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing search request");
		}
		ESClientFactory factory = registry.getESClientFactory();
		Client client = factory.getClient();
		SearchRequestBuilder request = client.prepareSearch(esIndex);
		request.setTypes(registry.getType(ESSpecimen.class));
		return request;
	}

	static String dump(Object obj)
	{
		ObjectMapper om = new ObjectMapper();
		ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new DaoException(e);
		}
	}

	static String dump(QuerySpec obj)
	{
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		om.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
		om.setSerializationInclusion(Include.NON_NULL);
		om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new DaoException(e);
		}
	}
}
