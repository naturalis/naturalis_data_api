package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureFieldIsDateOrNumber;
import static nl.naturalis.nba.dao.query.TranslatorUtil.*;
import static org.domainobject.util.ClassUtil.isA;
import static org.domainobject.util.ClassUtil.isNumber;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

abstract class RangeConditionTranslator extends ConditionTranslator {

	RangeConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		RangeQueryBuilder query = QueryBuilders.rangeQuery(path());
		setRange(query);
		String nestedPath = MappingInfo.getNestedPath(field());
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	@Override
	void ensureOperatorValidForField() throws IllegalOperatorException
	{
		ensureFieldIsDateOrNumber(condition, mappingInfo);
	}

	@Override
	void ensureValueValidForOperator() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ensureValueIsDateOrNumber(condition);
	}

	abstract void setRange(RangeQueryBuilder query);

}