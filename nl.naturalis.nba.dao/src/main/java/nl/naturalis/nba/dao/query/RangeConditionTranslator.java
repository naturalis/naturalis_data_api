package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.query.TranslatorUtil.searchTermHasWrongType;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;

abstract class RangeConditionTranslator extends ConditionTranslator {

	RangeConditionTranslator(Condition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		RangeQueryBuilder query = QueryBuilders.rangeQuery(field);
		setRange(query);
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null || forSortField) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ESField field = getESField(condition, mappingInfo);
		switch (field.getType()) {
			case DATE:
				handleDateField();
				break;
			case BYTE:
			case DOUBLE:
			case FLOAT:
			case INTEGER:
			case LONG:
			case SHORT:
				Object val = condition.getValue();
				if (val instanceof String) {
					try {
						Double d = Double.valueOf(val.toString());
						condition.setValue(d);
					}
					catch (NumberFormatException e) {
						throw searchTermHasWrongType(condition);
					}
				}
				break;
			default:
				throw searchTermHasWrongType(condition);
		}
	}

	private void handleDateField() throws InvalidConditionException
	{
		Object val = condition.getValue();
		if (val instanceof Date) {
			return;
		}
		if (val instanceof String) {
			Date date = TranslatorUtil.asDate(val.toString());
			if (date == null) {
				String fmt = "Invalid date for query condition on field %s: %s";
				String msg = String.format(fmt, condition.getField());
				throw new InvalidConditionException(msg);
			}
			condition.setValue(date);
			return;
		}
		throw searchTermHasWrongType(condition);
	}

	abstract void setRange(RangeQueryBuilder query);

}