package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.EnumSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Converts a {@link QueryCondition} to an Elasticsearch {@link QueryBuilder}
 * instance.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class ConditionTranslator {

	private static final Logger logger = getLogger(ConditionTranslator.class);

	/*
	 * Negating operators are operators that are translated just like their
	 * opposite (e.g. NOT_BETWEEN <-> BETWEEN), but then wrapped into a
	 * BoolQuery.mustNot() query. Note that NOT_EQUALS and NOT_EQUALS_IC are not
	 * included here! For them separate ConditionTranslator subclasses have been
	 * made. This is because they require special code for NULL handling, and
	 * also because not having them handled separately results in valid but
	 * awkward Elasticsearch queries (mustNot within mustNot within mustNot
	 * queries).
	 */
	private static final EnumSet<ComparisonOperator> negatingOperators;

	static {
		negatingOperators = EnumSet.of(NOT_BETWEEN, NOT_LIKE, NOT_IN, NOT_MATCHES);
	}

	final QueryCondition condition;
	final MappingInfo<?> mappingInfo;

	boolean forSortField = false;

	ConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	public ConditionTranslator forSortField()
	{
		this.forSortField = true;
		return this;
	}

	/**
	 * Converts the {@link QueryCondition} passed in through the
	 * {@link #ConditionTranslator(QueryCondition) constructor} to an Elasticsearch
	 * {@link QueryBuilder} instance.
	 * 
	 * @return
	 * @throws InvalidConditionException
	 */
	public QueryBuilder translate() throws InvalidConditionException
	{
		return translate(false);
	}

	/*
	 * Convert the Condition to a QueryBuilder as appropriate for the operator
	 * that the subclass is dealing with.
	 */
	abstract QueryBuilder translateCondition() throws InvalidConditionException;

	/*
	 * Implement any up-front/fail-fast checks you can think of. Throw an
	 * InvalidConditionException if value is not compatible with operator.
	 */
	abstract void checkCondition() throws InvalidConditionException;

	private QueryBuilder translate(boolean nested) throws InvalidConditionException
	{
		checkCondition();
		List<QueryCondition> and = condition.getAnd();
		List<QueryCondition> or = condition.getOr();
		QueryBuilder result;
		if (and == null && or == null) {
			if (!nested && mustNegate()) {
				result = not(translateCondition());
			}
			else {
				result = translateCondition();
			}
		}
		else if (or != null) {
			if (and == null) {
				result = translateWithOrSiblings();
			}
			else {
				result = translateOrSiblings();
				BoolQueryBuilder meWithAndSiblings = translateWithAndSiblings();
				((BoolQueryBuilder) result).should(meWithAndSiblings);
			}
		}
		else {
			result = translateWithAndSiblings();
		}
		/*
		 * Condition might be negated using operator NOT as well as use a
		 * negating comparison operator like NOT_BETWEEN, causing the condition
		 * to be doubly negated.
		 */
		return condition.isNegated() ? not(result) : result;
	}

	private BoolQueryBuilder translateWithAndSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		if (mustNegate()) {
			boolQuery.mustNot(translateCondition());
		}
		else {
			boolQuery.must(translateCondition());
		}
		for (QueryCondition sibling : condition.getAnd()) {
			ConditionTranslator translator = getTranslator(sibling, mappingInfo);
			if (translator.mustNegate()) {
				boolQuery.mustNot(translator.translate(true));
			}
			else {
				boolQuery.must(translator.translate(true));
			}
		}
		return boolQuery;
	}

	private BoolQueryBuilder translateWithOrSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		if (mustNegate()) {
			boolQuery.should(not(translateCondition()));
		}
		else {
			boolQuery.should(translateCondition());
		}
		for (QueryCondition sibling : condition.getOr()) {
			ConditionTranslator translator = getTranslator(sibling, mappingInfo);
			if (translator.mustNegate()) {
				boolQuery.should(not(translator.translate(true)));
			}
			else {
				boolQuery.should(translator.translate(true));
			}
		}
		return boolQuery;
	}

	private BoolQueryBuilder translateOrSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		for (QueryCondition sibling : condition.getOr()) {
			ConditionTranslator translator = getTranslator(sibling, mappingInfo);
			if (translator.mustNegate()) {
				boolQuery.should(not(translator.translate(true)));
			}
			else {
				boolQuery.should(translator.translate(true));
			}
		}
		return boolQuery;
	}

	private static QueryBuilder not(QueryBuilder qb)
	{
		return boolQuery().mustNot(qb);
	}

	private ConditionTranslator getTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
			throws InvalidConditionException
	{
		ConditionTranslator ct = ConditionTranslatorFactory.getTranslator(condition, mappingInfo);
		ct.forSortField = forSortField;
		if (logger.isDebugEnabled()) {
			logger.debug("Translating condition using {}", ct.getClass().getSimpleName());
		}
		return ct;
	}

	/*
	 * Whether or not the condition translated by this translator instance uses
	 * a negating operator.
	 */
	private boolean mustNegate()
	{
		return negatingOperators.contains(condition.getOperator());
	}

}
