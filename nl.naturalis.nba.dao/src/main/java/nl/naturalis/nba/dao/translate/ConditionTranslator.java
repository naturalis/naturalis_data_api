package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.*;
import static nl.naturalis.nba.utils.CollectionUtil.hasElements;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.EnumSet;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates a {@link QueryCondition} into an Elasticsearch
 * {@link QueryBuilder} instance. You cannot instantiate a
 * {@code ConditionTranslator} directly. Instances are obtained using a
 * {@link ConditionTranslatorFactory}.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class ConditionTranslator {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(ConditionTranslator.class);

	/*
	 * Negating operators are operators that are translated just like their
	 * opposite (e.g. NOT_BETWEEN <-> BETWEEN), but then wrapped into a
	 * BoolQuery.mustNot() query. Note that NOT_EQUALS and NOT_EQUALS_IC are not
	 * included here. For them separate ConditionTranslator subclasses have been
	 * created. This is because they require special code for NULL handling, and
	 * also because not having them handled separately may result in valid but
	 * awkward Elasticsearch queries (mustNot within mustNot within mustNot
	 * queries).
	 */
	private static final EnumSet<ComparisonOperator> negatingOperators;

	static {
		negatingOperators = EnumSet.of(NOT_BETWEEN, NOT_LIKE, NOT_IN, NOT_MATCHES);
	}

	QueryCondition condition;
	MappingInfo<?> mappingInfo;

	/*
	 * Whether or not to translate this condition for a "nested_filter" block
	 * within the "sort" section of a search request. Ordinarily conditions are
	 * translated for the "query" section of a search request. However, when
	 * sorting on a field within a nested object, you must copy all conditions
	 * on that same field to the sort clause.
	 */
	private boolean forSortField = false;

	ConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	ConditionTranslator forSortField()
	{
		this.forSortField = true;
		return this;
	}

	/**
	 * Converts the {@link QueryCondition} passed in through the
	 * {@link #ConditionTranslator(QueryCondition) constructor} to an
	 * Elasticsearch {@link QueryBuilder} instance.
	 * 
	 * @return
	 * @throws InvalidConditionException
	 */
	public QueryBuilder translate() throws InvalidConditionException
	{
		preprocess();
		QueryBuilder query = translateCondition();
		if (forSortField)
			query = postprocessForSortField(query);
		else
			query = postprocess(query);
		if (hasElements(condition.getAnd())) {
			query = generateAndSiblings(query);
			if (hasElements(condition.getOr())) {
				query = generateOrSiblings(query);
			}
		}
		else if (hasElements(condition.getOr())) {
			query = generateOrSiblings(query);
		}
		return condition.isNegated() ? not(query) : query;
	}

	/*
	 * Implement any up-front/fail-fast checks you can think of. Subclasses
	 * should throw an InvalidConditionException if the condition is deemed
	 * invalid. Subclasses can also use this method to preprocess the condition,
	 * e.g. cast or convert the condition's value. This method is called just
	 * before translateCondition().
	 */
	abstract void preprocess() throws InvalidConditionException;

	/*
	 * Convert the Condition to a QueryBuilder as appropriate for the operator
	 * that the subclass is dealing with.
	 */
	abstract QueryBuilder translateCondition() throws InvalidConditionException;

	/*
	 * Applies processing steps to be taken after the condition has been turned
	 * into an Elasticsearch query. These steps are ordinarily
	 * operator-independent, and hence are implemented here (in the base class).
	 * However, if the need arises subclasses can override this method. This
	 * method is called right after translateCondition().
	 */
	QueryBuilder postprocess(QueryBuilder query)
	{
		if (!isTrueCondition(condition)) {
			String nestedPath = getNestedPath(condition.getField(), mappingInfo);
			if (nestedPath != null) {
				query = nestedQuery(nestedPath, query, ScoreMode.Avg);
			}
			if (hasNegativeOperator()) {
				query = not(query);
			}
		}
		if (constantScoreQueryRequired()) {
			query = constantScoreQuery(query);
		}
		/*
		 * NB even if we created a constant_score query, we still need to honour
		 * the condition's boost setting, because the condition might be
		 * embedded in a bool query, which _is_ a scoring query.
		 */
		query.boost(condition.getBoost());
		return query;
	}

	QueryBuilder postprocessForSortField(QueryBuilder query)
	{
		if (hasNegativeOperator()) {
			query = not(query);
		}
		return query;
	}

	private BoolQueryBuilder generateAndSiblings(QueryBuilder firstSibling)
			throws InvalidConditionException
	{
		BoolQueryBuilder query = boolQuery();
		query.must(firstSibling);
		for (QueryCondition c : condition.getAnd()) {
			query.must(getTranslator(c, mappingInfo).translate());
		}
		return query;
	}

	private BoolQueryBuilder generateOrSiblings(QueryBuilder firstSibling)
			throws InvalidConditionException
	{
		BoolQueryBuilder query = boolQuery();
		query.should(firstSibling);
		for (QueryCondition c : condition.getOr()) {
			query.should(getTranslator(c, mappingInfo).translate());
		}
		return query;
	}

	/*
	 * Whether or not the condition translated by this translator instance uses
	 * a negating operator.
	 */
	boolean hasNegativeOperator()
	{
		return negatingOperators.contains(condition.getOperator());
	}

	private boolean constantScoreQueryRequired()
	{
		QueryCondition c = condition;
		if (c.getOperator() != MATCHES && c.getOperator() != LIKE) {
			return false;
		}
		if (c.isNegated()) {
			return false;
		}
		return c.isConstantScore();
	}

	private static QueryBuilder not(QueryBuilder query)
	{
		return boolQuery().mustNot(query);
	}

	private ConditionTranslator getTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
			throws InvalidConditionException
	{
		ConditionTranslator ct = ConditionTranslatorFactory.getTranslator(condition, mappingInfo);
		ct.forSortField = forSortField;
		return ct;
	}

}
