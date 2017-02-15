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
 * A {@code SearchSpecTranslator} translates a {@link SearchSpec} object into an
 * Elasticsearch {@link SearchRequestBuilder query}.
 * 
 * @author Ayco Holleman
 *
 */
public class SearchSpecTranslator {

	private static final Logger logger = getLogger(SearchSpecTranslator.class);
	private static final int DEFAULT_SIZE = 10;

	private SearchSpec spec;
	private DocumentType<?> dt;

	/**
	 * Creates a translator for the specified {@link SearchSpec} object
	 * generating a query for the specified document type.
	 * 
	 * @param querySpec
	 * @param documentType
	 */
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
			overrideNonScoringIfNecessary();
			QueryBuilder query;
			if (spec.isConstantScore()) {
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
		request.setFrom(spec.getFrom() == null ? 0 : spec.getFrom());
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
				 * itself, but it IS an allowed field, populated through
				 * SearchHit.getId() rather than through document data.
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

	/*
	 * This will set the nonScoring field of individual conditions within a
	 * SearchSpec to false if the SearchSpec as a whole is non-scoring or if the
	 * condition is negated. If we are dealing with a non-scoring search the
	 * Elasticsearch query generated from all conditions together is wrapped
	 * into one big constant_score query. The queries generated from the
	 * individual conditions should then not also be wrapped into a
	 * constant_score query. Negated conditions are intrinsically non-scoring,
	 * so do not need to be wrapped into a constant_score query.
	 */
	private void overrideNonScoringIfNecessary()
	{
		if (spec.isConstantScore()) {
			for (SearchCondition c : spec.getConditions()) {
				resetToScoring(c);
			}
		}
		else {
			for (SearchCondition c : spec.getConditions()) {
				if (c.isNegated()) {
					resetToScoring(c);
				}
			}
		}
	}

	private static void resetToScoring(SearchCondition condition)
	{
		if (condition.isConstantScore()) {
			condition.setConstantScore(false);
			if (logger.isDebugEnabled()) {
				String field = condition.getField().toString();
				String msg = "constantScore field for Condition on field {} "
						+ "reset to false because it is a negated condition "
						+ "or because it is already embedded  within a "
						+ "non-scoring context";
				logger.debug(msg, field);
			}
		}
		if (condition.getAnd() != null) {
			for (SearchCondition c : condition.getAnd()) {
				resetToScoring(c);
			}
		}
		if (condition.getOr() != null) {
			for (SearchCondition c : condition.getOr()) {
				resetToScoring(c);
			}
		}
	}

}
