package nl.naturalis.nba.dao.translate;

import static org.elasticsearch.index.query.QueryBuilders.existsQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * This ConditionTranslator is called when Condition.operator is EQUALS and
 * Condition.value is null. By not having this situation handled by the
 * {@link EqualsConditionTranslator}, we keep our code and the generated
 * Elasticsearch query easier to read.
 * 
 * @author Ayco Holleman
 *
 */
class IsNullConditionTranslator extends ConditionTranslator {

	IsNullConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField().toString();
		/*
		 * Note what happens here!!! We return an existsQuery, so it seems like
		 * this will translate into an IS NOT NULL query instead of an IS NULL
		 * query. However, we also override hasNegativeOperator() to force the
		 * query to be wrapped into a mustNot() query later on. See
		 * ConditionTranslator.postprocess(). The reason we cannot simply do the
		 * wrapping here is that if the field belongs to a nested object, you
		 * must FIRST wrap the existsQuery into a nestedQuery and then wrap the
		 * nestedQuery into a mustNot query. If you do it the other way round it
		 * won't work. We violate the semantics of hasNegativeOperator somewhat,
		 * although not entirely: IS NULL is equivalent to a unary NOT_EXISTS
		 * operator. Alternatively we could also simply have overridden the
		 * entire postprocess() method. But for now, simply overriding
		 * hasNegativeOperator() does the trick.
		 */
		return existsQuery(field);
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}

	@Override
	boolean hasNegativeOperator()
	{
	  /* If the original queryspec contains just one condition, we need to 
	   * to wrap the exists query just after it has been wrapped  (only when
	   * needed) as a nested path query during translate().
	   */
	  if ( isSingleCondition() ) {
	    return false;
	  }
		return true;
	}

}
