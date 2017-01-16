package nl.naturalis.nba.dao.query;

import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.Index;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

/**
 * Ensures the a {@link QueryCondition} specifies an existing field and that it is a
 * primitive field (not an object).
 * 
 * @author Ayco Holleman
 *
 */
class FieldCheck {

	private QueryCondition condition;
	private MappingInfo<?> mappingInfo;

	FieldCheck(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	void execute() throws InvalidConditionException
	{
		ESField field;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidConditionException(e.getMessage());
		}
		if (!(field instanceof SimpleField)) {
			String fmt = "Field %s cannot be queried: field is an object";
			String msg = String.format(fmt, condition.getField());
			throw new InvalidConditionException(msg);
		}
		SimpleField sf = (SimpleField) field;
		if (sf.getIndex() == Index.NO) {
			String fmt = "Field %s cannot be queried: field is not indexed";
			String msg = String.format(fmt, condition.getField());
			throw new InvalidConditionException(msg);
		}
	}

}
