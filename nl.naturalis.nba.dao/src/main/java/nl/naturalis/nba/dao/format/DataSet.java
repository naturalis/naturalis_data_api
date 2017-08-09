package nl.naturalis.nba.dao.format;

/**
 * Class capturing the information necessary to generate a data set. In other
 * words: this class does <i>not</i> model dataset; it models the configuration
 * for how to generate it. A data set is a file or collection of files
 * containing formatted data (e.g. CSV records). Use a {@link DataSetBuilder} to
 * get hold of a {@code DataSet} instance.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSet {

	private DataSource sharedDataSource;
	private Entity[] entities;

	public DataSource getSharedDataSource()
	{
		return sharedDataSource;
	}

	void setSharedDataSource(DataSource sharedDataSource)
	{
		this.sharedDataSource = sharedDataSource;
	}

	public Entity[] getEntities()
	{
		return entities;
	}

	void setEntities(Entity[] entities)
	{
		this.entities = entities;
	}

	public Entity getEntity(String name) throws DataSetConfigurationException
	{
		for (Entity entity : entities) {
			if (entity.getName().equals(name)) {
				return entity;
			}
		}
		String msg = String.format("No such entity: \"%s\"", name);
		throw new DataSetConfigurationException(msg);
	}

}
