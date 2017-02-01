package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.convertValueForDateField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.searchTermHasWrongType;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
		QueryBuilder query = QueryBuilders.rangeQuery(path.toString());
		setRange((RangeQueryBuilder) query);

		if (forSortField) {
			return query;
		}
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null) {
			query = nestedQuery(nestedPath, query, ScoreMode.None);
		}
		if (condition.isFilter().booleanValue()) {
			query = constantScoreQuery(query);
		}
		else if (condition.getBoost() != null) {
			query.boost(condition.getBoost());
		}
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