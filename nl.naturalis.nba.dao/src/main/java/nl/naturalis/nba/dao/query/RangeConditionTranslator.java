package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.convertValueForDateField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.query.TranslatorUtil.searchTermHasWrongType;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
		String field = condition.getField();
		RangeQueryBuilder query = QueryBuilders.rangeQuery(field);
		setRange(query);
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null || forSortField) {
			return query;
		}
		return nestedQuery(nestedPath, query, ScoreMode.None);
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
						throw searchTermHasWrongType(condition);
					}
				}
				break;
			default:
				throw searchTermHasWrongType(condition);
		}
	}
	
	abstract void setRange(RangeQueryBuilder query);

}