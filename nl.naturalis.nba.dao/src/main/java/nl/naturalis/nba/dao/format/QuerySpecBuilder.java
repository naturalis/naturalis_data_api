package nl.naturalis.nba.dao.format;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.dao.format.config.ConditionXmlConfig;
import nl.naturalis.nba.dao.format.config.ConditionsXmlConfig;
import nl.naturalis.nba.dao.format.config.QuerySpecXmlConfig;
import nl.naturalis.nba.dao.format.config.SortFieldXmlConfig;

import static nl.naturalis.nba.api.SortOrder.*;
import static java.lang.Boolean.*;

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

	private Integer getFrom() throws DataSetConfigurationException
	{
		if (config.getFrom() == null)
			return null;
		try {
			return Integer.valueOf(config.getFrom());
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_NOT_AN_INTEGER, "from");
			throw new DataSetConfigurationException(msg);
		}
	}

	private Integer getSize() throws DataSetConfigurationException
	{
		if (config.getSize() == null)
			return null;
		try {
			return Integer.valueOf(config.getSize());
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_NOT_AN_INTEGER, "size");
			throw new DataSetConfigurationException(msg);
		}
	}

	private List<QueryCondition> getConditions() throws DataSetConfigurationException
	{
		ConditionsXmlConfig cxcs = config.getConditions();
		List<QueryCondition> conditions = new ArrayList<>(cxcs.getCondition().size());
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
			SortOrder so = sfxc.isAscending() == FALSE ? DESC : ASC;
			SortField sortField = new SortField(sfxc.getValue(), so);
			sortFields.add(sortField);
		}
		return sortFields;
	}

}
