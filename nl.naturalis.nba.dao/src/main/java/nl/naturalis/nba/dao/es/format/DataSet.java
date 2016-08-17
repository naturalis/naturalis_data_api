package nl.naturalis.nba.dao.es.format;

import java.io.File;

public class DataSet {

	private DataSetCollection dsc;
	private String name;
	private File home;

	public DataSetCollection getDataSetCollection()
	{
		return dsc;
	}

	public void setDataSetCollection(DataSetCollection dsc)
	{
		this.dsc = dsc;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public File getHome()
	{
		return home;
	}

	public void setHome(File home)
	{
		this.home = home;
	}


}
