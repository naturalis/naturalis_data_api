package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class ConditionTranslatorFactory {

	private ConditionTranslatorFactory()
	{
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified {@link Condition
	 * condition} and the specified {@link DocumentType document type}.
	 * 
	 * @param condition
	 * @param type
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(Condition condition, DocumentType type)
	{
		MappingInfo inspector = new MappingInfo(type.getMapping());
		return getTranslator(condition, inspector);
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified {@link Condition
	 * condition} and the specified {@link MappingInfo} object.
	 * 
	 * @param condition
	 * @param mappingInfo
	 * @return
	 */
	public static ConditionTranslator getTranslator(Condition condition, MappingInfo mappingInfo)
	{
		switch (condition.getOperator()) {
			case EQUALS:
			case NOT_EQUALS:
				return new EqualsConditionTranslator(condition, mappingInfo);
			case EQUALS_IC:
			case NOT_EQUALS_IC:
				return new EqualsIgnoreCaseConditionTranslator(condition, mappingInfo);
			case GT:
				return new GTConditionTranslator(condition, mappingInfo);
			case GTE:
				return new GTEConditionTranslator(condition, mappingInfo);
			case LT:
				return new LTConditionTranslator(condition, mappingInfo);
			case LTE:
				return new LTEConditionTranslator(condition, mappingInfo);
			case BETWEEN:
			case NOT_BETWEEN:
				return new BetweenConditionTranslator(condition, mappingInfo);
			case LIKE:
			case NOT_LIKE:
				return new LikeConditionTranslator(condition, mappingInfo);
			case IN:
			case NOT_IN:
				return new InConditionTranslator(condition, mappingInfo);
		}
		return null;
	}

}
