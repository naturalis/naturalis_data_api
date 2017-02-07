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
import java.util.List;

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
 * Converts a {@link SearchCondition} to an Elasticsearch {@link QueryBuilder}
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
		List<SearchCondition> and = condition.getAnd();
		List<SearchCondition> or = condition.getOr();
		QueryBuilder query;
		if (and == null && or == null) {
			query = translateCondition();
			if (!siblingCondition) {
				Path path = condition.getFields().iterator().next();
				String nestedPath = getNestedPath(path, mappingInfo);
				if (nestedPath != null) {
					query = nestedQuery(nestedPath, query, ScoreMode.Avg);
				}
				if (isScoringCondition()) {
					if (condition.getBoost() != 0F) {
						query.boost(condition.getBoost());
					}
				}
				else {
					query = constantScoreQuery(query);
				}
			}
			if (condition.getBoost() != 0F) {
				query.boost(condition.getBoost());
			}
		}
		else if (or != null) {
			if (and == null) {
				query = translateWithOrSiblings();
			}
			else {
				query = translateOrSiblings().should(translateWithAndSiblings());
			}
		}
		else {
			query = translateWithAndSiblings();
		}
		/*
		 * Condition might be negated using operator NOT as well as use a
		 * negating comparison operator like NOT_BETWEEN, causing the condition
		 * to be doubly negated.
		 */
		return condition.isNegated() ? not(query) : query;
	}

	private BoolQueryBuilder translateWithAndSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		if (hasNegativeOperator()) {
			boolQuery.mustNot(translateCondition());
		}
		else {
			boolQuery.must(translateCondition());
		}
		for (SearchCondition sibling : condition.getAnd()) {
			ConditionTranslator translator = getTranslator(sibling, mappingInfo);
			if (translator.hasNegativeOperator()) {
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
		if (hasNegativeOperator()) {
			boolQuery.should(not(translateCondition()));
		}
		else {
			boolQuery.should(translateCondition());
		}
		for (SearchCondition sibling : condition.getOr()) {
			ConditionTranslator translator = getTranslator(sibling, mappingInfo);
			if (translator.hasNegativeOperator()) {
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
		for (SearchCondition sibling : condition.getOr()) {
			ConditionTranslator translator = getTranslator(sibling, mappingInfo);
			if (translator.hasNegativeOperator()) {
				boolQuery.should(not(translator.translate(true)));
			}
			else {
				boolQuery.should(translator.translate(true));
			}
		}
		return boolQuery;
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
		if (logger.isDebugEnabled()) {
			logger.debug("Translating condition using {}", ct.getClass().getSimpleName());
		}
		return ct;
	}

	private boolean isScoringCondition()
	{
		SearchCondition c = condition;
		if (c.isNonScoring()) {
			return false;
		}
		if (c.isNegated()) {
			return false;
		}
		ComparisonOperator op = c.getOperator();
		if (negatingOperators.contains(op)) {
			return false;
		}
		return op == MATCHES || op == LIKE;
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
