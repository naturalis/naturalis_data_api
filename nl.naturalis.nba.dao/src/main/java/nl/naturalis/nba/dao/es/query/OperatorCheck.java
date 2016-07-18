package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.dao.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.es.map.MultiField.LIKE_MULTIFIELD;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.dao.es.map.AnalyzableField;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class OperatorCheck {

	private final Condition condition;
	private final MappingInfo mappingInfo;

	public OperatorCheck(Condition condition, MappingInfo mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	public void execute() throws IllegalOperatorException
	{
		if (!ok()) {
			throw new IllegalOperatorException(condition);
		}
	}

	public boolean ok()
	{
		DocumentField field = (DocumentField) mappingInfo.getField(condition.getField());
		switch (condition.getOperator()) {
			case EQUALS:
			case NOT_EQUALS:
			case IN:
			case NOT_IN:
				return true;
			case EQUALS_IC:
			case NOT_EQUALS_IC:
				if (field instanceof AnalyzableField) {
					AnalyzableField af = (AnalyzableField) field;
					return af.hasMultiField(IGNORE_CASE_MULTIFIELD);
				}
				return false;
			case BETWEEN:
			case NOT_BETWEEN:
				switch (field.getType()) {
					case INTEGER:
					case DATE:
					case FLOAT:
					case DOUBLE:
					case BYTE:
					case LONG:
					case SHORT:
						return true;
					default:
						return false;
				}
			case LIKE:
			case NOT_LIKE:
				if (field instanceof AnalyzableField) {
					AnalyzableField af = (AnalyzableField) field;
					return af.hasMultiField(LIKE_MULTIFIELD);
				}
				return false;
			default:
				return false;
		}
	}

}
