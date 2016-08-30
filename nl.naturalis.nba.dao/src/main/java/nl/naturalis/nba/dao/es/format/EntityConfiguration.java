package nl.naturalis.nba.dao.es.format;

public class EntityConfiguration {

	private String[] pathToEntity;
	private IDataSetField[] fields;

	public String[] getPathToEntity()
	{
		return pathToEntity;
	}

	void setPathToEntity(String[] pathToEntity)
	{
		this.pathToEntity = pathToEntity;
	}

	public IDataSetField[] getFields()
	{
		return fields;
	}

	void setFields(IDataSetField[] fields)
	{
		this.fields = fields;
	}

}
