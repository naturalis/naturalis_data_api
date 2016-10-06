package nl.naturalis.nba.dao.format;

import java.util.List;

/**
 * An {@code EntityConfiguration} specifies how to generate one particular file
 * in a data set. For example, DwC archives may contain multiple CSV files, each
 * containing a different type of data (e.g. taxa, literature references,
 * vernacular names, etc.). These files are referred to as entities.
 * 
 * @author Ayco Holleman
 *
 */
public class Entity {

	private String name;
	private DataSource dataSource;
	private List<IEntityFilter> filters;
	private List<IField> fields;

	Entity()
	{
	}

	public String getName()
	{
		return name;
	}

	void setName(String name)
	{
		this.name = name;
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public List<IEntityFilter> getFilters()
	{
		return filters;
	}

	void setFilters(List<IEntityFilter> filters)
	{
		this.filters = filters;
	}

	public List<IField> getFields()
	{
		return fields;
	}

	void setFields(List<IField> fields)
	{
		this.fields = fields;
	}

}
