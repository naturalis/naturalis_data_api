package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class ConditionTranslatorFactory {

	public ConditionTranslatorFactory()
	{
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified condition and the
	 * specified document type.
	 * 
	 * @param condition
	 * @param type
	 * @return
	 * @throws InvalidConditionException
	 */
	public ConditionTranslator getTranslator(Condition condition, DocumentType type)
			throws InvalidConditionException
	{
		MappingInfo inspector = new MappingInfo(type.getMapping());
		return getTranslator(condition, inspector);
	}

	public ConditionTranslator getTranslator(Condition condition, MappingInfo inspector)
			throws InvalidConditionException
	{
		switch (condition.getOperator()) {
			case EQUALS:
			case NOT_EQUALS:
				return new EqualsConditionTranslator(condition, inspector);
			case EQUALS_IC:
			case NOT_EQUALS_IC:
				return new EqualsIgnoreCaseConditionTranslator(condition, inspector);
			case GT:
				break;
			case GTE:
				break;
			case LT:
				break;
			case LTE:
				break;
			case BETWEEN:
			case NOT_BETWEEN:
				break;
			case LIKE:
			case NOT_LIKE:
				return new LikeConditionTranslator(condition, inspector);
			default:
				break;
		}
		return null;
	}

}
