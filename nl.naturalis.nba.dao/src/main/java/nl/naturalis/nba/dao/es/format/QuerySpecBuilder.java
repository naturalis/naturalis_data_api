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

	private static String ERR_NOT_AN_INTEGER = "Element <%s> must contain an integer";

	private QuerySpecXmlConfig config;

	QuerySpecBuilder(QuerySpecXmlConfig config)
	{
		this.config = config;
	}

	QuerySpec build() throws DataSetConfigurationException
	{
		QuerySpec querySpec = new QuerySpec();
		querySpec.setFrom(getFrom());
		querySpec.setSize(getSize());
		querySpec.setConditions(getConditions());
		querySpec.setLogicalOperator(getLogicalOperator());
		querySpec.setSortFields(getSortFields());
		return querySpec;
	}

	private int getFrom() throws DataSetConfigurationException
	{
		if (config.getFrom() == null)
			return 0;
		try {
			return Integer.parseInt(config.getFrom());
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_NOT_AN_INTEGER, "from");
			throw new DataSetConfigurationException(msg);
		}
	}

	private int getSize() throws DataSetConfigurationException
	{
		if (config.getSize() == null)
			return 0;
		try {
			return Integer.parseInt(config.getSize());
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_NOT_AN_INTEGER, "size");
			throw new DataSetConfigurationException(msg);
		}
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

	private LogicalOperator getLogicalOperator()
	{
		ConditionsXmlConfig cxcs = config.getConditions();
		if (cxcs.getOperator() == null)
			return null;
		String name = cxcs.getOperator().name();
		return LogicalOperator.parse(name);
	}

	private List<SortField> getSortFields()
	{
		if (config.getSortFields().isEmpty())
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
