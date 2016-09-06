package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.DocumentField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

public class FieldCheck {

	private final Condition condition;
	private final MappingInfo mappingInfo;

	public FieldCheck(Condition condition, MappingInfo mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	public void execute() throws InvalidConditionException
	{
		if (!ok()) {
			String fmt = "Field %s cannot be queried";
			String msg = String.format(fmt, condition.getField());
			throw new InvalidConditionException(msg);
		}
	}

	public boolean ok() throws InvalidConditionException
	{
		ESField field;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidConditionException(e.getMessage());
		}
		return field instanceof DocumentField;
	}

}