package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Class capturing the information necessary to generate a data set. A data set
 * is a file or collection of files containing formatted data (e.g. CSV
 * records). Use a {@link DataSetConfigurationBuilder} to get hold of a
 * {@code DataSetConfiguration} instance.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSet {

	private String name;
	private QuerySpec querySpec;

	public String getName()
	{
		return name;
	}

	void setName(String name)
	{
		this.name = name;
	}

	public QuerySpec getQuerySpec()
	{
		return querySpec;
	}

	void setQuerySpec(QuerySpec querySpec)
	{
		this.querySpec = querySpec;
	}

}
