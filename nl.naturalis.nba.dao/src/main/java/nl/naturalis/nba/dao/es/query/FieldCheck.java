package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInfo;

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

	public boolean ok()
	{
		ESField field = mappingInfo.getField(condition.getField());
		return field.getClass() == DocumentField.class;
	}

}
