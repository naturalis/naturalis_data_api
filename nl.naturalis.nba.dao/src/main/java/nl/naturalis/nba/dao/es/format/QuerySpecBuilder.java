package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.LogicalOperator;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.api.query.SortField;
import nl.naturalis.nba.dao.es.format.config.ConditionXmlConfig;
import nl.naturalis.nba.dao.es.format.config.ConditionsXmlConfig;
import nl.naturalis.nba.dao.es.format.config.QuerySpecXmlConfig;
import nl.naturalis.nba.dao.es.format.config.SortFieldXmlConfig;

class QuerySpecBuilder {

	private static String ERR_NOT_AN_INTEGER = "<%s> element must contain an integer";

	private QuerySpecXmlConfig config;

	QuerySpecBuilder(QuerySpecXmlConfig config)
	{
		this.config = config;
	}

	QuerySpec build() throws DataSetConfigurationException
	{
		QuerySpec querySpec = new QuerySpec();
		try {
			querySpec.setFrom(config.getFrom());
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_NOT_AN_INTEGER, "from");
			throw new DataSetConfigurationException(msg);
		}
		try {
			querySpec.setSize(config.getSize());
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_NOT_AN_INTEGER, "size");
			throw new DataSetConfigurationException(msg);
		}
		ConditionsXmlConfig cxcs = config.getConditions();
		if (cxcs.getOperator() != null) {
			String name = cxcs.getOperator().name();
			querySpec.setLogicalOperator(LogicalOperator.parse(name));
		}
		querySpec.setConditions(getConditions());
		querySpec.setSortFields(getSortFields());
		return querySpec;
	}

	private List<Condition> getConditions() throws DataSetConfigurationException
	{
		ConditionsXmlConfig cxcs = config.getConditions();
		List<Condition> conditions = new ArrayList<>(cxcs.getCondition().size());
		for (ConditionXmlConfig cxc : cxcs.getCondition()) {
			conditions.add(new ConditionBuilder(cxc).build());
		}
		return conditions;
	}

	private List<SortField> getSortFields()
	{
		if (config.getSortFields().size() == 0)
			return null;
		List<SortField> sortFields = new ArrayList<>(config.getSortFields().size());
		for (SortFieldXmlConfig sfxc : config.getSortFields()) {
			SortField sortField = new SortField();
			sortField.setPath(sfxc.getValue());
			if (sfxc.isAscending() != null) {
				sortField.setAscending(sfxc.isAscending());
			}
			sortFields.add(sortField);
		}
		return sortFields;
	}

}
