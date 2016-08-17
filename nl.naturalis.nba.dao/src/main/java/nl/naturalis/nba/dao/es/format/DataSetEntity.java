package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.api.query.QuerySpec;

public class DataSetEntity {

	private String name;
	private QuerySpec querySpec;
	private IDataSetField[] fields;

	public DataSetEntity()
	{
	}

	public DataSetEntity(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public QuerySpec getQuerySpec()
	{
		return querySpec;
	}

	public void setQuerySpec(QuerySpec querySpec)
	{
		this.querySpec = querySpec;
	}

	public IDataSetField[] getFields()
	{
		return fields;
	}

	public void setFields(IDataSetField[] fields)
	{
		this.fields = fields;
	}

}
