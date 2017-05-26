package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.dao.translate.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.invalidDataType;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;

abstract class RangeConditionTranslator extends ConditionTranslator {

	RangeConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField().toString();
		RangeQueryBuilder query = rangeQuery(field);
		setRange(query);
		return query;
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ESField field = getESField(condition, mappingInfo);
		switch (field.getType()) {
			case DATE:
				break;
			case INTEGER:
			case BYTE:
			case DOUBLE:
			case FLOAT:
			case LONG:
			case SHORT:
				Object val = condition.getValue();
				if (val instanceof String) {
					try {
						Double d = Double.valueOf(val.toString());
						condition.setValue(d);
					}
					catch (NumberFormatException e) {
						throw invalidDataType(condition);
					}
				}
				break;
			default:
				throw invalidDataType(condition);
		}
	}

	abstract void setRange(RangeQueryBuilder query);

}