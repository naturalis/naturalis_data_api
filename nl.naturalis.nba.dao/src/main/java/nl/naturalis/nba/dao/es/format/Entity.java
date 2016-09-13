package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
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
	private IEntityFilter filter;
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

	public IEntityFilter getFilter()
	{
		return filter;
	}

	void setFilter(IEntityFilter filter)
	{
		this.filter = filter;
	}

	public List<IField> getFields()
	{
		return fields;
	}

	public void addField(IField field)
	{
		if (fields == null)
			fields = new ArrayList<>(24);
		fields.add(field);
	}

}
