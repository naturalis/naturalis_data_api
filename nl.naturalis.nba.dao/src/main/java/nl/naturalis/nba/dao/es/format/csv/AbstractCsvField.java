package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.dao.es.format.IDataSetField;

abstract class AbstractCsvField implements IDataSetField {

	private final String name;

	AbstractCsvField(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

}
