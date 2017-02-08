package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
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
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates a {@link SearchCondition} into an Elasticsearch
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

	final SearchCondition condition;
	final MappingInfo<?> mappingInfo;

	boolean forSortField = false;

	ConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
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
	 * Converts the {@link SearchCondition} passed in through the
	 * {@link #ConditionTranslator(SearchCondition) constructor} to an
	 * Elasticsearch {@link QueryBuilder} instance.
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
	 * InvalidConditionException if the condition is deemed invalid. You can
	 * also use this method to preprocess the condition, e.g. cast or convert
	 * the condition's value.
	 */
	abstract void checkCondition() throws InvalidConditionException;

	private QueryBuilder translate(boolean siblingCondition) throws InvalidConditionException
	{
		checkCondition();
		QueryBuilder query = translateCondition();
		Path path = condition.getFields().iterator().next();
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null) {
			query = nestedQuery(nestedPath, query, ScoreMode.Avg);
		}
		if (hasNegativeOperator()) {
			query = not(query);
		}
		if (constantScoreQueryRequired()) {
			query = constantScoreQuery(query);
		}
		else {
			query.boost(condition.getBoost());
		}
		if (condition.getAnd() != null && !condition.getAnd().isEmpty()) {
			query = generateAndSiblings(query);
			if (condition.getOr() != null && !condition.getOr().isEmpty()) {
				query = generateOrSiblings(query);
			}
		}
		else if (condition.getOr() != null && !condition.getOr().isEmpty()) {
			query = generateOrSiblings(query);
		}
		if (condition.isNegated()) {
			return not(query);
		}
		return query;
	}

	private BoolQueryBuilder generateAndSiblings(QueryBuilder firstSibling)
			throws InvalidConditionException
	{
		BoolQueryBuilder query = boolQuery();
		query.must(firstSibling);
		for (SearchCondition c : condition.getAnd()) {
			query.must(getTranslator(c, mappingInfo).translate(true));
		}
		return query;
	}

	private BoolQueryBuilder generateOrSiblings(QueryBuilder firstSibling)
			throws InvalidConditionException
	{
		BoolQueryBuilder query = boolQuery();
		query.should(firstSibling);
		for (SearchCondition c : condition.getOr()) {
			query.should(getTranslator(c, mappingInfo).translate(true));
		}
		return query;
	}

	private static QueryBuilder not(QueryBuilder query)
	{
		return boolQuery().mustNot(query);
	}

	private ConditionTranslator getTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
			throws InvalidConditionException
	{
		ConditionTranslator ct = ConditionTranslatorFactory.getTranslator(condition, mappingInfo);
		ct.forSortField = forSortField;
		return ct;
	}

	private boolean constantScoreQueryRequired()
	{
		SearchCondition c = condition;
		if (c.getOperator() != MATCHES && c.getOperator() != LIKE) {
			return false;
		}
		if (c.isNegated()) {
			return false;
		}
		return c.isConstantScore();
	}

	/*
	 * Whether or not the condition translated by this translator instance uses
	 * a negating operator.
	 */
	private boolean hasNegativeOperator()
	{
		return negatingOperators.contains(condition.getOperator());
	}

}
