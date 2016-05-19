package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.Operator.NOT_BETWEEN;
import static nl.naturalis.nba.api.query.Operator.NOT_EQUALS;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.EnumSet;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.api.query.Operator;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.es.types.ESType;

/**
 * Converts a {@link Condition} to an Elasticsearch {@link QueryBuilder}
 * instance.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class ConditionTranslator {

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

	final Condition condition;
	final MappingInspector inspector;

	/**
	 * Creates a translator for the specified condition.
	 * 
	 * @param condition
	 */
	ConditionTranslator(Condition condition, Class<? extends ESType> forType)
	{
		this.condition = condition;
		this.inspector = MappingInspector.forType(forType);
	}

	ConditionTranslator(Condition condition, MappingInspector inspector)
	{
		this.condition = condition;
		this.inspector = inspector;
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

	protected DocumentField getDocumentField(String path) throws InvalidConditionException
	{
		ESField f;
		try {
			f = inspector.getField(field());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidConditionException(e.getMessage());
		}
		if (!(f instanceof DocumentField)) {
			String fmt = "Path %s specifies a nested structure. Only simple, "
					+ "single-value field are allowed";
			String msg = String.format(fmt, path);
			throw new InvalidConditionException(msg);
		}
		return (DocumentField) f;
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
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		for (Condition sibling : and()) {
			ConditionTranslator translator = ctf.getTranslator(sibling, inspector);
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
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		for (Condition sibling : or()) {
			ConditionTranslator translator = ctf.getTranslator(sibling, inspector);
			if (translator.isNegatingOperator()) {
				boolQuery.should(not(translator.translate(true)));
			}
			else {
				boolQuery.should(translator.translate(true));
			}
		}
		return boolQuery;
	}

	protected abstract QueryBuilder translateCondition() throws InvalidConditionException;

	private static QueryBuilder not(QueryBuilder qb)
	{
		return boolQuery().mustNot(qb);
	}

	private boolean isNegatingOperator()
	{
		Operator op = operator();
		return negatingOperators.contains(op);
	}

	String field()
	{
		return condition.getField();
	}

	Operator operator()
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

}
