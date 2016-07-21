package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.LogicalOperator.AND;
import static nl.naturalis.nba.common.json.JsonUtil.toPrettyJson;
import static nl.naturalis.nba.dao.es.query.ConditionTranslatorFactory.getTranslator;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.api.query.SortField;
import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.ESClientManager;

public class QuerySpecTranslator {

	private static final Logger logger;

	static {
		logger = DAORegistry.getInstance().getLogger(QuerySpecTranslator.class);
	}

	private QuerySpec spec;
	private DocumentType type;

	public QuerySpecTranslator(QuerySpec querySpec, DocumentType documentType)
	{
		this.spec = querySpec;
		this.type = documentType;
	}

	public SearchRequestBuilder translate() throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Query using QuerySpec:\n{}", toPrettyJson(spec));
		}
		QueryBuilder query = translateConditions();
		ConstantScoreQueryBuilder csq = constantScoreQuery(query);
		SearchRequestBuilder request = newSearchRequest();
		request.setQuery(csq);
		request.setFrom(spec.getFrom());
		request.setSize(spec.getSize());
		if (spec.getSortFields() != null) {
			List<SortField> fields = spec.getSortFields();
			SortFieldsTranslator sft = new SortFieldsTranslator(fields, type);
			for (SortBuilder sb : sft.translate()) {
				request.addSort(sb);
			}
		}
		return request;
	}
	

	private QueryBuilder translateConditions() throws InvalidConditionException
	{
		List<Condition> conditions = spec.getConditions();
		if (conditions == null || conditions.size() == 0) {
			return QueryBuilders.matchAllQuery();
		}
		if (conditions.size() == 1) {
			Condition c = conditions.iterator().next();
			return getTranslator(c, type).translate();
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (spec.getLogicalOperator() == AND) {
			for (Condition c : conditions) {
				result.must(getTranslator(c, type).translate());
			}
		}
		else {
			for (Condition c : conditions) {
				result.should(getTranslator(c, type).translate());
			}
		}
		return result;
	}

	private SearchRequestBuilder newSearchRequest()
	{
		String index = type.getIndexInfo().getName();
		if (logger.isDebugEnabled()) {
			String pattern = "New search request (index={};type={})";
			logger.debug(pattern, index, type.getName());
		}
		Client client = ESClientManager.getInstance().getClient();
		SearchRequestBuilder request = client.prepareSearch(index);
		request.setTypes(type.getName());
		return request;
	}
}
