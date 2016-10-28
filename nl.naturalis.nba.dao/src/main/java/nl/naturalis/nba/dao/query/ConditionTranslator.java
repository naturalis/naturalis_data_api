package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS_IC;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_LIKE;
import static nl.naturalis.nba.api.query.LogicalOperator.AND;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.EnumSet;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.PrimitiveField;
import nl.naturalis.nba.dao.DocumentType;

/**
 * Converts a {@link Condition} to an Elasticsearch {@link QueryBuilder}
 * instance.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class ConditionTranslator {

	/**
	 * Translates the conditions in the provided {@link QuerySpec} instance.
	 * 
	 * @param qs
	 * @param type
	 * @return
	 * @throws InvalidConditionException
	 */
	public static QueryBuilder translate(QuerySpec qs, DocumentType<?> type)
			throws InvalidConditionException
	{
		List<Condition> conditions = qs.getConditions();
		if (conditions == null || conditions.size() == 0) {
			return QueryBuilders.matchAllQuery();
		}
		if (conditions.size() == 1) {
			return getTranslator(conditions.get(0), type).translate();
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (qs.getLogicalOperator() == AND) {
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

	/*
	 * Negating operators are operators that are translated by replacing them
	 * with their opposite (e.g. NOT_EQUALS with EQUALS) and then wrapping them
	 * within a BoolQuery.mustNot() query.
	 */
	private static final EnumSet<ComparisonOperator> negatingOperators;

	static {
		negatingOperators = EnumSet.of(NOT_EQUALS, NOT_EQUALS_IC, NOT_BETWEEN, NOT_LIKE, NOT_IN);
	}

	final Condition condition;
	final MappingInfo mappingInfo;

	ConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	/**
	 * Converts the {@link Condition} passed in through the
	 * {@link #ConditionTranslator(Condition) constructor} to an Elasticsearch
	 * {@link QueryBuilder} instance.
	 * 
	 * @return
	 * @throws InvalidConditionException
	 */
	public QueryBuilder translate() throws InvalidConditionException
	{
		return translate(false);
	}

	abstract QueryBuilder translateCondition() throws InvalidConditionException;

	/*
	 * Implement any up-front/fail-fast field-operator compatibility check you
	 * can think of. Throw an InvalidConditionException if field is not
	 * compatible with operator.
	 */
	abstract void checkOperatorFieldCombi() throws IllegalOperatorException;

	/*
	 * Implement any up-front/fail-fast operator-value compatibility check you
	 * can think of. Throw an InvalidConditionException if value is not
	 * compatible with operator.
	 */
	abstract void checkOperatorValueCombi() throws InvalidConditionException;

	/**
	 * Returns a {@link PrimitiveField} instance corresponding to the field
	 * specified in the condition.
	 * 
	 * @return
	 * @throws InvalidConditionException
	 */
//	PrimitiveField field() throws InvalidConditionException
//	{
//		try {
//			return (PrimitiveField) mappingInfo.getField(condition.getField());
//		}
//		catch (NoSuchFieldException e) {
//			throw new InvalidConditionException(e.getMessage());
//		}
//	}

	private QueryBuilder translate(boolean nested) throws InvalidConditionException
	{
		checkOperatorFieldCombi();
		checkOperatorValueCombi();
		List<Condition> and = condition.getAnd();
		List<Condition> or = condition.getOr();
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
				BoolQueryBuilder extra = translateWithAndSiblings();
				((BoolQueryBuilder) result).should(extra);
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
		for (Condition sibling : condition.getAnd()) {
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
		for (Condition sibling : condition.getOr()) {
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
		for (Condition sibling : condition.getOr()) {
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

	/*
	 * Whether or not the condition translated by this translator instance uses
	 * a negating operator.
	 */
	private boolean mustNegate()
	{
		return negatingOperators.contains(condition.getOperator());
	}

}
