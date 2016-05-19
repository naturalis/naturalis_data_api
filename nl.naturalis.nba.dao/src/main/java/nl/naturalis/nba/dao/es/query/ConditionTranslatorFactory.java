package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.types.ESType;

public class ConditionTranslatorFactory {

	public ConditionTranslatorFactory()
	{
	}

	public ConditionTranslator getTranslator(Condition condition, Class<? extends ESType> type)
	{
		MappingInspector inspector = MappingInspector.forType(type);
		return getTranslator(condition, inspector);
	}

	public ConditionTranslator getTranslator(Condition condition, MappingInspector inspector)
	{
		switch (condition.getOperator()) {
			case EQUALS:
			case NOT_EQUALS:
				return new EqualsConditionTranslator(condition, inspector);
			case EQUALS_IC:
			case NOT_EQUALS_CI:
				break;
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
