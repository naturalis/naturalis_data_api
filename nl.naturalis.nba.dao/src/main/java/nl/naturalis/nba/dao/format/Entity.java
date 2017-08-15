package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.dao.format.config.EntityXmlConfig;

/**
 * An {@code Entity} contains the configuration for how to generate one
 * particular file in a data set. For example, DwC archives may contain multiple
 * CSV files, each containing a different type of data (taxa, literature
 * references, vernacular names, etc.). These CSV files are referred to as
 * entities. The {@code Entity} class corresponds to is the &lt;entity&gt;
 * element of the XML configuration file for a dataset and is a beautified
 * version of JAXB class of JAXB class {@link EntityXmlConfig}.
 * 
 * @author Ayco Holleman
 *
 */
public class Entity {

	private String name;
	private DataSource dataSource;
	private IEntityFilter[] filters;
	private IField[] fields;

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

	public IEntityFilter[] getFilters()
	{
		return filters;
	}

	void setFilters(IEntityFilter[] filters)
	{
		this.filters = filters;
	}

	public IField[] getFields()
	{
		return fields;
	}

	void setFields(IField[] fields)
	{
		this.fields = fields;
	}

}
