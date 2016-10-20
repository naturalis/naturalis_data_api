package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.LogicalOperator.AND;
import static nl.naturalis.nba.common.json.JsonUtil.toPrettyJson;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;
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
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;

public class QuerySpecTranslator {

	private static final Logger logger = getLogger(QuerySpecTranslator.class);

	private QuerySpec spec;
	private DocumentType<?> type;

	public QuerySpecTranslator(QuerySpec querySpec, DocumentType<?> documentType)
	{
		this.spec = querySpec;
		this.type = documentType;
	}

	public SearchRequestBuilder translate() throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Translating QuerySpec:\n{}", toPrettyJson(spec));
		}
		QueryBuilder query = translateConditions();
		ConstantScoreQueryBuilder csq = constantScoreQuery(query);
		SearchRequestBuilder request = newSearchRequest();
		if (spec.getFields() != null) {
			addFields(request);
		}
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

	private void addFields(SearchRequestBuilder request) throws InvalidQueryException
	{
		MappingInfo mappingInfo = new MappingInfo(type.getMapping());
		List<String> fields = spec.getFields();
		for (String field : fields) {
			try {
				mappingInfo.getField(field);
			}
			catch (NoSuchFieldException e) {
				throw new InvalidQueryException(e.getMessage());
			}
		}
		String[] include = fields.toArray(new String[fields.size()]);
		request.setFetchSource(include, null);
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
			String fmt = "New search request (index={};type={})";
			logger.debug(fmt, index, type.getName());
		}
		Client client = ESClientManager.getInstance().getClient();
		SearchRequestBuilder request = client.prepareSearch(index);
		request.setTypes(type.getName());
		return request;
	}
}
