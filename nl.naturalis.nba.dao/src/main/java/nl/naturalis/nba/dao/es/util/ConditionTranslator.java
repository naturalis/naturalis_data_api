package nl.naturalis.nba.dao.es.util;

import static nl.naturalis.nba.api.query.Operator.NOT_BETWEEN;
import static nl.naturalis.nba.api.query.Operator.NOT_EQUALS;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.EnumSet;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.api.query.Operator;

/**
 * Converts an NBA {@link Condition} instance to Elasticsearch
 * {@link QueryBuilder} instance.
 * 
 * @author Ayco Holleman
 *
 */
public class ConditionTranslator {

	/*
	 * Negating operators are operators that are translated by replacing them
	 * with their opposite (e.g. EQUALS in stead of NOT_EQUALS) and then
	 * negating the entire condition that they are part of. For example
	 * ("firstName", NOT_EQUALS, "John") becomes (not("firstName", EQUALS,
	 * "John")).
	 */
	private static final EnumSet<Operator> negatingOperators;

	static {
		negatingOperators = EnumSet.of(NOT_EQUALS, NOT_BETWEEN);
	}

	private final Condition condition;
	private final Class<?> type;

	/**
	 * Creates a translator for the specified condition.
	 * 
	 * @param condition
	 */
	public ConditionTranslator(Condition condition, Class<?> forType)
	{
		this.condition = condition;
		this.type = forType;
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

	private QueryBuilder translate(boolean nested) throws InvalidConditionException
	{
		QueryBuilder result;
		if (and() == null && or() == null) {
			if (!nested && isNegatingOperator()) {
				result = not(translateCondition());
			}
			else {
				result = translateCondition();
			}
		}
		else if (and() != null && or() == null) {
			result = translateWithAndSiblings();
		}
		else if (or() != null && and() == null) {
			result = translateWithOrSiblings();
		}
		else {
			String msg = "A query condition cannot have both AND and OR siblings";
			throw new InvalidConditionException(msg);
		}
		return condition.isNegated() ? not(result) : result;
	}

	private QueryBuilder translateWithAndSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		if (isNegatingOperator()) {
			boolQuery.mustNot(translateCondition());
		}
		else {
			boolQuery.must(translateCondition());
		}
		for (Condition sibling : and()) {
			ConditionTranslator translator = new ConditionTranslator(sibling, type);
			if (translator.isNegatingOperator()) {
				boolQuery.mustNot(translator.translate(true));
			}
			else {
				boolQuery.must(translator.translate(true));
			}
		}
		return boolQuery;
	}

	private QueryBuilder translateWithOrSiblings() throws InvalidConditionException
	{
		BoolQueryBuilder boolQuery = boolQuery();
		if (isNegatingOperator()) {
			boolQuery.should(not(translateCondition()));
		}
		else {
			boolQuery.should(translateCondition());
		}
		for (Condition sibling : or()) {
			ConditionTranslator translator = new ConditionTranslator(sibling, type);
			if (translator.isNegatingOperator()) {
				boolQuery.should(not(translator.translate(true)));
			}
			else {
				boolQuery.should(translator.translate(true));
			}
		}
		return boolQuery;
	}

	private QueryBuilder translateCondition() throws InvalidConditionException
	{
		switch (condition.getOperator()) {
			case EQUALS:
			case NOT_EQUALS:
				if (field().indexOf('.') == -1)
					return termQuery(field(), value());
				int i = field().lastIndexOf('.');
				String path = field().substring(0, i);
				TermQueryBuilder tq = termQuery(field(), value());
				return nestedQuery(path, tq);
			case GT:
				break;
			case GTE:
				break;
			case LT:
				break;
			case LTE:
				break;
			default:
				break;
		}
		return null;
	}

	private static QueryBuilder not(QueryBuilder qb)
	{
		return boolQuery().mustNot(qb);
	}

	private boolean isNegatingOperator()
	{
		Operator op = operator();
		return negatingOperators.contains(op);
	}

	private String field()
	{
		return condition.getField();
	}

	private Operator operator()
	{
		return condition.getOperator();
	}

	private Object value()
	{
		return condition.getValue();
	}

	private List<Condition> and()
	{
		return condition.getAnd();
	}

	private List<Condition> or()
	{
		return condition.getOr();
	}

}
