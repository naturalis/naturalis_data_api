package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.dao.es.format.config.ConditionXmlConfig;

class ConditionBuilder {

	private ConditionXmlConfig config;

	ConditionBuilder(ConditionXmlConfig config)
	{
		this.config = config;
	}

	Condition build() throws DataSetConfigurationException
	{
		return build(config);
	}

	Condition build(ConditionXmlConfig config) throws DataSetConfigurationException
	{
		Condition condition = new Condition();
		condition.setField(config.getField());
		condition.setOperator(ComparisonOperator.parse(config.getOperator()));
		condition.setValue(config.getValue());
		if (config.isNegated()) {
			condition.negate();
		}
		if (config.getAnd().size() != 0) {
			List<Condition> and = new ArrayList<>(config.getAnd().size());
			for (ConditionXmlConfig cfg : config.getAnd()) {
				and.add(build(cfg));
			}
			condition.setAnd(and);
		}
		if (config.getOr().size() != 0) {
			List<Condition> or = new ArrayList<>(config.getOr().size());
			for (ConditionXmlConfig cfg : config.getAnd()) {
				or.add(build(cfg));
			}
			condition.setOr(or);
		}
		return condition;
	}

}
