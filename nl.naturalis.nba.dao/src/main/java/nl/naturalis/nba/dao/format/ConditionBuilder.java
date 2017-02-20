package nl.naturalis.nba.dao.format;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.dao.format.config.ConditionXmlConfig;

class ConditionBuilder {

	private ConditionXmlConfig config;

	ConditionBuilder(ConditionXmlConfig config)
	{
		this.config = config;
	}

	SearchCondition build() throws DataSetConfigurationException
	{
		return build(config);
	}

	SearchCondition build(ConditionXmlConfig config) throws DataSetConfigurationException
	{
		SearchCondition condition = new SearchCondition();
		condition.setField(new Path(config.getField()));
		condition.setOperator(ComparisonOperator.parse(config.getOperator()));
		condition.setValue(config.getValue());
		if (config.isNegated() != null && config.isNegated()) {
			condition.negate();
		}
		if (config.getAnd().size() != 0) {
			List<SearchCondition> and = new ArrayList<>(config.getAnd().size());
			for (ConditionXmlConfig cfg : config.getAnd()) {
				and.add(build(cfg));
			}
			condition.setAnd(and);
		}
		if (config.getOr().size() != 0) {
			List<SearchCondition> or = new ArrayList<>(config.getOr().size());
			for (ConditionXmlConfig cfg : config.getAnd()) {
				or.add(build(cfg));
			}
			condition.setOr(or);
		}
		return condition;
	}

}
