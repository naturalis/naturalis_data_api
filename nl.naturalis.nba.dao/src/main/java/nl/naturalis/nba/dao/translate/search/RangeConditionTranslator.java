package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.convertValueForDateField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.invalidDataType;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;

abstract class RangeConditionTranslator extends ConditionTranslator {

	RangeConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getFields().iterator().next();
		RangeQueryBuilder query = rangeQuery(path.toString());
		setRange(query);
		return query;
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ESField field = getESField(condition, mappingInfo);
		switch (field.getType()) {
			case DATE:
				convertValueForDateField(condition);
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