package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS_IC;
import static nl.naturalis.nba.api.query.LogicalOperator.AND;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.EnumSet;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.MappingInfo;

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
	public static QueryBuilder translate(QuerySpec qs, DocumentType type)
			throws InvalidConditionException
	{
		List<Condition> conditions = qs.getConditions();
		if (conditions == null || conditions.size() == 0) {
			return QueryBuilders.matchAllQuery();
		}
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		if (conditions.size() == 1) {
			return ctf.getTranslator(conditions.get(0), type).translate();
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (qs.getLogicalOperator() == AND) {
			for (Condition c : conditions) {
				result.must(ctf.getTranslator(c, type).translate());
			}
		}
		else {
			for (Condition c : conditions) {
				result.should(ctf.getTranslator(c, type).translate());
			}
		}
		return result;
	}

	/*
	 * Negating operators are operators that are translated by replacing them
	 * with their opposite (e&#46;g&#46; NOT_EQUALS with EQUALS) and then
	 * wrapping them with BoolQuery.mustNot().
	 */
	private static final EnumSet<ComparisonOperator> negatingOperators;

	static {
		negatingOperators = EnumSet.of(NOT_EQUALS, NOT_EQUALS_IC, NOT_BETWEEN);
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
	 * {@link #ConditionTranslator(Condition) constructor} into an Elasticsearch
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

	InvalidConditionException error(String msg, Object... msgArgs)
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append("Invalid query condition for field ");
		sb.append(condition.getField());
		sb.append(". ");
		sb.append(String.format(msg, msgArgs));
		return new InvalidConditionException(sb.toString());
	}

	InvalidConditionException searchTermMustNotBeNull()
	{
		return error("Search term must not be null when using operator %s", operator());
	}

	InvalidConditionException searchTermHasWrongType()
	{
		ComparisonOperator op = condition.getOperator();
		Class<?> type = value().getClass();
		return error("Search term has wrong type for operator %s: %s", op, type);
	}

	String path()
	{
		return condition.getField();
	}

	DocumentField field()
	{
		return (DocumentField) mappingInfo.getField(path());
	}

	ComparisonOperator operator()
	{
		return condition.getOperator();
	}

	Object value()
	{
		return condition.getValue();
	}

	List<Condition> and()
	{
		return condition.getAnd();
	}

	List<Condition> or()
	{
		return condition.getOr();
	}

	private QueryBuilder translate(boolean nested) throws InvalidConditionException
	{
		new FieldCheck(condition, mappingInfo).execute();
		new OperatorCheck(condition, mappingInfo).execute();
		QueryBuilder result;
		if (and() == null && or() == null) {
			if (!nested && withNegatingOperator()) {
				result = not(translateCondition());
			}
			else {
				result = translateCondition();
			}
		}
		else if (or() != null) {
			if (and() == null) {
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
		return condition.isNegated() ? not(result) : result;
	}

	private BoolQueryBuilder translateWithAndSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		if (withNegatingOperator()) {
			boolQuery.mustNot(translateCondition());
		}
		else {
			boolQuery.must(translateCondition());
		}
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		for (Condition sibling : and()) {
			ConditionTranslator translator = ctf.getTranslator(sibling, mappingInfo);
			if (translator.withNegatingOperator()) {
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
		if (withNegatingOperator()) {
			boolQuery.should(not(translateCondition()));
		}
		else {
			boolQuery.should(translateCondition());
		}
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		for (Condition sibling : or()) {
			ConditionTranslator translator = ctf.getTranslator(sibling, mappingInfo);
			if (translator.withNegatingOperator()) {
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
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		for (Condition sibling : or()) {
			ConditionTranslator translator = ctf.getTranslator(sibling, mappingInfo);
			if (translator.withNegatingOperator()) {
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
	private boolean withNegatingOperator()
	{
		return negatingOperators.contains(operator());
	}

}
