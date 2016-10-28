package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;
import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.AnalyzableField;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

class LikeConditionTranslator extends ConditionTranslator {

	LikeConditionTranslator(Condition condition, MappingInfo inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		if (value().getClass() != String.class) {
			throw searchTermHasWrongType();
		}
		String value = (String) value();
		if (value.length() < 3) {
			throw error("Search term must contain at least 3 characters with operator %s", LIKE);
		}
		if (value.length() > 10) {
			throw error("Search term must contain at most 10 characters with operator %s", LIKE);
		}
		String nestedPath = MappingInfo.getNestedPath(field());
		String multiField = path() + '.' + LIKE_MULTIFIELD.getName();
		if (nestedPath == null) {
			return termQuery(multiField, value.toLowerCase());
		}
		return nestedQuery(nestedPath, termQuery(multiField, value.toLowerCase()));
	}

	@Override
	void ensureFieldCompatibleWithOperator() throws IllegalOperatorException
	{
		ESField field = null;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Won't happend, already checked
			assert (false);
		}
		if (field.getType() != ESDataType.STRING) {
			throw new IllegalOperatorException(condition);
		}
		if (field.getType() != ESDataType.STRING) {
			throw new IllegalOperatorException(condition);
		}
		if (field instanceof AnalyzableField) {
			AnalyzableField af = (AnalyzableField) field;
			if (af.hasMultiField(LIKE_MULTIFIELD)) {
				return;
			}
		}
		throw new IllegalOperatorException(condition);
	}
}
