package nl.naturalis.nba.dao.es.format;

import java.io.File;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;

/**
 * A {@code DataSet} defines and specifies a data set. A data set is a file or
 * collection of files containing formatted data (e.g. CSV records). Note that
 * instances of this class provide <i>meta data</i> about a data set. They do
 * not actually store, generate or write data.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSet {

	private DataSetCollection dsc;
	private String name;
	private File home;
	private QuerySpec querySpec;

	/**
	 * Returns the data set collection that this data set belongs to.
	 */
	public DataSetCollection getDataSetCollection()
	{
		return dsc;
	}

	public void setDataSetCollection(DataSetCollection dsc)
	{
		this.dsc = dsc;
	}

	/**
	 * Returns the name of this data set. Data set names must be unique across
	 * all data set collections for an Elasticsearch {@link DocumentType
	 * document type}. In other words, two data set collections based on the
	 * same document type may not each contain a data set with name "X".
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the home directory for this data set. This is where data set
	 * writers will expect to find configuration data for the data set. It could
	 * possibly also be used as a working directory when generating the data.
	 */
	public File getHome()
	{
		return home;
	}

	public void setHome(File home)
	{
		this.home = home;
	}

	/**
	 * Returns the Elasticsearch query that will provide the data for the data
	 * set.
	 */
	public QuerySpec getQuerySpec()
	{
		return querySpec;
	}

	public void setQuerySpec(QuerySpec querySpec)
	{
		this.querySpec = querySpec;
	}

}
