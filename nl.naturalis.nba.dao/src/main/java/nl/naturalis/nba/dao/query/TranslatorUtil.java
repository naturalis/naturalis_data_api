package nl.naturalis.nba.dao.query;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

class TranslatorUtil {

	static void ensureFieldIsDateOrNumber(Condition condition, MappingInfo mappingInfo)
			throws IllegalOperatorException
	{
		ESField field = null;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Won't happend, already checked
			assert (false);
		}
		switch (field.getType()) {
			case BYTE:
			case DATE:
			case DOUBLE:
			case FLOAT:
			case INTEGER:
			case LONG:
			case SHORT:
				break;
			default:
				throw new IllegalOperatorException(condition);
		}
	}
}
