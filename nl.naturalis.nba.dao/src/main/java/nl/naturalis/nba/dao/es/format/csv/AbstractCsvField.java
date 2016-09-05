package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.dao.es.format.IField;

abstract class AbstractCsvField implements IField {

	private String name;

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
