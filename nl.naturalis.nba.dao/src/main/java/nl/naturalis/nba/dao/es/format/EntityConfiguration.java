package nl.naturalis.nba.dao.es.format;

class EntityConfiguration {

	private String[] pathToEntity;
	private IDataSetField[] fields;

	String[] getPathToEntity()
	{
		return pathToEntity;
	}

	void setPathToEntity(String[] pathToEntity)
	{
		this.pathToEntity = pathToEntity;
	}

	IDataSetField[] getFields()
	{
		return fields;
	}

	void setFields(IDataSetField[] fields)
	{
		this.fields = fields;
	}

}
