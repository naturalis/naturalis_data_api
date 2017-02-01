package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.LogicalOperator.OR;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.translate.search.ConditionTranslatorFactory.getTranslator;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.DocumentType;

/**
 * A {@code SearchSpecTranslator} is responsible for translating an NBA
 * {@link SearchSpec} object into an Elasticsearch {@link SearchRequestBuilder}
 * object.
 * 
 * @author Ayco Holleman
 *
 */
public class SearchSpecTranslator {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(SearchSpecTranslator.class);
	private static final int DEFAULT_FROM = 0;
	private static final int DEFAULT_SIZE = 10;

	private SearchSpec spec;
	private DocumentType<?> dt;

	public SearchSpecTranslator(SearchSpec querySpec, DocumentType<?> documentType)
	{
		this.spec = querySpec;
		this.dt = documentType;
	}

	/**
	 * Translates the {@link SearchSpec} object into an Elasticsearch query.
	 * 
	 * @return
	 * @throws InvalidQueryException
	 */
	public SearchRequestBuilder translate() throws InvalidQueryException
	{
		SearchRequestBuilder request = newSearchRequest(dt);
		if (spec.getConditions() != null && !spec.getConditions().isEmpty()) {
			QueryBuilder query;
			if (spec.isNonScoring()) {
				query = constantScoreQuery(translateConditions());
			}
			else {
				query = translateConditions();
			}
			request.setQuery(query);
		}
		if (spec.getFields() != null) {
			addFields(request);
		}
		request.setFrom(spec.getFrom() == null ? DEFAULT_FROM : spec.getFrom());
		request.setSize(spec.getSize() == null ? DEFAULT_SIZE : spec.getSize());
		if (spec.getSortFields() != null) {
			SortFieldsTranslator sft = new SortFieldsTranslator(spec, dt);
			for (FieldSortBuilder sb : sft.translate()) {
				request.addSort(sb);
			}
		}
		return request;
	}

	private void addFields(SearchRequestBuilder request) throws InvalidQueryException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
		List<String> fields = spec.getFields();
		for (String field : fields) {
			if (field.equals("id")) {
				/*
				 * This is a special field that can be used to retrieve the
				 * Elasticsearch document ID, which is not part of the document
				 * itself.
				 */
				continue;
			}
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
		List<SearchCondition> conditions = spec.getConditions();
		if (spec.isNonScoring()) {
			for (SearchCondition c : conditions) {
				turnIntoFilter(c);
			}
		}
		if (conditions.size() == 1) {
			SearchCondition c = conditions.iterator().next();
			return getTranslator(c, dt).translate();
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (spec.getLogicalOperator() == OR) {
			for (SearchCondition c : conditions) {
				result.should(getTranslator(c, dt).translate());
			}
		}
		else {
			for (SearchCondition c : conditions) {
				result.must(getTranslator(c, dt).translate());
			}
		}
		return result;
	}

	private static void turnIntoFilter(SearchCondition condition)
	{
		condition.setFilter(Boolean.TRUE);
		if (condition.getAnd() != null) {
			for (SearchCondition c : condition.getAnd()) {
				turnIntoFilter(c);
			}
		}
		if (condition.getOr() != null) {
			for (SearchCondition c : condition.getOr()) {
				turnIntoFilter(c);
			}
		}
	}

}
